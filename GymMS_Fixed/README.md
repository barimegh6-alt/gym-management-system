# 💪 FitPro — Gym Management System (Fixed)

Java 17 · JavaFX · MySQL · JDBC · DAO Pattern

---

## 🐛 Bugs Fixed in This Version

| # | Bug | Fix Applied |
|---|-----|-------------|
| 1 | Workouts page blank / crash | Removed `@FXML statusCombo` & `@FXML deleteBtn` from `WorkoutsController` — those fields had no matching `fx:id` in the FXML, causing `FXMLLoader` to throw silently |
| 2 | Schedule page blank | Same root cause pattern — all `@FXML` fields now match `fx:id` in FXML exactly |
| 3 | Navigation not switching views | `loadView()` now calls `setMaxWidth/Height(MAX)` on loaded node so it fills the `StackPane` |
| 4 | Buttons hidden off-screen | All form panels wrapped in `ScrollPane fitToWidth="true"` |
| 5 | Content area not expanding | Center `VBox` in `DashboardView.fxml` now has `VBox.vgrow="ALWAYS"` |
| 6 | DB connection drops silently | `DatabaseConnection` now uses `connection.isValid(2)` instead of `isClosed()` |
| 7 | Trainer table overflows | Column `prefWidth` values reduced so form panel is always visible |
| 8 | Errors swallowed silently | `loadView()` now shows a visible red label in the content area on failure |

---

## ⚙️ Setup

### 1. MySQL
```sql
-- In MySQL Workbench or terminal:
source /path/to/GymMS_Fixed/schema.sql
```

### 2. Set your MySQL password
Open `src/util/DatabaseConnection.java` and change:
```java
private static final String PASSWORD = "root";  // ← your MySQL password
```

### 3. MySQL Connector/J
Download from https://dev.mysql.com/downloads/connector/j/  
Place the `.jar` file inside `lib/`

### 4. JavaFX SDK
Download from https://gluonhq.com/products/javafx/ (version 17+)

---

## ▶️ Run in VS Code

`.vscode/launch.json` is pre-configured. Update the `JAVAFX_HOME` path:
```json
"vmArgs": "--module-path /YOUR/PATH/TO/javafx-sdk-17/lib --add-modules javafx.controls,javafx.fxml"
```
Then press **F5**.

## ▶️ Run in IntelliJ

1. File → Project Structure → Libraries → add JavaFX SDK + MySQL JAR  
2. Run → Edit Configurations → VM Options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
3. Main class: `ui.MainApp`

---

## 🔑 Default Credentials

| Role    | Email                | Password    |
|---------|----------------------|-------------|
| Admin   | admin@fitpro.com     | admin123    |
| Trainer | arjun@fitpro.com     | trainer123  |
| Member  | ananya@gmail.com     | member123   |
