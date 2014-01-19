package com.github.users.schlabberdog.blocks.solver;


import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.moves.IMove;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Solver implements ISolverSharedState {
	private final Board board;
	private final ISolutionChecker checker;
	private final LeveledSteps steps = new LeveledSteps();

	private ArrayList<SolverThread> solvers = new ArrayList<SolverThread>();

	private AtomicInteger solversRunning = new AtomicInteger();

	private boolean avoidWorseStacks = true;
	private int stackDepthLimit = 0;
	private ISolverDelegate delegate;
	private int numThreads = 1;
	private final Object _lockProperties = new Object();

	private int solutionImprovedCount;
	private int bestPathLength;
	private List<IMove> bestStepList;
	private final Object _lockBestPathLength = new Object();



	public Solver(Board board, ISolutionChecker checker) {
		this.board = board;
		this.checker = checker;
	}

	public void solve() {
		synchronized (_lockProperties) {
			steps.clear();
			solvers.clear();
			synchronized (_lockBestPathLength) {
				solutionImprovedCount = -1;
				bestPathLength = Integer.MAX_VALUE;
				bestStepList = null;
			}
			solversRunning.set(0);

			delegate_solverStarted();
			//solver threads starten
			for (int i = numThreads; i > 0; i--) {
				SolverThread st = new SolverThread(this, board.copy());
				solvers.add(st);
				Thread t = new Thread(st);
				t.start();
			}
		}
	}

	public long getCheckCount() {
		long checkCount = 0;
		for (SolverThread solver : solvers) {
			checkCount += solver.getCheckCount();
		}
		return checkCount;
	}

	public int getSolutionCount() {
		int solutionCount = 0;
		for (SolverThread solver : solvers) {
			solutionCount += solver.getSolutionCount();
		}
		return solutionCount;
	}

	public int getWorstStack() {
		int worstStack = 0;
		for (SolverThread solver : solvers) {
			if (solver.getWorstStack() > worstStack)
				worstStack = solver.getWorstStack();
		}
		return worstStack;
	}

	public int getSolutionImprovedCount() {
		synchronized (_lockBestPathLength) {
			return solutionImprovedCount;
		}
	}

	public int getBestPathLength() {
		synchronized (_lockBestPathLength) {
			return bestPathLength;
		}
	}

	public boolean shouldAvoidWorseStacks() {
		return avoidWorseStacks;
	}

	public void setAvoidWorseStacks(boolean avoidWorseStacks) {
		synchronized (_lockProperties) {
			this.avoidWorseStacks = avoidWorseStacks;
		}
	}

	@Override
	public int getStackDepthLimit() {
		return stackDepthLimit;
	}

	public synchronized void setStackDepthLimit(int stackDepthLimit) {
		synchronized (_lockProperties) {
			this.stackDepthLimit = stackDepthLimit;
		}
	}

	public synchronized void setDelegate(ISolverDelegate delegate) {
		synchronized (_lockProperties) {
			this.delegate = delegate;
		}
	}

	public synchronized void setNumThreads(int numThreads) {
		synchronized (_lockProperties) {
			this.numThreads = numThreads;
		}
	}

	@Override
	public LeveledSteps steps() {
		return steps;
	}

	@Override
	public void solutionImproved(SolverThread solverThread, int solSize) {
		synchronized (_lockBestPathLength) {
			//es kann passieren, dass ein solver sich zwar improved hat, aber ein anderer trotzdem schon besser war
			if (solSize < bestPathLength) {
				bestPathLength = solSize;
				solutionImprovedCount++;
				bestStepList = solverThread.getStepList();
				delegate_solutionImproved(solSize);
			}
		}
	}

	public List<IMove> getBestStepList() {
		synchronized (_lockBestPathLength) {
			return bestStepList;
		}
	}

	@Override
	public void solverStarted(SolverThread solverThread) {
		solversRunning.incrementAndGet();
	}

	@Override
	public void solverDone(SolverThread solverThread) {
		if (solversRunning.decrementAndGet() < 1)
			delegate_solverDone();
	}

	@Override
	public boolean checkBoard(Board board) {
		return checker.checkBoard(board);
	}

	private void delegate_solverStarted() {
		synchronized (_lockProperties) {
			if (delegate != null)
				delegate.solverStarted(this);
		}
	}

	private void delegate_solutionImproved(int solSize) {
		synchronized (_lockProperties) {
			if (delegate != null)
				delegate.solutionImproved(this, solSize);
		}
	}

	private void delegate_solverDone() {
		synchronized (_lockProperties) {
			if (delegate != null)
				delegate.solverDone(this);
		}
	}
}
