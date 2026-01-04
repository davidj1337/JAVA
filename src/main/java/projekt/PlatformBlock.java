package projekt;

import javafx.scene.canvas.GraphicsContext;

/**
 * Statická platforma, po ktorej sa dá stáť.
 * Parameter oneWay reprezentuje jednosmernú platformu – kolízia sa rieši len zhora (umožňuje drop-through).
 */

public class PlatformBlock extends WorldEntity {

    private final boolean oneWay;

    public PlatformBlock(double x, double y, double w, double h, boolean oneWay) {
        super(x, y, w, h);
        this.oneWay = oneWay;
    }

    public PlatformBlock(double x, double y, double w, double h) {
        this(x, y, w, h, false);
    }

    public boolean isOneWay() {
        return oneWay;
    }

    @Override
    public void simulate(double deltaTime) {
        // nič
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.setImageSmoothing(false);

        // Platforma sa kreslí dlaždicovaním textúry, aby fungovala pre ľubovoľnú šírku/výšku.

        double px = left();
        double py = top();
        double pw = right() - left();
        double ph = bottom() - top();

        double tile = 16.0;
        double dest = 32.0;

        int col = 8;
        int row = 0;

        double sx = col * tile;
        double sy = row * tile;

        for (double yy = 0.0; yy < ph; yy += dest) {
            for (double xx = 0.0; xx < pw; xx += dest) {

                double dw = Math.min(dest, pw - xx);
                double dh = Math.min(dest, ph - yy);

                double sw = tile * (dw / dest);
                double sh = tile * (dh / dest);

                gc.drawImage(
                        ResourceManager.getImage(ResourceManager.class, "/tilemap.png"),
                        sx, sy, sw, sh,
                        px + xx, py + yy, dw, dh
                );
            }
        }
    }
}
