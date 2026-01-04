module lab {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires com.h2database;

    opens lab to javafx.fxml;
    opens lab.login to javafx.fxml;

    exports lab;
    exports lab.login;
}