package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class vistaBuscadorPeliculas {

    TMDBDao tmdbDao = new TMDBDao();

    @FXML
    Button btnVolver;

    @FXML
    Label labelBuscador;

    @FXML
    ComboBox<String> boxFiltros;

    @FXML
    private ScrollPane scrollPeliculas;

    @FXML
    private GridPane gridPeliculas;

    @FXML
    private TextField labelText;

    @FXML
    private Button search;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    ObservableList<String> opciones = FXCollections.observableArrayList(
            "Nombre", "Autor", "Género"
    );

    @FXML
    public void initialize() {
        boxFiltros.setItems(opciones);
        boxFiltros.getSelectionModel().selectFirst();
        search.setOnAction(e -> busqueda(labelText));
        btnVolver.setOnAction(e -> volver());
    }

    @FXML
    private void abrirLanding() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) scrollPeliculas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo abrir la vista principal");
        }
    }

    public void busqueda(TextField labelText){
        gridPeliculas.getChildren().clear();
        String texto = labelText.getText();

        if (boxFiltros.getValue().equalsIgnoreCase("Nombre")){
            Pelicula p = tmdbDao.findByName(texto);
            gridPeliculas.add(returnPeliculaConFormato(p), 1, 1);
        }

        if (boxFiltros.getValue().equalsIgnoreCase("Autor")){
            ArrayList<Pelicula> p = tmdbDao.findByAutor(texto);
            ArrayList<VBox> boxs = returnPeliculaConFormatoArray(p);
            mostrarPeliculas(boxs);
        }

        if (boxFiltros.getValue().equalsIgnoreCase("Género")){
            ArrayList<Pelicula> p = tmdbDao.findByGenre(texto);
            ArrayList<VBox> boxs = returnPeliculaConFormatoArray(p);
            mostrarPeliculas(boxs);
        }
    }

    public VBox returnPeliculaConFormato(Pelicula p) {
        VBox box = new VBox(5);
        box.setPrefWidth(200);

        String baseUrl = "https://image.tmdb.org/t/p/w500";
        String fullUrl = baseUrl + p.getPathBanner();

        ImageView img;
        try {
            img = new ImageView(new Image(fullUrl, true));
        } catch (IllegalArgumentException e) {
            img = new ImageView();
        }

        img.setFitWidth(180);
        img.setFitHeight(250);
        img.setPreserveRatio(true);

        Label titulo = new Label(p.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        titulo.setTextFill(Color.WHITE);
        titulo.setWrapText(true);
        titulo.setMaxWidth(180);

        Label director = new Label("Director: " + p.getDirector());
        director.setTextFill(Color.WHITE);

        Label resumen = new Label("Resumen: " + p.getResumen());
        resumen.setTextFill(Color.WHITE);

        Label anioSalida = new Label("Año de salida: " + p.getAnioSalida());
        anioSalida.setTextFill(Color.WHITE);

        Label valoracion = new Label("Valoración: " + p.getValoracion() + "/5");
        valoracion.setTextFill(Color.WHITE);

        box.getChildren().addAll(img, titulo, director, resumen, anioSalida, valoracion);

        LinearGradient gradient = new LinearGradient(
                0, 0,
                1, 1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#a34fb0")),
                new Stop(1, Color.web("#7b2cc9"))
        );

        box.setBackground(new Background(
                new BackgroundFill(gradient, new CornerRadii(8), Insets.EMPTY)
        ));
        box.setPadding(new Insets(10));

        return box;
    }

    public ArrayList<VBox> returnPeliculaConFormatoArray(ArrayList<Pelicula> p) {
        ArrayList<VBox> boxs = new ArrayList<>();

        for (Pelicula pelicula : p) {
            VBox box = new VBox(5);
            box.setPrefWidth(200);

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
            titulo.setTextFill(Color.WHITE);
            titulo.setWrapText(true);
            titulo.setMaxWidth(180);

            Label director = new Label("Director: " + pelicula.getDirector());
            director.setTextFill(Color.WHITE);

            Label resumen = new Label("Resumen: " + pelicula.getResumen());
            resumen.setTextFill(Color.WHITE);

            Label anioSalida = new Label("Año de salida: " + pelicula.getAnioSalida());
            anioSalida.setTextFill(Color.WHITE);

            Label valoracion = new Label("Valoración: " + pelicula.getValoracion());
            valoracion.setTextFill(Color.WHITE);

            box.getChildren().addAll(img, titulo, director, resumen, anioSalida, valoracion);

            LinearGradient gradient = new LinearGradient(
                    0, 0,
                    1, 1,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#a34fb0")),
                    new Stop(1, Color.web("#7b2cc9"))
            );

            box.setBackground(new Background(
                    new BackgroundFill(gradient, new CornerRadii(8), Insets.EMPTY)
            ));
            box.setPadding(new Insets(10));

            boxs.add(box);
        }

        return boxs;
    }

    private void mostrarPeliculas(ArrayList<VBox> peliculas) {
        gridPeliculas.getChildren().clear();
        int col = 0;
        int row = 0;
        for (VBox p : peliculas) {
            gridPeliculas.add(p, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader;

            if (usuario != null) {
                loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            }

            Parent root = loader.load();

            if (usuario != null) {
                Object controller = loader.getController();
                try {
                    controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, usuario);
                } catch (Exception ignored) {}
            }

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            mostrarError("No se pudo volver a la pantalla anterior");
        }
    }
}
