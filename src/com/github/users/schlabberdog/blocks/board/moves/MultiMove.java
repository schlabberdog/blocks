package com.github.users.schlabberdog.blocks.board.moves;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;

import java.util.ArrayList;

public class MultiMove implements IMove {
    private ArrayList<SMove> moves = new ArrayList<SMove>();

    private static class SMove {
        Block block;
        int deltaX;
        int deltaY;

        private SMove(Block block, int deltaX, int deltaY) {
            this.block = block;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }

    public MultiMove() {
    }

    public MultiMove(Block block, int deltaX, int deltaY) {
        add(block, deltaX, deltaY);
    }

    public void add(Block block, int deltaX, int deltaY) {
        moves.add(new SMove(block,deltaX,deltaY));
    }

    @Override
    public void apply(Board board) {
        //zuerst müssen alle blocks runter vom board, sonst könnten sie sich beim verschieben überlagern
        for (SMove move : moves) {
            board.removeBlock(move.block);
        }
        //dann korrigieren wir alle positionen
        for (SMove move : moves) {
            int newX = move.block.getX() + move.deltaX;
            int newY = move.block.getY() + move.deltaY;

            move.block.putAt(newX,newY);
        }
        //und zum schluss nehmen wir wieder alle auf
        for (SMove move : moves) {
            board.insertBlock(move.block);
        }
    }

    @Override
    public IMove mergeWith(IMove nextMove) {
        //kommt in der lösung nicht vor...nicht implementiert
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Multi{");
        int i = 0;
        for (SMove move : moves) {
            if(i > 0)
                sb.append(", ");
            sb.append(move.block);
            sb.append(" -> V[");
            sb.append(move.deltaX);
            sb.append('|');
            sb.append(move.deltaY);
            sb.append(']');
            i++;
        }
        sb.append('}');
        return sb.toString();
    }
}
