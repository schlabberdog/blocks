package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.solver.Solver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JLabel worstStackLabel;
	private JCheckBox avoidWorseCheckbox;

	private Board board;
    private Solver solver;
	private Timer timer;

//    private int tos;

    private IJGUI(Board b,Solver s) {
	    this.board = b;
	    this.solver = s;

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

	    timer = new Timer(50,new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent actionEvent) {
				validateButtons();
		    }
	    });
	    timer.start();
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

	    checkCountLabel.setText(  String.format("%,d", solver.getCheckCount()));
        stackLabel.setText(       String.format("%,d", solver.getStackDepth()));
        numSolutionsLabel.setText(String.format("%,d", solver.getSolutionCount()));
        solImprovLabel.setText(   String.format("%,d", solver.getSolutionImprovedCount()));
	    worstStackLabel.setText(  String.format("%,d", solver.getWorstStack()));
        bestPathLabel.setText(    String.format("%,d", solver.getBestPathLength()));

	    avoidWorseCheckbox.setSelected(solver.shouldAvoidWorseStacks());
    }

    public void stackStart() {
       /* board.applySave(solver.getSaveStack().get((tos = 0)).initialState);
        validateButtons();*/
    }

    public void stackEnd() {
    /*    tos = solver.getSaveStack().size();
        board.applySave(solver.getSaveStack().get(tos).initialState);
        validateButtons();*/
    }

    public void stackUp() {
     /*   --tos;
        selectFromStack();
        validateButtons();*/
    }

/*

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
*/

    public void stackDown() {
     /*   ++tos;
        selectFromStack();
        validateButtons();*/
    }

    public void doNext() {
      /*  solver.skipSolution();
        validateButtons();*/
    }

    public void createFFAction() {
     /*   solver.setStackLimit(((SpinnerNumberModel) stackLimiterSpinner.getModel()).getNumber().intValue());

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
*/
    }

    public void doStep() {
     /*   solver.setStackLimit(((SpinnerNumberModel) stackLimiterSpinner.getModel()).getNumber().intValue());
        solver.step();
        validateButtons();*/
    }

    private void createUIComponents() {
        boardView = new BoardView(board);
    }

    public static IJGUI Create(Board b, Solver s) {
	    IJGUI gui = new IJGUI(b,s);

        JFrame frame = new JFrame("IJGUI");
        frame.setContentPane(gui.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

	    return gui;
    }
}
