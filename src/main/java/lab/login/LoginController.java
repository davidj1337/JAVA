package lab.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lab.App;

/**
 * Controller pre prihlasovaciu obrazovku.
 * Validuje vstupy a komunikuje UserRepository (H2 databáza) pre registráciu a prihlásenie.
 */

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label msgLabel;

    private App app;
    private UserRepository userRepository;

    public void setDependencies(App app, UserRepository userRepository) {
        this.app = app;
        this.userRepository = userRepository;
        this.userRepository.init();
    }

    @FXML
    public void initialize() {
        // nič
    }

    // Prihlásenie: validácia vstupov + volanie repository. Pri úspechu prepíname scénu na hru.
    @FXML
    private void onLogin() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            msgLabel.setText("Vyplň meno aj heslo.");
            return;
        }

        try {
            if (userRepository.login(u, p)) {
                msgLabel.setText("");
                app.showGame(u);
            } else {
                msgLabel.setText("Zlé meno alebo heslo.");
            }
        } catch (UserException e) {
            msgLabel.setText("Chyba DB: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Registrácia: najprv skontrolujeme existenciu mena, potom vložíme používateľa do DB.
    @FXML
    private void onRegister() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            msgLabel.setText("Vyplň meno aj heslo.");
            return;
        }

        try {
            if (userRepository.exists(u)) {
                msgLabel.setText("Meno už existuje.");
                return;
            }

            userRepository.save(new User(u, p));
            msgLabel.setText("Účet vytvorený. Teraz sa prihlás.");
        } catch (UserException e) {
            msgLabel.setText("Chyba DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}