module projekt {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires com.h2database;

    opens projekt to javafx.fxml;
    opens projekt.login to javafx.fxml;

    exports projekt;
    exports projekt.login;
}