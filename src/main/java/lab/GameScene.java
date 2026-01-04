package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Jadrová herná logika nezávislá od UI: správa miestností, entít, kolízií a pravidiel hry.
 * Trieda obsahuje „update“ (simulate) a „render“ (draw) časť a drží stav checkpointov, kľúčov a prechodov medzi roomami.
 */

public class GameScene {

    private final Player player;

    private final List<Room> rooms = new ArrayList<>();

    private boolean gameEndRequested = false;
    private boolean gameOverRequested = false;

    private static final int COLS = 2;
    private int roomX = 0;
    private int roomY = 0;

    private double respawnX;
    private double respawnY;

    private int respawnRoomX = 0;
    private int respawnRoomY = 0;

    private boolean hasCheckpoint = false;

    private static final double SPAWN_X = 40.0;
    private static final double SPAWN_Y = 420.0;

    private boolean roomsInitialized = false;

    private int keysCollected = 0;
    private int totalKeys = 0;

    private PlatformBlock groundPlatform = null;

    private KeyListener keyListener;

    // Zjednodušené strany nárazu hráča do platformy – používame to na korektné „odtlačenie“ hráča.
    private enum HitSide { ABOVE, BELOW, LEFT, RIGHT, OTHER }

    private final Comparator<DrawableSimulable> drawComparator = (a, b) -> Integer.compare(drawOrder(a), drawOrder(b));

    public GameScene() {
        player = new Player(SPAWN_X, SPAWN_Y);

        respawnX = SPAWN_X;
        respawnY = SPAWN_Y;
        respawnRoomX = 0;
        respawnRoomY = 0;
        hasCheckpoint = false;
    }

    private Room room() {
        int idx = roomY * COLS + roomX;
        return rooms.get(idx);
    }

    private int rows() {
        return (int) Math.ceil((double) rooms.size() / COLS);
    }

    public boolean isGameOverRequested() {
        return gameOverRequested;
    }

    public boolean isGameEndRequested() {
        return gameEndRequested;
    }

    public Player getPlayer() {
        return player;
    }

    public void setKeyListener(KeyListener listener) {
        this.keyListener = listener;
        if (keyListener != null) {
            keyListener.keyCollected(keysCollected, totalKeys);
        }
    }

    public int getTotalKeys() {
        return totalKeys;
    }

    public void tryDropThrough() {
        if (groundPlatform != null && groundPlatform.isOneWay()) {
            player.requestDrop();
        }
    }


    // Level je poskladaný z viacerých „roomov“. Každý room obsahuje zoznam entít (platformy, nepriatelia, kľúče...).
    // Pozície sú v pixeloch v rámci canvasu; tento prístup je jednoduchý na obhájenie a ľahko sa ladí.
    public void initRooms(double canvasW, double canvasH) {
        if (roomsInitialized) return;
        roomsInitialized = true;

        rooms.clear();

        // ============ ROOM (0,0) - SPAWN ==========
        Room r0 = new Room();

        r0.entities.add(new PlatformBlock(0, 50, canvasW, 32));
        r0.entities.add(new PlatformBlock(0, 50, 32, canvasH));
        r0.entities.add(new PlatformBlock(1232, 50, 32, canvasH));

        r0.entities.add(new PlatformBlock(60, 350, 180, 32));
        r0.entities.add(new PlatformBlock(350, 280, 120, 32));
        r0.entities.add(new PlatformBlock(250, 520, 150, 32));
        r0.entities.add(new PlatformBlock(480, 400, 150, 32));
        r0.entities.add(new PlatformBlock(0, canvasH - 32, 680, 32));

        r0.entities.add(new Checkpoint(100, 310));
        r0.entities.add(new Key(380, 240));

        r0.entities.add(new Enemy(500, 350));
        r0.entities.add(new Spike(360, 665));

        rooms.add(r0);

        // ============ ROOM (1,0) - RIGHT ==========
        Room r1 = new Room();
        rooms.add(r1);

        // ============ ROOM (0,1) - BOTTOM ==========
        Room r2 = new Room();

        r2.entities.add(new PlatformBlock(0, 50, 680, 32));
        r2.entities.add(new PlatformBlock(0, 50, 32, canvasH));
        r2.entities.add(new PlatformBlock(0, canvasH - 32, canvasW, 32));
        r2.entities.add(new PlatformBlock(60, canvasH - 32, 180, 32));
        r2.entities.add(new PlatformBlock(100, 600, 140, 32));
        r2.entities.add(new PlatformBlock(370, 520, 140, 32));

        r2.entities.add(new PlatformBlock(150, 440, 140, 32));
        r2.entities.add(new PlatformBlock(450, 360, 140, 32));
        r2.entities.add(new PlatformBlock(200, 280, 140, 32));

        r2.entities.add(new PlatformBlock(600, 150, 140, 32));
        r2.entities.add(new PlatformBlock(650, 450, 180, 20, true));
        r2.entities.add(new PlatformBlock(canvasW - 250, 120, 200, 32));

        r2.entities.add(new Enemy(300, 568));
        r2.entities.add(new FlyingEnemy(canvasW / 2.0, 250, canvasW / 2.0, 400, 90.0));
        r2.entities.add(new Key(630, 110));
        r2.entities.add(new Spike(230, 248));
        r2.entities.add(new Checkpoint(140, 560));

        rooms.add(r2);

        // ============ ROOM (1,1) - FINAL ==========
        Room r3 = new Room();

        r3.entities.add(new PlatformBlock(0, 50, canvasW, 32));
        r3.entities.add(new PlatformBlock(0, canvasH - 32, canvasW, 32));
        r3.entities.add(new PlatformBlock(1232, 50, 32, canvasH));

        r3.entities.add(new PlatformBlock(200, 500, 880, 32));
        r3.entities.add(new PlatformBlock(80, 360, 150, 32));
        r3.entities.add(new PlatformBlock(canvasW - 230, 360, 150, 32));
        r3.entities.add(new PlatformBlock(450, 340, 380, 32));
        r3.entities.add(new PlatformBlock(540, 200, 200, 32));

        r3.entities.add(new FlyingEnemy(220, 420, canvasW - 220, 420, 140.0));

        r3.entities.add(new ShooterBlock(0, 310, 50, 32, Direction.RIGHT, 2.5));
        r3.entities.add(new ShooterBlock(canvasW - 82, 600, 50, 32, Direction.LEFT, 2.5));
        r3.entities.add(new ShooterBlock(250, 82, 50, 32, Direction.DOWN, 3.2));

        r3.entities.add(new Key(120, 320));
        r3.entities.add(new Key(canvasW - 180, 320));
        r3.entities.add(new Key(624.0, 160.0));

        rooms.add(r3);

        totalKeys = 0;
        for (Room rr : rooms) {
            for (DrawableSimulable ds : rr.entities) {
                if (ds instanceof Key) {
                    totalKeys++;
                }
            }
        }
    }


    // Hlavný update krok hry. Poradie je dôležité: najprv fyzika hráča + kolízie, potom zbery, pasce, projektily,
    // nepriatelia a nakoniec prechody medzi miestnosťami. Takto sa vyhneme „náhodným“ interakciám.
    public void simulate(double dt, double canvasW, double canvasH) {
        Room r = room();

        simulatePlayerAndPlatforms(r, dt);
        if (stopAndFlush(r)) return;

        if (collectKeys(r)) {
            r.flush();
            return;
        }

        updateCheckpoints(r);

        updateShootersAndProjectiles(r, dt, canvasW, canvasH);

        if (stopAndFlush(r)) {
            return;
        }

        updateEnemies(r, dt, canvasW);

        handleSpikes(r);
        if (stopAndFlush(r)) {
            return;
        }

        if (playerHitsAnyEnemy(r)) {
            respawnPlayer();
            r.flush();
            return;
        }

        handleRoomTransitions(canvasW, canvasH);
        r.flush();
    }

    // Simulujeme pohyb hráča a následne riešime kolízie s platformami.
    // Ukladáme si aj platformu pod nohami (groundPlatform) – využíva sa pre jednosmerné platformy.
    private void simulatePlayerAndPlatforms(Room r, double dt) {
        Rectangle2D prev = player.getBoundingBox();
        groundPlatform = null;

        player.simulate(dt);

        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof PlatformBlock plat && player.intersect(plat)) {
                HitSide side = playerHitSide(prev, player.getBoundingBox(), plat.getBoundingBox());
                resolvePlayerPlatformCollision(plat, side, prev);
            }
        }
    }



    // Stranu nárazu určujeme porovnaním bounding boxu z minulého a aktuálneho kroku.
    // Vďaka tomu vieme, či hráč narazil zhora (pristál), zdola (udrel hlavou) alebo z boku.
    private HitSide playerHitSide(Rectangle2D prevPlayer, Rectangle2D curPlayer, Rectangle2D plat) {
        if (prevPlayer.getMaxY() <= plat.getMinY() && curPlayer.getMaxY() >= plat.getMinY()) {
            return HitSide.ABOVE;
        }
        if (prevPlayer.getMinY() >= plat.getMaxY() && curPlayer.getMinY() <= plat.getMaxY()) {
            return HitSide.BELOW;
        }
        if (prevPlayer.getMaxX() <= plat.getMinX() && curPlayer.getMaxX() >= plat.getMinX()) {
            return HitSide.LEFT;
        }
        if (prevPlayer.getMinX() >= plat.getMaxX() && curPlayer.getMinX() <= plat.getMaxX()) {
            return HitSide.RIGHT;
        }
        return HitSide.OTHER;
    }


    // Jednosmerná platforma (oneWay) sa správa ako podlaha iba zhora – z boku a zdola ju ignorujeme.
    // Pri dopade zhora nastavíme y a vynulujeme rýchlosť, aby hráč „neprepadával“.
    private void resolvePlayerPlatformCollision(PlatformBlock plat, HitSide side, Rectangle2D prevPlayer) {
        switch (side) {
            case ABOVE -> {
                if (plat.isOneWay() && player.isDroppingThrough()) {
                    return;
                }
                player.setY(plat.top() - player.getHeight());
                player.setVy(0.0);
                player.setOnGround(true);
                groundPlatform = plat;
            }
            case BELOW -> {
                if (plat.isOneWay()) {
                    return;
                }
                player.setY(plat.bottom());
                player.cancelRiseNow();
            }
            case LEFT -> {
                if (plat.isOneWay()) {
                    return;
                }
                player.setX(plat.left() - player.getWidth());
            }
            case RIGHT -> {
                if (plat.isOneWay()) {
                    return;
                }
                player.setX(plat.right());
            }
            case OTHER -> {
                if (plat.isOneWay()) {
                    return;
                }

                player.setX(prevPlayer.getMinX());
                player.setY(prevPlayer.getMinY());
                player.cancelRiseNow();
            }
        }
    }

    // Kľúče sú hlavný cieľ levelu. Po zozbieraní posledného kľúča nastavíme flag gameEndRequested.
    private boolean collectKeys(Room r) {
        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof Key key && !key.isCollected() && player.intersect(key)) {
                key.collect();
                keysCollected++;

                if (keyListener != null) {
                    keyListener.keyCollected(keysCollected, totalKeys);
                }

                if (keysCollected == totalKeys) {
                    gameEndRequested = true;
                    return true;
                }
            }
        }
        return false;
    }

    // Checkpointy fungujú ako respawn body. Keď hráč aktivuje nový, deaktivujeme ostatné v roome.
    private void updateCheckpoints(Room r) {
        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof Checkpoint c) {
                if (player.intersect(c) && !c.isActive()) {
                    for (DrawableSimulable other : r.entities) {
                        if (other instanceof Checkpoint oc) {
                            oc.setActive(false);
                        }
                    }
                    c.activate();
                    activateCheckpoint(c.getX(), c.getY() - player.getHeight());
                }
            }
        }
    }


    private boolean stopAndFlush(Room r) {
        if (gameOverRequested || gameEndRequested) {
            r.flush();
            return true;
        }
        return false;
    }

    // Najprv aktualizujeme „veže“ (ShooterBlock) a prípadne pridáme nový projektil do roome.
    // Projektily iterujeme cez snapshot zoznamu, aby sme mohli bezpečne odoberať položky (bez ConcurrentModificationException).
    private void updateShootersAndProjectiles(Room r, double dt, double canvasW, double canvasH) {
        List<PlatformBlock> platforms = collectPlatforms(r);

        // shooters
        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof ShooterBlock shooter) {
                shooter.simulate(dt);
                if (shooter.canFire()) {
                    r.add(shooter.fire());
                }
            }
        }

        // projectiles
        List<DrawableSimulable> snapshot = new ArrayList<>(r.entities);
        for (DrawableSimulable ds : snapshot) {
            if (!(ds instanceof Projectile proj)) {
                continue;
            }

            proj.simulate(dt);

            boolean remove = false;

            if (!proj.isAlive()) {
                remove = true;
            } else if (player.intersect(proj)) {
                remove = true;
                respawnPlayer();
            } else if (hitsAnyPlatform(proj, platforms)) {
                remove = true;
            } else if (isOutOfBounds(proj, canvasW, canvasH)) {
                remove = true;
            }

            if (remove) {
                r.remove(proj);
                if (gameOverRequested) return;
            }
        }
    }

    private List<PlatformBlock> collectPlatforms(Room r) {
        List<PlatformBlock> platforms = new ArrayList<>();
        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof PlatformBlock plat) {
                platforms.add(plat);
            }
        }
        return platforms;
    }

    private boolean hitsAnyPlatform(WorldEntity obj, List<PlatformBlock> platforms) {
        for (PlatformBlock plat : platforms) {
            if (obj.intersect(plat)) return true;
        }
        return false;
    }

    private boolean isOutOfBounds(WorldEntity obj, double w, double h) {
        return obj.right() < 0.0 || obj.left() > w || obj.bottom() < 0.0 || obj.top() > h;
    }

    private void handleSpikes(Room r) {
        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof Spike spike && player.intersect(spike)) {
                respawnPlayer();
                return;
            }
        }
    }

    private void updateEnemies(Room r, double dt, double canvasW) {
        List<PlatformBlock> platforms = collectPlatforms(r);

        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof FlyingEnemy f) {
                f.simulate(dt);
            } else if (ds instanceof Enemy enemy) {
                updateGroundEnemy(enemy, platforms, dt, canvasW);
            }
        }
    }


    // Pozemného nepriateľa integrujeme v dvoch krokoch: najprv X (kolízie so stenami), potom Y (gravitácia a dopad).
    private void updateGroundEnemy(Enemy enemy, List<PlatformBlock> platforms, double dt, double canvasW) {
        double oldY = enemy.getY();

        // --- 1) X krok ---
        enemy.stepX(dt);

        for (PlatformBlock plat : platforms) {
            if (!plat.isOneWay() && enemy.intersect(plat)) {
                if (enemy.getDir() > 0) {
                    enemy.setX(plat.left() - enemy.getWidth());
                } else {
                    enemy.setX(plat.right());
                }
                enemy.reverse();
            }
        }


        // --- 2) Y krok ---
        enemy.stepY(dt);

        PlatformBlock landedOn = null;

        for (PlatformBlock plat : platforms) {
            if (enemy.intersect(plat)) {
                Rectangle2D b = plat.getBoundingBox();
                Rectangle2D e = enemy.getBoundingBox();

                boolean fromAbove = (oldY + enemy.getHeight()) <= b.getMinY() && e.getMaxY() >= b.getMinY();
                if (fromAbove) {
                    enemy.landOn(plat.top());
                    landedOn = plat;
                    break;
                }

                if (!plat.isOneWay()) {
                    boolean fromBelow = oldY >= b.getMaxY() && e.getMinY() <= b.getMaxY();
                    if (fromBelow) {
                        enemy.setY(plat.bottom());
                        enemy.setVy(0.0);
                    }
                }
            }
        }

        if (landedOn != null) {
            double enemyCenter = enemy.getX() + enemy.getWidth() / 2.0;
            double margin = 5.0;

            if (enemy.getDir() > 0) {
                if (enemyCenter > landedOn.right() - margin) {
                    enemy.reverse();
                }
            } else {
                if (enemyCenter < landedOn.left() + margin) {
                    enemy.reverse();
                }
            }
        }

        if (enemy.getX() < 0.0) {
            enemy.setX(0.0);
            enemy.reverse();
        }
        double maxX = canvasW - enemy.getWidth();
        if (enemy.getX() > maxX) {
            enemy.setX(maxX);
            enemy.reverse();
        }
    }

    private boolean playerHitsAnyEnemy(Room r) {
        for (DrawableSimulable ds : r.entities) {
            if (ds instanceof Enemy || ds instanceof FlyingEnemy) {
                if (ds instanceof WorldEntity we && player.intersect(we)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Prechod medzi roomami nastáva, keď hráč prejde za okraj canvasu. Hráča následne „vložíme“ na opačnú stranu.
    // Pri prechode v osi Y posúvame aj interný jumpStartY, aby sa nezlomil výpočet maximálnej výšky skoku.
    private void handleRoomTransitions(double canvasW, double canvasH) {
        // X
        double enterMargin = 64.0;

        boolean changedRoomX = false;

        if (player.getX() + player.getWidth() < 0.0) {
            if (roomX > 0) {
                roomX--;
                player.setX(canvasW - player.getWidth() - enterMargin);
                changedRoomX = true;
            } else {
                player.setX(0.0);
            }
        } else if (player.getX() > canvasW) {
            if (roomX < COLS - 1 && (roomY * COLS + (roomX + 1)) < rooms.size()) {
                roomX++;
                player.setX(enterMargin);
                changedRoomX = true;
            } else {
                player.setX(canvasW - player.getWidth());
            }
        }

        if (changedRoomX) {
            afterRoomChange(room());
        }

        // Y
        int maxRows = rows();

        if (player.getY() + player.getHeight() < 0.0) {
            if (roomY > 0) {
                double oldY = player.getY();
                roomY--;
                player.setY(canvasH - player.getHeight() - 1.0);
                player.onRoomTeleport(player.getY() - oldY);
            } else {
                player.setY(0.0);
            }
        }

        if (player.getY() > canvasH) {
            if (roomY < maxRows - 1 && ((roomY + 1) * COLS + roomX) < rooms.size()) {
                double oldY = player.getY();
                roomY++;
                player.setY(1.0);
                player.onRoomTeleport(player.getY() - oldY);
            } else {
                player.setY(canvasH - player.getHeight());
            }
        }
    }

    // Po zmene roomu môže hráč skončiť čiastočne „v stene“ (kvôli teleportu).
    // Tento blok ho pár iteráciami jemne odtlačí do najbližšieho voľného priestoru.
    private void afterRoomChange(Room r) {
        groundPlatform = null;
        player.setVy(0.0);

        for (int it = 0; it < 6; it++) {
            boolean moved = false;

            for (DrawableSimulable ds : r.entities) {
                if (ds instanceof PlatformBlock plat && player.intersect(plat)) {
                    Rectangle2D p = player.getBoundingBox();
                    Rectangle2D b = plat.getBoundingBox();

                    double pushLeft = p.getMaxX() - b.getMinX();
                    double pushRight = b.getMaxX() - p.getMinX();
                    double pushUp = p.getMaxY() - b.getMinY();
                    double pushDown = b.getMaxY() - p.getMinY();

                    double minX = Math.min(pushLeft, pushRight);
                    double minY = Math.min(pushUp, pushDown);

                    if (minX < minY) {
                        double dx;
                        if (pushLeft < pushRight) {
                            dx = -pushLeft;
                        } else {
                            dx = pushRight;
                        }
                        player.setX(player.getX() + dx);
                    } else {
                        if (pushUp < pushDown) {
                            player.setY(player.getY() - pushUp);
                            player.setVy(0.0);
                            player.setOnGround(true);
                            groundPlatform = plat;
                        } else {
                            player.setY(player.getY() + pushDown);
                            player.cancelRiseNow();
                        }
                    }

                    moved = true;
                }
            }

            if (!moved) {
                break;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        Room r = room();

        List<DrawableSimulable> sorted = new ArrayList<>(r.entities);
        sorted.sort(drawComparator);

        for (DrawableSimulable ds : sorted) {
            ds.draw(gc);
        }

        player.draw(gc);
    }

    private static int drawOrder(DrawableSimulable ds) {
        if (ds instanceof PlatformBlock) {
            return 10;
        }
        if (ds instanceof Checkpoint) {
            return 20;
        }
        if (ds instanceof Key) {
            return 30;
        }
        if (ds instanceof Spike) {
            return 40;
        }
        if (ds instanceof Projectile) {
            return 50;
        }
        if (ds instanceof FlyingEnemy) {
            return 60;
        }
        if (ds instanceof Enemy) {
            return 60;
        }
        return 100;
    }

    // Ak checkpoint neexistuje, berieme to ako prehru (game over). Inak hráča presunieme na posledný checkpoint.
    private void respawnPlayer() {
        if (!hasCheckpoint) {
            gameOverRequested = true;
            return;
        }

        roomX = respawnRoomX;
        roomY = respawnRoomY;

        player.setX(respawnX);
        player.setY(respawnY);
        player.setVy(0.0);
        player.setOnGround(false);
        player.cancelRiseNow();
    }

    public void activateCheckpoint(double x, double y) {
        hasCheckpoint = true;
        respawnX = x;
        respawnY = y;

        respawnRoomX = roomX;
        respawnRoomY = roomY;
    }
}