package model;

public class Schedule {
    private int id;
    private int memberId;
    private String memberName;
    private int trainerId;
    private String trainerName;
    private String sessionDate;
    private String sessionTime;
    private String sessionType;
    private int durationMinutes;
    private String notes;
    private String status;

    public Schedule() {}

    public Schedule(int memberId, int trainerId, String sessionDate, String sessionTime,
                    String sessionType, int durationMinutes, String notes) {
        this.memberId = memberId; this.trainerId = trainerId;
        this.sessionDate = sessionDate; this.sessionTime = sessionTime;
        this.sessionType = sessionType; this.durationMinutes = durationMinutes;
        this.notes = notes; this.status = "Scheduled";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int id) { this.memberId = id; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String s) { this.memberName = s; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int id) { this.trainerId = id; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String s) { this.trainerName = s; }
    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String s) { this.sessionDate = s; }
    public String getSessionTime() { return sessionTime; }
    public void setSessionTime(String s) { this.sessionTime = s; }
    public String getSessionType() { return sessionType; }
    public void setSessionType(String s) { this.sessionType = s; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int d) { this.durationMinutes = d; }
    public String getNotes() { return notes; }
    public void setNotes(String s) { this.notes = s; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
}
