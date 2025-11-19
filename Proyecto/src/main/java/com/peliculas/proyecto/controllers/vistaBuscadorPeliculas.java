package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dto.Pelicula;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

public class vistaBuscadorPeliculas {

    TMDBDao tmdbDao = new TMDBDao();

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

    ObservableList<String> opciones = FXCollections.observableArrayList(
            "Nombre", "Autor", "Género"
    );

    @FXML
    public void initialize() {
        boxFiltros.setItems(opciones);
        boxFiltros.getSelectionModel().selectFirst(); // Selecciona el primer item

        search.setOnAction(e -> busqueda(labelText));

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
        VBox box = new VBox(5); // 5px de spacing
        box.setPrefWidth(200);

        // Construir URL completa de TMDb
        String baseUrl = "https://image.tmdb.org/t/p/w500"; // w500 es tamaño de ejemplo
        String fullUrl = baseUrl + p.getPathBanner(); // p.getPathBanner() debe ser "/abcd1234.jpg"

        ImageView img;
        try {
            img = new ImageView(new Image(fullUrl, true)); // true = carga en background
        } catch (IllegalArgumentException e) {
            // Fallback si la imagen no se carga
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


        box.getChildren().addAll(img, titulo, director, resumen, anioSalida, valoracion); // añadir la imagen también

        LinearGradient gradient = new LinearGradient(
                0, 0, // startX, startY (0%,0%)
                1, 1, // endX, endY (100%,100%)
                true, // proportional
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

        ArrayList<VBox> boxs = new ArrayList<VBox>();

        for (int i = 0; i < p.size(); i++){

            VBox box = new VBox(5);
            box.setPrefWidth(200);

            // Construir URL completa de TMDb
            String baseUrl = "https://image.tmdb.org/t/p/w500"; // w500 es tamaño de ejemplo
            String fullUrl = baseUrl + p.get(i).getPathBanner(); // p.getPathBanner() debe ser "/abcd1234.jpg"

            ImageView img;
            try {
                img = new ImageView(new Image(fullUrl, true)); // true = carga en background
            } catch (IllegalArgumentException e) {
                // Fallback si la imagen no se carga
                img = new ImageView();
            }

            img.setFitWidth(180);
            img.setFitHeight(250);
            img.setPreserveRatio(true);

            Label titulo = new Label(p.get(i).getTitulo());
            titulo.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            titulo.setTextFill(Color.WHITE);
            titulo.setWrapText(true);
            titulo.setMaxWidth(180);
            Label director = new Label("Director: " + p.get(i).getDirector());
            director.setTextFill(Color.WHITE);
            Label resumen = new Label("Resumen: " + p.get(i).getResumen());
            resumen.setTextFill(Color.WHITE);
            Label anioSalida = new Label("Año de salida: " + p.get(i).getAnioSalida());
            anioSalida.setTextFill(Color.WHITE);
            Label valoracion = new Label("Valoración: " + p.get(i).getValoracion());
            valoracion.setTextFill(Color.WHITE);

            box.getChildren().addAll(img, titulo, director, resumen, anioSalida, valoracion); // añadir la imagen también

            LinearGradient gradient = new LinearGradient(
                    0, 0, // startX, startY (0%,0%)
                    1, 1, // endX, endY (100%,100%)
                    true, // proportional
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
        gridPeliculas.getChildren().clear(); // Limpiar GridPane
        int col = 0;
        int row = 0;
        for (VBox p : peliculas) {
            gridPeliculas.add(p, col, row);
            col++;
            if (col == 3) { // 3 por fila
                col = 0;
                row++;
            }
        }
    }
}
