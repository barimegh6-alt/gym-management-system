package model;

import java.time.LocalDate;

public class Member extends User {
    private String membershipType;
    private LocalDate joinDate;
    private LocalDate expiryDate;
    private String address;
    private int age;
    private String gender;
    private String status;

    public Member() { super(); }

    public Member(int id, String name, String email, String phone, String password,
                  String membershipType, LocalDate joinDate, LocalDate expiryDate,
                  String address, int age, String gender, String status) {
        super(id, name, email, phone, password);
        this.membershipType = membershipType; this.joinDate = joinDate;
        this.expiryDate = expiryDate; this.address = address;
        this.age = age; this.gender = gender; this.status = status;
    }

    public Member(String name, String email, String phone, String password,
                  String membershipType, LocalDate joinDate, LocalDate expiryDate,
                  String address, int age, String gender) {
        super(0, name, email, phone, password);
        this.membershipType = membershipType; this.joinDate = joinDate;
        this.expiryDate = expiryDate; this.address = address;
        this.age = age; this.gender = gender; this.status = "Active";
    }

    @Override public String getRole() { return "Member"; }
    @Override public String getDisplayInfo() {
        return "Member: " + getName() + " | Plan: " + membershipType + " | Status: " + status;
    }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String s) { this.membershipType = s; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate d) { this.joinDate = d; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate d) { this.expiryDate = d; }
    public String getAddress() { return address; }
    public void setAddress(String s) { this.address = s; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String s) { this.gender = s; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
