import ai.Evolution;
import game.Screen;
import game.util.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameScreen implements Screen {

    private Evolution evolution;
    private boolean showEvolution = true;

    @Override
    public void create() {
        evolution = new Evolution();
    }

    @Override
    public void update() {
        evolution.update();
        if(Game.input.isKeyDown(KeyEvent.VK_UP)) showEvolution = false;
        if(Game.input.isKeyDown(KeyEvent.VK_DOWN)) showEvolution = true;

        if(showEvolution) {
            Game.conf.setFPS(30);
            Game.conf.setDrawScreen(true);
        } else {
            Game.conf.setFPS(50000);
            Game.conf.setDrawScreen(false);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if(showEvolution) evolution.draw(g);
    }


}
