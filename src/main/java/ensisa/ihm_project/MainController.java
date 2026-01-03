package ensisa.ihm_project;

import ensisa.ihm_project.model.Courbe;
import ensisa.ihm_project.model.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.awt.event.ActionEvent;

public class MainController {
    private Courbe modele;

    @FXML
    private Pane drawArea;
    @FXML
    private Polyline courbePolyline;
    private static final double MARGIN_LEFT = 0.0;
    private static final double MARGIN_BOTTOM = 255.0;

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    private void initialiserPointsControleVisuels() {
        for (int i = 0; i < modele.getPoints().size(); i++) {
            Point p = modele.getPoints().get(i);

            Circle c = new Circle(5, Color.GRAY);
            c.setCenterX(MARGIN_LEFT + p.getX());
            c.setCenterY(MARGIN_BOTTOM - p.getY());

            //glisser point
            final int index = i;
            c.setOnMouseDragged(event -> {
                double mouseScreenY = event.getY() + c.getTranslateY();
                double newScreenY = event.getSceneY() - drawArea.localToScene(0,0).getY();

                double newModelY = MARGIN_BOTTOM - newScreenY;
                modele.setPointY(index, newModelY);

                double validatedModelY = modele.getPoints().get(index).getY();
                c.setCenterY(MARGIN_BOTTOM - validatedModelY);

                dessinerCourbe();
            });

            // Ajouter le cercle au Pane
            drawArea.getChildren().add(c);
        }
    }
    
    private void dessinerCourbe() {
        courbePolyline.getPoints().clear();
        for (int x = 0; x <= 255; x++) {
            double y = modele.polynome(x);
            double screenX = MARGIN_LEFT + x;
            double screenY = MARGIN_BOTTOM - y;
            courbePolyline.getPoints().addAll(screenX, screenY);
        }
    }

    @FXML
    public void initialize() {
        modele = new Courbe(4);
        initialiserPointsControleVisuels();
        dessinerCourbe();
    }


    @FXML
    private void lineariserAction() {
        modele.lineariser();
        int indexPoint = 0;
        for (javafx.scene.Node node : drawArea.getChildren()) {
            if (node instanceof Circle) {
                Point p = modele.getPoints().get(indexPoint);
                ((Circle)node).setCenterY(MARGIN_BOTTOM-p.getY());
                indexPoint++;
            }
        }
        dessinerCourbe();
    }

    @FXML
    private void changerNbPointsAction(ActionEvent event) {
        javafx.scene.control.MenuItem item = (javafx.scene.control.MenuItem) event.getSource();
        String data = (String) item.getUserData();
        int n = Integer.parseInt(data);
        modele = new Courbe(n);
        drawArea.getChildren().removeIf(node -> node instanceof javafx.scene.shape.Circle);
        initialiserPointsControleVisuels();
        dessinerCourbe();
    }
}
