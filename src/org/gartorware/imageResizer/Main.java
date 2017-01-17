package org.gartorware.imageResizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Text progressText;
    private ProgressBar progressBar;
    private File origenFolder;
    private File destinoFolder;
    private int ancho;
    private int alto;
    private float calidad;

    private Button redimensionarBtn;
    private Button btnOrigen;
    private Button btnDestino;
    private TextField anchoTextField;
    private TextField altoTextField;
    private TextField calidadTextField;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Image Resizer");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Redimensionar imágenes");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label origenLabel = new Label("Carpeta origen:");
        origenLabel.setMinWidth(125);
        grid.add(origenLabel, 0, 1);

        final Text origenText = new Text("Seleccionar carpeta origen");
        grid.add(origenText, 1, 1);

        DirectoryChooser origenChooser = new DirectoryChooser();
        origenChooser.setTitle("Carpeta Origen");

        btnOrigen = new Button("Seleccionar");
        btnOrigen.setMinWidth(100);
        btnOrigen.setOnAction(
                e -> {
                    File selectedDirectory = origenChooser.showDialog(primaryStage);
                    if (selectedDirectory != null) {
                        origenText.setText(selectedDirectory.getAbsolutePath());
                        origenFolder = selectedDirectory;
                    }
                });
        grid.add(btnOrigen, 2, 1);

        Label destinoLabel = new Label("Carpeta destino:");
        destinoLabel.setMinWidth(125);
        grid.add(destinoLabel, 0, 2);

        final Text destinoText = new Text("Seleccionar carpeta destino");
        grid.add(destinoText, 1, 2);

        DirectoryChooser destinoChooser = new DirectoryChooser();
        destinoChooser.setTitle("Carpeta Destino");

        btnDestino = new Button("Seleccionar");
        btnDestino.setMinWidth(100);
        btnDestino.setOnAction(
                e -> {
                    File selectedDirectory = destinoChooser.showDialog(primaryStage);
                    if (selectedDirectory != null) {
                        destinoText.setText(selectedDirectory.getAbsolutePath());
                        destinoFolder = selectedDirectory;
                    }
                });
        grid.add(btnDestino, 2, 2);

        Label anchoLabel = new Label("Ancho:");
        grid.add(anchoLabel, 0, 3);
        anchoTextField = new TextField("3000");
        grid.add(anchoTextField, 1, 3);

        Label altoLabel = new Label("Alto:");
        grid.add(altoLabel, 0, 4);
        altoTextField = new TextField("2000");
        grid.add(altoTextField, 1, 4);

        Label calidadLabel = new Label("Calidad:");
        calidadTextField = new TextField("0.8");
        //grid.add(calidadLabel, 0, 5);
        //grid.add(calidadTextField, 1, 5);

        redimensionarBtn = new Button("Redimensionar");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(redimensionarBtn);
        grid.add(hbBtn, 1, 6, 1, 1);

        progressText = new Text();
        progressText.setVisible(false);
        progressText.setFill(Color.FIREBRICK);
        grid.add(progressText, 0, 7, 3, 1);

        VBox pb = new VBox();
        pb.setAlignment(Pos.CENTER);
        pb.setFillWidth(true);
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setVisible(false);
        pb.getChildren().add(progressBar);
        grid.add(pb, 0, 8, 3, 1);

        redimensionarBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                disableUI(true);

                progressText.setVisible(true);
                progressText.setText("Comenzando proceso de redimensión...");
                progressBar.setVisible(true);
                progressBar.setProgress(0);

                try {
                    ancho = Integer.parseInt(anchoTextField.getText());
                    alto = Integer.parseInt(altoTextField.getText());
                    calidad = Float.parseFloat(calidadTextField.getText());
                    resizeImages();
                } catch (NumberFormatException ex) {
                    progressText.setText("Error al parsear el número");
                }
            }
        });

        Scene scene = new Scene(grid, 800, 350);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void resizeImages() {

        if (origenFolder == null || !origenFolder.isDirectory() || origenFolder.listFiles() == null || origenFolder.listFiles().length <= 0) {
            progressText.setText("La carpeta de origen no es válida");
            progressBar.setVisible(false);
            disableUI(false);
            return;
        } else if (destinoFolder == null || !destinoFolder.exists() || !destinoFolder.isDirectory()) {
            progressText.setText("La carpeta de destino no es válida");
            progressBar.setVisible(false);
            disableUI(false);
            return;
        }

        new Thread() {
            public void run() {
                try {
                    List<File> images = new ArrayList<File>();
                    prepareImages(origenFolder, images);

                    for (int i = 0; i < images.size(); i++) {
                        int currentImage = i;
                        Platform.runLater(() -> {
                            progressText.setText(String.format("Procesando imagen %d de %d", currentImage + 1, images.size()));
                            progressBar.setProgress((float) currentImage / images.size());
                        });

                        File image = images.get(i);
                        String imageName = image.getAbsolutePath().replace(origenFolder.getAbsolutePath(), "");
                        String imageFormat = imageName.substring(imageName.lastIndexOf(".") + 1);

                        BufferedImage srcImage = ImageIO.read(image);
                        BufferedImage scaledImage = Scalr.resize(srcImage, Scalr.Method.QUALITY, ancho, alto);
                        File newImageFile = new File(destinoFolder.getAbsolutePath() + imageName);
                        if (newImageFile.getParentFile() != null) {
                            newImageFile.getParentFile().mkdirs();
                        }
                        ImageIO.write(scaledImage, imageFormat, newImageFile);
                    }

                    Platform.runLater(() -> {
                        progressText.setText(String.format("Proceso de redimensión completo: %d imágenes procesadas.", images.size()));
                        progressBar.setProgress(1);
                        disableUI(false);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        progressText.setText(String.format("El proceso ha fallado: %s", e.getMessage()));
                        disableUI(false);
                    });
                }
            }
        }.start();
    }

    private void disableUI(boolean value) {
        redimensionarBtn.setDisable(value);
        btnOrigen.setDisable(value);
        btnDestino.setDisable(value);
        anchoTextField.setDisable(value);
        altoTextField.setDisable(value);
        calidadTextField.setDisable(value);
    }

    private void prepareImages(File folder, List<File> images) {
        if (folder != null) {
            for (File file : folder.listFiles()) {
                if (!file.isDirectory()) {
                    String path = file.getAbsolutePath();
                    if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".png")) {
                        images.add(file);
                    }
                } else {
                    prepareImages(file, images);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
