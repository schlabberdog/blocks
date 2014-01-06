package com.github.users.schlabberdog.blocks.w32;

import com.github.users.schlabberdog.blocks.board.Block;


import java.awt.*;


public class RedBlock extends Block {



    public RedBlock() {
        super(2,2);
    }

    public RedBlock(int x, int y) {
        this();
        putAt(x,y);
    }

    @Override
    public char getRepresentation() {
        return '*';
    }


    @Override
    public Color getColor() {
        return Color.red;
    }

    @Override
    public Block copy() {
        Block block = new RedBlock();
        block.putAt(getX(), getY());
        return block;
    }



    @Override
    public String toString() {
        return "Red{@"+getCoords()+"}";
    }

}
