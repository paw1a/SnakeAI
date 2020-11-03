import main.GameScreen;
import org.game.framework.Application;
import org.game.framework.util.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration conf = new Configuration();
        conf.setFPS(30);
        conf.setTitle("Snake");
        conf.setWidth(1600);
        conf.setHeight(900);
        //conf.setShowFPS(true);
        new Application(conf).setScreen(new GameScreen());
    }
}
