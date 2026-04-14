package ui;

import dao.MemberDAO;
import dao.ScheduleDAO;
import dao.TrainerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Member;
import model.Schedule;
import model.Trainer;
import util.ValidationUtil;

import java.util.ArrayList;

public class ScheduleController {

    // ── Table ──────────────────────────────────────────────────────────────
    @FXML private TableView<Schedule>            scheduleTable;
    @FXML private TableColumn<Schedule, Integer> colId;
    @FXML private TableColumn<Schedule, String>  colMember;
    @FXML private TableColumn<Schedule, String>  colTrainer;
    @FXML private TableColumn<Schedule, String>  colDate;
    @FXML private TableColumn<Schedule, String>  colTime;
    @FXML private TableColumn<Schedule, String>  colType;
    @FXML private TableColumn<Schedule, Integer> colDuration;
    @FXML private TableColumn<Schedule, String>  colNotes;
    @FXML private TableColumn<Schedule, String>  colStatus;

    // ── Form ───────────────────────────────────────────────────────────────
    @FXML private ComboBox<String>  memberCombo;
    @FXML private ComboBox<String>  trainerCombo;
    @FXML private DatePicker        sessionDatePicker;
    @FXML private ComboBox<String>  timeCombo;
    @FXML private ComboBox<String>  sessionTypeCombo;
    @FXML private TextField         durationField;
    @FXML private TextArea          notesArea;
    @FXML private Label             messageLabel;

    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final MemberDAO   memberDAO   = new MemberDAO();
    private final TrainerDAO  trainerDAO  = new TrainerDAO();
    private final ObservableList<Schedule> scheduleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupCombos();
        loadAll();
        messageLabel.setVisible(false);
    }

    // ── Setup ──────────────────────────────────────────────────────────────

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMember.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colTrainer.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("sessionTime"));
        colType.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Scheduled"  -> setStyle("-fx-text-fill:#60a5fa;-fx-font-weight:bold;");
                    case "Completed"  -> setStyle("-fx-text-fill:#4ade80;-fx-font-weight:bold;");
                    default           -> setStyle("-fx-text-fill:#f87171;-fx-font-weight:bold;");
                }
            }
        });
        scheduleTable.setItems(scheduleList);
    }

    private void setupCombos() {
        sessionTypeCombo.getItems().addAll(
            "Weight Training","Cardio","Yoga","HIIT","CrossFit",
            "Swimming","Pilates","Stretching","Boxing","General Fitness"
        );
        sessionTypeCombo.setValue("Weight Training");

        String[] times = {
            "06:00 AM","07:00 AM","08:00 AM","09:00 AM","10:00 AM","11:00 AM",
            "12:00 PM","01:00 PM","02:00 PM","03:00 PM","04:00 PM","05:00 PM",
            "06:00 PM","07:00 PM","08:00 PM","09:00 PM"
        };
        timeCombo.getItems().addAll(times);
        timeCombo.setValue("07:00 AM");

        // Load members
        try {
            ArrayList<Member> members = memberDAO.getAllMembers();
            for (Member m : members)
                memberCombo.getItems().add(m.getId() + " - " + m.getName());
            if (!memberCombo.getItems().isEmpty())
                memberCombo.setValue(memberCombo.getItems().get(0));
        } catch (Exception e) {
            System.err.println("ScheduleController - load members: " + e.getMessage());
        }

        // Load trainers
        try {
            ArrayList<Trainer> trainers = trainerDAO.getAllTrainers();
            for (Trainer t : trainers)
                trainerCombo.getItems().add(t.getId() + " - " + t.getName());
            if (!trainerCombo.getItems().isEmpty())
                trainerCombo.setValue(trainerCombo.getItems().get(0));
        } catch (Exception e) {
            System.err.println("ScheduleController - load trainers: " + e.getMessage());
        }
    }

    private void loadAll() {
        try {
            scheduleList.setAll(scheduleDAO.getAllSchedules());
        } catch (Exception e) {
            showMsg("Error loading schedules: " + e.getMessage(), true);
        }
    }

    // ── Actions ────────────────────────────────────────────────────────────

    @FXML
    public void handleSchedule() {
        try {
            if (memberCombo.getValue() == null)      { showMsg("Select a member.", true); return; }
            if (trainerCombo.getValue() == null)     { showMsg("Select a trainer.", true); return; }
            if (sessionDatePicker.getValue() == null){ showMsg("Select a session date.", true); return; }
            if (!ValidationUtil.isPositiveInteger(durationField.getText())) {
                showMsg("Enter a valid duration in minutes.", true); return;
            }

            int memberId  = Integer.parseInt(memberCombo.getValue().split(" - ")[0]);
            int trainerId = Integer.parseInt(trainerCombo.getValue().split(" - ")[0]);

            Schedule s = new Schedule(
                memberId, trainerId,
                sessionDatePicker.getValue().toString(),
                timeCombo.getValue(),
                sessionTypeCombo.getValue(),
                Integer.parseInt(durationField.getText().trim()),
                notesArea.getText().trim()
            );

            if (scheduleDAO.addSchedule(s)) {
                showMsg("✓ Session scheduled successfully!", false);
                loadAll(); clearForm();
            } else {
                showMsg("✗ Failed to schedule session.", true);
            }
        } catch (Exception e) {
            showMsg("✗ Error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleMarkComplete() {
        Schedule sel = scheduleTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a session to mark complete.", true); return; }
        if (scheduleDAO.updateScheduleStatus(sel.getId(), "Completed")) {
            showMsg("✓ Session marked Completed!", false); loadAll();
        } else {
            showMsg("✗ Could not update status.", true);
        }
    }

    @FXML
    public void handleCancel() {
        Schedule sel = scheduleTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a session to cancel.", true); return; }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel this session?", ButtonType.OK, ButtonType.CANCEL);
        dlg.setTitle("Cancel Session"); dlg.setHeaderText("Confirm Cancellation");
        dlg.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (scheduleDAO.updateScheduleStatus(sel.getId(), "Cancelled")) {
                    showMsg("✓ Session cancelled.", false); loadAll();
                } else { showMsg("✗ Failed to cancel.", true); }
            }
        });
    }

    @FXML
    public void handleDelete() {
        Schedule sel = scheduleTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a session to delete.", true); return; }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Permanently delete this record?", ButtonType.OK, ButtonType.CANCEL);
        dlg.setTitle("Delete Schedule"); dlg.setHeaderText("Confirm Delete");
        dlg.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (scheduleDAO.deleteSchedule(sel.getId())) {
                    showMsg("✓ Record deleted!", false); loadAll();
                } else { showMsg("✗ Delete failed.", true); }
            }
        });
    }

    @FXML
    public void clearForm() {
        sessionDatePicker.setValue(null);
        durationField.clear();
        notesArea.clear();
        sessionTypeCombo.setValue("Weight Training");
        timeCombo.setValue("07:00 AM");
        if (!memberCombo.getItems().isEmpty())  memberCombo.setValue(memberCombo.getItems().get(0));
        if (!trainerCombo.getItems().isEmpty()) trainerCombo.setValue(trainerCombo.getItems().get(0));
        messageLabel.setVisible(false);
    }

    private void showMsg(String msg, boolean err) {
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
        messageLabel.setStyle(err ? "-fx-text-fill:#f87171;" : "-fx-text-fill:#4ade80;");
    }
}
