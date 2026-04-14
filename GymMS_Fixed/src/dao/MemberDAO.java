package dao;

import model.Member;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class MemberDAO {

    public boolean addMember(Member m) {
        String sql = "INSERT INTO members (name,email,phone,password,membership_type,join_date,expiry_date,address,age,gender,status) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,m.getName()); ps.setString(2,m.getEmail()); ps.setString(3,m.getPhone());
            ps.setString(4,m.getPassword()); ps.setString(5,m.getMembershipType());
            ps.setDate(6,Date.valueOf(m.getJoinDate())); ps.setDate(7,Date.valueOf(m.getExpiryDate()));
            ps.setString(8,m.getAddress()); ps.setInt(9,m.getAge());
            ps.setString(10,m.getGender()); ps.setString(11,m.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("addMember: "+e.getMessage()); return false; }
    }

    public ArrayList<Member> getAllMembers() {
        ArrayList<Member> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM members ORDER BY id DESC")) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { System.err.println("getAllMembers: "+e.getMessage()); }
        return list;
    }

    public ArrayList<Member> searchMembers(String kw) {
        ArrayList<Member> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM members WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?")) {
            String s = "%"+kw+"%"; ps.setString(1,s); ps.setString(2,s); ps.setString(3,s);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { System.err.println("searchMembers: "+e.getMessage()); }
        return list;
    }

    public boolean updateMember(Member m) {
        String sql = "UPDATE members SET name=?,email=?,phone=?,membership_type=?,expiry_date=?,address=?,age=?,gender=?,status=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,m.getName()); ps.setString(2,m.getEmail()); ps.setString(3,m.getPhone());
            ps.setString(4,m.getMembershipType()); ps.setDate(5,Date.valueOf(m.getExpiryDate()));
            ps.setString(6,m.getAddress()); ps.setInt(7,m.getAge());
            ps.setString(8,m.getGender()); ps.setString(9,m.getStatus()); ps.setInt(10,m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("updateMember: "+e.getMessage()); return false; }
    }

    public boolean deleteMember(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM members WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("deleteMember: "+e.getMessage()); return false; }
    }

    public int getTotalMembers() {
        try (Connection c = DatabaseConnection.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM members")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("getTotalMembers: "+e.getMessage()); }
        return 0;
    }

    public Member authenticate(String email, String password) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM members WHERE email=? AND password=?")) {
            ps.setString(1,email); ps.setString(2,password);
            ResultSet rs = ps.executeQuery(); if (rs.next()) return extract(rs);
        } catch (SQLException e) { System.err.println("auth member: "+e.getMessage()); }
        return null;
    }

    private Member extract(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(rs.getInt("id")); m.setName(rs.getString("name"));
        m.setEmail(rs.getString("email")); m.setPhone(rs.getString("phone"));
        m.setPassword(rs.getString("password")); m.setMembershipType(rs.getString("membership_type"));
        Date jd = rs.getDate("join_date"); if (jd!=null) m.setJoinDate(jd.toLocalDate());
        Date ed = rs.getDate("expiry_date"); if (ed!=null) m.setExpiryDate(ed.toLocalDate());
        m.setAddress(rs.getString("address")); m.setAge(rs.getInt("age"));
        m.setGender(rs.getString("gender")); m.setStatus(rs.getString("status"));
        return m;
    }
}
