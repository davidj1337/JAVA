package projekt;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * Herná slučka postavená na AnimationTimer
 * V každom frame vypočíta delta time, vykoná simuláciu logiky a následne vykreslenie na Canvas
 */

public class DrawingThread extends AnimationTimer {

    private final Canvas canvas;
    private final GameScene gameScene;

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    private long lastTime = 0;

    public DrawingThread(Canvas canvas, GameScene gameScene) {
        this.canvas = canvas;
        this.gameScene = gameScene;
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        var gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gameScene.simulate(dt, canvas.getWidth(), canvas.getHeight());

        if (gameScene.isGameOverRequested()) {
            stop();
            if (app != null) {
                app.showGameOver();
            }
            return;
        }

        if (gameScene.isGameEndRequested()) {
            stop();
            if (app != null) {
                app.showGameEnd();
            }
            return;
        }

        gameScene.draw(gc);
    }
}
