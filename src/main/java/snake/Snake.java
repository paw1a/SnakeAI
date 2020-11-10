package snake;

import ai.NeuralNetwork;
import game.util.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Snake {

    private boolean isEaten;
    public boolean isDead;

    private int score;
    private int totalSteps;
    private int dieSteps;
    private double fitness;

    private List<Tile> snake;
    private Tile head;
    private Point apple;
    
    public NeuralNetwork model;

    public Snake() {
        model = new NeuralNetwork();
        spawnSnake();
    }

    public Snake(NeuralNetwork model) {
        this.model = model;
        spawnSnake();
    }

    public void update() {
        Direction bestDir = getBestDirection();
        if(Direction.isOpposite(head.dir, bestDir)) { isDead = true; return; }
        head.dir = bestDir;
        Direction prevTileDir = head.dir;

        for (int i = 0; i < snake.size(); i++) {
            Tile currTile = snake.get(i);
            switch (currTile.dir) {
                case UP: currTile.y--;break;
                case RIGHT: currTile.x++;break;
                case DOWN: currTile.y++;break;
                case LEFT: currTile.x--;break;
            }
            if (i > 0) {
                Direction temp = currTile.dir;
                currTile.dir = prevTileDir;
                prevTileDir = temp;
            }
        }

        if(head.x < 0 || head.y < 0 || head.x > Const.FIELD_SIZE-1 || head.y > Const.FIELD_SIZE-1) isDead = true;
        for(int i = 1; i < snake.size(); i++)
            if (head.x == snake.get(i).x && head.y == snake.get(i).y) {
                isDead = true;
                break;
            }

        if(apple.x == head.x && apple.y == head.y) isEaten = true;
        if(isEaten) eatApple();
        totalSteps++;
        dieSteps++;
        if(dieSteps > Const.FIELD_SIZE*Const.FIELD_SIZE*2) isDead = true;
    }

    public void calculateFitness() {
        fitness = totalSteps + (Math.pow(2, score) + Math.pow(score, 2.1)*500)
                - (Math.pow(score, 1.2)*Math.pow(0.25*totalSteps, 1.3));
    }

    private Direction getBestDirection() {
        double[] inputs = new double[32];

        for (int i = 0; i < 8; i++) {
            int dist = 1;
            double apple = Double.POSITIVE_INFINITY;
            double self = Double.POSITIVE_INFINITY;
            boolean selfFound = false;
            boolean appleFound = false;
            Point pos = new Point(head.x+Const.DELTA[i].x, head.y+Const.DELTA[i].y);
            while (pos.x >= 0 && pos.x < Const.FIELD_SIZE && pos.y >= 0 && pos.y < Const.FIELD_SIZE) {
                if(!selfFound)
                    for(Tile tile : snake)
                        if (tile.x == pos.x && tile.y == pos.y) {
                            selfFound = true;
                            self = dist;
                            break;
                        }
                if(!appleFound && this.apple.equals(pos)) {
                    appleFound = true;
                    apple = dist;
                }
                dist++;
                pos.x += Const.DELTA[i].x;
                pos.y += Const.DELTA[i].y;
            }
            inputs[i*3] = 1.0 / dist;
            inputs[i*3+1] = 1.0 / apple;
            inputs[i*3+2] = 1.0 / self;
        }

        for(int i = 0; i < 4; i++) {
            if(head.dir == Direction.values()[i]) inputs[24+i] = 1.0;
            if(snake.get(snake.size()-1).dir == Direction.values()[i]) inputs[28+i] = 1.0;
        }

        double[] out = model.predict(inputs);
        double max = out[0];
        int maxIndex = 0;
        for (int i = 1; i < 4; i++) {
            if(out[i] > max) {
                max = out[i];
                maxIndex = i;
            }
        }

        return Direction.values()[maxIndex];
    }

    public void spawnSnake() {
        snake = new ArrayList<>();
        Direction startDirection = Direction.values()[(int) (Math.random()*4)];
        int x = (int) (Math.random()*(Const.FIELD_SIZE-6)) + 3;
        int y = (int) (Math.random()*(Const.FIELD_SIZE-6)) + 3;

        snake.add(new Tile(x, y, startDirection));
        switch (startDirection) {
            case LEFT: {
                snake.add(new Tile(x+1, y, startDirection));
                snake.add(new Tile(x+2, y, startDirection));
                break;
            }
            case RIGHT: {
                snake.add(new Tile(x-1, y, startDirection));
                snake.add(new Tile(x-2, y, startDirection));
                break;
            }
            case DOWN: {
                snake.add(new Tile(x, y-1, startDirection));
                snake.add(new Tile(x, y-2, startDirection));
                break;
            }
            case UP: {
                snake.add(new Tile(x, y+1, startDirection));
                snake.add(new Tile(x, y+2, startDirection));
                break;
            }
        }
        head = snake.get(0);
        isDead = false;
        spawnApple();
    }

    private void spawnApple() {
        boolean correctPosition;
        if(snake.size() == Const.FIELD_SIZE*Const.FIELD_SIZE) {
            isDead = true;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
            return;
        }
        do {
            apple = new Point((int)(Math.random() * Const.FIELD_SIZE), (int)(Math.random() * Const.FIELD_SIZE));
            correctPosition = true;
            for (Tile tile : snake) {
                if (apple.getX() == tile.x && apple.getY() == tile.y) {
                    correctPosition = false;
                    break;
                }
            }
        } while (!correctPosition);
    }

    private void eatApple() {
        Tile tale = snake.get(snake.size()-1);

        if(tale.dir == Direction.RIGHT) snake.add(new Tile(tale.x-1, tale.y, tale.dir));
        else if(tale.dir == Direction.UP) snake.add(new Tile(tale.x, tale.y+1, tale.dir));
        else if(tale.dir == Direction.LEFT) snake.add(new Tile(tale.x+1, tale.y, tale.dir));
        else snake.add(new Tile(tale.x, tale.y-1, tale.dir));

        tale = snake.get(snake.size()-1);
        if(tale.x < 0 || tale.x > Const.FIELD_SIZE-1 || tale.y < 0 || tale.y > Const.FIELD_SIZE-1
                || head.x == tale.x && head.y == tale.y) {
            snake.remove(tale);
        } else {
            isEaten = false;
            score++;
            spawnApple();
        }
        dieSteps = 0;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.decode("#616E54"));
        g.fillRect(0, 0, Game.conf.getWidth(), Game.conf.getHeight());
        g.setColor(Color.decode("#92A67F"));
        g.fillRect(40, 40, 820, 820);

        g.setColor(Color.decode("#87382F"));
        g.drawRect(apple.x*Const.TILE_SIZE + 40, apple.y*Const.TILE_SIZE + 40, Const.TILE_SIZE, Const.TILE_SIZE);
        g.fillRect(apple.x*Const.TILE_SIZE + Const.TILE_SIZE / 10 + 40, apple.y*Const.TILE_SIZE + Const.TILE_SIZE / 10 + 40,
                Const.TILE_SIZE - 2*Const.TILE_SIZE/10, Const.TILE_SIZE - 2*Const.TILE_SIZE/10);

        g.setColor(Color.decode("#0C0B08"));
        g.setStroke(new BasicStroke(10f));
        for(Tile tile : snake) {
            g.drawRect(tile.x*Const.TILE_SIZE + 40, tile.y*Const.TILE_SIZE + 40, Const.TILE_SIZE, Const.TILE_SIZE);
            g.fillRect(tile.x*Const.TILE_SIZE + Const.TILE_SIZE / 10 + 40, tile.y*Const.TILE_SIZE + Const.TILE_SIZE / 10 + 40,
                    Const.TILE_SIZE - 2*Const.TILE_SIZE/10, Const.TILE_SIZE - 2*Const.TILE_SIZE/10);
        }
    }

    public int getScore() { return score; }
    public double fitness() {
        return fitness;
    }
}
