package com.github.users.schlabberdog.blocks.w32;

import com.github.users.schlabberdog.blocks.board.Block;


import java.awt.*;


public class YellowBlock extends Block {


    public YellowBlock() {
        super(2,1);
    }

    public YellowBlock(int x, int y) {
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
        Block lb = new YellowBlock();
        lb.putAt(getX(),getY());
        return lb;
    }

    @Override
    public String toString() {
        return "Yellow{@"+getCoords()+"}";
    }
}
