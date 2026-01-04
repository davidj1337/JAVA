package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Jednoduchý projektil vystrelený zo ShooterBlock
 * Má konštantnú rýchlosť a obmedzenú životnosť, po kolízii alebo po vypršaní sa odstráni z miestnosti.
 */

public class Projectile extends WorldEntity {

    private double vx;
    private double vy;

    private double life = 4.0; // life je čas do zániku projektilu (sekundy)

    public Projectile(double x, double y, double vx, double vy) {
        super(x, y, 12, 12);
        this.vx = vx;
        this.vy = vy;
    }

    public boolean isAlive() {
        return life > 0.0;
    }

    @Override
    public void simulate(double dt) {
        x += vx * dt;
        y += vy * dt;
        life -= dt;
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillOval(x, y, width, height);
    }
}
