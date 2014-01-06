package com.github.users.schlabberdog.blocks.r010;

import com.github.users.schlabberdog.blocks.board.Block;

import java.awt.*;

/* |XX
 */
public class LBlock extends Block {

    public LBlock() {
        super(2,1);
    }

    public LBlock(int x, int y) {
        this();
        putAt(x,y);
    }

    @Override
    public char getRepresentation() {
        return '=';
    }

    @Override
    public Color getColor() {
        return Color.yellow;
    }

    @Override
    public Block copy() {
        Block lb = new LBlock();
        lb.putAt(getX(),getY());
        return lb;
    }

    @Override
    public String toString() {
        return " L{@"+getCoords()+"}";
    }
}
