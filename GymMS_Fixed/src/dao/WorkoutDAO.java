package dao;

import model.Workout;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class WorkoutDAO {

    public boolean addWorkout(Workout w) {
        String sql = "INSERT INTO workouts (plan_name,description,category,difficulty,duration_minutes,member_id,trainer_id,assigned_date,status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,w.getPlanName()); ps.setString(2,w.getDescription());
            ps.setString(3,w.getCategory()); ps.setString(4,w.getDifficulty());
            ps.setInt(5,w.getDurationMinutes()); ps.setInt(6,w.getMemberId());
            ps.setInt(7,w.getTrainerId()); ps.setString(8,w.getAssignedDate()); ps.setString(9,"Active");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("addWorkout: "+e.getMessage()); return false; }
    }

    public ArrayList<Workout> getAllWorkouts() {
        ArrayList<Workout> list = new ArrayList<>();
        String sql = "SELECT w.*, m.name AS member_name, t.name AS trainer_name FROM workouts w LEFT JOIN members m ON w.member_id=m.id LEFT JOIN trainers t ON w.trainer_id=t.id ORDER BY w.id DESC";
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { System.err.println("getAllWorkouts: "+e.getMessage()); }
        return list;
    }

    public boolean updateWorkoutStatus(int id, String status) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE workouts SET status=? WHERE id=?")) {
            ps.setString(1,status); ps.setInt(2,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("updateWorkoutStatus: "+e.getMessage()); return false; }
    }

    public boolean deleteWorkout(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM workouts WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("deleteWorkout: "+e.getMessage()); return false; }
    }

    public int getTotalWorkouts() {
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM workouts")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("getTotalWorkouts: "+e.getMessage()); }
        return 0;
    }

    private Workout extract(ResultSet rs) throws SQLException {
        Workout w = new Workout();
        w.setId(rs.getInt("id")); w.setPlanName(rs.getString("plan_name"));
        w.setDescription(rs.getString("description")); w.setCategory(rs.getString("category"));
        w.setDifficulty(rs.getString("difficulty")); w.setDurationMinutes(rs.getInt("duration_minutes"));
        w.setMemberId(rs.getInt("member_id")); w.setTrainerId(rs.getInt("trainer_id"));
        w.setAssignedDate(rs.getString("assigned_date")); w.setStatus(rs.getString("status"));
        try { w.setMemberName(rs.getString("member_name")); } catch (SQLException ignored) {}
        try { w.setTrainerName(rs.getString("trainer_name")); } catch (SQLException ignored) {}
        return w;
    }
}
