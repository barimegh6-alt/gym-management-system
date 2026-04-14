package model;

public class Workout {
    private int id;
    private String planName;
    private String description;
    private String category;
    private String difficulty;
    private int durationMinutes;
    private int memberId;
    private String memberName;
    private int trainerId;
    private String trainerName;
    private String assignedDate;
    private String status;

    public Workout() {}

    public Workout(String planName, String description, String category, String difficulty,
                   int durationMinutes, int memberId, int trainerId, String assignedDate) {
        this.planName = planName; this.description = description;
        this.category = category; this.difficulty = difficulty;
        this.durationMinutes = durationMinutes; this.memberId = memberId;
        this.trainerId = trainerId; this.assignedDate = assignedDate; this.status = "Active";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlanName() { return planName; }
    public void setPlanName(String s) { this.planName = s; }
    public String getDescription() { return description; }
    public void setDescription(String s) { this.description = s; }
    public String getCategory() { return category; }
    public void setCategory(String s) { this.category = s; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String s) { this.difficulty = s; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int d) { this.durationMinutes = d; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int id) { this.memberId = id; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String s) { this.memberName = s; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int id) { this.trainerId = id; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String s) { this.trainerName = s; }
    public String getAssignedDate() { return assignedDate; }
    public void setAssignedDate(String s) { this.assignedDate = s; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
