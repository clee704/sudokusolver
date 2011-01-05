package net.lemonfactory.sudoku;

import java.util.Arrays;

/**
 * Sudoku solver. This implementation permits the {@link #solvePuzzle()} and
 * {@link #solvePuzzleForAnotherSolution()} method to be run in another thread.
 * When the solver is solving a puzzle in another thread, the only callable
 * method by other threads is {@link #abort()}; for other methods, the
 * resulting* behavior is undefined.
 *
 * @author Chungmin Lee
 * @see SudokuType
 * @see SudokuTypes
 * @see SudokuTypeStructure
 */
public final class SudokuSolver {

    /* Constants for the operation stack. */
    private static final int ASSIGN = 0;
    private static final int DELETE_CANDIDATE = 1;
    private static final int CONTROL_VARS = 2;
    private static final int SAVE_POINT = 3;

    private final SudokuTypeStructure structure;

    private final int size;        /* redundant for efficiency */
    private final int totalCells;  /* redundant for efficiency */

    private final int[] givenPuzzle;
    private final int[] grid;
    private final SimpleSet[] candidates;
    private final Technique[] techniques;
    private final OperationStack opStack;
    private final SimpleMinPriorityQueue cellIndexQueue;

    private long initTime;
    private long elapsedTime;
    private int guesses;

    private volatile boolean aborted;
    private boolean assignPhase;

    public SudokuSolver(SudokuTypeStructure structure) {
        this.structure = structure;
        size = structure.size();
        totalCells = size * size;
        givenPuzzle = new int[totalCells];
        grid = new int[totalCells];
        Arrays.fill(grid, -1);
        candidates = new SimpleSet[totalCells];
        for (int i = 0; i < totalCells; ++i)
            candidates[i] = new BooleanArraySimpleSet(size);
        techniques = new Technique[] {
                new NakedPair(),
                new Interactions()};
        opStack = new OperationStack(2048);
        cellIndexQueue = new BinaryMinHeap(totalCells);
    }

    public SudokuTypeStructure getStructure() {
        return structure;
    }

    /**
     * <p>
     * Sets the specified string as a puzzle to solve. Returns {@code false} if
     * the given puzzle is reported as not valid by the given type's
     * {@link SudokuType#isValidPuzzle(String)} method.
     * </p>
     * <p>
     * It may throw a {@link IllegalArgumentException} if the
     * given Sudoku type does not match with the structure of this solver.
     * </p>
     *
     * @param type Sudoku type to be referenced to interpret symbols in the
     *     puzzle
     * @param puzzle puzzle to be set.
     * @return {@code true} if the puzzle is valid and the setting succeed
     * @see SudokuType#isValidPuzzle(String)
     * @see #getStructure()
     */
    public boolean setPuzzle(SudokuType type, String puzzle) {
        if (!structure.equals(type.getStructure()))
            throw new IllegalArgumentException();
        if (type.isValidPuzzle(puzzle)) {
            convert(type, puzzle.toCharArray(), givenPuzzle);
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * Returns the last assigned puzzle or {@code null} if this does not have
     * such a puzzle.
     * </p>
     * <p>
     * It may throw a {@link IllegalArgumentException} if the
     * given Sudoku type does not match with the structure of this solver.
     * </p>
     *
     * @param type Sudoku type to be referenced to construct a puzzle from the
     *     internal representation
     * @return the last assigned puzzle or {@code null} if this does not have
     *     such a puzzle
     * @see #getStructure()
     */
    public String getGivenPuzzle(SudokuType type) {
        if (!structure.equals(type.getStructure()))
            throw new IllegalArgumentException();
        if (givenPuzzle == null)
            return null;
        return String.valueOf(convert(type, givenPuzzle));
    }

    /**
     * <p>
     * Returns the solution if it solved the given puzzle.
     * </p>
     * <p>
     * It may throw a {@link IllegalArgumentException} if the
     * given Sudoku type does not match with the structure of this solver.
     * </p>
     *
     * @param type Sudoku type to be referenced to construct a puzzle from the
     *     internal representation
     * @return the solution if it solved the given puzzle
     * @see #getStructure()
     */
    public String getSolution(SudokuType type) {
        if (!structure.equals(type.getStructure()))
            throw new IllegalArgumentException();
        return String.valueOf(convert(type, grid));
    }

    /**
     * Returns the time, in nanoseconds, spent for the last solving or 0 if
     * this solver has not yet solved any puzzle.
     *
     * @return the time, in nanoseconds, spent for the last solving or 0 if
     *     this solver has not yet solved any puzzle
     */
    public long getTime() {
        return elapsedTime;
    }

    /**
     * Returns the number of guessing of previous solving or 0 if this solver
     * has not yet solved any puzzle.
     *
     * @return the number of guessing of previous solving or 0 if this solver
     *         has not yet solved any puzzle
     */
    public int getGuesses() {
        return guesses;
    }

    /**
     * Solves the assigned puzzle. Returns {@code true} if it solved the
     * puzzle; otherwise {@code false}, especially if it has not been given a
     * puzzle or if the given puzzle is not valid.
     *
     * @return {@code true} if it solved the puzzle; otherwise {@code false},
     *     especially if it has not been given a puzzle or if the given
     *     puzzle is not valid
     * @see #abort()
     */
    public boolean solvePuzzle() {
        if (givenPuzzle == null)
            return false;
        aborted = false;
        initTime = System.nanoTime();
        elapsedTime = 0;
        guesses = 0;
        for (int i = 0; i < totalCells; ++i)
            grid[i] = givenPuzzle[i];
        makeInitialCandidateLists();
        opStack.clear();
        opStack.push(SAVE_POINT);
        cellIndexQueue.clear();
        for (int i = 0; i < totalCells; ++i)
            if (grid[i] < 0)
                cellIndexQueue.push(i, candidates[i].cardinality());
        assignPhase = false;
        return solve();
    }

    /**
     * Continues the previous solving process for another solution. If the
     * solver has never solved any puzzle or there is no another solution, this
     * returns {@code false}. If another solution has been found, this returns
     * {@code true}. When another solution is found, the elapsed time and the
     * number of guesses for the solver will be incremented properly.
     *
     * @return {@code true} if another solution has been found, or
     *     {@code false} if the solver has never solved any puzzle or there is
     *     no another solution
     */
    public boolean solvePuzzleForAnotherSolution() {
        if (opStack.isEmpty() || guesses == 0)
            return false;
        aborted = false;
        initTime = System.nanoTime();
        return solve();
    }

    /**
     * Aborts the current solving process. When aborted, the currently running
     * call for {@link #solvePuzzle} or
     * {@link #solvePuzzleForAnotherSolution()} will return {@code false}.
     *
     * @see #solvePuzzle()
     */
    public void abort() {
        aborted = true;
    }

    /**
     * Returns {@code true} if this solver have never solved any puzzle after
     * {@link #abort()} is called. Due to the timing, it is possible that
     * the previous solving process had been successful even {@link #abort()}
     * is called during the process, although very rare.
     *
     * @return {@code true} if this solver have never solved any puzzle after
     *     {@link #abort()} is called
     */
    public boolean isAborted() {
        return aborted;
    }

    private int[] convert(SudokuType type, char[] from, int[] to) {
        for (int i = 0; i < totalCells; ++i)
            if (from[i] == type.getBlank())
                to[i] = -1;
            else
                to[i] = type.getSymbols().indexOf(from[i]);
        return to;
    }

    private char[] convert(SudokuType type, int[] puzzle) {
        char[] converted = new char[totalCells];
        for (int i = 0; i < totalCells; ++i)
            if (puzzle[i] == -1)
                converted[i] = type.getBlank();
            else
                converted[i] = type.getSymbols().get(puzzle[i]);
        return converted;
    }

    private void makeInitialCandidateLists() {
        for (int i = 0; i < totalCells; ++i) {
            candidates[i].clear();
            if (grid[i] < 0) {
                for (int j = 0; j < size; ++j)
                    candidates[i].add(j);
                for (int j : structure.getNeighborCells(i))
                    if (grid[j] >= 0)
                        candidates[i].remove(grid[j]);
            }
        }
    }

    /**
     * Solves the given puzzle. Returns {@code true} if it solved the puzzle;
     * otherwise returns {@code false}, especially if it faced a dead end which
     * can be formed by the result of a wrong guess.
     *
     * @return {@code true} if it solved the puzzle; otherwise {@code false}
     */
    private boolean solve() {
    mainLoop:
        while (!aborted) {
            if (!assignPhase) {

                /* Stop solving if all cells are not empty;
                   a solution is found. */
                if (cellIndexQueue.isEmpty()) {
                    assignPhase = true;
                    elapsedTime += System.nanoTime() - initTime;
                    return true;
                }

                /* Search the empty cell
                   that has minimum candidate characters. */
                int index = cellIndexQueue.peek();
                int numCandidates = cellIndexQueue.getPriority(index);

                /* Find a hidden single if a naked single is not found. */
                if (numCandidates > 1) {
                    int newCellIndex = findHiddenSingle();
                    if (newCellIndex != -1) {  // a hidden single is found
                        index = newCellIndex;
                        numCandidates = 1;
                    }
                }

                /* Apply other logics (reducing candidates). */
                if (numCandidates > 1) {
                    int opStackSize;
                    reduce: do {
                        opStackSize = opStack.size();
                        for (Technique s : techniques) {
                            if (!s.resolve()) {
                                assignPhase = true;
                                continue mainLoop;
                            }
                            if (opStackSize != opStack.size()) {
                                // re-scan for a naked single
                                index = cellIndexQueue.peek();
                                numCandidates = cellIndexQueue.getPriority(index);
                                // re-scan for a hidden single
                                if (numCandidates > 1) {
                                    int newCellIndex = findHiddenSingle();
                                    if (newCellIndex != -1) {
                                        index = newCellIndex;
                                        numCandidates = 1;
                                        break reduce;
                                    }
                                } else
                                    break reduce;
                            }
                        }
                    } while (opStackSize != opStack.size());
                }

                opStack.push(CONTROL_VARS, index, numCandidates, -1);
                assignPhase = true;
            } else {
                if (opStack.isEmpty()) {
                    elapsedTime += System.nanoTime() - initTime;
                    return false;
                }
                if (opStack.peek()[0] != CONTROL_VARS)
                    cancelUpdate();  // back-track
                int index = opStack.peek()[1];
                int numCandidates = opStack.peek()[2];
                int j = opStack.peek()[3] + 1;

                /* Assign one of the candidates
                   as a confirmed value to the cell. */
                for (int i = j; i < numCandidates; ++i) {
                    if (numCandidates > 1)
                        ++guesses;
                    opStack.peek()[3] = i;
                    opStack.push(SAVE_POINT);
                    if (updateCandidateLists(index)) {
                        assignPhase = false;
                        continue mainLoop;
                    } else {
                        cancelUpdate();
                    }
                }
                cancelUpdate();
            }
        }
        elapsedTime += System.nanoTime() - initTime;
        return false;
    }

    private boolean updateCandidateLists(int updated) {
        grid[updated] = candidates[updated].next();
        opStack.push(ASSIGN, updated, grid[updated]);
        cellIndexQueue.remove(updated);
        for (int i : structure.getNeighborCells(updated)) {
            if (grid[i] < 0) {
                int symbol = grid[updated];
                SimpleSet cands = candidates[i];
                if (cands.contains(symbol)) {
                    int c = cands.cardinality();
                    if (c == 1)
                        return false;
                    cands.remove(symbol);
                    opStack.push(DELETE_CANDIDATE, i, symbol);
                    cellIndexQueue.updatePriority(i, c - 1);
                }
            }
        }
        return true;
    }

    private void cancelUpdate() {
        int[] op;
        for (;;) {
            op = opStack.pop();
            switch (op[0]) {
            case ASSIGN:
                grid[op[1]] = -1;
                cellIndexQueue.push(op[1], candidates[op[1]].cardinality());
                break;
            case DELETE_CANDIDATE:
                candidates[op[1]].add(op[2]);
                cellIndexQueue.updatePriority(op[1], candidates[op[1]].cardinality());
                break;
            case SAVE_POINT:
                return;
            }
        }
    }

    /**
     * Scans rows, columns and boxes to find a cell that has a candidate which
     * is unique along the row, the column or the box which the cell belongs.
     *
     * @return index of the cell found, or <tt>-1</tt> if no cell is found.
     */
    private int findHiddenSingle() {
        for (int i : cellIndexQueue.getElements()) {
            if (i == -1)
                break;
            SimpleSet cands = candidates[i];
            for (int j = 0, n = cands.cardinality(); j < n; ++j) {
                int c = cands.next();
                for (int cgIndex : structure.getCellGroupIndexes(i)) {
                    if (findHiddenSingleHelper(i, c, structure.getCellGroup(cgIndex))) {
                        cands.withdrawCursor();
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private boolean findHiddenSingleHelper(int i, int c, int[] cellGroup) {
        for (int k : cellGroup)
            if (candidates[k].contains(c) && grid[k] < 0 && i != k)
                return false;
        return true;
    }

    private static interface Technique {

        /**
         * Removes redundant candidates by examining the cells. Returns {@code
         * false} if it faced a dead end (an illegal state that can be formed
         * by a wrong guess); otherwise returns {@code true}. To find if it
         * really resolved, the operation stack must be observed.
         *
         * @return {@code false} if it faced a dead end; otherwise {@code true}
         */
        public boolean resolve();
    }

    private class NakedPair implements Technique {

        @Override
        public boolean resolve() {
            for (int i : cellIndexQueue.getElements()) {
                if (i == -1)
                    break;
                SimpleSet cands = candidates[i];
                if (cands.cardinality() == 2) {
                    int c1 = cands.next();
                    int c2 = cands.next();
                    for (int cgIndex : structure.getCellGroupIndexes(i))
                        if (!helper(i, cands, c1, c2, structure.getCellGroup(cgIndex)))
                            return false;
                }
            }
            return true;
        }

        private boolean helper(
                int i,
                SimpleSet candidatesI,
                int c1,
                int c2,
                int[] cellGroup) {
            for (int j : cellGroup) {
                if (i == j)
                    continue;
                SimpleSet candidatesJ = candidates[j];
                if (grid[j] < 0 && candidatesI.equals(candidatesJ))
                    for (int k : cellGroup)
                        if (grid[k] < 0 && i != k && j != k) {
                            SimpleSet candidatesK = candidates[k];
                            int numCandidates = candidatesK.cardinality();
                            if (candidatesK.contains(c1)) {
                                candidatesK.remove(c1);
                                opStack.push(DELETE_CANDIDATE, k, c1);
                                cellIndexQueue.updatePriority(k, --numCandidates);
                            }
                            if (candidatesK.contains(c2)) {
                                candidatesK.remove(c2);
                                opStack.push(DELETE_CANDIDATE, k, c2);
                                cellIndexQueue.updatePriority(k, --numCandidates);
                            }
                            if (numCandidates == 0)
                                return false;
                        }
            }
            return true;
        }
    }

    private class Interactions implements Technique {

        private final SimpleSet merged = new BooleanArraySimpleSet(size);

        @Override
        public boolean resolve() {
            int n = structure.getNumCellGroups();
            for (int cgIndex1 = 0; cgIndex1 < n; ++cgIndex1) {
                for (int cgIndex2 = cgIndex1 + 1; cgIndex2 < n; ++cgIndex2) {
                    if (structure.getNumIntersections(cgIndex1, cgIndex2) <= 1)
                        continue;
                    if (!helper(cgIndex1, cgIndex2))
                        return false;
                    if (!helper(cgIndex2, cgIndex1))
                        return false;
                }
            }
            return true;
        }

        private boolean helper(int cgIndex1, int cgIndex2) {
            merged.clear();
            for (int k : structure.getCellGroupDiff(cgIndex1, cgIndex2)) {
                if (grid[k] >= 0)
                    merged.add(grid[k]);
                else
                    merged.addAll(candidates[k]);
                if (merged.cardinality() == size)
                    return true;
            }
            merged.complement();
            int c;
            while (merged.cardinality() != 0) {
                c = merged.next();
                merged.remove(c);
                for (int k : structure.getCellGroupDiff(cgIndex2, cgIndex1)) {
                    if (grid[k] >= 0)
                        continue;
                    SimpleSet cands = candidates[k];
                    int numCandidates = cands.cardinality();
                    if (cands.contains(c)) {
                        cands.remove(c);
                        opStack.push(DELETE_CANDIDATE, k, c);
                        cellIndexQueue.updatePriority(k, --numCandidates);
                    }
                    if (numCandidates == 0)
                        return false;
                }
            }
            return true;
        }
    }
}
