package lab;

import javafx.scene.canvas.GraphicsContext;

/**
 * Pasca – bodce, ktoré hráča okamžite „zabijú“ (respawn / game over podľa checkpointu).
 */

public class Spike extends WorldEntity {

    public Spike(double x, double y) {
        super(x, y, 32, 32);
    }

    @Override
    public void simulate(double deltaTime) {
        // nič
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        gc.drawImage(ResourceManager.getImage(ResourceManager.class, "/Spike.png"), x, y, width, height);
    }
}
