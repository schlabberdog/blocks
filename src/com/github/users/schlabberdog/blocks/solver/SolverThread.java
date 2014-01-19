package com.github.users.schlabberdog.blocks.solver;

import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.BoardSave;
import com.github.users.schlabberdog.blocks.board.moves.IMove;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

class SolverThread implements Runnable {

	private final ISolverSharedState sharedState;
	private final Board board;
	private final int instance;

	private long checkCount;
	private int solutionCount;
	private int solutionImprovedCount;
	private int bestPathLength;
	private int worstStack;

	private Stack<Backtrack> btStack = new Stack<Backtrack>();

	private static AtomicInteger instanceGenerator = new AtomicInteger(1);

	SolverThread(ISolverSharedState sharedState, Board board) {
		this.sharedState = sharedState;
		this.board = board;
		this.instance = instanceGenerator.getAndIncrement();
	}

	@Override
	public String toString() {
		return "SolverThread[" + instance +	']';
	}

	public synchronized void solve() {
		solverStarted();

		btStack.clear();
		checkCount = 0;
		solutionCount = 0;
		solutionImprovedCount = -1;
		worstStack = 0;
		bestPathLength = Integer.MAX_VALUE;
		//ausgangssituation auf level 0 (ungschlagbar)
		sharedState.steps().pushOnLevel(board.getBoardHash(), 0);
		//um das ganze anzustoßen müssen wir zuerst die alternativen für Schritt 0 aufstellen
		goDeeper();

		while(true) {
			//solange wir noch alternativen haben...
			if (!goAnywhere())
				break;
		}

		solverDone();
	}

	private boolean goAnywhere() {

		if(btStack.isEmpty())
			return false;
		// vielleicht haben wir auf dieser ebene noch alternativen?
		if(btStack.peek().alternatives.size() > 0) {

			goRight();

		}
		//nein haben wir nicht, d.h. wir müssen eins zurück und nach rechts
		else {
			goUp();

		}
		return true;
	}

	private void goRight() {

		//nach rechts gehen bedeutet:
		Backtrack cur = btStack.peek();
		// 1. board zum anfangswert dieses stack-levels resetten

		board.applySave(cur.initialState);

		// 2. alternative anwenden und verwerfen
		IMove m = cur.alternatives.get(0);
		cur.alternatives.remove(0);

		board.applyMove(m);

		cur.selected = m;

		goDeeper();
	}

	private void checkSolution() {
		//prüfen wir, ob das was wir haben eine lösung ist
		checkCount++;
		if(sharedState.checkBoard(board)) {
			solutionCount++;

			int pathLength = (btStack.size() - 1); //-1 ist notwendig weil die ausgangsposition auch auf dem stack liegt

			//uns interessieren nur lösungen, die besser sind als bereits bekannte
			if(pathLength < bestPathLength) {
				solutionImprovedCount++;
				bestPathLength = pathLength;

				solutionImproved(pathLength);
			}
		}
	}


	private void goDeeper() {
		//schlechtere stacks können eigentlich keine besseren lösungen produzieren
		if(sharedState.shouldAvoidWorseStacks() && btStack.size() > sharedState.getBestPathLength())
			return;
		//über limit?
		if(sharedState.getStackDepthLimit() > 0 && btStack.size() > sharedState.getStackDepthLimit())
			return;

		//sicherung machen
		BoardSave save = board.getSave();


		//tiefergehen bedeutet:
		// 1. alternativen aufstellen
		ArrayList<IMove> alts = board.getAlternatives();


		Iterator<IMove> mi = alts.iterator();
		while(mi.hasNext()) {
			IMove alt = mi.next();
			//alt anwenden
			board.applyMove(alt);
			//hash holen
			String nextHash = board.getBoardHash();
			boolean removed = false;
			//jetzt müssen wir vermeiden, dass diese situation einer situation entspricht die bereits im stack ist (= im kreis gelaufen)
			if(sharedState.steps().containsOnBetterLevel(nextHash, btStack.size() + 1)) {
				mi.remove();
				removed = true;
			}
			else {
				if(!btStack.empty()) {
					//es kann sein dass der zug die verlängerung des zuges ist, der uns hier hin gebracht hat
					//in dem fall muss der gemergte zug eine ebene hoch (weil es eben als ein zug möglich ist)
					IMove prevMove = btStack.peek().selected;
					//die grundlage für das merge bildet der vorherige zustand sonst kommt der falsche move bei raus
					board.applySave(btStack.peek().initialState);
					IMove merged = prevMove.mergeWith(alt);
					if(merged != null) {
						board.applyMove(merged);
						nextHash = board.getBoardHash();
						//wir nehmen es nur auf, wenn es den nicht schon besser gab
						if(sharedState.steps().pushOnLevelOrDefault(nextHash, btStack.size())) {
							btStack.peek().alternatives.add(merged);
						}
						mi.remove();
						removed = true;
					}
				}
			}
			if(!removed) {
				//bereits in dem moment wo wir die absicht haben eine alternative auf level X auszuführen sollte niemand
				//auf level X+n das noch versuchen...
				if(!sharedState.steps().pushOnLevelOrDefault(nextHash, btStack.size() + 1)) {
					//zwischen dem vergleich oben und dem insert hier könnte mittlerweile ein anderer thread
					//eine bessere lösung präsentiert haben.
					mi.remove();
					removed = true;
				}
			}

			//sicherung anwenden
			board.applySave(save);
		}


		// 3. board sichern, alternativen hinzufügen und hash speichern
		btStack.push(new Backtrack(save,alts));

		//lösung prüfen?
		checkSolution();

		if(btStack.size() > worstStack)
			worstStack = btStack.size();

	}

	private void goUp() {

		// 1. aktuellen stack verwerfen
		btStack.pop();

	}


	public long getCheckCount() {
		return checkCount;
	}

	public int getStackDepth() {
		return (btStack.size() - 1);
	}

	public int getSolutionCount() {
		return solutionCount;
	}

	public int getSolutionImprovedCount() {
		return solutionImprovedCount;
	}

	public int getBestPathLength() {
		return bestPathLength;
	}

	public int getWorstStack() {
		return worstStack;
	}


	public synchronized List<IMove> getStepList() {
		ArrayList<IMove> steps = new ArrayList<IMove>();

		for (Backtrack backtrack : btStack) {
			IMove m = backtrack.selected;
			if (m == null)
				break;
			steps.add(m);
		}

		return steps;
	}

	private void solutionImproved(int solSize) {
			sharedState.solutionImproved(this,solSize);
	}

	private void solverStarted() {
			sharedState.solverStarted(this);
	}

	private void solverDone() {
			sharedState.solverDone(this);
	}

	@Override
	public void run() {
		Thread.currentThread().setName(toString());
		solve();
	}
}
