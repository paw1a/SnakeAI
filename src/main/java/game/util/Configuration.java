package game.util;

public class Configuration {
    private int width, height;
    private int FPS;
    private String title;
    private boolean showFPS;
    private boolean drawScreen;

    public Configuration() {
        width = 320;
        height = 240;
        FPS = 60;
        title = "Screen";
        showFPS = false;
        drawScreen = true;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowFPS() {
        return showFPS;
    }

    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    public boolean isDrawScreen() {
        return drawScreen;
    }

    public void setDrawScreen(boolean drawScreen) {
        this.drawScreen = drawScreen;
    }
}
