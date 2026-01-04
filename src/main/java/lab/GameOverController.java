package lab;

/**
 * Controller obrazovky „Game Over“ – obsahuje len obsluhu tlačidla na reštart.
 */

public class GameOverController {

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    public void onRestart() {
        app.restartGame();
    }
}