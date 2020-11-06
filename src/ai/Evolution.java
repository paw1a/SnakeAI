package ai;

import main.Const;
import main.Snake;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Evolution {
    public List<Snake> population;
    private int currentSnake;
    private int generationNumber;
    private boolean generationDone;

    public Evolution() {
        population = new ArrayList<>();
        for (int i = 0; i < Const.PARENTS_NUMBER; i++) {
            population.add(new Snake());
        }
        currentSnake = 0;
        generationNumber = 1;
        generationDone = false;
    }

    private void createNextPopulation() {
        population.sort(Comparator.comparing(Snake::fitness));
        Map<Snake, Double> best = new LinkedHashMap<>();
        List<Snake> parents = new ArrayList<>();

        for (int i = population.size()-1; i >= population.size()-Const.PARENTS_NUMBER; i--)
            parents.add(population.get(i));

        double fitnessSum = parents.stream().mapToDouble(Snake::fitness).sum();

        parents.forEach(snake -> best.put(snake, snake.fitness() / fitnessSum));

        population = new ArrayList<>();
        best.forEach((snake, aDouble) -> population.add(new Snake(snake.model)));

        List<Snake> snakes = new ArrayList<>();
        for(Map.Entry<Snake, Double> entry : best.entrySet())
            snakes.add(entry.getKey());

        Collections.shuffle(snakes);

        for (int i = 0; i < Const.CHILDREN_NUMBER/2; i++) {
            Snake parentA = Operators.onePointRouletteWheel(snakes);
            Snake parentB = Operators.onePointRouletteWheel(snakes);
            Pair children = crossover(new Pair(parentA, parentB));
            population.add(mutate(children.parentA));
            population.add(mutate(children.parentB));
        }
        Collections.shuffle(population);
    }

    private Snake mutate(Snake snake) {
        return Operators.mutation(snake);
    }

    private Pair crossover(Pair pair) {
        double cross = Math.random();
        if(cross < 0.5)
            return Operators.sbcCrossover(pair);
        else
            return new Pair(Operators.singlePointCrossover(pair), Operators.singlePointCrossover(pair));
    }

    public void update() {
        if(generationDone) {
            for(Snake snake : population) snake.calculateFitness();
            printGenerationMetrics();
            createNextPopulation();

            generationNumber++;
            currentSnake = 0;
            generationDone = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            if(population.get(currentSnake).isDead) currentSnake++;
            else population.get(currentSnake).update();
            if(currentSnake >= population.size()) generationDone = true;
        }
    }

    public void draw(Graphics2D g) {
        if(!generationDone) population.get(currentSnake).draw(g);
    }

    private void printGenerationMetrics() {
        System.out.println("Generation #" + generationNumber + ": ");
        System.out.println("Max score = " + population.stream().max(Comparator.comparing(Snake::getScore)).get().getScore());
        double maxFit = population.stream().max(Comparator.comparing(Snake::fitness)).get().fitness();
        System.out.println("Max fitness = " + maxFit);
        System.out.println("Avg fitness = " +
                population.stream().mapToDouble(Snake::fitness).sum()
                        / (Const.PARENTS_NUMBER+ Const.CHILDREN_NUMBER));
        System.out.println();
    }
}
