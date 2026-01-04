package projekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import projekt.login.LoginController;
import projekt.login.UserRepository;

/**
 * Hlavný vstup aplikácie JavaFX.
 * Zodpovedá za prepínanie scén (login, hra, game over, koniec) a drží informáciu o aktuálne prihlásenom používateľovi.
 */

public class App extends Application {

    private Stage stage;
    private String currentUser;

    private final UserRepository userRepository = new UserRepository();


    @Override
    public void start(Stage primaryStage){
        this.stage = primaryStage;

        showLogin();
        stage.setTitle("The Ruins of Machi Itcza");
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setResizable(false);

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        stage.show();
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setDependencies(this, userRepository);

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGame(String username) {
        try {
            currentUser = username;
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/gameWindow.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setApp(this);

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOver() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/gameOver.fxml"));
            Parent root = loader.load();

            GameOverController controller = loader.getController();
            controller.setApp(this);

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameEnd() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/gameEnd.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartGame() {
        showGame(currentUser);
    }
}