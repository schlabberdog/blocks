package com.github.users.schlabberdog.blocks.r010;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;
import com.github.users.schlabberdog.blocks.board.moves.IMove;
import com.github.users.schlabberdog.blocks.board.moves.Move;
import com.github.users.schlabberdog.blocks.board.moves.MultiMove;
import com.github.users.schlabberdog.blocks.mccs.Rect;
import com.github.users.schlabberdog.blocks.mccs.RectSet;

import java.awt.*;
import java.util.ArrayList;


/*  |XX
 *  | X
 *  |XX
 */
public class RUBlock extends Block {

    public RUBlock() {
        super(2,3);
    }

    public RUBlock(int x, int y) {
        this();
        putAt(x,y);
    }

    @Override
    public void printOntoMap(char[][] m) {
        char c = getRepresentation();
        m[getX()][getY()] = c;
        m[getX()+1][getY()] = c;
        m[getX()+1][getY()+1] = c;
        m[getX()][getY()+2] = c;
        m[getX()+1][getY()+2] = c;
    }

    @Override
    public Color getColor() {
        return Color.blue;
    }

    @Override
    public Block copy() {
        RUBlock rb = new RUBlock();
        rb.putAt(getX(),getY());
        return rb;
    }

    @Override
    public String toString() {
        return "RU{@"+getCoords()+"}";
    }

    @Override
    public RectSet getRectSet() {
        return new RectSet(
                new Rect(getX()  ,getY()  ,2,1),
                new Rect(getX()+1,getY()  ,1,3),
                new Rect(getX()  ,getY()+2,2,1)
        );
    }

    @Override
    public char getRepresentation() {
        return 'U';
    }

    @Override
    public void addAlts(Board board, ArrayList<IMove> alts) {
        //oben
        {
            if(getY() > 0 && !board.intersectsWithRect(new Rect(getX(),getY()-1,2,1)) && board.getBlockCovering(getX(), getY() + 1) == null)
                alts.add(new Move(this,0,-1,"RUBlock Up"));
        }
        //links
        {
            if(getX() > 0 && board.getBlockCovering(getX() - 1, getY()) == null && board.getBlockCovering(getX(), getY() + 1) == null && board.getBlockCovering(getX() - 1, getY() + 2) == null)
                alts.add(new Move(this,-1,0,"RUBlock Left"));
        }
        //rechts
        {
            if(getX()+width < board.width && !board.intersectsWithRect(new Rect(getX()+2,getY(),1,3)))
                alts.add(new Move(this,1,0,"RUBlock Right"));
        }
        //unten
        {
            if(getY()+height < board.height && board.getBlockCovering(getX(), getY() + 1) == null && !board.intersectsWithRect(new Rect(getX(),getY()+3,2,1)))
                alts.add(new Move(this,0,1,"RUBlock Down"));
        }
        // hier gibt es die zusätzliche besonderheit, dass wenn in der Auskerbung ein LBlock steckt,
        // wir den ggf. mitbewegen können (nur nach oben, unten, links)
        Block insert = board.getBlockCovering(getX(), getY() + 1);
        if(insert instanceof LBlock) {
            //vielleicht können wir die zwei nach links bewegen?
            if(insert.getX() > 0) {
                //sind die felder links davon frei?
                if(
                        board.getBlockCovering(getX() - 1, getY()) == null &&
                        board.getBlockCovering(insert.getX() - 1, insert.getY()) == null &&
                        board.getBlockCovering(getX() - 1, getY() + 2) == null) {
                    MultiMove mm = new MultiMove(this,-1,0);
                    mm.add(insert,-1,0);
                    alts.add(mm);
                }
            }
            //vielleicht nach oben?
            if(getY() > 0) {
                //sind die felder oben davon frei?
                if(
                        board.getBlockCovering(getX(), getY() - 1) == null &&
                        board.getBlockCovering(getX() + 1, getY() - 1) == null &&
                        board.getBlockCovering(insert.getX(), insert.getY() - 1) == null) {
                    MultiMove mm = new MultiMove(this,0,-1);
                    mm.add(insert,0,-1);
                    alts.add(mm);
                }
            }
            //vielleicht nach unten?
            if(getY()+3 < board.height) {
                //sind die felder darunter frei?
                if(
                        board.getBlockCovering(getX(), getY() + 3) == null &&
                        board.getBlockCovering(getX() + 1, getY() + 3) == null &&
                        board.getBlockCovering(insert.getX(), insert.getY() + 1) == null) {
                    MultiMove mm = new MultiMove(this,0,+1);
                    mm.add(insert,0,+1);
                    alts.add(mm);
                }
            }
        }
    }
}
