package ai;

import snake.Snake;

public class Pair {
    public Snake parentA;
    public Snake parentB;

    public Pair(Snake parentA, Snake parentB) {
        this.parentA = parentA;
        this.parentB = parentB;
    }
}
