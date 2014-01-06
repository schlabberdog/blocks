package com.github.users.schlabberdog.blocks.w32;

import com.github.users.schlabberdog.blocks.board.Block;


import java.awt.*;


public class GreenBlock extends Block {


    public GreenBlock() {
        super(1,2);
    }

    public GreenBlock(int x, int y) {
        this();
        putAt(x,y);
    }

    @Override
    public char getRepresentation() {
        return '§';
    }


    @Override
    public Color getColor() {
        return Color.green;
    }

    @Override
    public Block copy() {
        Block kblock = new GreenBlock();
        kblock.putAt(getX(),getY());
        return kblock;
    }


    @Override
    public String toString() {
        return "Green{@"+getCoords()+"}";
    }
}
