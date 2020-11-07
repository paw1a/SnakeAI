import ai.Evolution;
import ai.NeuralNetwork;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.Screen;
import game.util.Game;
import snake.Snake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class GameScreen implements Screen {

    private Evolution evolution;
    private boolean showEvolution = true;
    private Snake loadedSnake;

    public GameScreen() {
        evolution = new Evolution();
    }
    public GameScreen(int run, int loadGeneration) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            loadedSnake = new Snake(mapper.readValue(new File(
                    "saves/run"+run+"/snake"+loadGeneration+".json"), NeuralNetwork.class));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void create() {

    }

    @Override
    public void update() {
        if(loadedSnake != null) {
            if(loadedSnake.isDead) {
                loadedSnake.spawnSnake();
                loadedSnake.isDead = false;
            }
            loadedSnake.update();
        } else {
            evolution.update();
            if (Game.input.isKeyDown(KeyEvent.VK_UP)) showEvolution = false;
            if (Game.input.isKeyDown(KeyEvent.VK_DOWN)) showEvolution = true;

            if (showEvolution) {
                Game.conf.setFPS(30);
                Game.conf.setDrawScreen(true);
            } else {
                Game.conf.setFPS(50000);
                Game.conf.setDrawScreen(false);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if(loadedSnake != null) loadedSnake.draw(g);
        else if(showEvolution) evolution.draw(g);
    }


}
