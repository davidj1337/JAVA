package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Spoločné rozhranie pre herné objekty: v každom ticku sa simulujú (simulate) a následne vykreslia (draw).
 * Vrátený bounding box slúži na jednoduché kolízie.
 */

public interface DrawableSimulable {

    void draw(GraphicsContext gc);

    void simulate(double deltaTime);

    Rectangle2D getBoundingBox();
}
