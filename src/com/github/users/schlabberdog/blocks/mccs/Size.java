package com.github.users.schlabberdog.blocks.mccs;

public class Size {
    public final int width;
    public final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Size) {
            Size o = (Size) obj;
            return (width == o.width && height == o.height);
        }
        return super.equals(obj);
    }
}
