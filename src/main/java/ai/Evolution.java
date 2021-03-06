package ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.util.Game;
import snake.Const;
import snake.Snake;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Evolution {
    public List<Snake> population;
    private int currentSnake;
    public int generationNumber;
    private boolean generationDone;
    private int bestScore;

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
    }

    private Snake mutate(Snake snake) {
        return Operators.mutation(snake);
    }

    private Pair crossover(Pair pair) {
        if(Math.random() < 0.5) return Operators.sbcCrossover(pair);
        else return new Pair(Operators.singlePointCrossover(pair), Operators.singlePointCrossover(pair));
    }

    public void update() {
        if(generationDone) {
            for (Snake snake : population) snake.calculateFitness();
            population.sort(Comparator.comparing(Snake::fitness));
            saveSnake(population.get(population.size() - 1), generationNumber);
            printGenerationMetrics();
            createNextPopulation();

            generationNumber++;
            currentSnake = 0;
            generationDone = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { e.printStackTrace(); }
        } else {
            if(population.get(currentSnake).isDead) {
                //if(population.get(currentSnake).getScore() == Const.FIELD_SIZE*Const.FIELD_SIZE-3)
                    //System.out.println("Best snake generation = " + generationNumber);
                if(population.get(currentSnake).getScore() > bestScore)
                    bestScore = population.get(currentSnake).getScore();
                currentSnake++;
            } else population.get(currentSnake).update();

            if(currentSnake >= population.size()) generationDone = true;
        }
    }

    public void draw(Graphics2D g) {
        if(!generationDone && !population.get(currentSnake).isDead && Game.conf.isDrawScreen())
            population.get(currentSnake).draw(g);

        if(!Game.conf.isDrawScreen()) {
            g.setColor(Color.decode("#616E54"));
            g.fillRect(0, 0, Game.conf.getWidth(), Game.conf.getHeight());
            g.setColor(Color.decode("#92A67F"));
            g.fillRect(40,  40, 820, 820);
            g.setColor(Color.decode("#0C0B08"));
            g.setFont(g.getFont().deriveFont(80f));
            g.drawString("PLAYING...", 200, 430);
        }

        g.setColor(Color.decode("#0C0B08"));
        g.setFont(g.getFont().deriveFont(90f));
        g.drawString("SNAKE", 970, 120);
        g.setFont(g.getFont().deriveFont(40f));
        g.setColor(Color.decode("#A5B696"));
        g.drawString("Generation: " + generationNumber, 970, 300);
        g.drawString("Snake: " + currentSnake, 970, 400);
        g.drawString("Best score: " + (bestScore+3), 970, 500);
        g.drawString("MODE: " + (!Game.conf.isDrawScreen() ? "FAST" : "SLOW"), 970, 800);
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString("SWITCH MODE: UP-FAST, DOWN-SLOW", 970, 850);
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

    public static void saveSnake(Snake snake, int generationNumber) {
        ObjectMapper mapper = new ObjectMapper();
        int size = Objects.requireNonNull(new File("saves").list()).length;
        if(generationNumber == 1) {
            File file = new File("saves/run" + size);
            file.mkdir();
            size++;
        }
        try {
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(snake.model);
            FileWriter writer = new FileWriter("saves/run" + (size-1) + "/snake" + generationNumber + ".json");
            writer.write(s);
            writer.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
