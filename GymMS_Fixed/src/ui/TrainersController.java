package ui;

import dao.TrainerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Trainer;
import util.ValidationUtil;

import java.util.ArrayList;

public class TrainersController {

    // ── Table ──────────────────────────────────────────────────────────────
    @FXML private TableView<Trainer>             trainersTable;
    @FXML private TableColumn<Trainer, Integer>  colId;
    @FXML private TableColumn<Trainer, String>   colName;
    @FXML private TableColumn<Trainer, String>   colEmail;
    @FXML private TableColumn<Trainer, String>   colPhone;
    @FXML private TableColumn<Trainer, String>   colSpecialization;
    @FXML private TableColumn<Trainer, Integer>  colExperience;
    @FXML private TableColumn<Trainer, String>   colQualification;
    @FXML private TableColumn<Trainer, Double>   colSalary;
    @FXML private TableColumn<Trainer, String>   colAvailability;
    @FXML private TableColumn<Trainer, String>   colStatus;

    // ── Form fields ────────────────────────────────────────────────────────
    @FXML private TextField      searchField;
    @FXML private TextField      nameField;
    @FXML private TextField      emailField;
    @FXML private TextField      phoneField;
    @FXML private PasswordField  passwordField;
    @FXML private TextField      specializationField;
    @FXML private TextField      experienceField;
    @FXML private TextField      qualificationField;
    @FXML private TextField      salaryField;
    @FXML private ComboBox<String> availabilityCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button         updateBtn;
    @FXML private Label          messageLabel;

    private final TrainerDAO trainerDAO = new TrainerDAO();
    private final ObservableList<Trainer> trainerList = FXCollections.observableArrayList();
    private Trainer selectedTrainer = null;

    @FXML
    public void initialize() {
        setupColumns();
        setupCombos();
        loadAll();
        setupSelection();
        messageLabel.setVisible(false);
        updateBtn.setDisable(true);
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colExperience.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        colQualification.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Active".equals(item) ? "-fx-text-fill:#4ade80;-fx-font-weight:bold;"
                                               : "-fx-text-fill:#f87171;-fx-font-weight:bold;");
            }
        });
        trainersTable.setItems(trainerList);
    }

    private void setupCombos() {
        availabilityCombo.getItems().addAll("Full-Time", "Part-Time");
        availabilityCombo.setValue("Full-Time");
        statusCombo.getItems().addAll("Active", "Inactive");
        statusCombo.setValue("Active");
    }

    private void setupSelection() {
        trainersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) { selectedTrainer = sel; populate(sel); updateBtn.setDisable(false); }
        });
    }

    private void populate(Trainer t) {
        nameField.setText(t.getName()); emailField.setText(t.getEmail());
        phoneField.setText(t.getPhone()); passwordField.setText(t.getPassword());
        specializationField.setText(t.getSpecialization());
        experienceField.setText(String.valueOf(t.getExperienceYears()));
        qualificationField.setText(t.getQualification() != null ? t.getQualification() : "");
        salaryField.setText(String.valueOf(t.getSalary()));
        availabilityCombo.setValue(t.getAvailability()); statusCombo.setValue(t.getStatus());
    }

    private void loadAll() {
        try { trainerList.setAll(trainerDAO.getAllTrainers()); }
        catch (Exception e) { showMsg("Error loading trainers: " + e.getMessage(), true); }
    }

    @FXML public void handleSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadAll(); return; }
        try { trainerList.setAll(trainerDAO.searchTrainers(kw)); }
        catch (Exception e) { showMsg("Search error: " + e.getMessage(), true); }
    }

    @FXML public void handleRefresh() { searchField.clear(); loadAll(); clearForm(); }

    @FXML public void handleAdd() {
        try {
            if (!validateAdd()) return;
            Trainer t = new Trainer(
                nameField.getText().trim(), emailField.getText().trim(),
                phoneField.getText().trim(), passwordField.getText().trim(),
                specializationField.getText().trim(),
                Integer.parseInt(experienceField.getText().trim()),
                qualificationField.getText().trim(),
                Double.parseDouble(salaryField.getText().trim()),
                availabilityCombo.getValue()
            );
            if (trainerDAO.addTrainer(t)) { showMsg("✓ Trainer added!", false); loadAll(); clearForm(); }
            else showMsg("✗ Failed to add trainer.", true);
        } catch (NumberFormatException e) { showMsg("✗ Invalid experience or salary.", true);
        } catch (Exception e) { showMsg("✗ Error: " + e.getMessage(), true); }
    }

    @FXML public void handleUpdate() {
        if (selectedTrainer == null) { showMsg("Select a trainer to update.", true); return; }
        try {
            if (!validateUpdate()) return;
            selectedTrainer.setName(nameField.getText().trim());
            selectedTrainer.setEmail(emailField.getText().trim());
            selectedTrainer.setPhone(phoneField.getText().trim());
            selectedTrainer.setSpecialization(specializationField.getText().trim());
            selectedTrainer.setExperienceYears(Integer.parseInt(experienceField.getText().trim()));
            selectedTrainer.setQualification(qualificationField.getText().trim());
            selectedTrainer.setSalary(Double.parseDouble(salaryField.getText().trim()));
            selectedTrainer.setAvailability(availabilityCombo.getValue());
            selectedTrainer.setStatus(statusCombo.getValue());
            if (trainerDAO.updateTrainer(selectedTrainer)) { showMsg("✓ Trainer updated!", false); loadAll(); clearForm(); }
            else showMsg("✗ Update failed.", true);
        } catch (Exception e) { showMsg("✗ Error: " + e.getMessage(), true); }
    }

    @FXML public void handleDelete() {
        Trainer sel = trainersTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a trainer to delete.", true); return; }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + sel.getName() + "?", ButtonType.OK, ButtonType.CANCEL);
        dlg.setTitle("Delete Trainer"); dlg.setHeaderText("Confirm Delete");
        dlg.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (trainerDAO.deleteTrainer(sel.getId())) { showMsg("✓ Trainer deleted.", false); loadAll(); clearForm(); }
                else showMsg("✗ Delete failed.", true);
            }
        });
    }

    @FXML public void clearForm() {
        nameField.clear(); emailField.clear(); phoneField.clear(); passwordField.clear();
        specializationField.clear(); experienceField.clear(); qualificationField.clear(); salaryField.clear();
        availabilityCombo.setValue("Full-Time"); statusCombo.setValue("Active");
        selectedTrainer = null; updateBtn.setDisable(true);
        trainersTable.getSelectionModel().clearSelection(); messageLabel.setVisible(false);
    }

    private boolean validateAdd() {
        if (ValidationUtil.isEmpty(nameField.getText()))           { showMsg("Name required.", true); return false; }
        if (!ValidationUtil.isValidEmail(emailField.getText()))    { showMsg("Valid email required.", true); return false; }
        if (!ValidationUtil.isValidPhone(phoneField.getText()))    { showMsg("10-digit phone required.", true); return false; }
        if (!ValidationUtil.isValidPassword(passwordField.getText())) { showMsg("Password ≥ 4 chars.", true); return false; }
        if (ValidationUtil.isEmpty(specializationField.getText())) { showMsg("Specialization required.", true); return false; }
        if (!ValidationUtil.isPositiveInteger(experienceField.getText())) { showMsg("Valid experience years required.", true); return false; }
        if (!ValidationUtil.isPositiveNumber(salaryField.getText()))      { showMsg("Valid salary required.", true); return false; }
        return true;
    }

    private boolean validateUpdate() {
        if (ValidationUtil.isEmpty(nameField.getText()))           { showMsg("Name required.", true); return false; }
        if (!ValidationUtil.isValidEmail(emailField.getText()))    { showMsg("Valid email required.", true); return false; }
        if (!ValidationUtil.isValidPhone(phoneField.getText()))    { showMsg("10-digit phone required.", true); return false; }
        if (ValidationUtil.isEmpty(specializationField.getText())) { showMsg("Specialization required.", true); return false; }
        return true;
    }

    private void showMsg(String msg, boolean err) {
        messageLabel.setText(msg); messageLabel.setVisible(true);
        messageLabel.setStyle(err ? "-fx-text-fill:#f87171;" : "-fx-text-fill:#4ade80;");
    }
}
