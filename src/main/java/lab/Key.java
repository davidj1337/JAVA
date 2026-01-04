package lab;

import javafx.scene.canvas.GraphicsContext;

/**
 * Zberateľný kľúč – po dotyku s hráčom sa označí ako zozbieraný a prestane sa vykresľovať.
 * Po zozbieraní všetkých kľúčov sa hra vyhodnotí ako úspešne dokončená.
 */

public class Key extends WorldEntity {

    private boolean collected = false;

    public Key(double x, double y) {
        super(x, y, 32, 32);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    @Override
    public void simulate(double deltaTime) {
        // nič
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        // Ak je kľúč zozbieraný, nevykresľujeme ho (vizuálne zmizne).
        if (collected) {
            return;
        }
        gc.setImageSmoothing(false);

        int tile = 32;
        int col = 0;
        int row = 0;

        double sx = col * (double) tile;
        double sy = row * (double) tile;

        gc.drawImage(
                ResourceManager.getImage(ResourceManager.class, "/KeyIcons.png"),
                sx, sy, tile, tile,
                x, y, width, height
        );
    }
}
