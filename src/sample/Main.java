package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private Text progressText;
    private File origenFolder;
    private File destinoFolder;
    private int ancho;
    private int alto;
    private float calidad;

    {
        progressText = new Text();
        progressText.setFill(Color.FIREBRICK);
    }

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
        grid.add(origenLabel, 0, 1);

        final Text origenText = new Text("Seleccionar carpeta origen");
        grid.add(origenText, 1, 1);

        DirectoryChooser origenChooser = new DirectoryChooser();
        origenChooser.setTitle("Carpeta Origen");
        origenChooser.setInitialDirectory(new File("c:/"));

        Button btnOrigen = new Button("Seleccionar");
        btnOrigen.setOnAction(
                e -> {
                    File selectedDirectory = origenChooser.showDialog(primaryStage);
                    origenText.setText(selectedDirectory.getAbsolutePath());
                    origenFolder = selectedDirectory;
                });
        grid.add(btnOrigen, 2, 1);

        Label destinoLabel = new Label("Carpeta destino:");
        grid.add(destinoLabel, 0, 2);

        final Text destinoText = new Text("Seleccionar carpeta destino");
        grid.add(destinoText, 1, 2);

        DirectoryChooser destinoChooser = new DirectoryChooser();
        destinoChooser.setTitle("Carpeta Destino");
        destinoChooser.setInitialDirectory(new File("c:/"));

        Button btnDestino = new Button("Seleccionar");
        btnDestino.setOnAction(
                e -> {
                    File selectedDirectory = destinoChooser.showDialog(primaryStage);
                    destinoText.setText(selectedDirectory.getAbsolutePath());
                    destinoFolder = selectedDirectory;
                });
        grid.add(btnDestino, 2, 2);

        Label anchoLabel = new Label("Ancho:");
        grid.add(anchoLabel, 0, 3);
        TextField anchoTextField = new TextField("1280");
        grid.add(anchoTextField, 1, 3);

        Label altoLabel = new Label("Alto:");
        grid.add(altoLabel, 0, 4);
        TextField altoTextField = new TextField("1024");
        grid.add(altoTextField, 1, 4);

        Label calidadLabel = new Label("Calidad:");
        grid.add(calidadLabel, 0, 5);
        TextField calidadTextField = new TextField("0.8");
        grid.add(calidadTextField, 1, 5);

        Button redimensionarBtn = new Button("Redimensionar");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(redimensionarBtn);
        grid.add(hbBtn, 1, 6, 1, 1);

        grid.add(progressText, 0, 7, 3, 1);

        redimensionarBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                redimensionarBtn.setDisable(true);
                btnOrigen.setDisable(true);
                btnDestino.setDisable(true);
                anchoTextField.setDisable(true);
                altoTextField.setDisable(true);
                calidadTextField.setDisable(true);

                try {
                    ancho = Integer.parseInt(anchoTextField.getText());
                    alto = Integer.parseInt(altoTextField.getText());
                    calidad = Float.parseFloat(calidadTextField.getText());
                    resizeImages();
                } catch (NumberFormatException ex) {
                    progressText.setText("Error al parsear el número");
                }

                redimensionarBtn.setDisable(false);
                btnOrigen.setDisable(false);
                btnDestino.setDisable(false);
                anchoTextField.setDisable(false);
                altoTextField.setDisable(false);
                calidadTextField.setDisable(false);
            }
        });

        Scene scene = new Scene(grid, 1024, 400);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void resizeImages() {
        progressText.setText("Comenzando proceso de redimensión...");

        progressText.setText("Proceso de redimensión completo");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
