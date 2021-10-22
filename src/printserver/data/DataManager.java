package printserver.data;

import printserver.security.Hasher;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    private static Connection connection = null;

    public static void init(char[] key) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:file:data.db?cipher=sqlcipher&key=" + new String(key) + "&legacy=4");
    }

    public static void createDataIfNotExists() throws InvalidKeySpecException, NoSuchAlgorithmException, SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS user (userid INTEGER PRIMARY KEY, username TEXT UNIQUE, password BLOB, salt BLOB, iterationcount INTEGER)");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS role (roleid INTEGER PRIMARY KEY, rolename TEXT UNIQUE)");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS user_role (userid INTEGER, roleid INTEGER, FOREIGN KEY(userid) REFERENCES user(userid), FOREIGN KEY(roleid) REFERENCES role(roleid), UNIQUE(userid, roleid))");

        addUser(1, "alice", Hasher.DEFAULT_ITERATION_COUNT);
        addUser(2, "bart", Hasher.DEFAULT_ITERATION_COUNT);
        addUser(3, "cecile", Hasher.DEFAULT_ITERATION_COUNT);
        addUser(4, "dirk", Hasher.DEFAULT_ITERATION_COUNT);
        addUser(5, "erica", Hasher.DEFAULT_ITERATION_COUNT);

        addRole(1, "sysadmin");
        addRole(2, "concierge");
        addRole(3, "poweruser");
        addRole(4, "normaluser");

        addUserRole(1, 1);
        addUserRole(2, 2);
        addUserRole(3, 3);
        addUserRole(4, 4);
        addUserRole(5, 4);
    }

    public static boolean validateUsername(String username) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM user WHERE username = ?");
        preparedStatement.setString(1, username);

        ResultSet rs = preparedStatement.executeQuery();
        rs.next();

        int count = rs.getInt(1);
        return count == 1;
    }

    public static boolean validatePassword(String username, char[] passwordAttempt) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT salt, password, iterationcount FROM user WHERE username = ?");

        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();

        byte[] salt = rs.getBytes("salt");
        byte[] password = rs.getBytes("password");
        int iterationCount = rs.getInt("iterationcount");

        byte[] passwordAttemptHash = Hasher.createHash(passwordAttempt, salt, iterationCount);

        return Arrays.equals(password, passwordAttemptHash);
    }

    public static String[] getUserRoles(String username) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT r.rolename FROM user_role ur JOIN user u ON ur.userid = u.userid JOIN role r ON ur.roleid = r.roleid WHERE u.username = ?");

        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();

        List<String> roles = new ArrayList<>();

        while (rs.next()) {
            String role = rs.getString("rolename");
            roles.add(role);
        }

        return roles.toArray(new String[0]);
    }

    private static void addUser(int userid, String username, int iterationCount) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR IGNORE INTO user (userid, username, password, salt, iterationcount) VALUES (?, ?, ?, ?, ?)");

        byte[] salt = Hasher.createRandomSalt();
        byte[] hash = Hasher.createHash(new char[]{'t', 'e', 's', 't', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'}, salt, iterationCount);

        preparedStatement.setInt(1, userid);
        preparedStatement.setString(2, username);
        preparedStatement.setBytes(3, hash);
        preparedStatement.setBytes(4, salt);
        preparedStatement.setInt(5, iterationCount);

        preparedStatement.executeUpdate();
    }

    private static void addRole(int roleid, String rolename) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR IGNORE INTO role (roleid, rolename) VALUES (?, ?)");

        preparedStatement.setInt(1, roleid);
        preparedStatement.setString(2, rolename);

        preparedStatement.executeUpdate();
    }

    private static void addUserRole(int userid, int roleid) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR IGNORE INTO user_role (userid, roleid) VALUES (?, ?)");

        preparedStatement.setInt(1, userid);
        preparedStatement.setInt(2, roleid);

        preparedStatement.executeUpdate();
    }
}