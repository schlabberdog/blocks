package com.github.users.schlabberdog.blocks.solver;


import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.BoardSave;
import com.github.users.schlabberdog.blocks.board.moves.IMove;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class Solver2 {
    private Board board;
    private ISolutionChecker checker;
    long checks;
    int solutionCount;
    int solutionImprovedCount;
    private boolean solved;
    int minPathLength = Integer.MAX_VALUE;
    private int stackLimit = 0;

    /* In dem Array bewahren wir uns alle Schritte auf, die wir gemacht haben */
    private LeveledSteps steps = new LeveledSteps();

    private Stack<Backtrack> btStack = new Stack<Backtrack>();

    public Solver2(Board board, ISolutionChecker checker) {
        this.board = board;
        this.checker = checker;
    }

    public Stack<Backtrack> getSaveStack() {
        return btStack;
    }


    public void startSolve() {
        steps.clear();
        btStack.clear();
        checks = 0;
        solutionCount = 0;
        solutionImprovedCount = -1;
        solved = false;
        //ausgangssituation auf level 0 (ungschlagbar)
        steps.pushOnLevel(board.getBoardHash(),0);
        //um das ganze anzustoßen müssen wir zuerst die alternativen für Schritt 0 aufstellen
        goDeeper();
        //jetzt können wir uns am lösen versuchen
    }

    public boolean isSolved() {
        return solved;
    }

    public int getSolutionCount() {
        return solutionCount;
    }

    public int getSolutionImprovedCount() {
        return solutionImprovedCount;
    }

    public int getBestPathLength() {
        return minPathLength;
    }

    public void step() {
        //System.out.println("step");
        if(solved)
            return;



        //System.out.println("%% Eval Board:");
        //board.print(System.out);
        //prüfen wir, ob das was wir haben eine lösung ist
        checks++;
        if(checker.checkBoard(board)) {
            solutionCount++;
            //System.out.println("<Solution Found in: "+(btStack.size() -1)+" moves>");
            int solSize = (btStack.size() - 1);

            //wenn das hier eine versuchte alternative ist müssen wir noch eins tiefer gehen
            if(btStack.peek().selected != null) {
                //System.out.println("!! SLF goDeeper()");
                //goDeeper();
                solSize++;
                //System.out.println("~~");
            }



            //uns interessieren nur lösungen, die besser sind als bereits bekannte
            if(solSize < minPathLength) { //-1 ist notwendig weil die ausgangsposition auch auf dem stack liegt


                System.out.println("<(Better) Solution Found in: "+solSize+" moves>");

                solutionImprovedCount++;
                solved = true;
                //der kürzeste stack produziert nicht zwangsweise die kürzesten lösungen, weil die noch zusammengefasst werden können
                minPathLength = solSize;
                return;
            }
        }
        //System.out.println("%%");

        //nein, ist es nicht.
        goAnywhere();
    }

    private void goAnywhere() {
        //System.out.println("goAnywhere");
        if(btStack.isEmpty())
            throw new RuntimeException("Alle Varianten versucht, keine Lösung gefunden!");
        // vielleicht haben wir auf dieser ebene noch alternativen?
        if(btStack.peek().alternatives.size() > 0) {

            goRight();

        }
        //nein haben wir nicht, d.h. wir müssen eins zurück und nach rechts
        else {
            goUp();

        }

    }

    private void goRight() {
        //System.out.println("goRight");
        //System.out.println("-> RIGHT");
        //nach rechts gehen bedeutet:
        Backtrack cur = btStack.peek();
        // 1. board zum anfangswert dieses stack-levels resetten
        //System.out.print("NEXT -> ");
        board.applySave(cur.initialState);
        //System.out.println("B: "+board.getBoardHash());
        // 2. alternative anwenden und verwerfen
        IMove m = cur.alternatives.get(0);
        cur.alternatives.remove(0);
        //System.out.println("Board before apply:");
        //board.print(System.out);
        //System.out.println("ALT: [S: "+btStack.size()+"] [A: "+cur.alternatives.size()+"] "+m);
        String bkMov = m.toString();
        try {

            board.applyMove(m);

        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println("Before:");
            board.applySave(cur.initialState);
            board.print(System.out);
            System.out.println("Move was:");
            System.out.println(bkMov);
            throw e;
        }
        cur.selected = m;

        //System.out.println("**");

        //System.out.println("A: "+board.getBoardHash());
        String hash = board.getBoardHash();
        steps.pushOnLevel(hash, btStack.size());


        if(stackLimit > 0 && btStack.size() > stackLimit)
            return;

        goDeeper();
    }


    private void goDeeper() {
        //System.out.println("goDeeper");

        //System.out.println("vv goDeeper()");
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

            //jetzt müssen wir vermeiden, dass diese situation einer situation entspricht die bereits im stack ist (= im kreis gelaufen)
            if(steps.containsOnBetterLevel(nextHash,btStack.size()+1)) {
                mi.remove();
            }
            else if(!btStack.empty()) {
                //es kann sein dass der zug die verlängerung des zuges ist, der uns hier hin gebracht hat
                //in dem fall muss der gemergte zug eine ebene hoch (weil es eben als ein zug möglich ist)
                IMove prevMove = btStack.peek().selected;
                //die grundlage für das merge bildet der vorherige zustand sonst kommt der falsche move bei raus
                board.applySave(btStack.peek().initialState);
                IMove merged = prevMove.mergeWith(alt);
                if(merged != null) {
                    //System.out.println("PRV ADD ALT: [S: "+btStack.size()+"]"+merged);
                    btStack.peek().alternatives.add(merged);
                    mi.remove();
                }
            }

            //sicherung anwenden
            board.applySave(save);
        }

        //wenn jetzt noch was übrig geblieben ist:
        //     if(alts.size() > 0 ) {
        //System.out.println(">> Board:");
        //board.print(System.out);
        //System.out.println(">> Alt:");
        //for (IMove alt : alts) {
        //    System.out.println(alt);
        //}
        // 3. board sichern, alternativen hinzufügen und hash speichern
        btStack.push(new Backtrack(save,alts));
        //System.out.println("[S: "+btStack.size()+"] ++");
        //    }

    }

    private void goUp() {
        //System.out.println("goUp");
        //System.out.println("^^ UP [S: "+btStack.size()+"]");
        //nach oben gehen bedeutet:
        // 1. aktuellen stack verwerfen
        btStack.pop();

        //System.out.print("UP   -> ");
        //goAnywhere();

    }

    public long getCheckCount() {
        return checks;
    }


    /**
     * Verwirft eine gefundene Lösung und sucht weiter
     */
    public void skipSolution() {
        //System.out.println("§§ SKIP");
        if(!isSolved())return;
        solved = false;
        goAnywhere();
    }

    public int getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(int stackLimit) {
        this.stackLimit = stackLimit;
    }
}
