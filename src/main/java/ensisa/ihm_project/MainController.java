package ensisa.ihm_project;

import ensisa.ihm_project.model.Courbe;
import ensisa.ihm_project.model.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import javafx.event.ActionEvent;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    private Courbe modeleRed;
    private Courbe modeleGreen;
    private Courbe modeleBlue;

    private EditeurCourbe editorRed;
    private EditeurCourbe editorGreen;
    private EditeurCourbe editorBlue;

    @FXML
    private Pane drawAreaRed;
    @FXML
    private Pane drawAreaGreen;
    @FXML
    private Pane drawAreaBlue;

    @FXML
    private Polyline courbePolyline;
    private static final double MARGIN_LEFT = 20.0;
    private static final double MARGIN_BOTTOM = 275.0;

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    @FXML
    public void initialize() {

        modeleRed = new Courbe(4);
        modeleGreen = new Courbe(4);
        modeleBlue = new Courbe(4);

        editorRed = new EditeurCourbe(drawAreaRed, modeleRed);
        editorGreen = new EditeurCourbe(drawAreaGreen, modeleGreen);
        editorBlue = new EditeurCourbe(drawAreaBlue, modeleBlue);
    }


    @FXML
    private void lineariserAction() {

        modeleRed.lineariser();
        modeleGreen.lineariser();
        modeleBlue.lineariser();

        editorRed.updateView();
        editorGreen.updateView();
        editorBlue.updateView();
    }

    @FXML
    private void changerNbPointsAction(ActionEvent event) {
        RadioMenuItem item = (RadioMenuItem) event.getSource();
        int n = Integer.parseInt(item.getText());

        modeleRed = new Courbe(n);
        modeleGreen = new Courbe(n);
        modeleBlue = new Courbe(n);

        drawAreaRed.getChildren().clear();
        drawAreaGreen.getChildren().clear();
        drawAreaBlue.getChildren().clear();

        editorRed = new EditeurCourbe(drawAreaRed, modeleRed);
        editorGreen = new EditeurCourbe(drawAreaGreen, modeleGreen);
        editorBlue = new EditeurCourbe(drawAreaBlue, modeleBlue);
    }

    @FXML
    private ImageView myImageView;

    @FXML
    public void setImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Image1");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(myImageView.getScene().getWindow());

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            myImageView.setImage(image);
        }
    }
}
