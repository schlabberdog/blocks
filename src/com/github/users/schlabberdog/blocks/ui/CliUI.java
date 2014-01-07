package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.ImBlock;
import com.github.users.schlabberdog.blocks.mccs.Coord;
import com.github.users.schlabberdog.blocks.r010.*;
import com.github.users.schlabberdog.blocks.solver.ISolutionChecker;
import com.github.users.schlabberdog.blocks.solver.SolverA;

public class CliUI {

    public static void main(String[] args) {

        Board board = new Board(6,7);
        board.insertBlockAt(new ImBlock(1, 3), 0, 0);
        board.insertBlockAt(new ImBlock(1, 1), 0, 6);
        board.insertBlockAt(new ImBlock(1, 3), 5, 0);
        board.insertBlockAt(new ImBlock(2, 2), 2, 0);

        final Block kblock = new KBlock();
        board.insertBlockAt(kblock, 4, 0);

        board.insertBlockAt(new LBlock(), 2, 3);
        board.insertBlockAt(new LBlock(), 3, 4);
        board.insertBlockAt(new LUBlock(), 1, 2);
        board.insertBlockAt(new RUBlock(), 4, 3);

        board.insertBlockAt(new RLBlock(), 1, 5);

        SolverA solver = new SolverA(board,new ISolutionChecker() {
            @Override
            public boolean checkBoard(Board b) {
                //gelöst ist das ganze, wenn sich der kblock mit origin bei 1,0 befindet
	            Coord kcoord = b.getBlockCoord(kblock);
                return kcoord.x == 1 && kcoord.y == 0;
            }
        });


/*
	    Board board = new Board(4,6);
	    board.insertBlock(new GreenBlock(0,0));
	    board.insertBlock(new GreenBlock(0,2));
	    board.insertBlock(new GreenBlock(0,4));
	    board.insertBlock(new GreenBlock(3,0));
	    board.insertBlock(new GreenBlock(3,2));
	    final Block rblock = new RedBlock(1,0);
	    board.insertBlock(rblock);
	    board.insertBlock(new YellowBlock(1,2));
	    board.insertBlock(new YellowBlock(1,3));

	    board.insertBlock(new BlueBlock(1,4));
	    board.insertBlock(new BlueBlock(2,4));
	    board.insertBlock(new BlueBlock(3,4));
	    board.insertBlock(new BlueBlock(3,5));

	    SolverA solver = new SolverA(board,new ISolutionChecker() {
		    @Override
		    public boolean checkBoard(Board b) {
			    //gelöst ist das ganze, wenn sich der kblock mit origin bei 1,0 befindet
			    return rblock.getX() == 1 && rblock.getY() == 4;
		    }
	    });
*/

        solver.startSolve();

        System.out.println("Done");

    }
}
