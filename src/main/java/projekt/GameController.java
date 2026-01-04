package projekt;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;

/**
 * JavaFX controller pre herné okno (FXML: gameWindow.fxml).
 * Pripája canvas na veľkosť okna, spúšťa hernú slučku (AnimationTimer) a spracúva klávesové vstupy hráča.
 */

public class GameController implements KeyListener {

    @FXML private AnchorPane root;
    @FXML private Canvas canvas;
    @FXML private Label keysLabel;

    private GameScene gameScene;
    private DrawingThread timer;

    public void setApp(App app) {
        if (timer != null) {
            timer.setApp(app);
        }
    }

    @FXML
    public void initialize() {
        gameScene = new GameScene();
        timer = new DrawingThread(canvas, gameScene);

        Platform.runLater(() -> {
            canvas.widthProperty().bind(root.widthProperty());
            canvas.heightProperty().bind(root.heightProperty());

            gameScene.initRooms(canvas.getWidth(), canvas.getHeight());

            gameScene.setKeyListener(this);
            keyCollected(0, gameScene.getTotalKeys());

            timer.start();

            root.requestFocus();
            root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
            root.getScene().addEventFilter(KeyEvent.KEY_RELEASED, this::onKeyReleased);
        });
    }

    public void onKeyPressed(KeyEvent e) {
        Player p = gameScene.getPlayer();
        if (e.getCode() == KeyCode.A) {
            p.setLeft(true);
        }
        if (e.getCode() == KeyCode.D) {
            p.setRight(true);
        }
        if (e.getCode() == KeyCode.W) {
            p.setUpHeld(true);
        }
        if (e.getCode() == KeyCode.S) {
            gameScene.tryDropThrough();
        }
    }

    public void onKeyReleased(KeyEvent e) {
        Player p = gameScene.getPlayer();
        if (e.getCode() == KeyCode.A) p.setLeft(false);
        if (e.getCode() == KeyCode.D) p.setRight(false);
        if (e.getCode() == KeyCode.W) p.setUpHeld(false);
    }

    @Override
    public void keyCollected(int collectedKeys, int totalKeys) {
        keysLabel.setText("Keys: " + collectedKeys + "/" + totalKeys);
    }
}