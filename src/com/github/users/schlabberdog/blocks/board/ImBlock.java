package com.github.users.schlabberdog.blocks.board;

import com.github.users.schlabberdog.blocks.board.moves.IMove;

import java.awt.*;
import java.util.ArrayList;

/**
 * Der ImBlock ist ein einfacherer, rechteckiger Block, der aber nach dem Platzieren nicht mehr verschoben werden kann.
 * Er dient dazu das Board zuzuschneiden.
 */
public class ImBlock extends Block {

    public ImBlock(int w, int h,int x,int y) {
        super(w, h);
        putAt(x,y);
    }

    @Override
    public Color getColor() {
        return Color.gray;
    }

    @Override
    public Block copy() {
        return new ImBlock(width,height,getX(),getY());
    }

    @Override
    public char getRepresentation() {
        return '#';
    }

    @Override
    public void addAlts(Board board, ArrayList<IMove> alts) {
        //der hat nie welche
    }
}
