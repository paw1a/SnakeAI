package snake;

public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public static boolean isOpposite(Direction direction1, Direction direction2) {
        return direction1 == UP && direction2 == DOWN
                || direction1 == DOWN && direction2 == UP
                || direction1 == RIGHT && direction2 == LEFT
                || direction1 == LEFT && direction2 == RIGHT;
    }
}
