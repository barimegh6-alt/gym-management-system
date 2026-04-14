package ui;

import dao.MemberDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Member;
import util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;

public class MembersController {

    // ── Table ──────────────────────────────────────────────────────────────
    @FXML private TableView<Member>               membersTable;
    @FXML private TableColumn<Member, Integer>    colId;
    @FXML private TableColumn<Member, String>     colName;
    @FXML private TableColumn<Member, String>     colEmail;
    @FXML private TableColumn<Member, String>     colPhone;
    @FXML private TableColumn<Member, String>     colMembership;
    @FXML private TableColumn<Member, String>     colGender;
    @FXML private TableColumn<Member, LocalDate>  colExpiry;
    @FXML private TableColumn<Member, String>     colStatus;

    // ── Form fields ────────────────────────────────────────────────────────
    @FXML private TextField      searchField;
    @FXML private TextField      nameField;
    @FXML private TextField      emailField;
    @FXML private TextField      phoneField;
    @FXML private PasswordField  passwordField;
    @FXML private TextField      ageField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private ComboBox<String> membershipCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private DatePicker     expiryDatePicker;
    @FXML private TextField      addressField;

    // ── Buttons & labels ──────────────────────────────────────────────────
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button clearBtn;
    @FXML private Label  messageLabel;

    private final MemberDAO memberDAO = new MemberDAO();
    private final ObservableList<Member> memberList = FXCollections.observableArrayList();
    private Member selectedMember = null;

    @FXML
    public void initialize() {
        setupColumns();
        setupCombos();
        loadAll();
        setupTableSelection();
        messageLabel.setVisible(false);
        updateBtn.setDisable(true);
    }

    // ── Setup ──────────────────────────────────────────────────────────────

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colMembership.setCellValueFactory(new PropertyValueFactory<>("membershipType"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Colour-code the Status column
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Active"   -> setStyle("-fx-text-fill:#4ade80;-fx-font-weight:bold;");
                    case "Expired"  -> setStyle("-fx-text-fill:#f87171;-fx-font-weight:bold;");
                    default         -> setStyle("-fx-text-fill:#facc15;-fx-font-weight:bold;");
                }
            }
        });

        membersTable.setItems(memberList);
    }

    private void setupCombos() {
        genderCombo.getItems().addAll("Male", "Female", "Other");
        genderCombo.setValue("Male");
        membershipCombo.getItems().addAll("Basic", "Standard", "Premium");
        membershipCombo.setValue("Basic");
        statusCombo.getItems().addAll("Active", "Inactive", "Expired");
        statusCombo.setValue("Active");
    }

    private void setupTableSelection() {
        membersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                selectedMember = sel;
                populate(sel);
                updateBtn.setDisable(false);
            }
        });
    }

    private void populate(Member m) {
        nameField.setText(m.getName());
        emailField.setText(m.getEmail());
        phoneField.setText(m.getPhone());
        passwordField.setText(m.getPassword());
        ageField.setText(String.valueOf(m.getAge()));
        addressField.setText(m.getAddress() != null ? m.getAddress() : "");
        genderCombo.setValue(m.getGender());
        membershipCombo.setValue(m.getMembershipType());
        statusCombo.setValue(m.getStatus());
        if (m.getExpiryDate() != null) expiryDatePicker.setValue(m.getExpiryDate());
    }

    // ── Data ──────────────────────────────────────────────────────────────

    private void loadAll() {
        try {
            ArrayList<Member> all = memberDAO.getAllMembers();
            memberList.setAll(all);
        } catch (Exception e) { showMsg("Error loading members: " + e.getMessage(), true); }
    }

    // ── Actions (all wired via onAction in FXML) ───────────────────────────

    @FXML
    public void handleSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadAll(); return; }
        try {
            memberList.setAll(memberDAO.searchMembers(kw));
        } catch (Exception e) { showMsg("Search error: " + e.getMessage(), true); }
    }

    @FXML
    public void handleRefresh() {
        searchField.clear();
        loadAll();
        clearForm();
    }

    @FXML
    public void handleAdd() {
        try {
            if (!validateAdd()) return;

            Member m = new Member(
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                passwordField.getText().trim(),
                membershipCombo.getValue(),
                LocalDate.now(),
                expiryDatePicker.getValue(),
                addressField.getText().trim(),
                Integer.parseInt(ageField.getText().trim()),
                genderCombo.getValue()
            );

            if (memberDAO.addMember(m)) {
                showMsg("✓ Member added successfully!", false);
                loadAll(); clearForm();
            } else {
                showMsg("✗ Failed to add member. Check console for details.", true);
            }
        } catch (Exception e) { showMsg("✗ Error: " + e.getMessage(), true); e.printStackTrace(); }
    }

    @FXML
    public void handleUpdate() {
        if (selectedMember == null) { showMsg("Select a row first.", true); return; }
        try {
            if (!validateUpdate()) return;
            selectedMember.setName(nameField.getText().trim());
            selectedMember.setEmail(emailField.getText().trim());
            selectedMember.setPhone(phoneField.getText().trim());
            selectedMember.setMembershipType(membershipCombo.getValue());
            selectedMember.setGender(genderCombo.getValue());
            selectedMember.setStatus(statusCombo.getValue());
            selectedMember.setAge(Integer.parseInt(ageField.getText().trim()));
            selectedMember.setAddress(addressField.getText().trim());
            if (expiryDatePicker.getValue() != null)
                selectedMember.setExpiryDate(expiryDatePicker.getValue());

            if (memberDAO.updateMember(selectedMember)) {
                showMsg("✓ Member updated successfully!", false);
                loadAll(); clearForm();
            } else {
                showMsg("✗ Update failed.", true);
            }
        } catch (Exception e) { showMsg("✗ Error: " + e.getMessage(), true); }
    }

    @FXML
    public void handleDelete() {
        Member sel = membersTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showMsg("Select a member to delete.", true); return; }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + sel.getName() + "? This cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        dlg.setTitle("Delete Member"); dlg.setHeaderText("Confirm Delete");
        dlg.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (memberDAO.deleteMember(sel.getId())) {
                    showMsg("✓ Member deleted.", false); loadAll(); clearForm();
                } else { showMsg("✗ Delete failed.", true); }
            }
        });
    }

    @FXML
    public void clearForm() {
        nameField.clear(); emailField.clear(); phoneField.clear();
        passwordField.clear(); ageField.clear(); addressField.clear();
        expiryDatePicker.setValue(null);
        genderCombo.setValue("Male");
        membershipCombo.setValue("Basic");
        statusCombo.setValue("Active");
        selectedMember = null;
        updateBtn.setDisable(true);
        membersTable.getSelectionModel().clearSelection();
        messageLabel.setVisible(false);
    }

    // ── Validation ────────────────────────────────────────────────────────

    private boolean validateAdd() {
        if (ValidationUtil.isEmpty(nameField.getText()))       { showMsg("Name is required.", true); return false; }
        if (!ValidationUtil.isValidEmail(emailField.getText())) { showMsg("Valid email required.", true); return false; }
        if (!ValidationUtil.isValidPhone(phoneField.getText())) { showMsg("10-digit phone required.", true); return false; }
        if (!ValidationUtil.isValidPassword(passwordField.getText())) { showMsg("Password ≥ 4 chars.", true); return false; }
        if (!ValidationUtil.isValidAge(ageField.getText()))    { showMsg("Valid age (5-120) required.", true); return false; }
        if (expiryDatePicker.getValue() == null)               { showMsg("Expiry date required.", true); return false; }
        return true;
    }

    private boolean validateUpdate() {
        if (ValidationUtil.isEmpty(nameField.getText()))       { showMsg("Name is required.", true); return false; }
        if (!ValidationUtil.isValidEmail(emailField.getText())) { showMsg("Valid email required.", true); return false; }
        if (!ValidationUtil.isValidPhone(phoneField.getText())) { showMsg("10-digit phone required.", true); return false; }
        if (!ValidationUtil.isValidAge(ageField.getText()))    { showMsg("Valid age (5-120) required.", true); return false; }
        return true;
    }

    private void showMsg(String msg, boolean err) {
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
        messageLabel.setStyle(err ? "-fx-text-fill:#f87171;" : "-fx-text-fill:#4ade80;");
    }
}
