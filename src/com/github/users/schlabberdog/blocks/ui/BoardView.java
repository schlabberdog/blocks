package com.github.users.schlabberdog.blocks.ui;

import com.github.users.schlabberdog.blocks.board.Block;
import com.github.users.schlabberdog.blocks.board.Board;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JComponent {
    private final Board board;

    public BoardView(Board b) {
        board = b;
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200,200);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.white);
        g.fillRect(0,0,getWidth(),getHeight());

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        Stroke stroke2 = new BasicStroke(2.0f);
        Stroke stroke1 = new BasicStroke(1.0f);

        g2.setColor(Color.black);

        Board board = this.board.copy();

        //grid zeichnen
        double boxWidth  = (getWidth()-7) / board.width;
        double boxHeight = (getHeight()-7) / board.height;

        double xOffset = 3;
        double yOffset = 3;
        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                g2.setStroke(stroke2);
                g2.drawRect((int)xOffset,(int)yOffset,(int)boxWidth,(int)boxHeight);

                xOffset += boxWidth;
            }
            yOffset += boxHeight;
            xOffset = 3;
        }

        //blocks zeichnen
        for (Block blk : board.getBlocks()) {
        /*    g2.setColor(blk.getColor());
            g2.fillRect((int)(boxWidth*blk.getX()+10),(int)(boxHeight*blk.getY()+10),(int)(boxWidth*blk.width-15),(int)(boxHeight*blk.height-15));
            g2.setColor(Color.black);
            g2.drawRect((int)(boxWidth*blk.getX()+10),(int)(boxHeight*blk.getY()+10),(int)(boxWidth*blk.width-15),(int)(boxHeight*blk.height-15));
            */
            g2.setColor(blk.getColor());
            g2.fill(blk.drawShape(boxWidth,boxHeight));
        }

    }
}
