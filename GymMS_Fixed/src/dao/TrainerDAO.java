package dao;

import model.Trainer;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class TrainerDAO {

    public boolean addTrainer(Trainer t) {
        String sql = "INSERT INTO trainers (name,email,phone,password,specialization,experience_years,qualification,salary,availability,status) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,t.getName()); ps.setString(2,t.getEmail()); ps.setString(3,t.getPhone());
            ps.setString(4,t.getPassword()); ps.setString(5,t.getSpecialization());
            ps.setInt(6,t.getExperienceYears()); ps.setString(7,t.getQualification());
            ps.setDouble(8,t.getSalary()); ps.setString(9,t.getAvailability()); ps.setString(10,t.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("addTrainer: "+e.getMessage()); return false; }
    }

    public ArrayList<Trainer> getAllTrainers() {
        ArrayList<Trainer> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM trainers ORDER BY id DESC")) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { System.err.println("getAllTrainers: "+e.getMessage()); }
        return list;
    }

    public ArrayList<Trainer> searchTrainers(String kw) {
        ArrayList<Trainer> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM trainers WHERE name LIKE ? OR specialization LIKE ? OR email LIKE ?")) {
            String s = "%"+kw+"%"; ps.setString(1,s); ps.setString(2,s); ps.setString(3,s);
            ResultSet rs = ps.executeQuery(); while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { System.err.println("searchTrainers: "+e.getMessage()); }
        return list;
    }

    public boolean updateTrainer(Trainer t) {
        String sql = "UPDATE trainers SET name=?,email=?,phone=?,specialization=?,experience_years=?,qualification=?,salary=?,availability=?,status=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,t.getName()); ps.setString(2,t.getEmail()); ps.setString(3,t.getPhone());
            ps.setString(4,t.getSpecialization()); ps.setInt(5,t.getExperienceYears());
            ps.setString(6,t.getQualification()); ps.setDouble(7,t.getSalary());
            ps.setString(8,t.getAvailability()); ps.setString(9,t.getStatus()); ps.setInt(10,t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("updateTrainer: "+e.getMessage()); return false; }
    }

    public boolean deleteTrainer(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM trainers WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("deleteTrainer: "+e.getMessage()); return false; }
    }

    public int getTotalTrainers() {
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM trainers")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("getTotalTrainers: "+e.getMessage()); }
        return 0;
    }

    public Trainer authenticate(String email, String password) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM trainers WHERE email=? AND password=?")) {
            ps.setString(1,email); ps.setString(2,password);
            ResultSet rs = ps.executeQuery(); if (rs.next()) return extract(rs);
        } catch (SQLException e) { System.err.println("auth trainer: "+e.getMessage()); }
        return null;
    }

    private Trainer extract(ResultSet rs) throws SQLException {
        Trainer t = new Trainer();
        t.setId(rs.getInt("id")); t.setName(rs.getString("name"));
        t.setEmail(rs.getString("email")); t.setPhone(rs.getString("phone"));
        t.setPassword(rs.getString("password")); t.setSpecialization(rs.getString("specialization"));
        t.setExperienceYears(rs.getInt("experience_years")); t.setQualification(rs.getString("qualification"));
        t.setSalary(rs.getDouble("salary")); t.setAvailability(rs.getString("availability")); t.setStatus(rs.getString("status"));
        return t;
    }
}
