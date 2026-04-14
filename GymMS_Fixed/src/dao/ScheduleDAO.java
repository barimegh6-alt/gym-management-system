package dao;

import model.Schedule;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class ScheduleDAO {

    public boolean addSchedule(Schedule s) {
        String sql = "INSERT INTO schedule (member_id,trainer_id,session_date,session_time,session_type,duration_minutes,notes,status) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,s.getMemberId()); ps.setInt(2,s.getTrainerId());
            ps.setString(3,s.getSessionDate()); ps.setString(4,s.getSessionTime());
            ps.setString(5,s.getSessionType()); ps.setInt(6,s.getDurationMinutes());
            ps.setString(7,s.getNotes()); ps.setString(8,"Scheduled");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("addSchedule: "+e.getMessage()); return false; }
    }

    public ArrayList<Schedule> getAllSchedules() {
        ArrayList<Schedule> list = new ArrayList<>();
        String sql = "SELECT s.*, m.name AS member_name, t.name AS trainer_name FROM schedule s LEFT JOIN members m ON s.member_id=m.id LEFT JOIN trainers t ON s.trainer_id=t.id ORDER BY s.session_date DESC, s.session_time DESC";
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { System.err.println("getAllSchedules: "+e.getMessage()); }
        return list;
    }

    public boolean updateScheduleStatus(int id, String status) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE schedule SET status=? WHERE id=?")) {
            ps.setString(1,status); ps.setInt(2,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("updateScheduleStatus: "+e.getMessage()); return false; }
    }

    public boolean deleteSchedule(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM schedule WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("deleteSchedule: "+e.getMessage()); return false; }
    }

    public int getTotalSchedules() {
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM schedule WHERE status='Scheduled'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("getTotalSchedules: "+e.getMessage()); }
        return 0;
    }

    private Schedule extract(ResultSet rs) throws SQLException {
        Schedule s = new Schedule();
        s.setId(rs.getInt("id")); s.setMemberId(rs.getInt("member_id")); s.setTrainerId(rs.getInt("trainer_id"));
        s.setSessionDate(rs.getString("session_date")); s.setSessionTime(rs.getString("session_time"));
        s.setSessionType(rs.getString("session_type")); s.setDurationMinutes(rs.getInt("duration_minutes"));
        s.setNotes(rs.getString("notes")); s.setStatus(rs.getString("status"));
        try { s.setMemberName(rs.getString("member_name")); } catch (SQLException ignored) {}
        try { s.setTrainerName(rs.getString("trainer_name")); } catch (SQLException ignored) {}
        return s;
    }
}
