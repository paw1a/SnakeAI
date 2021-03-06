package snake;

import java.awt.*;

public interface Const {

    int TILE_SIZE = 800/Const.FIELD_SIZE;
    int FIELD_SIZE = 10;

    int PARENTS_NUMBER = 500;
    int CHILDREN_NUMBER = 1000;
    double MUTATION_RATE = 0.05;
    double ETA = 100;

    Point[] DELTA = new Point[] {
        new Point(0,1),
        new Point(1,1),
        new Point(1,0),
        new Point(1,-1),
        new Point(0,-1),
        new Point(-1,-1),
        new Point(-1,0),
        new Point(-1,1)
    };

}
