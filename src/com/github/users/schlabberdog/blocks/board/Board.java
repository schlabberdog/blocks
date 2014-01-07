package com.github.users.schlabberdog.blocks.board;

import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.mccs.Coord;
import com.github.users.schlabberdog.blocks.mccs.Rect;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

	public void insertBlockAt(Block b, Coord c) {
		//gucken, dass kein block vor dem board landet
		if(c.x < 0 || c.y < 0)
			throw new RuntimeException("Versuche Block <"+b+"> an ungültigen Koordinaten ["+c.x+","+c.y+"] abzulegen!");
		//gucken, dass kein block hinter dem board landet
		if(b.width + c.x > this.width || b.height + c.y > this.height)
			throw new RuntimeException("Block <"+b+"> an Position ["+c.x+","+c.y+"] geht über das Board hinaus!");
		//gucken, dass der block nicht schon auf dem board liegt
		if(blockMap.containsKey(b))
			throw new RuntimeException("Block <"+b+"> liegt schon auf dem Board!");
		//gucken, dass der platz nicht schon belegt ist
		if(intersectsWithRectSet(b.getRectSet(c)))
			throw new RuntimeException("Kann Block <"+b+"> nicht an ["+c.x+","+c.y+"] ablegen. Belegt von anderem Block!");
		//ok, dann einfügen
		insertUnchecked(b,c);
	}

	private void insertUnchecked(Block b, Coord c) {
		blockMapReverse[c.x][c.y] = b;
		blockMap.put(b,c);
	}

    public void insertBlockAt(Block b,int x,int y) {
		insertBlockAt(b,new Coord(x,y));
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
     * @param rs Der zu prüfende Block
     * @return True oder False
     */
    public boolean intersectsWithRectSet(Rect[] rs) {
	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
		    if(e.getKey().coversArea(e.getValue(), rs))
			    return true;
	    }

        return false;
    }

    public boolean intersectsWithRect(Rect r) {
	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
		    if(e.getKey().coversArea(e.getValue(), r))
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
        for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
            if(e.getKey().coversArea(e.getValue(), r))
                return e.getKey();
        }
        return null;
    }

    public Map<Block,Coord> getBlockMap() {
        return blockMap;
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
	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
		    e.getKey().printOntoMap(e.getValue(), map);
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
	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
		    e.getKey().printOntoMap(e.getValue(), map);
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
	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
			e.getKey().addAlts(e.getValue(),this,alts);
	    }
        return alts;
    }

    /**
     * Erzeugt ein Snapshot des Boards im aktuellen Zustand
     * @return Der Snapshot
     */
    public BoardSave getSave() {
        BoardSave save = new BoardSave();

	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
		    save.put(e.getKey(),e.getValue());
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
        for (Map.Entry<Block, Coord> e : save.entrySet()) {
			insertUnchecked(e.getKey(),e.getValue());
        }
    }

    public Board copy() { //TODO thread safety
        Board out = new Board(width,height);

	    for (Map.Entry<Block, Coord> e : blockMap.entrySet()) {
		    out.insertBlockAt(e.getKey(),e.getValue());
	    }

        return out;
    }

	public Coord getBlockCoord(Block block) {
		return blockMap.get(block);
	}
}
