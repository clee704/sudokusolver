package net.lemonfactory.sudoku.gui;

import static net.lemonfactory.sudoku.SudokuTypes.BOX_9;

import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.lemonfactory.sudoku.SudokuSolver;
import net.lemonfactory.sudoku.SudokuType;
import net.lemonfactory.sudoku.SudokuTypeStructure;

/**
 * 
 * @author Chungmin Lee
 */
final class SudokuGUIModel {

    private static final Map<SudokuTypeStructure, SudokuSolver> SOLVERS;

    static {
        SOLVERS = new WeakHashMap<SudokuTypeStructure, SudokuSolver>();
        SOLVERS.put(BOX_9.getStructure(), new SudokuSolver(BOX_9.getStructure()));
    }

    private static SudokuSolver getSolver(SudokuTypeStructure structure) {
        if (!SOLVERS.containsKey(structure))
            SOLVERS.put(structure, new SudokuSolver(structure));
        return SOLVERS.get(structure);
    }

    private List<ChangeListener> changeListeners;
    private final Runnable afterSolving;

    private SudokuType type;
    private SudokuSolver solver;
    private long solutionNo;
    private boolean solving;
    private boolean solved;
    private boolean anotherSolution;
    private boolean changed;
    private boolean structureChanged;
    private ChangeEvent changeEvent;

    public SudokuGUIModel(SudokuType type, Runnable afterSolving) {
        this.changeListeners = new ArrayList<ChangeListener>();
        this.afterSolving = afterSolving;
        this.type = type;
        this.solver = getSolver(type.getStructure());
        this.solving = false;
        this.solved = false;
        this.anotherSolution = false;
        this.changed = false;
        this.structureChanged = false;
    }

    public void setSudokuType(SudokuType type) {
        if (solving || this.type.equals(type)) {
            changed = false;
            structureChanged = false;
            fireStateChanged();
            return;
        }
        changed = true;
        structureChanged =
                this.type.getStructure().equals(type.getStructure())
                ? false : true;
        this.type = type;
        if (structureChanged) {
            solver = getSolver(type.getStructure());
            solved = false;
        }
        fireStateChanged();
    }

    public SudokuType getSudokuType() {
        return type;
    }

    public boolean isSudokuTypeStructureChanged() {
        return structureChanged;
    }

    public boolean isSudokuTypeChanged() {
        return changed;
    }

    public void solve(final String puzzle) {
        solve(puzzle, false);
    }

    public void solveTwice(final String puzzle) {
        solve(puzzle, true);
    }

    private void solve(final String puzzle, final boolean twice) {
        if (solving)
            return;
        if (!type.isValidPuzzle(puzzle))
            throw new IllegalArgumentException(
                    "unmatching type and puzzle; type=" + type
                     + ", puzzle=" + puzzle);
        solving = true;
        solved = false;
        anotherSolution = true;
        solutionNo = 0;
        fireStateChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                solver.setPuzzle(type, puzzle);
                solved = solver.solvePuzzle();
                if (!solver.isAborted()) {
                    if (solved)
                        solutionNo += 1;
                    solving = false;
                    fireStateChanged();
                    afterSolving.run();
                    if (twice)
                        solveForAnotherSolution();
                }
            }
        }).start();
    }

    public void solveForAnotherSolution() {
        if (solving || !solved || !anotherSolution)
            return;
        solving = true;
        fireStateChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                anotherSolution = solver.solvePuzzleForAnotherSolution();
                if (!solver.isAborted()) {
                    if (anotherSolution)
                        solutionNo += 1;
                    solving = false;
                    fireStateChanged();
                    afterSolving.run();
                } else
                    anotherSolution = true;
            }
        }).start();
    }

    public boolean isSolving() {
        return solving;
    }

    public boolean hasSolved() {
        return solved;
    }

    public boolean hasAnotherSolution() {
        return anotherSolution;
    }

    public void abort() {
        if (!solving)
            return;
        solving = false;
        solved = false;
        fireStateChanged();
        solver.abort();
    }

    public void reset() {
        solving = false;
        solved = false;
        changed = false;
        structureChanged = false;
        fireStateChanged();
    }

    public String getGivenPuzzle() {
        return solver.getGivenPuzzle(type);
    }

    public String getSolution() {
        return solver.getSolution(type);
    }

    public long getSolutionNo() {
        return solutionNo;
    }

    public long getTime() {
        return solver.getTime();
    }

    public int getGuesses() {
        return solver.getGuesses();
    }

    public void addChangeListener(ChangeListener l) {
        if (l == null)
            throw new NullPointerException();
        changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }

    public ChangeListener[] getChangeListeners() {
        return changeListeners.toArray(new ChangeListener[0]);
    }

    public void fireStateChanged() {
        if (changeEvent == null)
         changeEvent = new ChangeEvent(this);
        for (ChangeListener l : changeListeners)
            l.stateChanged(changeEvent);
    }
}
