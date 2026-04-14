package ui;

import dao.AdminDAO;
import dao.MemberDAO;
import dao.TrainerDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import util.SessionManager;
import util.ValidationUtil;

public class LoginController {

    @FXML private TextField      emailField;
    @FXML private PasswordField  passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label          errorLabel;
    @FXML private Button         loginButton;

    private final AdminDAO   adminDAO   = new AdminDAO();
    private final MemberDAO  memberDAO  = new MemberDAO();
    private final TrainerDAO trainerDAO = new TrainerDAO();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Admin", "Trainer", "Member");
        roleComboBox.setValue("Admin");
        errorLabel.setVisible(false);
        passwordField.setOnAction(e -> handleLogin());
        emailField.setOnAction(e -> passwordField.requestFocus());
    }

    @FXML
    public void handleLogin() {
        errorLabel.setVisible(false);
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = roleComboBox.getValue();

        try {
            if (ValidationUtil.isEmpty(email))       { showError("Email is required.");              return; }
            if (!ValidationUtil.isValidEmail(email)) { showError("Enter a valid email address.");    return; }
            if (ValidationUtil.isEmpty(password))    { showError("Password is required.");           return; }

            User user = null;
            switch (role) {
                case "Admin":   user = adminDAO.authenticate(email, password);   break;
                case "Trainer": user = trainerDAO.authenticate(email, password); break;
                case "Member":  user = memberDAO.authenticate(email, password);  break;
            }

            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                openDashboard(user);
            } else {
                showError("Invalid email or password.");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/DashboardView.fxml"));
            Parent root = loader.load();
            DashboardController dc = loader.getController();
            dc.initializeForUser(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 760);
            scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
            stage.setTitle("FitPro GMS — " + user.getRole() + " Dashboard");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(680);
            stage.centerOnScreen();
        } catch (Exception e) {
            showError("Could not load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) { errorLabel.setText(msg); errorLabel.setVisible(true); }
}
