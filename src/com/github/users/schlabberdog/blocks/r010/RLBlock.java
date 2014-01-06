package com.github.users.schlabberdog.blocks.r010;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.board.moves.Move;
import com.github.users.schlabberdog.blocks.mccs.Rect;
import com.github.users.schlabberdog.blocks.mccs.RectSet;

import java.awt.*;
import java.util.ArrayList;

/* |XX
 * | X
 */
public class RLBlock extends Block {

    public RLBlock() {
        super(2,2);
    }

    public RLBlock(int x, int y) {
        this();
        putAt(x,y);
    }

    @Override
    public void printOntoMap(char[][] m) {
        char c = getRepresentation();
        m[getX()][getY()] = c;
        m[getX()+1][getY()] = c;
        m[getX()+1][getY()+1] = c;
    }

    @Override
    public void addAlts(Board board, ArrayList<IMove> alts) {
        //oben
        {
            if(getY() > 0 && !board.intersectsWithRect(new Rect(getX(),getY()-1,2,1)))
                alts.add(new Move(this,0,-1,"RLBlock Up"));
        }
        //links
        {
            if(getX() > 0 && board.getBlockCovering(getX() - 1, getY()) == null && board.getBlockCovering(getX(), getY() + 1) == null)
                alts.add(new Move(this,-1,0,"RLBlock Left"));
        }
        //rechts
        {
            if(getX()+width < board.width && !board.intersectsWithRect(new Rect(getX()+2,getY(),1,2)))
                alts.add(new Move(this,1,0,"RLBlock Right"));
        }
        //unten
        {
            if(getY()+height < board.height && board.getBlockCovering(getX(), getY() + 1) == null && board.getBlockCovering(getX() + 1, getY() + 2) == null)
                alts.add(new Move(this,0,1,"RLBlock Down"));
        }
    }

    @Override
    public String toString() {
        return "RL{@"+getCoords()+"}";
    }

    @Override
    public Color getColor() {
        return Color.cyan;
    }

    @Override
    public Block copy() {
        Block b = new RLBlock();
        b.putAt(getX(),getY());
        return b;
    }

    @Override
    public RectSet getRectSet() {
        return new RectSet(
                new Rect(getX()  ,getY(),2,1),
                new Rect(getX()+1,getY(),1,2)
        );
    }

    @Override
    public char getRepresentation() {
        return 'R';
    }

}
