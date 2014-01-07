package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.mccs.Coord;
import com.github.users.schlabberdog.blocks.solver.ISolutionChecker;
import com.github.users.schlabberdog.blocks.solver.Solver2;
import com.github.users.schlabberdog.blocks.w32.BlueBlock;
import com.github.users.schlabberdog.blocks.w32.GreenBlock;
import com.github.users.schlabberdog.blocks.w32.RedBlock;
import com.github.users.schlabberdog.blocks.w32.YellowBlock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class IJGUI {
    private BoardView boardView;
    private JButton stepButton;
    private JPanel root;
    private JLabel checkCountLabel;
    private JLabel stackLabel;
    private JButton fastForwardButton;
    private JButton nextButton;
    private JButton stackUpButton;
    private JButton stackDownButton;
    private JLabel numSolutionsLabel;
    private JLabel solImprovLabel;
    private JLabel bestPathLabel;
    private JButton stackStartButton;
    private JButton stackEndButton;
    private JSpinner pathStopLength;
    private JSpinner stackLimiterSpinner;

    private Board board;
    private Solver2 solver;

    private int tos;

    public IJGUI() {
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doStep();
            }
        });
        fastForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFFAction();
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doNext();
            }
        });
        stackUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stackUp();
            }
        });
        stackDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stackDown();
            }
        });

        validateButtons();
        stackStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stackStart();
            }
        });
        stackEndButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stackEnd();
            }
        });

        pathStopLength.setModel(new SpinnerNumberModel(30,0,Integer.MAX_VALUE,1));
        stackLimiterSpinner.setModel(new SpinnerNumberModel(50,0, Integer.MAX_VALUE,1));
    }

    public void validateButtons() {

/*
        stackUpButton.setEnabled(!doingFF && tos > 0);
        stackStartButton.setEnabled(!doingFF && tos > 0);

        stackDownButton.setEnabled(!doingFF && tos+1 < solver.getSaveStack().size());
        stackEndButton.setEnabled(!doingFF && tos+1 < solver.getSaveStack().size());

        stepButton.setEnabled(!doingFF && !solver.isSolved());
        fastForwardButton.setEnabled(!doingFF && !solver.isSolved());
        nextButton.setEnabled(!doingFF && solver.isSolved());

        pathStopLength.setEnabled(!doingFF && !solver.isSolved());
*/
        boardView.repaint();
        checkCountLabel.setText(String.format(Locale.getDefault(), "%,d", solver.getCheckCount()));
        stackLabel.setText(String.format(Locale.getDefault(), "%,d", tos));
        numSolutionsLabel.setText(String.format(Locale.getDefault(), "%,d", solver.getSolutionCount()));
        solImprovLabel.setText(String.format(Locale.getDefault(), "%,d", solver.getSolutionImprovedCount()));
        bestPathLabel.setText(String.format(Locale.getDefault(), "%,d", solver.getBestPathLength()));
    }

    public void stackStart() {
        board.applySave(solver.getSaveStack().get((tos = 0)).initialState);
        validateButtons();
    }

    public void stackEnd() {
        tos = solver.getSaveStack().size();
        board.applySave(solver.getSaveStack().get(tos).initialState);
        validateButtons();
    }

    public void stackUp() {
        --tos;
        selectFromStack();
        validateButtons();
    }

    private void selectFromStack() {
        if(tos == solver.getSaveStack().size()) {
            if(solver.getSaveStack().peek().selected != null) {
                board.applySave(solver.getSaveStack().peek().initialState);
                board.applyMove(solver.getSaveStack().peek().selected);
            }
        }
        else {
            board.applySave(solver.getSaveStack().get(tos).initialState);
        }
    }

    public void stackDown() {
        ++tos;
        selectFromStack();
        validateButtons();
    }

    public void doNext() {
        solver.skipSolution();
        validateButtons();
    }

    public void createFFAction() {
        solver.setStackLimit(((SpinnerNumberModel) stackLimiterSpinner.getModel()).getNumber().intValue());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    doStep();

                    if (solver.isSolved()) {
                        int stopNum = ((SpinnerNumberModel)pathStopLength.getModel()).getNumber().intValue();

                        if(stopNum > 0) {
                            //gleich weiterlaufen, wenn wir da nicht anhalten sollen
                            if(solver.getBestPathLength() > stopNum) {
                                doNext();
                                continue;
                            }
                        }
                        validateButtons();
                        return;
                    }
                    SwingUtilities.invokeLater(this);
                    return;
                }
            }
        };

        SwingUtilities.invokeLater(r);

    }

    public void doStep() {
        solver.setStackLimit(((SpinnerNumberModel) stackLimiterSpinner.getModel()).getNumber().intValue());
        solver.step();
        validateButtons();
    }

    private void createUIComponents() {
/*
        board = new Board(6,7);
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

        solver = new Solver(board,new ISolutionChecker() {
            @Override
            public boolean checkBoard(Board b) {
                tos = solver.getSaveStack().size() - 1;
                validateButtons();
                //gelöst ist das ganze, wenn sich der kblock mit origin bei 1,0 befindet
                return kblock.getX() == 1 && kblock.getY() == 0;
            }
        });
*/
        board = new Board(4,6);
        board.insertBlockAt(new GreenBlock(), 0, 0);
        board.insertBlockAt(new GreenBlock(), 0, 2);
        board.insertBlockAt(new GreenBlock(), 0, 4);
        board.insertBlockAt(new GreenBlock(), 3, 0);
        board.insertBlockAt(new GreenBlock(), 3, 2);
	    
        final Block rblock = new RedBlock();
        board.insertBlockAt(rblock, 1, 0);

	    board.insertBlockAt(new YellowBlock(), 1, 2);
        board.insertBlockAt(new YellowBlock(), 1, 3);

        board.insertBlockAt(new BlueBlock(), 1, 4);
        board.insertBlockAt(new BlueBlock(), 2, 4);
        board.insertBlockAt(new BlueBlock(), 3, 4);
        board.insertBlockAt(new BlueBlock(), 3, 5);

        solver = new Solver2(board,new ISolutionChecker() {
            @Override
            public boolean checkBoard(Board b) {
                tos = solver.getSaveStack().size() - 1;
                validateButtons();
                //gelöst ist das ganze, wenn sich der kblock mit origin bei 1,0 befindet
	            Coord rcoord = b.getBlockCoord(rblock);
                return rcoord.x == 1 && rcoord.y == 4;
            }
        });

        solver.startSolve();

        boardView = new BoardView(board);
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("IJGUI");
        frame.setContentPane(new IJGUI().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
