module ensisa.ihm_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ensisa.ihm_project to javafx.fxml;
    exports ensisa.ihm_project;
}