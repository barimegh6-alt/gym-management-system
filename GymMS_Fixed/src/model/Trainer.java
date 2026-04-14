package model;

public class Trainer extends User {
    private String specialization;
    private int experienceYears;
    private String qualification;
    private double salary;
    private String availability;
    private String status;

    public Trainer() { super(); }

    public Trainer(int id, String name, String email, String phone, String password,
                   String specialization, int experienceYears, String qualification,
                   double salary, String availability, String status) {
        super(id, name, email, phone, password);
        this.specialization = specialization; this.experienceYears = experienceYears;
        this.qualification = qualification; this.salary = salary;
        this.availability = availability; this.status = status;
    }

    public Trainer(String name, String email, String phone, String password,
                   String specialization, int experienceYears, String qualification,
                   double salary, String availability) {
        super(0, name, email, phone, password);
        this.specialization = specialization; this.experienceYears = experienceYears;
        this.qualification = qualification; this.salary = salary;
        this.availability = availability; this.status = "Active";
    }

    @Override public String getRole() { return "Trainer"; }
    @Override public String getDisplayInfo() {
        return "Trainer: " + getName() + " | " + specialization + " | " + experienceYears + " yrs";
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String s) { this.specialization = s; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int y) { this.experienceYears = y; }
    public String getQualification() { return qualification; }
    public void setQualification(String s) { this.qualification = s; }
    public double getSalary() { return salary; }
    public void setSalary(double d) { this.salary = d; }
    public String getAvailability() { return availability; }
    public void setAvailability(String s) { this.availability = s; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
