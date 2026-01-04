package lab;

import javafx.scene.canvas.GraphicsContext;

/**
 * Pozemný nepriateľ – pohybuje sa doľava/doprava, pôsobí naň gravitácia a otáča sa pri náraze do steny/hrany platformy.
 */

public class Enemy extends WorldEntity {

    private static final double SPEED = 140.0;
    private static final double GRAVITY = 1800.0;

    private double vy = 0.0;
    private int dir = 1; // 1 = right, -1 = left

    public Enemy(double x, double y) {
        super(x, y, 36, 36);
    }

    public int getDir() {
        return dir;
    }

    public void reverse() {
        dir = -dir;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void stepX(double dt) {
        x += dir * SPEED * dt;
    }

    public void stepY(double dt) {
        vy += GRAVITY * dt;
        y += vy * dt;
    }

    @Override
    public void simulate(double dt) {
        x += dir * SPEED * dt;
        vy += GRAVITY * dt;
        y += vy * dt;
    }


    // Pri dopade na platformu nastavíme pozíciu tesne nad ňu a vynulujeme vertikálnu rýchlosť.
    public void landOn(double platformTopY) {
        y = platformTopY - height;
        vy = 0.0;
    }

    @Override
    public void hitBy(Collisionable another) {
        // nič
    }

    @Override
    public void drawInternal(GraphicsContext gc) {
        gc.setImageSmoothing(false);

        int tile = 16;
        int col = 0;
        int row = 0;

        double sx = col * (double) tile;
        double sy = row * (double) tile;

        gc.drawImage(
                ResourceManager.getImage(ResourceManager.class, "/Enemy.png"),
                sx, sy, tile, tile,
                x, y, width, height
        );
    }
}
