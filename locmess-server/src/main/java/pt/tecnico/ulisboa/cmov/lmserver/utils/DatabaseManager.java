package pt.tecnico.ulisboa.cmov.lmserver.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DatabaseManager {
    private static final String dbURL = "jdbc:sqlite:LocMess.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbURL);
    }

    public static void addNewUser(Connection conn, String username, String password, String salt) throws SQLException {

        String sql = "INSERT INTO accounts(user, passwd, salt) VALUES(?,?,?)";
        if (conn != null) {
            System.out.println("Adding user " + username + " to DB.");

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, salt);
            pstmt.executeUpdate();
        }else throw new NullPointerException();
    }

    public static boolean userExists(String username) {
        String sql = "SELECT EXISTS (SELECT user FROM accounts WHERE user = ?)";
        return false;
    }
}
