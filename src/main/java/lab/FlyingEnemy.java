package lab;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lietajúci nepriateľ, ktorý sa pohybuje medzi dvoma bodmi.
 * Pri dosiahnutí limitov min/max sa smer invertuje.
 */


public class FlyingEnemy extends WorldEntity {

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    private final double speed;

    private int dirX; // -1, 0, 1
    private int dirY; // -1, 0, 1

    public FlyingEnemy(double x1, double y1, double x2, double y2, double speed) {
        super(x1, y1, 34, 34);

        // min/max určujú oblasť pohybu (hranice medzi dvomi bodmi).
        this.minX = Math.min(x1, x2);
        this.maxX = Math.max(x1, x2);
        this.minY = Math.min(y1, y2);
        this.maxY = Math.max(y1, y2);

        this.speed = speed;

        dirX = Double.compare(x2, x1);
        dirY = Double.compare(y2, y1);

    }

    @Override
    public void simulate(double dt) {
        x += dirX * speed * dt;
        y += dirY * speed * dt;

        if (x <= minX) {
            x = minX;
            dirX = 1;
        }
        if (x >= maxX) {
            x = maxX;
            dirX = -1;
        }
        if (y <= minY) {
            y = minY;
            dirY = 1;
        }
        if (y >= maxY) {
            y = maxY;
            dirY = -1;
        }
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
        int row = 1;

        double sx = col * (double) tile;
        double sy = row * (double) tile;

        gc.drawImage(
                ResourceManager.getImage(ResourceManager.class, "/Enemy.png"),
                sx, sy, tile, tile,
                x, y, width, height
        );
    }
}
