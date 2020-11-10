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
import java.io.InputStream;

public class GameScreen implements Screen {

    private Evolution evolution;
    private Snake loadedSnake;
    private Font font;

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
        InputStream is = Main.class.getResourceAsStream("back-to-1982.regular.ttf");
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
        } catch (FontFormatException | IOException e) { e.printStackTrace(); }
    }

    @Override
    public void update() {
        if (Game.input.isKeyDown(KeyEvent.VK_UP)) Game.conf.setDrawScreen(false);
        if (Game.input.isKeyDown(KeyEvent.VK_DOWN)) Game.conf.setDrawScreen(true);

        if(loadedSnake != null) {
            if(loadedSnake.isDead) {
                loadedSnake = new Snake(loadedSnake.model);
                loadedSnake.isDead = false;
            }
            loadedSnake.update();
        } else evolution.update();
    }

    @Override
    public void draw(Graphics2D g) {
        g.setFont(font.deriveFont(40f));
        if(loadedSnake != null) {
            if (!loadedSnake.isDead) loadedSnake.draw(g);
            g.setColor(Color.decode("#0C0B08"));
            g.setFont(g.getFont().deriveFont(90f));
            g.drawString("SNAKE", 970, 120);
            g.setFont(g.getFont().deriveFont(40f));
            g.setColor(Color.decode("#A5B696"));
            g.drawString("Generation: BEST", 970, 300);
            g.drawString("Snake: 0", 970, 400);
            g.drawString("Best score: " + 100, 970, 500);
            g.drawString("MODE: SLOW", 970, 800);
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString("SWITCH MODE: UP-FAST, DOWN-SLOW", 970, 850);
        } else evolution.draw(g);
    }


}
