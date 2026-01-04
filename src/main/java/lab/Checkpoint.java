package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Checkpoint (respawn bod) – keď hráč doň vstúpi, uloží sa pozícia a miestnosť pre respawn.
 * V rámci jednej miestnosti je vždy aktívny maximálne jeden checkpoint.
 */

public class Checkpoint extends WorldEntity {

    private boolean active = false;

    public Checkpoint(double x, double y) {
        super(x, y, 32, 32);
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void hitBy(Collisionable another) {
        // nič
    }

    @Override
    public void simulate(double deltaTime) {
        // nič
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.setFill(active ? Color.LIMEGREEN : Color.DARKGREEN);
        gc.fillRect(x, y, width, height);
    }
}
