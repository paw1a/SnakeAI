import game.Application;
import game.util.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration conf = new Configuration();
        conf.setFPS(30);
        conf.setTitle("Snake");
        conf.setWidth(1600);
        conf.setHeight(900);
        new Application(conf).setScreen(new GameScreen());
    }
}
