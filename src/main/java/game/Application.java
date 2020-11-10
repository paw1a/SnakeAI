package game;

import game.util.Files;
import game.util.Game;
import game.util.Input;
import game.util.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Application extends JPanel implements Runnable {
    public Configuration config;
    private Screen screen;

    public boolean running;

    private static BufferedImage image;
    private static Graphics2D g;

    public Application(Configuration configuration) {
        super();
        this.config = configuration;

        setPreferredSize(new Dimension(configuration.getWidth(), configuration.getHeight()));
        setFocusable(true);
        requestFocus();

        JFrame window = new JFrame(config.getTitle());
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        image = new BufferedImage(configuration.getWidth(),
                configuration.getHeight(), BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
    }

    public void addNotify() {
        super.addNotify();

        Game.input = new Input();
        Game.files = new Files();
        Game.conf = config;
        Game.application = this;

        addKeyListener(Game.input);
        addMouseListener(Game.input);
        addMouseMotionListener(Game.input);

        new Thread(this, "Main Game Loop").start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int frames = 0;
        running = true;

        while(running) {
            if(Game.conf.isDrawScreen()) {
                double targetTime = 1000000000d / config.getFPS();
                long now = System.nanoTime();
                delta += (now - lastTime) / targetTime;
                lastTime = now;
                while (delta >= 1) {
                    update();
                    delta--;
                }

                draw();
                drawToScreen();
                frames++;

                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    if (config.isShowFPS()) System.out.println("FPS: " + frames);
                    frames = 0;
                }
            } else {
                int count = 0;
                while(true) {
                    if(!Game.conf.isDrawScreen()) {
                        count++;
                        update();
                        if(count % 100 == 0) { draw(); drawToScreen(); }
                    } else break;
                }
            }
        }
    }

    public void setScreen(Screen screen) {
        screen.create();
        this.screen = screen;
    }

    public void update() {
        if(screen != null) screen.update();
        Game.input.updateInput();
    }

    public void draw() {
        if(screen != null) screen.draw(g);
    }

    public void drawToScreen() {
        Graphics g2 = getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
}
