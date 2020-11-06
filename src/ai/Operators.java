package ai;

import main.Const;
import main.Snake;

import java.util.*;

public class Operators {

    public static Snake mutation(Snake snake) {
        Random rand = new Random();
        for (int i = 0; i < snake.model.weights1.length; i++)
            for (int j = 0; j < snake.model.weights1[i].length; j++) {
                if (Math.random() < Const.MUTATION_RATE)
                    snake.model.weights1[i][j] += rand.nextGaussian() * 0.4;
                snake.model.weights1[i][j] = Math.max(-1.0, snake.model.weights1[i][j]);
                snake.model.weights1[i][j] = Math.min(1.0, snake.model.weights1[i][j]);
            }

        for (int i = 0; i < snake.model.weights2.length; i++)
            for (int j = 0; j < snake.model.weights2[i].length; j++) {
                if (Math.random() < Const.MUTATION_RATE)
                    snake.model.weights2[i][j] += rand.nextGaussian() * 0.4;
                snake.model.weights2[i][j] = Math.max(-1.0, snake.model.weights2[i][j]);
                snake.model.weights2[i][j] = Math.min(1.0, snake.model.weights2[i][j]);
            }

        for (int i = 0; i < snake.model.weights3.length; i++)
            for (int j = 0; j < snake.model.weights3[i].length; j++) {
                if (Math.random() < Const.MUTATION_RATE)
                    snake.model.weights3[i][j] += rand.nextGaussian() * 0.4;
                snake.model.weights3[i][j] = Math.max(-1.0, snake.model.weights3[i][j]);
                snake.model.weights3[i][j] = Math.min(1.0, snake.model.weights3[i][j]);
            }

        for (int i = 0; i < snake.model.biases1.length; i++) {
            if (Math.random() < Const.MUTATION_RATE) snake.model.biases1[i] += rand.nextGaussian() * 0.4;
            snake.model.biases1[i] = Math.max(-1.0, snake.model.biases1[i]);
            snake.model.biases1[i] = Math.min(1.0, snake.model.biases1[i]);
        }

        for (int i = 0; i < snake.model.biases2.length; i++) {
            if (Math.random() < Const.MUTATION_RATE) snake.model.biases2[i] += rand.nextGaussian() * 0.4;
            snake.model.biases2[i] = Math.max(-1.0, snake.model.biases2[i]);
            snake.model.biases2[i] = Math.min(1.0, snake.model.biases2[i]);
        }

        for (int i = 0; i < snake.model.biases3.length; i++) {
            if (Math.random() < Const.MUTATION_RATE) snake.model.biases3[i] += rand.nextGaussian() * 0.4;
            snake.model.biases3[i] = Math.max(-1.0, snake.model.biases3[i]);
            snake.model.biases3[i] = Math.min(1.0, snake.model.biases3[i]);
        }

        return snake;
    }

    public static Pair sbcCrossover(Pair pair) {
        Snake childA = new Snake();
        Snake childB = new Snake();

        for (int i = 0; i < childA.model.weights1.length; i++)
            for (int j = 0; j < childA.model.weights1[i].length; j++) {
                double u = Math.random();
                double beta = u <= 0.5 ? Math.pow(2.0*u, 1.0/(Const.ETA+1.0))
                        : Math.pow(1.0/(2.0*(1.0-u)), 1.0/(Const.ETA+1.0));
                childA.model.weights1[i][j] = 0.5 * ((1.0 + beta) * pair.parentA.model.weights1[i][j]
                        + (1 - beta) * pair.parentB.model.weights1[i][j]);
                childB.model.weights1[i][j] = 0.5*((1.0 + beta)*pair.parentB.model.weights1[i][j]
                        +(1-beta)*pair.parentA.model.weights1[i][j]);
            }

        for (int i = 0; i < childA.model.weights2.length; i++)
            for (int j = 0; j < childA.model.weights2[i].length; j++) {
                double u = Math.random();
                double beta = u <= 0.5 ? Math.pow(2.0*u, 1.0/(Const.ETA+1.0))
                        : Math.pow(1.0/(2.0*(1.0-u)), 1.0/(Const.ETA+1.0));
                childA.model.weights2[i][j] = 0.5 * ((1 + beta) * pair.parentA.model.weights2[i][j]
                        + (1 - beta) * pair.parentB.model.weights2[i][j]);
                childB.model.weights2[i][j] = 0.5*((1+beta)*pair.parentB.model.weights2[i][j]
                        +(1-beta)*pair.parentA.model.weights2[i][j]);
            }

        for (int i = 0; i < childA.model.weights3.length; i++)
            for (int j = 0; j < childA.model.weights3[i].length; j++) {
                double u = Math.random();
                double beta = u <= 0.5 ? Math.pow(2.0*u, 1.0/(Const.ETA+1.0))
                        : Math.pow(1.0/(2.0*(1.0-u)), 1.0/(Const.ETA+1.0));
                childA.model.weights3[i][j] = 0.5 * ((1 + beta) * pair.parentA.model.weights3[i][j]
                        + (1 - beta) * pair.parentB.model.weights3[i][j]);
                childB.model.weights3[i][j] = 0.5*((1+beta)*pair.parentB.model.weights3[i][j]
                        +(1-beta)*pair.parentA.model.weights3[i][j]);
            }

        for (int i = 0; i < childA.model.biases1.length; i++) {
            double u = Math.random();
            double beta = u <= 0.5 ? Math.pow(2.0*u, 1.0/(Const.ETA+1.0))
                    : Math.pow(1.0/(2.0*(1.0-u)), 1.0/(Const.ETA+1.0));
            childA.model.biases1[i] = 0.5 * ((1 + beta) * pair.parentA.model.biases1[i]
                    + (1 - beta) * pair.parentB.model.biases1[i]);
            childB.model.biases1[i] = 0.5*((1+beta)*pair.parentB.model.biases1[i]
                    +(1-beta)*pair.parentA.model.biases1[i]);
        }

        for (int i = 0; i < childA.model.biases2.length; i++) {
            double u = Math.random();
            double beta = u <= 0.5 ? Math.pow(2.0*u, 1.0/(Const.ETA+1.0))
                    : Math.pow(1.0/(2.0*(1.0-u)), 1.0/(Const.ETA+1.0));
            childA.model.biases2[i] = 0.5 * ((1 + beta) * pair.parentA.model.biases2[i]
                    + (1 - beta) * pair.parentB.model.biases2[i]);
            childB.model.biases2[i] = 0.5*((1+beta)*pair.parentB.model.biases2[i]
                    +(1-beta)*pair.parentA.model.biases2[i]);
        }

        for (int i = 0; i < childA.model.biases3.length; i++) {
            double u = Math.random();
            double beta = u <= 0.5 ? Math.pow(2.0*u, 1.0/(Const.ETA+1.0))
                    : Math.pow(1.0/(2.0*(1.0-u)), 1.0/(Const.ETA+1.0));
            childA.model.biases3[i] = 0.5 * ((1 + beta) * pair.parentA.model.biases3[i]
                    + (1 - beta) * pair.parentB.model.biases3[i]);
            childB.model.biases3[i] = 0.5*((1+beta)*pair.parentB.model.biases3[i]
                    +(1-beta)*pair.parentA.model.biases3[i]);
        }

        return new Pair(childA, childB);
    }

    public static Snake singlePointCrossover(Pair pair) {
        Snake child = new Snake();

        child.model.weights1 = Arrays.stream(pair.parentA.model.weights1).map(double[]::clone).toArray(double[][]::new);
        child.model.weights2 = Arrays.stream(pair.parentA.model.weights2).map(double[]::clone).toArray(double[][]::new);
        child.model.weights3 = Arrays.stream(pair.parentA.model.weights3).map(double[]::clone).toArray(double[][]::new);
        child.model.biases1 = Arrays.copyOf(pair.parentA.model.biases1, pair.parentA.model.biases1.length);
        child.model.biases2 = Arrays.copyOf(pair.parentA.model.biases2, pair.parentA.model.biases2.length);
        child.model.biases3 = Arrays.copyOf(pair.parentA.model.biases3, pair.parentA.model.biases3.length);

        int row = (int)(Math.random()*child.model.weights1.length);
        int col = (int)(Math.random()*child.model.weights1[0].length);
        for (int i = 0; i < child.model.weights1.length; i++)
            for (int j = 0; j < child.model.weights1[i].length; j++)
                if((i < row) || (i == row && j <= col))
                    child.model.weights1[i][j] = pair.parentB.model.weights1[i][j];

        row = (int)(Math.random()*child.model.weights2.length);
        col = (int)(Math.random()*child.model.weights2[0].length);
        for (int i = 0; i < child.model.weights2.length; i++)
            for (int j = 0; j < child.model.weights2[i].length; j++)
                if((i < row) || (i == row && j <= col))
                    child.model.weights2[i][j] = pair.parentB.model.weights2[i][j];

        row = (int)(Math.random()*child.model.weights3.length);
        col = (int)(Math.random()*child.model.weights3[0].length);
        for (int i = 0; i < child.model.weights3.length; i++)
            for (int j = 0; j < child.model.weights3[i].length; j++)
                if((i < row) || (i == row && j <= col))
                    child.model.weights3[i][j] = pair.parentB.model.weights3[i][j];

        int point = (int)(Math.random()*child.model.biases1.length);
        System.arraycopy(pair.parentB.model.biases1, point, child.model.biases1, point, child.model.biases1.length - point);

        point = (int)(Math.random()*child.model.biases2.length);
        System.arraycopy(pair.parentB.model.biases2, point, child.model.biases2, point, child.model.biases2.length - point);

        point = (int)(Math.random()*child.model.biases3.length);
        System.arraycopy(pair.parentB.model.biases3, point, child.model.biases3, point, child.model.biases3.length - point);

        return child;
    }

    public static Snake onePointRouletteWheel(List<Snake> snakes) {
        /*Snake snake = null;

        double random = Math.random();
        double chanceSum = 0;

        for(Map.Entry<main.Snake, Double> entry : map.entrySet()) {
            chanceSum += entry.getValue();
            if(random < chanceSum) {
                snake = entry.getKey();
                break;
            }
        }
        return snake;*/

        double sum = snakes.stream().mapToDouble(Snake::fitness).sum();
        double rand = Math.random()*sum;
        double current = 0;
        for(Snake snake1 : snakes) {
            current += snake1.fitness();
            if(current > rand) return snake1;
        }
        return null;
    }

}
