package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.BoardSave;
import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.r010.R010Game;
import com.github.users.schlabberdog.blocks.solver.ISolverDelegate;
import com.github.users.schlabberdog.blocks.solver.Solver;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IJGUI implements ISolverDelegate {
    private BoardView boardView;
    private JButton stepButton;
    private JPanel root;
    private JLabel checkCountLabel;
    private JLabel stackLabel;
    private JButton fastForwardButton;
    private JButton nextButton;
    private JLabel numSolutionsLabel;
    private JLabel solImprovLabel;
    private JLabel bestPathLabel;
    private JSpinner pathStopLength;
    private JSpinner stackLimiterSpinner;
	private JLabel worstStackLabel;
	private JCheckBox avoidWorseCheckbox;
	private JLabel timeTakenLabel;

	private final Board board;
    private final Solver solver;
	private Timer timer;

	private Board replyBoard;
	private BoardSave initialState;
	private List<IMove> bestSolution = null;

	private long startTime;
	private long endTime;

//    private int tos;

    private IJGUI(Board b,Solver s) {
	    this.board = b;
	    this.solver = s;

	    replyBoard = b.copy();
	    initialState = replyBoard.getSave();

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

        pathStopLength.setModel(new SpinnerNumberModel(30,0,Integer.MAX_VALUE,1));
        stackLimiterSpinner.setModel(new SpinnerNumberModel(50,0, Integer.MAX_VALUE,1));

	    timer = new Timer(50,new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent actionEvent) {
			    endTime = System.currentTimeMillis();
				validateButtons();
		    }
	    });
    }

    public void validateButtons() {

/*

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

	    long timeTaken = endTime - startTime;
	    timeTakenLabel.setText(String.format("%02d:%02d:%02d.%03d", TimeUnit.MILLISECONDS.toHours(timeTaken),
			    TimeUnit.MILLISECONDS.toMinutes(timeTaken),
			    TimeUnit.MILLISECONDS.toSeconds(timeTaken),
			    timeTaken%1000));
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

    public static void main(String[] args) {
	    try {
		    UIManager.setLookAndFeel(new NimbusLookAndFeel());
	    } catch (UnsupportedLookAndFeelException e) {
		    e.printStackTrace();
	    }

	    IGame game = new R010Game();
	    Board board = game.getBoard();

	    Solver solver = new Solver(board,game.getChecker());

	    IJGUI gui = new IJGUI(board,solver);

	    solver.setDelegate(gui);

        JFrame frame = new JFrame("IJGUI");
        frame.setContentPane(gui.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

	    solver.solve();
    }

	@Override
	public void solverStarted(Solver solver) {
		timer.start();
		startTime = System.currentTimeMillis();
		System.out.println("Solver started");
	}

	@Override
	public void solutionImproved(Solver solver, int solSize) {
		//System.out.println("Better solution: "+solSize);
		bestSolution = solver.getStepList();
	}

	@Override
	public void solverDone(Solver solver) {
		System.out.println("Solver done");
		//jetzt brauchen wir den timer nicht mehr
		timer.stop();
		//einmal müssen wir evtl. von hand noch nacharbeiten
		validateButtons();
		endTime = System.currentTimeMillis();

		//lösung anzeigen
		if(bestSolution != null) {
			IJSolutionBrowser.Create(replyBoard,initialState,bestSolution);
		}
	}
}
