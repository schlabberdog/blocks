package com.github.users.schlabberdog.blocks.solver;

import com.github.users.schlabberdog.blocks.board.Board;

public interface ISolverSharedState {
	int getBestPathLength();

	boolean shouldAvoidWorseStacks();
	int getStackDepthLimit();

	/* In dem Array bewahren wir uns alle Schritte auf, die wir gemacht haben */
	LeveledSteps steps();

	void solutionImproved(SolverThread solverThread, int solSize);

	void solverStarted(SolverThread solverThread);

	void solverDone(SolverThread solverThread);

	boolean checkBoard(Board board);
}
