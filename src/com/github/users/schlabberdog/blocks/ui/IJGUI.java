package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.BoardSave;
import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.solver.ISolverDelegate;
import com.github.users.schlabberdog.blocks.solver.Solver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
    private JFrame frame;

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
                startSolve();
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


        avoidWorseCheckbox.setSelected(solver.shouldAvoidWorseStacks());

    }

    public synchronized void validateButtons() {

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

	    long timeTaken = endTime - startTime;
	    long millis = timeTaken%1000;
	    timeTaken = (timeTaken - millis) / 1000;
	    long seconds = timeTaken%60;
	    timeTaken = (timeTaken - seconds) / 60;
	    long minutes = timeTaken % 60;
	    timeTaken = (timeTaken - minutes) / 60; //== hours

	    timeTakenLabel.setText(String.format("%02d:%02d:%02d.%03d", timeTaken, minutes, seconds, millis));
    }


    public synchronized void doNext() {
      /*  solver.skipSolution();
        validateButtons();*/
    }

    public synchronized void startSolve() {
        //gui deaktivieren
        fastForwardButton.setEnabled(false);
        stackLimiterSpinner.setEnabled(false);
        avoidWorseCheckbox.setEnabled(false);
        //werte kopieren
        solver.setStackDepthLimit(((Number) stackLimiterSpinner.getValue()).intValue());
        solver.setAvoidWorseStacks(avoidWorseCheckbox.isSelected());
        //dafür starten wir einen eigenen Thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                solver.solve();
            }
        });
        t.start();

     /*   solver.setStackLimit(((SpinnerNumberModel) stackLimiterSpinner.getModel()).getNumber().intValue());
     /*
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
*/
    }

    public synchronized void doStep() {
     /*   solver.setStackLimit(((SpinnerNumberModel) stackLimiterSpinner.getModel()).getNumber().intValue());
        solver.step();
        validateButtons();*/
    }

    private void createUIComponents() {
        boardView = new BoardView(board);
    }

    public static IJGUI Create(Board board, Solver solver) {
        IJGUI gui = new IJGUI(board,solver);

        solver.setDelegate(gui);

        JFrame frame = new JFrame("IJGUI");
        frame.setContentPane(gui.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
		frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);

        gui.frame = frame;

        return gui;
    }

	@Override
	public synchronized void solverStarted(Solver solver) {
		timer.start();
		startTime = System.currentTimeMillis();
	}

	@Override
	public synchronized void solutionImproved(Solver solver, int solSize) {
		//System.out.println("Better solution: "+solSize);
		bestSolution = solver.getStepList();
	}

	@Override
	public synchronized void solverDone(Solver solver) {
		//jetzt brauchen wir den timer nicht mehr
		timer.stop();
		//einmal müssen wir evtl. von hand noch nacharbeiten
		validateButtons();
		endTime = System.currentTimeMillis();

		//lösung anzeigen
		if(bestSolution != null) {
			IJSolutionBrowser.Create(replyBoard,initialState,bestSolution);
		}
        else {
            JOptionPane.showMessageDialog(frame,"Mit den gegebenen Einstellungen konnte keine Lösung gefunden werden!","Keine Lösung gefunden",JOptionPane.ERROR_MESSAGE);
        }
	}
}
