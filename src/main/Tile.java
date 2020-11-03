package main;

public class Tile {
    public int x, y;
    public double drawX, drawY;
    public Direction dir;

    public Tile(int x, int y, Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        drawX = x * Const.TILE_SIZE;
        drawY = y * Const.TILE_SIZE;
    }
}
