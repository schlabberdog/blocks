package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.r010.R010Game;
import com.github.users.schlabberdog.blocks.solver.Solver;

public class CliUI {

    public static void main(String[] args) {

		IGame game = new R010Game();
		Board board = game.getBoard();

        Solver solver = new Solver(board,game.getChecker());

	    IJGUI.Create(board,solver);

	    long start = System.currentTimeMillis();
        solver.startSolve();
	    long end = System.currentTimeMillis();

	    System.out.println("***");
	    System.out.println("Worst Stack Depth: "+solver.getWorstStack());
	    System.out.println("Solutions found: "+solver.getSolutionCount());
	    System.out.println("Boards checked: "+solver.getCheckCount());
	    System.out.println("Solution improved "+solver.getSolutionImprovedCount()+" times");
	    System.out.println("Best solution: "+solver.getBestPathLength()+" steps");

	    System.out.println("===");
	    System.out.println(String.format("Time taken: %,.3f sec.",((end-start)/1000d)));
	    System.out.println("Done");

    }
}
