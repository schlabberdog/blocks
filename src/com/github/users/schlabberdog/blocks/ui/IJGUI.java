package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.BoardSave;
import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.solver.ISolverDelegate;
import com.github.users.schlabberdog.blocks.solver.Solver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class IJGUI implements ISolverDelegate, Thread.UncaughtExceptionHandler {
    private JPanel root;
    private JLabel checkCountLabel;
    private JButton fastForwardButton;
    private JLabel numSolutionsLabel;
    private JLabel solImprovLabel;
    private JLabel bestPathLabel;
    private JSpinner stackLimiterSpinner;
	private JLabel worstStackLabel;
	private JCheckBox avoidWorseCheckbox;
	private JLabel timeTakenLabel;
	private JSpinner numThreadsSpinner;

	private final Board board;
    private final Solver solver;
	private Timer timer;

	private final BoardSave initialState;
	private List<IMove> bestSolution = null;

	private Stopwatch stopwatch;
    private JFrame frame;

	private Thread solverThread;
	private boolean isSolverRunning = false;

    private IJGUI(Board b,Solver s) {
	    this.board = b;
	    this.solver = s;

	    initialState = board.getSave();

        fastForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				startSolve();
            }
        });


        numThreadsSpinner.setModel(new SpinnerNumberModel(2, 1, Integer.MAX_VALUE, 1));
        stackLimiterSpinner.setModel(new SpinnerNumberModel(0,0, Integer.MAX_VALUE,1));

	    timer = new Timer(50,new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent actionEvent) {
				validateButtons();
		    }
	    });

		stopwatch = new Stopwatch();

        avoidWorseCheckbox.setSelected(solver.shouldAvoidWorseStacks());

    }

    public synchronized void validateButtons() {

	    checkCountLabel.setText(  String.format("%,d", solver.getCheckCount()));
        numSolutionsLabel.setText(String.format("%,d", solver.getSolutionCount()));
        solImprovLabel.setText(   String.format("%,d", solver.getSolutionImprovedCount()));
	    worstStackLabel.setText(  String.format("%,d", solver.getWorstStack()));
        bestPathLabel.setText(    String.format("%,d", solver.getBestPathLength()));

	    long timeTaken = stopwatch.getElapsedTime();
	    long millis = timeTaken%1000;
	    timeTaken = (timeTaken - millis) / 1000;
	    long seconds = timeTaken%60;
	    timeTaken = (timeTaken - seconds) / 60;
	    long minutes = timeTaken % 60;
	    timeTaken = (timeTaken - minutes) / 60; //== hours

	    timeTakenLabel.setText(String.format("%02d:%02d:%02d.%03d", timeTaken, minutes, seconds, millis));
    }

    public synchronized void startSolve() {
        //gui deaktivieren
        fastForwardButton.setEnabled(false);
        stackLimiterSpinner.setEnabled(false);
        avoidWorseCheckbox.setEnabled(false);
		numThreadsSpinner.setEnabled(false);
		//uhr zurücksetzen
		stopwatch.reset();
        //werte kopieren
        solver.setStackDepthLimit(((Number) stackLimiterSpinner.getValue()).intValue());
		solver.setNumThreads(((Number) numThreadsSpinner.getValue()).intValue());
        solver.setAvoidWorseStacks(avoidWorseCheckbox.isSelected());
        //für den solver benutzen wir einen eigenen Thread
		solverThread = new Thread(new Runnable() {
            @Override
            public void run() {
				solver.solve();
            }
        });
		solverThread.setUncaughtExceptionHandler(this);
		solverThread.start();

    }


    private void createUIComponents() {
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

	//region ISolverDelegate
	@Override
	public synchronized void solverStarted(Solver solver) {
		timer.start();
		stopwatch.start();
		isSolverRunning = true;
	}

	@Override
	public void solutionImproved(Solver solver, int solSize) {
		//System.out.println("Better solution: "+solSize);
		bestSolution = solver.getBestStepList();
	}

	@Override
	public synchronized void solverDone(Solver solver) {
		stopRun();

		//der solver thread ruft das hier auf, GUI aktionen müssen aber im GUI thread passieren
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//einmal müssen wir evtl. von hand noch nacharbeiten
				validateButtons();
				//lösung anzeigen
				if(bestSolution != null) {
					IJSolutionBrowser.Create(board.copy(),initialState,bestSolution);
				}
				else {
					JOptionPane.showMessageDialog(frame,"Mit den gegebenen Einstellungen konnte keine Lösung gefunden werden!","Keine Lösung gefunden",JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}
	//endregion

	private void stopRun() {
		//jetzt brauchen wir den timer nicht mehr
		timer.stop();
		//uhr anhalten, wir sind am ende
		stopwatch.stop();

		isSolverRunning = false;
	}

	//region UncaughtExceptionHandler
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		if(thread != solverThread) {
			//Kennen wir uns?
			throwable.printStackTrace(System.err);
			return;
		}
		//auf jeden fall machen wir jetzt nix mehr...
		stopRun();
		//stracktrace in string umwandeln
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		final String stackTrace = sw.toString()
				.replaceAll("&","&amp;")
				.replaceAll("<","&lt;")
				.replaceAll(">","&gt;")
				.replaceAll("\n","<br>")
				.replaceAll("\r","")
				.replaceAll("\t", "  ");
		//dialog anzeigen
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//einmal müssen wir evtl. von hand noch nacharbeiten
				validateButtons();
				//error
				JOptionPane.showMessageDialog(
						frame,
						"<html>Der Solver ist beim Finden einer Lösung abgestürzt.<br><br><pre>"+stackTrace+"</pre></html>",
						"Solver abgestürzt",
						JOptionPane.ERROR_MESSAGE
				);
			}
		});
	}
	//endregion
}
