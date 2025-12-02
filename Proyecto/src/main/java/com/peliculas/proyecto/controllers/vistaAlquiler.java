package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.*;
import com.peliculas.proyecto.dto.Alquiler;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.PeliculasDisponibles;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

// Asegúrate que el nombre de la clase coincida exactamente con el fx:controller de tu FXML
public class vistaAlquiler {

    @FXML private Button btnVolver;
    @FXML private ScrollPane scrollPeliculas; // Conexión al ScrollPane
    @FXML private GridPane gridPeliculas; // Conexión al GridPane (donde inyectamos las tarjetas)

    private AlquilerDao alquilerDao = new AlquilerDao();
    private PeliculaDao peliculaDao = new PeliculaDao();
    private Usuario usuarioActual;

    // Recibe el usuario actual desde vistaPanelUsuario
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarPeliculasParaAlquiler();
    }

    // 2. INICIALIZACIÓN
    @FXML
    public void initialize() {
        btnVolver.setOnAction(e -> volverAlPanelUsuario());
        //cargarPeliculasParaAlquiler();
    }



    public void cargarPeliculasParaAlquiler() {
        ArrayList<Pelicula> peliculasDisponibles = PeliculasDisponiblesDao.obtenerPeliculasDispos();
        ArrayList<VBox> cards = crearTarjetasAlquiler(peliculasDisponibles);
        mostrarPeliculas(cards);
    }

    public ArrayList<VBox> crearTarjetasAlquiler(ArrayList<Pelicula> p) {
        ArrayList<VBox> boxs = new ArrayList<>();

        for (Pelicula pelicula : p) {
            VBox box = new VBox(5);
            box.setPrefWidth(180);

            // Estilo de la tarjeta con degradado igual que en vistaBuscador
            LinearGradient gradient = new LinearGradient(
                    0, 0,
                    1, 1,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#a34fb0")),
                    new Stop(1, Color.web("#7b2cc9"))
            );
            box.setBackground(new Background(
                    new BackgroundFill(gradient, new CornerRadii(12), Insets.EMPTY)
            ));
            box.setPadding(new Insets(10));
            box.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

            String baseUrl = "https://image.tmdb.org/t/p/w500";
            String fullUrl = baseUrl + pelicula.getPathBanner();

            ImageView img;
            try {
                img = new ImageView(new Image(fullUrl, true));
            } catch (IllegalArgumentException e) {
                img = new ImageView();
            }

            img.setFitWidth(180);
            img.setFitHeight(250);
            img.setPreserveRatio(true);

            Label titulo = new Label(pelicula.getTitulo());
            titulo.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            titulo.setTextFill(Color.WHITE); // Blanco para contrastar con el fondo
            titulo.setWrapText(true);
            titulo.setMaxWidth(180);

            Label director = new Label("Director: " + pelicula.getDirector());
            director.setTextFill(Color.WHITE);

            Label resumen = new Label("Resumen: " + pelicula.getResumen());
            resumen.setTextFill(Color.WHITE);

            Label valoracion = new Label("Valoración: " + pelicula.getValoracion());
            valoracion.setTextFill(Color.WHITE);

            Label disponible = new Label("Disponible: " + pelicula.getDisponible());
            disponible.setTextFill(Color.WHITE);

            ArrayList<Alquiler> alquileres = new ArrayList<>();
            try {
                alquileres = alquilerDao.obtenerTodos();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS); // Crece para llenar el espacio

            Button btnAlquilar = new Button("Alquilar Película");
            btnAlquilar.setStyle(
                    "-fx-text-fill: #7B2CC9;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-radius: 25;"
            );

            for (Alquiler a : alquileres){
                if (a.getIdPelicula() == pelicula.getIdPelicula() && a.getIdUsuario() == usuarioActual.getIdUsuario()){
                    btnAlquilar.setText("Ya alquilada");
                    btnAlquilar.setStyle(
                            "-fx-text-fill: #7B2CC9;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-background-radius: 25;" +
                                    "-fx-cursor: default;"
                    );
                    btnAlquilar.setDisable(true);
                    break; // Sale del bucle una vez encontrado
                }
            }
            btnAlquilar.setOnAction(actionEvent -> {

                Date fecha_alquiler = Date.valueOf(LocalDate.now());
                String fechaParseada = fecha_alquiler.toString();

                Alquiler alquiler = new Alquiler(usuarioActual.getIdUsuario(), pelicula.getIdPelicula(), fechaParseada);
                try {
                    alquilerDao.crear(alquiler);
                    peliculaDao.actualizarDisponibilidad(pelicula, 1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                btnAlquilar.setDisable(true);
                cargarPeliculasParaAlquiler();
            }
            );
            btnAlquilar.setPrefWidth(180);

            LinearGradient btnGradient = new LinearGradient(
                    0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#dd4698")),
                    new Stop(1, Color.web("#7b2cc9"))
            );
            btnAlquilar.setBackground(new Background(
                    new BackgroundFill(btnGradient, new CornerRadii(8), Insets.EMPTY)
            ));

            box.getChildren().addAll(img, titulo, director, resumen, valoracion, disponible, spacer, btnAlquilar);

            boxs.add(box);
        }

        return boxs;
    }

    private void mostrarPeliculas(ArrayList<VBox> peliculas) {
        gridPeliculas.getChildren().clear();
        int col = 0;
        int row = 0;

        final int COLUMNAS_POR_FILA = 3;

        for (VBox p : peliculas) {
            gridPeliculas.add(p, col, row);
            col++;
            if (col >= COLUMNAS_POR_FILA) {
                col = 0;
                row++;
            }
        }
    }

    private void volverAlPanelUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            Scene scene = new Scene(loader.load());
            vistaPanelUsuario controller = loader.getController();
            controller.setUsuario(usuarioActual);
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}