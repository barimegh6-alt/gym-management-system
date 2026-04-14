package ui;

import dao.MemberDAO;
import dao.TrainerDAO;
import dao.WorkoutDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Member;
import model.Trainer;
import model.Workout;
import util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * WorkoutsController
 * BUG FIX: removed @FXML statusCombo and @FXML deleteBtn — those fields
 * had no matching fx:id in WorkoutsView.fxml, causing FXMLLoader to throw
 * a silent exception that made the page appear blank.
 */
public class WorkoutsController {

    // ── Table columns ──────────────────────────────────────────────────────
    @FXML private TableView<Workout>            workoutsTable;
    @FXML private TableColumn<Workout, Integer> colId;
    @FXML private TableColumn<Workout, String>  colPlanName;
    @FXML private TableColumn<Workout, String>  colCategory;
    @FXML private TableColumn<Workout, String>  colDifficulty;
    @FXML private TableColumn<Workout, Integer> colDuration;
    @FXML private TableColumn<Workout, String>  colMember;
    @FXML private TableColumn<Workout, String>  colTrainer;
    @FXML private TableColumn<Workout, String>  colDate;
    @FXML private TableColumn<Workout, String>  colStatus;

    // ── Form fields (each fx:id matches exactly what is in WorkoutsView.fxml)
    @FXML private TextField      planNameField;
    @FXML private TextArea       descriptionArea;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextField      durationField;
    @FXML private ComboBox<String> memberCombo;
    @FXML private ComboBox<String> trainerCombo;
    @FXML private Label          messageLabel;

    private final WorkoutDAO  workoutDAO  = new WorkoutDAO();
    private final MemberDAO   memberDAO   = new MemberDAO();
    private final TrainerDAO  trainerDAO  = new TrainerDAO();
    private final ObservableList<Workout> workoutList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupCombos();   // loads DB data into member/trainer combos
        loadAll();
        messageLabel.setVisible(false);
    }

    // ── Setup ──────────────────────────────────────────────────────────────

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlanName.setCellValueFactory(new PropertyValueFactory<>("planName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colMember.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colTrainer.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("assignedDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Colour-code Status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Active"    -> setStyle("-fx-text-fill:#4ade80;-fx-font-weight:bold;");
                    case "Completed" -> setStyle("-fx-text-fill:#60a5fa;-fx-font-weight:bold;");
                    default          -> setStyle("-fx-text-fill:#facc15;-fx-font-weight:bold;");
                }
            }
        });

        // Colour-code Difficulty
        colDifficulty.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Beginner"     -> setStyle("-fx-text-fill:#4ade80;");
                    case "Intermediate" -> setStyle("-fx-text-fill:#facc15;");
                    default             -> setStyle("-fx-text-fill:#f87171;");
                }
            }
        });

        workoutsTable.setItems(workoutList);
    }

    private void setupCombos() {
        categoryCombo.getItems().addAll("Cardio","Strength","Flexibility","Mixed","HIIT","Yoga","CrossFit");
        categoryCombo.setValue("Strength");

        difficultyCombo.getItems().addAll("Beginner","Intermediate","Advanced");
        difficultyCombo.setValue("Beginner");

        // Load members from DB
        try {
            ArrayList<Member> members = memberDAO.getAllMembers();
            for (Member m : members)
                memberCombo.getItems().add(m.getId() + " - " + m.getName());
            if (!memberCombo.getItems().isEmpty())
                memberCombo.setValue(memberCombo.getItems().get(0));
        } catch (Exception e) {
            System.err.println("WorkoutsController - load members: " + e.getMessage());
        }

        // Load trainers from DB
        try {
            ArrayList<Trainer> trainers = trainerDAO.getAllTrainers();
            for (Trainer t : trainers)
                trainerCombo.getItems().add(t.getId() + " - " + t.getName());
            if (!trainerCombo.getItems().isEmpty())
                trainerCombo.setValue(trainerCombo.getItems().get(0));
        } catch (Exception e) {
            System.err.println("WorkoutsController - load trainers: " + e.getMessage());
        }
    }

    private void loadAll() {
        try {
            workoutList.setAll(workoutDAO.getAllWorkouts());
        } catch (Exception e) {
            showMsg("Error loading workouts: " + e.getMessage(), true);
        }
    }

    // ── Actions ────────────────────────────────────────────────────────────

    @FXML
    public void handleAssign() {
        try {
            if (ValidationUtil.isEmpty(planNameField.getText())) {
                showMsg("Plan name is required.", true); return;
            }
            if (!ValidationUtil.isPositiveInteger(durationField.getText())) {
                showMsg("Enter a valid duration in minutes.", true); return;
            }
            if (memberCombo.getValue() == null) {
                showMsg("Please select a member.", true); return;
            }
            if (trainerCombo.getValue() == null) {
                showMsg("Please select a trainer.", true); return;
            }

            int memberId  = Integer.parseInt(memberCombo.getValue().split(" - ")[0]);
            int trainerId = Integer.parseInt(trainerCombo.getValue().split(" - ")[0]);

            Workout w = new Workout(
                planNameField.getText().trim(),
                descriptionArea.getText().trim(),
                categoryCombo.getValue(),
                difficultyCombo.getValue(),
                Integer.parseInt(durationField.getText().trim()),
                memberId,
                trainerId,
                LocalDate.now().toString()
            );

            if (workoutDAO.addWorkout(w)) {
                showMsg("✓ Workout plan assigned successfully!", false);
                loadAll();
                clearForm();
            } else {
                showMsg("✗ Failed to assign workout.", true);
            }
        } catch (Exception e) {
            showMsg("✗ Error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleMarkComplete() {
        Workout sel = workoutsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a workout to mark complete.", true); return; }
        if (workoutDAO.updateWorkoutStatus(sel.getId(), "Completed")) {
            showMsg("✓ Marked as Completed!", false); loadAll();
        } else {
            showMsg("✗ Could not update status.", true);
        }
    }

    @FXML
    public void handleDelete() {
        Workout sel = workoutsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a workout to delete.", true); return; }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete plan \"" + sel.getPlanName() + "\"?", ButtonType.OK, ButtonType.CANCEL);
        dlg.setTitle("Delete Workout"); dlg.setHeaderText("Confirm Delete");
        dlg.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (workoutDAO.deleteWorkout(sel.getId())) {
                    showMsg("✓ Workout deleted!", false); loadAll();
                } else {
                    showMsg("✗ Delete failed.", true);
                }
            }
        });
    }

    @FXML
    public void clearForm() {
        planNameField.clear();
        descriptionArea.clear();
        durationField.clear();
        categoryCombo.setValue("Strength");
        difficultyCombo.setValue("Beginner");
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
