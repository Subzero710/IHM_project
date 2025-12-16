module ensisa.ihm_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens ensisa.ihm_project to javafx.fxml;
    exports ensisa.ihm_project;
}