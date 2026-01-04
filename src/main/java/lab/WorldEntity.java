package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Spoločný predok pre všetky objekty vo svete.
 * Uchováva pozíciu a rozmery, poskytuje bounding box pre kolízie a obaluje kreslenie
 */

public abstract class WorldEntity implements DrawableSimulable, Collisionable {

    protected double x;
    protected double y;

    protected double width;
    protected double height;

    protected double rotation;

    protected WorldEntity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double left() {
        return x;
    }

    public double right() {
        return x + width;
    }

    public double top() {
        return y;
    }

    public double bottom() {
        return y + height;
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(x, y, width, height);
    }

    @Override
    public void hitBy(Collisionable another) {
        // nič
    }

    @Override
    public final void draw(GraphicsContext gc) {
        gc.save();
        drawInternal(gc);
        gc.restore();
    }

    protected abstract void drawInternal(GraphicsContext gc);
}
