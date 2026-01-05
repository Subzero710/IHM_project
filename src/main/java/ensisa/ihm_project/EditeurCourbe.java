package ensisa.ihm_project;

import ensisa.ihm_project.model.Courbe;
import ensisa.ihm_project.model.Point;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;

public class EditeurCourbe {
    public interface PointCommitListener {
        void onPointCommit(Courbe courbe, int index, double oldY, double newY);
    }

    private final Pane drawArea;
    private final Courbe modele;
    private final Polyline courbePolyline;
    private final double MARGIN = 20.0;
    private final double HEIGHT = 255.0;

    private final Runnable onCurveChanged;
    private final PointCommitListener onPointCommit;

    public EditeurCourbe(Pane drawArea, Courbe modele) {
        this(drawArea, modele, null, null);
    }

    public EditeurCourbe(Pane drawArea, Courbe modele, Runnable onCurveChanged, PointCommitListener onPointCommit) {
        this.drawArea = drawArea;
        this.modele = modele;
        this.onCurveChanged = onCurveChanged;
        this.onPointCommit = onPointCommit;
        this.courbePolyline = new Polyline();
        this.courbePolyline.setStrokeWidth(2.0);

        Line xAxis = new Line(MARGIN, HEIGHT + MARGIN, MARGIN + 255, HEIGHT + MARGIN);
        Line yAxis = new Line(MARGIN, MARGIN, MARGIN, HEIGHT + MARGIN);

        this.drawArea.getChildren().addAll(xAxis, yAxis, courbePolyline);
        initialiserPointsControleVisuels();
        dessinerCourbe();
    }

    private void initialiserPointsControleVisuels() {
        drawArea.getChildren().removeIf(node -> node instanceof Circle);

        for (int i = 0; i < modele.getPoints().size(); i++) {
            Point p = modele.getPoints().get(i);
            Circle c = new Circle(5, Color.GRAY);
            c.setCenterX(MARGIN + p.getX());
            c.setCenterY(MARGIN + (HEIGHT - p.getY()));

            final int ind = i;

            final double[] startY = new double[1];
            c.setOnMousePressed(event -> startY[0] = modele.getPoints().get(ind).getY());

            c.setOnMouseDragged(event -> {
                double currentMouseY = c.getParent().sceneToLocal(event.getSceneX(), event.getSceneY()).getY();
                double newModelY = HEIGHT - (currentMouseY - MARGIN);
                modele.setPointY(ind, newModelY);
                double validatedModelY = modele.getPoints().get(ind).getY();
                c.setCenterY(MARGIN + (HEIGHT - validatedModelY));

                dessinerCourbe();

                if (onCurveChanged != null) {
                    onCurveChanged.run();
                }
            });

            c.setOnMouseReleased(event -> {
                if (onPointCommit == null) {
                    return;
                }

                double endY = modele.getPoints().get(ind).getY();
                if (Math.abs(endY - startY[0]) < 1e-9) {
                    return;
                }

                onPointCommit.onPointCommit(modele, ind, startY[0], endY);
            });

            drawArea.getChildren().add(c);
        }
    }

    public void dessinerCourbe() {
        courbePolyline.getPoints().clear();
        for (int x = 0; x <= 255; x++) {
            double y = modele.polynome(x);
            double screenX = MARGIN + x;
            double screenY = MARGIN + (HEIGHT - y);
            courbePolyline.getPoints().addAll(screenX, screenY);
        }
    }

    public void updateView() {
        initialiserPointsControleVisuels();
        dessinerCourbe();
    }
}