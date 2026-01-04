package projekt;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Špeciálny blok, ktorý periodicky strieľa projektily do zvoleného smeru.
 * Dedi od PlatformBlock, takže sa správa aj ako prekážka/platforma.
 */

public class ShooterBlock extends PlatformBlock {

    private final Direction direction;
    private double cooldown;
    private final double fireInterval;

    public ShooterBlock(double x, double y, double w, double h,
                       Direction direction, double fireInterval) {
        super(x, y, w, h, false);
        this.direction = direction;
        this.fireInterval = fireInterval;
        this.cooldown = fireInterval;
    }

    @Override
    public void simulate(double dt) {
        // cooldown odpočítava čas do ďalšieho výstrelu.
        cooldown -= dt;
    }

    public boolean canFire() {
        return cooldown <= 0.0;
    }

    // Vytvorenie projektilu v strede bloku a posun mimo blok podľa smeru, aby hneď nekolidoval.
    public Projectile fire() {
        cooldown = fireInterval;

        double w = right() - left();
        double h = bottom() - top();

        double cx = left() + w / 2.0;
        double cy = top() + h / 2.0;

        double speed = 420.0;
        double vx = 0.0;
        double vy = 0.0;

        double px = cx - 6.0;
        double py = cy - 6.0;

        switch (direction) {
            case UP -> {
                vy = -speed;
                py = top() - 14.0;
            }
            case DOWN -> {
                vy = speed;
                py = bottom() + 2.0;
            }
            case LEFT -> {
                vx = -speed;
                px = left() - 14.0;
            }
            case RIGHT -> {
                vx = speed;
                px = right() + 2.0;
            }
        }

        return new Projectile(px, py, vx, vy);
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        double w = right() - left();
        double h = bottom() - top();

        gc.setFill(Color.DARKRED);
        gc.fillRect(left(), top(), w, h);

        gc.setFill(Color.ORANGE);
        double cx = left() + w / 2.0;
        double cy = top() + h / 2.0;

        switch (direction) {
            case UP -> gc.fillRect(cx - 3.0, top() - 4.0, 6.0, 6.0);
            case DOWN -> gc.fillRect(cx - 3.0, bottom() - 2.0, 6.0, 6.0);
            case LEFT -> gc.fillRect(left() - 4.0, cy - 3.0, 6.0, 6.0);
            case RIGHT -> gc.fillRect(right() - 2.0, cy - 3.0, 6.0, 6.0);
        }
    }
}
