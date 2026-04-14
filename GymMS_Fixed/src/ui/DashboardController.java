package ui;

import dao.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import util.SessionManager;

public class DashboardController {

    @FXML private Label     welcomeLabel;
    @FXML private Label     roleLabel;
    @FXML private Label     statMembers;
    @FXML private Label     statTrainers;
    @FXML private Label     statWorkouts;
    @FXML private Label     statSchedules;
    @FXML private StackPane mainContent;   // sub-views are injected here
    @FXML private Button    btnDashboard;
    @FXML private Button    btnMembers;
    @FXML private Button    btnTrainers;
    @FXML private Button    btnWorkouts;
    @FXML private Button    btnSchedule;
    @FXML private Button    btnLogout;

    private final MemberDAO   memberDAO   = new MemberDAO();
    private final TrainerDAO  trainerDAO  = new TrainerDAO();
    private final WorkoutDAO  workoutDAO  = new WorkoutDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    @FXML public void initialize() { /* real init via initializeForUser() */ }

    /** Called by LoginController right after FXML load. */
    public void initializeForUser(User user) {
        welcomeLabel.setText("Welcome, " + user.getName() + "!");
        roleLabel.setText(user.getRole());

        // Role-based restrictions
        if ("Member".equals(user.getRole())) {
            btnMembers.setDisable(true);
            btnTrainers.setDisable(true);
        } else if ("Trainer".equals(user.getRole())) {
            btnMembers.setDisable(true);
        }

        refreshStats();
        showHome();
        setActive(btnDashboard);
    }

    // ── Sidebar actions ────────────────────────────────────────────────────

    @FXML public void showDashboardHome() { setActive(btnDashboard); refreshStats(); showHome(); }
    @FXML public void showMembers()       { setActive(btnMembers);   loadView("/ui/MembersView.fxml"); }
    @FXML public void showTrainers()      { setActive(btnTrainers);  loadView("/ui/TrainersView.fxml"); }
    @FXML public void showWorkouts()      { setActive(btnWorkouts);  loadView("/ui/WorkoutsView.fxml"); }
    @FXML public void showSchedule()      { setActive(btnSchedule);  loadView("/ui/ScheduleView.fxml"); }

    @FXML
    public void handleLogout() {
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Your session will be ended.", ButtonType.OK, ButtonType.CANCEL);
        dlg.setTitle("Logout"); dlg.setHeaderText("Confirm Logout");
        dlg.getDialogPane().getStylesheets()
           .add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        dlg.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                SessionManager.getInstance().logout();
                try {
                    Parent root  = FXMLLoader.load(getClass().getResource("/ui/LoginView.fxml"));
                    Stage  stage = (Stage) btnLogout.getScene().getWindow();
                    Scene  scene = new Scene(root, 920, 600);
                    scene.getStylesheets().add(
                            getClass().getResource("/styles/dark-theme.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.centerOnScreen();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * FIX — NAVIGATION (Bug #1 & #3)
     *
     * Previously: FXMLLoader.load() returned a node whose preferred size was 0
     * because the sub-view VBox had no explicit pref size and the StackPane did
     * not propagate its grow constraints to dynamically added children.
     *
     * Fix: after loading, call setMaxWidth/Height(MAX) on the Region so it
     * stretches to fill all available space inside the StackPane.
     * Also use mainContent.getChildren().setAll() instead of clear()+add()
     * so the layout pass fires correctly.
     *
     * FIX — SILENT ERRORS (Bug #8)
     * Any exception is now shown as a visible red label inside the content area
     * instead of being swallowed silently, so you immediately see what broke.
     */
    private void loadView(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Node view = loader.load();

            // Critical: let the loaded node fill the StackPane
            if (view instanceof Region region) {
                region.setMaxWidth(Double.MAX_VALUE);
                region.setMaxHeight(Double.MAX_VALUE);
            }

            mainContent.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
            Label err = new Label("⚠  Failed to load page\n\n" + e.getMessage());
            err.setWrapText(true);
            err.setStyle("-fx-text-fill:#f87171;-fx-font-size:13px;"
                       + "-fx-background-color:rgba(239,68,68,.10);"
                       + "-fx-padding:24;-fx-background-radius:8;");
            mainContent.getChildren().setAll(err);
        }
    }

    private void showHome() {
        VBox home = new VBox(24);
        home.setAlignment(Pos.CENTER);
        home.setMaxWidth(Double.MAX_VALUE);
        home.setMaxHeight(Double.MAX_VALUE);
        Label h = new Label("🏋️  Welcome to FitPro GMS");
        h.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:#e2e8f0;");
        Label s = new Label("Use the sidebar to manage Members · Trainers · Workouts · Schedule");
        s.setStyle("-fx-font-size:14px;-fx-text-fill:#94a3b8;");
        home.getChildren().addAll(h, s);
        mainContent.getChildren().setAll(home);
    }

    private void refreshStats() {
        try { statMembers.setText(String.valueOf(memberDAO.getTotalMembers()));     } catch (Exception e) { statMembers.setText("–"); }
        try { statTrainers.setText(String.valueOf(trainerDAO.getTotalTrainers())); } catch (Exception e) { statTrainers.setText("–"); }
        try { statWorkouts.setText(String.valueOf(workoutDAO.getTotalWorkouts())); } catch (Exception e) { statWorkouts.setText("–"); }
        try { statSchedules.setText(String.valueOf(scheduleDAO.getTotalSchedules())); } catch (Exception e) { statSchedules.setText("–"); }
    }

    private void setActive(Button active) {
        for (Button b : new Button[]{btnDashboard, btnMembers, btnTrainers, btnWorkouts, btnSchedule})
            b.getStyleClass().remove("sidebar-btn-active");
        active.getStyleClass().add("sidebar-btn-active");
    }
}
