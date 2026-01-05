package ensisa.ihm_project;

import ensisa.ihm_project.model.Courbe;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;

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
    private MenuItem undoMenuItem;

    @FXML
    private MenuItem redoMenuItem;

    @FXML
    private ImageView myImageView;

    private Image originalImage;
    private WritableImage filteredImage;

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    @FXML
    public void initialize() {

        modeleRed = new Courbe(4);
        modeleGreen = new Courbe(4);
        modeleBlue = new Courbe(4);

        Runnable onCurveChanged = this::refreshFilteredImage;
        EditeurCourbe.PointCommitListener onPointCommit = (courbe, index, oldY, newY) ->
                pushCommand(new MovePointCommand(courbe, index, oldY, newY));

        editorRed = new EditeurCourbe(drawAreaRed, modeleRed, onCurveChanged, onPointCommit);
        editorGreen = new EditeurCourbe(drawAreaGreen, modeleGreen, onCurveChanged, onPointCommit);
        editorBlue = new EditeurCourbe(drawAreaBlue, modeleBlue, onCurveChanged, onPointCommit);

        updateUndoRedoMenuItems();
    }


    @FXML
    private void lineariserAction() {

        pushCommand(new LinearizeCommand());
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

        undoStack.clear();
        redoStack.clear();

        Runnable onCurveChanged = this::refreshFilteredImage;
        EditeurCourbe.PointCommitListener onPointCommit = (courbe, index, oldY, newY) ->
                pushCommand(new MovePointCommand(courbe, index, oldY, newY));

        editorRed = new EditeurCourbe(drawAreaRed, modeleRed, onCurveChanged, onPointCommit);
        editorGreen = new EditeurCourbe(drawAreaGreen, modeleGreen, onCurveChanged, onPointCommit);
        editorBlue = new EditeurCourbe(drawAreaBlue, modeleBlue, onCurveChanged, onPointCommit);

        refreshFilteredImage();
        updateUndoRedoMenuItems();
    }

    @FXML
    private void undoAction() {
        if (undoStack.isEmpty()) {
            return;
        }
        Command cmd = undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
        updateUndoRedoMenuItems();
    }

    @FXML
    private void redoAction() {
        if (redoStack.isEmpty()) {
            return;
        }
        Command cmd = redoStack.pop();
        cmd.execute();
        undoStack.push(cmd);
        updateUndoRedoMenuItems();
    }

    @FXML
    public void setImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Image1");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG images", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(myImageView.getScene().getWindow());

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString(), false);
            pushCommand(new OpenImageCommand(originalImage, image));
        }
    }

    private void refreshFilteredImage() {
        if (myImageView == null) {
            return;
        }

        if (originalImage == null) {
            myImageView.setImage(null);
            filteredImage = null;
            return;
        }

        PixelReader reader = originalImage.getPixelReader();
        if (reader == null) {
            return;
        }

        int w = (int) originalImage.getWidth();
        int h = (int) originalImage.getHeight();

        if (filteredImage == null || (int) filteredImage.getWidth() != w || (int) filteredImage.getHeight() != h) {
            filteredImage = new WritableImage(w, h);
        }

        int[] lutR = buildLut(modeleRed);
        int[] lutG = buildLut(modeleGreen);
        int[] lutB = buildLut(modeleBlue);

        int[] src = new int[w * h];
        int[] dst = new int[w * h];

        reader.getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), src, 0, w);

        for (int i = 0; i < src.length; i++) {
            int argb = src[i];
            int a = (argb >>> 24) & 0xFF;
            int r = (argb >>> 16) & 0xFF;
            int g = (argb >>> 8) & 0xFF;
            int b = argb & 0xFF;

            dst[i] = (a << 24) | (lutR[r] << 16) | (lutG[g] << 8) | lutB[b];
        }

        filteredImage.getPixelWriter().setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), dst, 0, w);
        myImageView.setImage(filteredImage);
    }

    private static int[] buildLut(Courbe courbe) {
        int[] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            int v = (int) Math.round(courbe.polynome(i));
            if (v < 0) v = 0;
            if (v > 255) v = 255;
            lut[i] = v;
        }
        return lut;
    }

    private void refreshAllViews() {
        editorRed.updateView();
        editorGreen.updateView();
        editorBlue.updateView();
        refreshFilteredImage();
    }

    private void pushCommand(Command cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
        updateUndoRedoMenuItems();
    }

    private void updateUndoRedoMenuItems() {
        if (undoMenuItem != null) {
            undoMenuItem.setDisable(undoStack.isEmpty());
        }
        if (redoMenuItem != null) {
            redoMenuItem.setDisable(redoStack.isEmpty());
        }
    }

    private interface Command {
        void execute();

        void undo();
    }

    private class OpenImageCommand implements Command {
        private final Image previous;
        private final Image next;

        private OpenImageCommand(Image previous, Image next) {
            this.previous = previous;
            this.next = next;
        }

        @Override
        public void execute() {
            originalImage = next;
            refreshFilteredImage();
        }

        @Override
        public void undo() {
            originalImage = previous;
            refreshFilteredImage();
        }
    }

    private class MovePointCommand implements Command {
        private final Courbe courbe;
        private final int index;
        private final double oldY;
        private final double newY;

        private MovePointCommand(Courbe courbe, int index, double oldY, double newY) {
            this.courbe = courbe;
            this.index = index;
            this.oldY = oldY;
            this.newY = newY;
        }

        @Override
        public void execute() {
            courbe.setPointY(index, newY);
            refreshAllViews();
        }

        @Override
        public void undo() {
            courbe.setPointY(index, oldY);
            refreshAllViews();
        }
    }

    private class LinearizeCommand implements Command {
        private final double[] beforeR;
        private final double[] beforeG;
        private final double[] beforeB;

        private LinearizeCommand() {
            this.beforeR = modeleRed.snapshotY();
            this.beforeG = modeleGreen.snapshotY();
            this.beforeB = modeleBlue.snapshotY();
        }

        @Override
        public void execute() {
            modeleRed.lineariser();
            modeleGreen.lineariser();
            modeleBlue.lineariser();
            refreshAllViews();
        }

        @Override
        public void undo() {
            modeleRed.restoreY(beforeR);
            modeleGreen.restoreY(beforeG);
            modeleBlue.restoreY(beforeB);
            refreshAllViews();
        }
    }
}
