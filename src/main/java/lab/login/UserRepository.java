package lab.login;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.h2.tools.Server;

/**
 * Jednoduché úložisko používateľov nad embedded databázou H2 (súbor users-db).
 */

public class UserRepository {

    private Server server = null;
    private Connection connection;

    // Inicializácia pripojenia – databázový súbor sa vytvorí automaticky pri prvom prístupe.
    private Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:h2:file:./users-db");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    // Vytvorenie tabuľky pri štarte aplikácie (ak ešte neexistuje).
    public void init() {
        try (Statement stm = getConnection().createStatement()) {
            stm.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL
                );
                """);
        } catch (SQLException e) {
            System.out.println("Table already exists.");
            e.printStackTrace();
        }
    }

    // Používame PreparedStatement kvôli bezpečnosti (ochrana pred SQL injection) a správnemu escapovaniu.
    public void save(User user) throws UserException {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(
                "INSERT INTO Users (username, password) VALUES (?, ?)")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new UserException("SQL insert error.", e);
        }
    }

    // Načítanie všetkých používateľov
    public List<User> load() throws UserException {
        try (Statement statement = getConnection().createStatement()) {
            List<User> result = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM Users")) {
                while (resultSet.next()) {
                    result.add(new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    ));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new UserException("SQL load error.", e);
        }
    }

    public boolean exists(String username) throws UserException {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT 1 FROM Users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new UserException("SQL select error.", e);
        }
    }

    //Overenie hesla
    public boolean login(String username, String password) throws UserException {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT password FROM Users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String stored = rs.getString("password");
                return stored != null && stored.equals(password);
            }
        } catch (SQLException e) {
            throw new UserException("SQL select error.", e);
        }
    }

    public void delete(User user) throws UserException {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate("DELETE FROM Users WHERE id=" + user.getId());
        } catch (SQLException e) {
            throw new UserException("DB delete problem", e);
        }
    }

    public void startDBWebServer() {
        Path h2ServerProperties = Paths.get(System.getProperty("user.home"), ".h2.server.properties");
        try {
            Files.writeString(
                    h2ServerProperties,
                    "0=Generic H2 (Embedded)|org.h2.Driver|jdbc\\:h2\\:file\\:./users-db|",
                    StandardOpenOption.CREATE_NEW
            );
        } catch (IOException e) {
            System.out.println("File " + h2ServerProperties + " probably exists.");
        }
        stopDBWebServer();
        try {
            server = Server.createWebServer();
            System.out.println(server.getURL());
            server.start();
            System.out.println("DB Web server started!");
        } catch (SQLException e) {
            System.out.println("Cannot create DB web server.");
            e.printStackTrace();
        }
    }

    public void stopDBWebServer() {
        if (server != null) {
            System.out.println("Ending DB web server BYE.");
            server.stop();
        }
    }
}