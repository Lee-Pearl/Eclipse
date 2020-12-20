package application;
import java.sql.*;
public class Database {
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
    public static void dbWrite(String ename, String message) {
        ResultSet rs = null;
        Connection con = null;
        Statement stmt = null;
        try {
            String dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
            con = DriverManager.getConnection(dbUrl, "kmj", "1234");
            stmt = con.createStatement();
            int count = 0;
            count = stmt.executeUpdate("INSERT INTO JDBCTEST VALUES ('" + ename + "', '" + message + "')");
            rs = stmt.executeQuery("SELECT * FROM jdbctest");
            while (rs.next()) {
                System.out.print(rs.getString("ename"));
                System.out.print(rs.getString("message"));
                System.out.println('\n');
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}