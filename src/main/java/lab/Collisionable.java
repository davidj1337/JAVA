package lab;

import javafx.geometry.Rectangle2D;

/**
 * Rozhranie pre objekty, ktoré sa môžu zúčastniť kolízie.
 * Kolízia je riešená cez bounding box (Rectangle2D).
 */

public interface Collisionable {

    Rectangle2D getBoundingBox();

    default boolean intersect(Collisionable another) {
        return getBoundingBox().intersects(another.getBoundingBox());
    }

    void hitBy(Collisionable another);
}
