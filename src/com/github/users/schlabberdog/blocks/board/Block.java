package com.github.users.schlabberdog.blocks.board;

import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.board.moves.Move;
import com.github.users.schlabberdog.blocks.mccs.Coord;
import com.github.users.schlabberdog.blocks.mccs.Rect;
import com.github.users.schlabberdog.blocks.mccs.RectSet;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Stellt einen beliebig großen, RECHTECKIGEN Block dar.
 */
public abstract class Block {
    public final int width;
    public final int height;
    private int x;
    private int y;

    protected Block(int w,int h) {
        width = w;
        height = h;
    }

    public void putAt(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public boolean coversArea(Rect other) {
        for (Rect myRect : getRectSet().rects) {
            if(other.intersect(myRect) != null)
                return true;
        }
        return false;
    }

    public boolean coversArea(RectSet other) {
        for (Rect rect : other.rects) {
            if(coversArea(rect))
                return true;
        }
        return false;
    }

    public RectSet getRectSet() {
        return new RectSet(new Rect(x,y,width,height));
    }

    public Coord getCoords() {
        return new Coord(x,y);
    }

    public abstract char getRepresentation();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void printOntoMap(char[][] m) {
        char c = getRepresentation();
        for (int i = getX(); i <(getX()+width); i++) {
            for (int j = getY(); j < (getY()+height); j++) {
                m[i][j] = c;
            }
        }
    }

    public Area drawShape(double bw, double bh) {
        Area out = new Area();
        for (Rect rect : getRectSet().rects) {
            out.add(new Area(new Rectangle2D.Double((bw*rect.getX()+10),(bh*rect.getY()+10),(bw*rect.getWidth()-15),(bh*rect.getHeight()-15))));
        }
        return out;
    }

    /**
     * Weist einen Block an alle seine möglichen Züge auf ein Array zu schreiben
     * @param board Board, auf dem sich der Block befindet
     * @param alts Array, dem die Alternativen hinzugefügt werden sollen
     */
    public void addAlts(Board board, ArrayList<IMove> alts) {
        //um einen block nach links/rechts (oben/unten) zu verschieben muss jeweils links/rechts (oben/unten) davon eine Spalte (Zeile)
        //in Höhe (Breite) des Blocks frei sein.

        //oben
        {
            if(getY() > 0 && !board.intersectsWithRect(new Rect(getX(),getY()-1,width,1)) )
                alts.add(new Move(this,0,-1,"Block Up"));
        }
        //links
        {
            if(getX() > 0 && !board.intersectsWithRect(new Rect(getX()-1,getY(),1,height)) )
                alts.add(new Move(this,-1,0,"Block Left"));
        }
        //rechts
        {
            if(getX()+width < board.width && !board.intersectsWithRect(new Rect(getX()+width,getY(),1,height)) )
                alts.add(new Move(this,1,0,"Block Right"));
        }
        //unten
        {
            if(getY()+height < board.height && !board.intersectsWithRect(new Rect(getX(),getY()+height,width,1)) )
                alts.add(new Move(this,0,1,"Block Down"));
        }
    }

    public abstract Color getColor();

    public abstract Block copy();

    public void putAt(Coord coord) {
        putAt(coord.x,coord.y);
    }
}
