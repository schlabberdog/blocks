package com.github.users.schlabberdog.blocks.w32;

import com.github.users.schlabberdog.blocks.board.Block;

import java.awt.*;

public class BlueBlock extends Block {



    public BlueBlock() {
        super(1, 1);
    }

    public BlueBlock(int x, int y) {
        this();
        putAt(x,y);
    }

    @Override
    public char getRepresentation() {
        return '+';
    }

    @Override
    public Color getColor() {
        return Color.blue;
    }

    @Override
    public Block copy() {
        Block block = new BlueBlock();
        block.putAt(getX(), getY());
        return block;
    }

    @Override
    public String toString() {
        return "Blue{@"+getCoords()+"}";
    }

}
