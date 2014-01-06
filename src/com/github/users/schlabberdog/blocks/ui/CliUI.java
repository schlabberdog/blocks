package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.ImBlock;
import com.github.users.schlabberdog.blocks.r010.*;
import com.github.users.schlabberdog.blocks.solver.ISolutionChecker;
import com.github.users.schlabberdog.blocks.solver.SolverA;

public class CliUI {

    public static void main(String[] args) {

        Board board = new Board(6,7);
        board.insertBlock(new ImBlock(1, 3, 0, 0));
        board.insertBlock(new ImBlock(1, 1, 0, 6));
        board.insertBlock(new ImBlock(1, 3, 5, 0));
        board.insertBlock(new ImBlock(2, 2, 2, 0));

        final Block kblock = new KBlock(4,0);
        board.insertBlock(kblock);

        board.insertBlock(new LBlock(2, 3));
        board.insertBlock(new LBlock(3, 4));
        board.insertBlock(new LUBlock(1, 2));
        board.insertBlock(new RUBlock(4, 3));

        board.insertBlock(new RLBlock(1, 5));

        SolverA solver = new SolverA(board,new ISolutionChecker() {
            @Override
            public boolean checkBoard(Board b) {
                //gelöst ist das ganze, wenn sich der kblock mit origin bei 1,0 befindet
                return kblock.getX() == 1 && kblock.getY() == 0;
            }
        });

        solver.startSolve();

        //solver.setStackLimit(25);
/*
        while(true) {
            solver.step();

            String hash = board.getBoardHash();

            if(hash.equals("/X XX§X/X XX§X/XUU  X/ U==  / UU LL/ RR==L/X R LL/")) {
                System.out.println("Zug 1");
            }
            if(hash.equals("/X XX§X/X XX§X/XUU  X/ U == / UU LL/ RR==L/X R LL/")) {
                System.out.println("Zug 2.0");
            }
            if(hash.equals("/X XX§X/X XX§X/XUU  X/ U  ==/ UU LL/ RR==L/X R LL/")) {
                System.out.println("Zug 2.1");
            }
            if(hash.equals("/X XX§X/X XX§X/X UU X/  U ==/  UULL/ RR==L/X R LL/")) {
                System.out.println("Zug 3");
            }
            if (hash.equals("/X XX§X/X XX§X/X UU X/RRU ==/ RUULL/   ==L/X   LL/"))
                System.out.println("Zug 4.$");

            if (solver.isSolved()) {
                int stopNum = 16;

                if(stopNum > 0) {
                    //gleich weiterlaufen, wenn wir da nicht anhalten sollen
                    if(solver.getBestPathLength() > stopNum) {
                        solver.skipSolution();
                        continue;
                    }
                }
                break;
            }
        }
*/
        System.out.println("Done");

    }
}
