package dao;

import model.Admin;
import util.DatabaseConnection;
import java.sql.*;

public class AdminDAO {
    public Admin authenticate(String email, String password) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM admins WHERE email=? AND password=?")) {
            ps.setString(1,email); ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin a = new Admin();
                a.setId(rs.getInt("id")); a.setName(rs.getString("name"));
                a.setEmail(rs.getString("email")); a.setPhone(rs.getString("phone"));
                a.setPassword(rs.getString("password")); a.setAdminLevel(rs.getString("admin_level"));
                return a;
            }
        } catch (SQLException e) { System.err.println("auth admin: "+e.getMessage()); }
        return null;
    }
}
