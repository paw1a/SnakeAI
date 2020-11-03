package main;

import ai.NeuralNetwork;
import org.game.framework.util.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Snake {
    
    private int moveCounter = 0;
    private boolean isEaten = false;
    public boolean isDead = false;

    private int score = 0;
    private int totalSteps = 0;
    private int dieSteps = 0;
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
        if(moveCounter == Const.DELAY) {
            Direction bestDir = getBestDirection();
            if(!Direction.isOpposite(head.dir, bestDir)) head.dir = bestDir;

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
            if(dieSteps > Const.FIELD_SIZE*Const.FIELD_SIZE) {
                //totalSteps = 0;
                isDead = true;
            }
            moveCounter = 0;
        } else {
            moveCounter++;
            for (Tile tile : snake) {
                tile.drawX += (tile.x*Const.TILE_SIZE > tile.drawX ? (double) Const.TILE_SIZE / Const.DELAY : (double) -Const.TILE_SIZE / Const.DELAY);
                tile.drawY += (tile.y*Const.TILE_SIZE > tile.drawY ? (double) Const.TILE_SIZE / Const.DELAY : (double) -Const.TILE_SIZE / Const.DELAY);
            }
        }
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
            /*g.drawRect((int)tile.drawX + 40, (int)tile.drawY + 40, main.Const.TILE_SIZE, main.Const.TILE_SIZE);
            g.fillRect((int)tile.drawX + main.Const.TILE_SIZE / 10 + 40, (int)tile.drawY + main.Const.TILE_SIZE / 10 + 40,
                    main.Const.TILE_SIZE - 2*main.Const.TILE_SIZE/10, main.Const.TILE_SIZE - 2*main.Const.TILE_SIZE/10);*/
            g.drawRect(tile.x*Const.TILE_SIZE + 40, tile.y*Const.TILE_SIZE + 40, Const.TILE_SIZE, Const.TILE_SIZE);
            g.fillRect(tile.x*Const.TILE_SIZE + Const.TILE_SIZE / 10 + 40, tile.y*Const.TILE_SIZE + Const.TILE_SIZE / 10 + 40,
                    Const.TILE_SIZE - 2*Const.TILE_SIZE/10, Const.TILE_SIZE - 2*Const.TILE_SIZE/10);
        }
    }

    public double fitness() {
        return fitness;
    }

    public void calculateFitness() {
        fitness = totalSteps + (Math.pow(2, score) + Math.pow(score, 2.1)*500)
                - (Math.pow(score, 1.2)*Math.pow(0.25*totalSteps, 1.3));
        /*if(score < 10) {
            return (totalSteps * totalSteps) * Math.pow(2,score);
        } else {
            return (totalSteps * totalSteps) * Math.pow(2,10) * (score-9);
        }*/
    }

    private Direction getBestDirection() {
        double[] inputs = new double[32];
        int x = head.x;
        int y = head.y;
        //w - dist to wall, a - dist to apple, s - dist to snake (indexes clockwise from up)
        inputs[0] = y;
        inputs[2] = Const.FIELD_SIZE - 1 - x;
        inputs[4] = Const.FIELD_SIZE - 1 - y;
        inputs[6] = x;
        inputs[1] = Math.min(inputs[0], inputs[2]) * Math.sqrt(2);
        inputs[3] = Math.min(inputs[2], inputs[4]) * Math.sqrt(2);
        inputs[5] = Math.min(inputs[4], inputs[6]) * Math.sqrt(2);
        inputs[7] = Math.min(inputs[6], inputs[0]) * Math.sqrt(2);

        inputs[8 ] = x == apple.x && y > apple.y ? y - apple.y : 0;
        inputs[9 ] = y - apple.y == apple.x - x && apple.x - x > 0 ? (apple.x - x)*Math.sqrt(2) : 0;
        inputs[10] = y == apple.y && apple.x > x ? apple.x - x : 0;
        inputs[11] = apple.x - x == apple.y - y && apple.x - x > 0 ? (apple.x - x)*Math.sqrt(2) : 0;
        inputs[12] = x == apple.x && apple.y > y ? apple.y - y : 0;
        inputs[13] = x - apple.x == apple.y - y && apple.y - y > 0 ? (apple.y - y)*Math.sqrt(2) : 0;
        inputs[14] = apple.y == y && apple.x < x ? x - apple.x : 0;
        inputs[15] = y - apple.y == x - apple.x && x - apple.x > 0 ? (x - apple.x)*Math.sqrt(2) : 0;

        for (int i = 0; i < 8; i++) {
            inputs[16+i] = Integer.MAX_VALUE;
        }
        for (int i = 1; i < snake.size(); i++) {
            if((x == snake.get(i).x && y > snake.get(i).y ? y - snake.get(i).y : Integer.MAX_VALUE) < inputs[16]) inputs[16] = y - snake.get(i).y;
            if((y - snake.get(i).y == snake.get(i).x - x &&  snake.get(i).x - x > 0 ? (snake.get(i).x - x)*Math.sqrt(2)
                    : Integer.MAX_VALUE) < inputs[17]) inputs[17] = (snake.get(i).x - x)*Math.sqrt(2);
            if((y == snake.get(i).y && snake.get(i).x > x ? snake.get(i).x - x : Integer.MAX_VALUE) < inputs[18]) inputs[18] = snake.get(i).x - x;
            if((snake.get(i).x - x == snake.get(i).y - y && snake.get(i).x - x > 0 ? (snake.get(i).x - x)*Math.sqrt(2)
                    : Integer.MAX_VALUE) < inputs[19]) inputs[19] = (snake.get(i).x - x)*Math.sqrt(2);
            if((x == snake.get(i).x && snake.get(i).y > y ? snake.get(i).y - y : Integer.MAX_VALUE) < inputs[20]) inputs[20] = snake.get(i).y - y;
            if((x - snake.get(i).x == snake.get(i).y - y && snake.get(i).y - y > 0 ? (snake.get(i).y - y)*Math.sqrt(2)
                    : Integer.MAX_VALUE) < inputs[21]) inputs[21] = (snake.get(i).y - y)*Math.sqrt(2);
            if((snake.get(i).y == y && snake.get(i).x < x ? x - snake.get(i).x : Integer.MAX_VALUE) < inputs[22]) inputs[22] = x - snake.get(i).x;
            if((y - snake.get(i).y == x - snake.get(i).x  && x - snake.get(i).x > 0 ? (x - snake.get(i).x)*Math.sqrt(2)
                    : Integer.MAX_VALUE) < inputs[23]) inputs[23] = (x - snake.get(i).x)*Math.sqrt(2);
        }
        for (int i = 0; i < 8; i++) {
            if(inputs[16+i] == Integer.MAX_VALUE) inputs[16+i] = 0;
        }
        for(int i = 0; i < 4; i++) {
            if(head.dir == Direction.values()[i]) inputs[24+i] = 1.0;
            if(snake.get(snake.size()-1).dir == Direction.values()[i]) inputs[28+i] = 1.0;
        }

        for (int i = 0; i < 24; i++) {
            inputs[i] /= (Const.FIELD_SIZE*Math.sqrt(2));
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

    private void spawnSnake() {
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

    public int getScore() { return score; }
}
