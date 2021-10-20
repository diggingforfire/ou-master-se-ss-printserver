package printserver.data;

import printserver.security.Hasher;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Arrays;

public class DataManager {
    private Connection connection = null;
    private Hasher hasher;

    public DataManager(Hasher hasher, char[] key) {
        this.hasher = hasher;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:file:data.db?cipher=sqlcipher&key=" + new String(key) + "&legacy=4");
            createDataIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void createDataIfNotExists() throws InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists user (userid INTEGER PRIMARY KEY, username TEXT UNIQUE, password BLOB, salt BLOB, iterationcount INTEGER)");
            statement.executeUpdate("create table if not exists role (roleid INTEGER PRIMARY KEY, rolename TEXT UNIQUE)");
            statement.executeUpdate("create table if not exists user_role (userid INTEGER, roleid INTEGER, FOREIGN KEY(userid) REFERENCES user(userid), FOREIGN KEY(roleid) REFERENCES role(roleid), UNIQUE(userid, roleid))");

            addUser(1, "alice", Hasher.ITERATION_COUNT);
            addUser(2, "bart", Hasher.ITERATION_COUNT);
            addUser(3, "cecile", Hasher.ITERATION_COUNT);
            addUser(4, "dirk", Hasher.ITERATION_COUNT);
            addUser(5, "erica", Hasher.ITERATION_COUNT);

            addRole(1, "sysadmin");
            addRole(2, "concierge");
            addRole(3, "poweruser");
            addRole(4, "normaluser");

            addUserRole(1, 1);
            addUserRole(2, 2);
            addUserRole(3, 3);
            addUserRole(4, 4);
            addUserRole(5, 4);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUser(int userid, String username, int iterationCount) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO user (userid, username, password, salt, iterationcount) VALUES (?, ?, ?, ?, ?)");
        byte[] salt = hasher.createRandomSalt();
        byte[] hash = hasher.createHash(new char[]{'t', 'e', 's', 't', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'}, salt, iterationCount);
        preparedStatement.setInt(1, userid);
        preparedStatement.setString(2, username);
        preparedStatement.setBytes(3, hash);
        preparedStatement.setBytes(4, salt);
        preparedStatement.setInt(5, iterationCount);
        preparedStatement.executeUpdate();
    }

    private void addRole(int roleid, String rolename) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO role (roleid, rolename) VALUES (?, ?)");
        preparedStatement.setInt(1, roleid);
        preparedStatement.setString(2, rolename);
        preparedStatement.executeUpdate();
    }

    private void addUserRole(int userid, int roleid) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO user_role (userid, roleid) VALUES (?, ?)");
        preparedStatement.setInt(1, userid);
        preparedStatement.setInt(2, roleid);
        preparedStatement.executeUpdate();
    }

    public boolean validateCredentials(String username, char[] passwordAttempt) {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT salt, password, iterationcount FROM user WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();

            byte[] salt = rs.getBytes("salt");
            byte[] password = rs.getBytes("password");
            int iterationCount = rs.getInt("iterationcount");

            byte[] passwordAttemptHash = hasher.createHash(passwordAttempt, salt, iterationCount);

            return Arrays.equals(password, passwordAttemptHash);

        } catch(SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            // connection close failed.
            System.err.println(e.getMessage());
        }finally {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        return true;
    }
}
