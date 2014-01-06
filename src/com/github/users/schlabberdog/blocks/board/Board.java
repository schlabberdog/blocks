package com.github.users.schlabberdog.blocks.board;

import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.mccs.Coord;
import com.github.users.schlabberdog.blocks.mccs.Rect;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {
    public final int width;
    public final int height;


    private HashMap<Block,Coord> blockMap = new HashMap<Block, Coord>();
    private Block[][] blockMapReverse;

    public Board(int w,int h) {
        width = w;
        height = h;

        blockMapReverse = new Block[w][h];
    }

    public void insertBlock(Block b) {
        //gucken, dass kein block vor dem board landet
        if(b.getX() < 0 || b.getY() < 0)
            throw new RuntimeException("Versuche Block <"+b+"> an ungültigen Koordinaten ["+b.getX()+","+b.getY()+"] abzulegen!");
        //gucken, dass kein block hinter dem board landet
        if(b.width + b.getX() > this.width || b.height + b.getY() > this.height)
            throw new RuntimeException("Block <"+b+"> an Position ["+b.getX()+","+b.getY()+"] geht über das Board hinaus!");
        //gucken, dass der block nicht schon auf dem board liegt
        if(blockMap.containsKey(b))
            throw new RuntimeException("Block <"+b+"> liegt schon auf dem Board!");
        //gucken, dass der platz nicht schon belegt ist
        if(intersectsWithBlock(b))
            throw new RuntimeException("Kann Block <"+b+"> nicht an ["+b.getX()+","+b.getY()+"] ablegen. Belegt von anderem Block!");
        //ok, dann einfügen
        blockMapReverse[b.getX()][b.getY()] = b;
        blockMap.put(b,b.getCoords());
    }

    public void removeBlock(Block b) {
        Coord c = blockMap.get(b);
        if(c != null) {
            blockMap.remove(b);
            blockMapReverse[c.x][c.y] = null;
        }
    }

    /**
     * Prüft ob sich der Block mit einem anderen Block auf dem Board überlagert
     * @param b Der zu prüfende Block
     * @return True oder False
     */
    public boolean intersectsWithBlock(Block b) {
        for (Block block : blockMap.keySet()) {
            if(block.coversArea(b.getRectSet()))
                return true;
        }
        return false;
    }

    public boolean intersectsWithRect(Rect r) {
        for (Block block : blockMap.keySet()) {
            if(block.coversArea(r))
                return true;
        }
        return false;
    }

    public Block getBlockOriginatingAt(Coord c) {
        return getBlockOriginatingAt(c.x,c.y);
    }

    public Block getBlockOriginatingAt(int x, int y) {
        return blockMapReverse[x][y];
    }

    public Block getBlockCovering(int x, int y) {
        Rect r = new Rect(x,y,1,1);
        for (Block block : blockMap.keySet()) {
            if(block.coversArea(r))
                return block;
        }
        return null;
    }

    public List<Block> getBlocks() { //todo adapt
        ArrayList<Block> bl = new ArrayList<Block>();
        for (Block block : blockMap.keySet()) {
            bl.add(block);
        }
        return bl;
    }

    /**
     * Der Board Hash identifiziert eine eindeutige Anordnung von Elementen
     * @return Ein String
     */
    public String getBoardHash() {
        StringBuilder sb = new StringBuilder();

        sb.append('/');
        //zuerst eine map für die elemente erzeugen
        char[][] map = new char[width][height];

        //alle elemente auf die map packen
        for (Block block : blockMap.keySet()) {
            block.printOntoMap(map);
        }

        //reihenweise drucken
        for (int y = 0; y < height; y++) {
            //spalten drucken
            for (int x = 0; x < width; x++) {
                char c = map[x][y];
                if(c == '\0')
                    c = ' '; //leere flächen mit leerzeichen füllen
                sb.append(c);
            }
            sb.append('/');
        }

        return sb.toString();
    }

    public void print(PrintStream ps) {
        //zuerst eine map für die elemente erzeugen
        char[][] map = new char[width][height];

        //oberen rand drucken
        ps.print('+');
        for (int i = 0; i < width; i++) {
            ps.print('-');
        }
        ps.print('+');
        ps.println();

        //alle elemente auf die map packen
        for (Block block : blockMap.keySet()) {
            block.printOntoMap(map);
        }

        //reihenweise drucken
        for (int y = 0; y < height; y++) {
            ps.print('|');
            //spalten drucken
            for (int x = 0; x < width; x++) {
                char c = map[x][y];
                if(c == '\0')
                    c = ' '; //leere flächen mit leerzeichen füllen
                ps.print(c);
            }
            ps.print('|');
            ps.println();
        }

        //unteren rand drucken
        ps.print('+');
        for (int i = 0; i < width; i++) {
            ps.print('-');
        }
        ps.print('+');
        ps.println();
    }

    /**
     * Gibt eine Liste aller Möglichen Schritte aus dem aktuellen Boardzustand an
     * @return .
     */
    public ArrayList<IMove> getAlternatives() {
        ArrayList<IMove> alts = new ArrayList<IMove>();
        for (Block block : blockMap.keySet()) {
            //für imblocks gibts keine alternativen, die sind fest
            if(block instanceof ImBlock)
                continue;

            block.addAlts(this, alts);
        }
        return alts;
    }

    /**
     * Erzeugt ein Snapshot des Boards im aktuellen Zustand
     * @return Der Snapshot
     */
    public BoardSave getSave() {
        BoardSave save = new BoardSave();

        for (Block block : blockMap.keySet()) {
            save.put(block,new Coord(block.getX(),block.getY()));
        }

        return save;
    }

    /**
     * Wendet einen Zug auf das aktuelle Board an
     * @param alt Der Zug
     */
    public void applyMove(IMove alt) {
        alt.apply(this);
    }

    public void clear() {
        for (Coord coord : blockMap.values()) {
            blockMapReverse[coord.x][coord.y] = null;
        }

        blockMap.clear();
    }

    /**
     * Stellt einen Snapshot des Boards wieder her
     * @param save Der Snapshot, der wiederhergestellt werden soll
     */
    public void applySave(BoardSave save) {
        //alle aktuellen blocks entfernen
        clear();
        //alle blocks im save wiederherstellen
        for (Block block : save.keySet()) {
            //block auf korrekte position verschieben
            block.putAt(save.get(block));
            //hinzufügen ohne kontrolle
            insertBlock(block); //todo adapt
        }
        //System.out.println("<Restore to "+getBoardHash()+">");
    }

    public Board copy() { //TODO thread safety
        Board out = new Board(width,height);

        for (Block block : blockMap.keySet()) {
            out.insertBlock(block.copy());
        }

        return out;
    }
}
