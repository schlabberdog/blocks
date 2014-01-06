package com.github.users.schlabberdog.blocks.board.moves;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;

public class Move implements IMove {
    public final Block block;
    public final int deltaX;
    public final int deltaY;

    private final int wasX;
    private final int wasY;

    public final String dbgBoard;

    public Move(Block block, int deltaX, int deltaY, String dbgBoard) {
        this.block = block;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.dbgBoard = dbgBoard;

        this.wasX = block.getX();
        this.wasY = block.getY();
    }

    public void apply(Board board) {
        if(wasX != block.getX() || wasY != block.getY())
            throw new RuntimeException("Position of block changed. Move invalid! (Expected: ["+wasX+"|"+wasY+"] Was: ["+block.getX()+"|"+block.getY()+"])");
        //um nicht die gültigkeit des boards zu verlieren müssen wir den block
        //entfernen, dann prüfen ob er sich an der neuen position platzieren lässt.
        int newX = block.getX() + deltaX;
        int newY = block.getY() + deltaY;

        board.removeBlock(block);
        block.putAt(newX,newY);
        board.insertBlock(block);
    }

    @Override
    public IMove mergeWith(IMove nextMove) {
        if(nextMove == this)
            throw new RuntimeException("Merge with myself! ("+this+")");
        if(nextMove instanceof Move) {
            Move next = (Move) nextMove;
            if(next.block == this.block) {
                //ein merge bei dem sich beide aufheben lohnt sich nicht
                if(next.deltaX+this.deltaX == 0 && next.deltaY+this.deltaY == 0)
                    return null;

                String dbg = "{Merging "+this+" with "+next+"}";
               // System.out.println(dbg);
                //da wir ja netterweise deltas (vektoren) haben können wir die einfach addieren (vektoraddition)
                return new Move(this.block,next.deltaX+this.deltaX,next.deltaY+this.deltaY, dbg);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Move{" + block + ": C["+wasX+"|"+wasY+"] -> V[" + deltaX + "|" + deltaY + "]} (Created on: "+dbgBoard+")";
    }
}
