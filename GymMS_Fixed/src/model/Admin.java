package model;

public class Admin extends User {
    private String adminLevel;

    public Admin() { super(); }

    public Admin(int id, String name, String email, String phone, String password, String adminLevel) {
        super(id, name, email, phone, password);
        this.adminLevel = adminLevel;
    }

    @Override public String getRole() { return "Admin"; }
    @Override public String getDisplayInfo() { return "Admin: " + getName() + " | Level: " + adminLevel; }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String s) { this.adminLevel = s; }
}
