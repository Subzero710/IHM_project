package ensisa.ihm_project;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainController {
    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }
}
