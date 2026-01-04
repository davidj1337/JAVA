package lab;

import javafx.scene.canvas.GraphicsContext;

/**
 * Hráč – entita s jednoduchou fyzikou (horizontálny pohyb + gravitácia) a skokom s variabilnou výškou.
 * Podporuje aj prepad cez jednosmerné platformy
 */

public class Player extends WorldEntity {

    private static final double MOVE_SPEED = 260.0;

    private static final double GRAVITY = 1800.0;
    private static final double ASCEND_SPEED = 650.0;
    private static final double MAX_JUMP_HEIGHT = 220.0;

    private double dropTimer = 0.0;
    private static final double DROP_TIME = 0.20;

    private int frame = 0;
    private double animTime = 0.0;

    private boolean left;
    private boolean right;

    private boolean upHeld = false;

    private double vy = 0.0;
    private boolean onGround = false;

    private double jumpStartY = 0.0;
    private boolean rising = false;

    public Player(double x, double y) {
        super(x, y, 64, 64);
    }

    public void setLeft(boolean value) {
        left = value;
    }

    public void setRight(boolean value) {
        right = value;
    }

    // „UpHeld“ znamená, že hráč drží kláves skoku. Keď ho pustí počas stúpania, skok sa skráti (variabilná výška).
    public void setUpHeld(boolean value) {
        upHeld = value;

        if (!value && rising) {
            rising = false;
            vy = 0.0;
        }
    }

    @Override
    public void simulate(double dt) {
        // dt = čas medzi frame-ami v sekundách. Fyzika je preto nezávislá od FPS.
        // Najprv riešime drop-through časovač, potom animáciu, horizontálny pohyb a nakoniec vertikálnu fyziku/skok.
        tickDrop(dt);

        animTime += dt;
        if (animTime > 0.15) {
            frame = (frame + 1) % 4;
            animTime = 0.0;
        }

        double dx = 0.0;
        // Vstup (A/D) prepočítame na smer -1/0/+1 a následne na posun v pixeloch.
        if (left) {
            dx -= 1.0;
        }
        if (right) {
            dx += 1.0;
        }
        x += dx * MOVE_SPEED * dt;

        // Skok sa spustí len keď stojíme na zemi a zároveň držíme kláves skoku.
        if (onGround && upHeld) {
            onGround = false;
            rising = true;
            jumpStartY = y;
            vy = -ASCEND_SPEED;
        }

        if (upHeld && rising) {
            // Držaním skoku pokračujeme v stúpaní, kým nedosiahneme MAX_JUMP_HEIGHT alebo kým hráč nepustí klávesu.
            double topLimitY = jumpStartY - MAX_JUMP_HEIGHT;

            if (y > topLimitY) {
                vy = -ASCEND_SPEED;
            } else {
                rising = false;
                vy = 0.0;
            }
        } else {
            vy += GRAVITY * dt;
        }

        y += vy * dt;
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.setImageSmoothing(false);

        int tile = 32;
        int col = 0;
        int row = 1;

        double sx = col * (double) tile;
        double sy = row * (double) tile;

        gc.drawImage(
                ResourceManager.getImage(ResourceManager.class, "/characters.png"),
                sx, sy, tile, tile,
                x, y, width, height
        );
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void cancelRiseNow() {
        rising = false;
        vy = 0.0;
    }

    // Krátko ignorujeme jednosmerné platformy, aby hráč mohol „prepadnúť“ nadol.
    public void requestDrop() {
        dropTimer = DROP_TIME;
        onGround = false;
        rising = false;
        vy = 1.0;
        y += 2.0;
    }

    public void tickDrop(double dt) {
        if (dropTimer > 0.0) {
            dropTimer -= dt;
            if (dropTimer < 0.0) {
                dropTimer = 0.0;
            }
        }
    }

    public boolean isDroppingThrough() {
        return dropTimer > 0.0;
    }

    public void onRoomTeleport(double dy) {
        if (rising) {
            jumpStartY += dy;
        }
    }
}
