package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.text.Text;
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
    Text textoInfo;

    @FXML
    ComboBox<String> boxFiltros;

    @FXML
    private ScrollPane scrollPeliculas;

    @FXML
    private GridPane gridPeliculas;

    @FXML
    private TextField labelText;

    @FXML
    private ImageView imgRecarga;

    @FXML
    private Button search;

    @FXML
    private Button btnActualizar;

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

        // Evitar foco en el label por si acaso
        labelText.setFocusTraversable(false);

        imgRecarga.setMouseTransparent(true);

        returnPeliculaConFormatoArray(recarga()); //Random cuando inicializa

        //Texto al etnrar
        if (boxFiltros.getValue().equalsIgnoreCase("Nombre")){
            textoInfo.setMouseTransparent(true); //Labvel invisible
            labelText.setText("");
            textoInfo.setText("Ingresa el título de la película");
        }

        boxFiltros.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            textoInfo.setMouseTransparent(true);
            switch (newValue) {
                case "Nombre":
                    labelText.setText("");
                    textoInfo.setText("Ingresa el título de la película");
                    break;

                case "Autor":
                    labelText.setText("");
                    textoInfo.setText("Ingresa el nombre del autor");
                    break;

                case "Género":
                    labelText.setText("");
                    textoInfo.setText("Ingresa el género");
                    break;
            }
        });

        labelText.setOnMouseClicked(mouseEvent -> {
            textoInfo.setText("");
        });

        search.setOnAction(e -> busqueda(labelText));
        btnVolver.setOnAction(e -> volver());
        btnActualizar.setOnAction(e -> recarga());
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
            ArrayList<Pelicula> p = tmdbDao.findByName(texto);
            ArrayList<VBox> boxs = returnPeliculaConFormatoArray(p);
            mostrarPeliculas(boxs);
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

    public ArrayList<Pelicula> recarga() {
        ArrayList<Pelicula> peliculasTendring = tmdbDao.findTrendingFilms();
        ArrayList<VBox> boxs = returnPeliculaConFormatoArray(peliculasTendring);
        mostrarPeliculas(boxs);
        return peliculasTendring;
    }

    public ArrayList<VBox> returnPeliculaConFormatoArray(ArrayList<Pelicula> peliculas) {
        ArrayList<VBox> boxs = new ArrayList<>();

        for (Pelicula pelicula : peliculas) {
            VBox box = new VBox(5);
            box.setPrefWidth(200);

            ImageView img;
            try {
                if (pelicula.getPathBanner() != null && !pelicula.getPathBanner().isEmpty()) {
                    if (pelicula.getPathBanner().startsWith("/")) {
                        // Es ruta TMDB
                        String baseUrl = "https://image.tmdb.org/t/p/w500";
                        img = new ImageView(new Image(baseUrl + pelicula.getPathBanner(), true));
                    } else if (pelicula.getPathBanner().equals("local_no_image")) {
                        // Imagen local por defecto
                        img = new ImageView(new Image(getClass().getResourceAsStream("/img/noavailable.jpg")));
                    } else {
                        // Cualquier otra ruta absoluta o URL
                        img = new ImageView(new Image(pelicula.getPathBanner(), true));
                    }
                } else {
                    // Fallback seguro
                    img = new ImageView(new Image(getClass().getResourceAsStream("/img/noavailable.jpg")));
                }
            } catch (Exception e) {
                img = new ImageView(new Image(getClass().getResourceAsStream("/img/noavailable.jpg")));
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

            Label generos = new Label("Géneros: " + pelicula.getGenero());
            generos.setTextFill(Color.WHITE);

            Label anioSalida = new Label("Año de salida: " + pelicula.getAnioSalida());
            anioSalida.setTextFill(Color.WHITE);

            Label valoracion = new Label("Valoración: " + pelicula.getValoracion());
            valoracion.setTextFill(Color.WHITE);

            box.setStyle("-fx-cursor: hand;");
            box.getChildren().addAll(img, titulo, director, resumen, generos, anioSalida, valoracion);

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

            box.setOnMouseClicked(event -> {
                abrirCartaPelicula(pelicula);
            });

            boxs.add(box);
        }

        return boxs;
    }

    private void abrirCartaPelicula(Pelicula pelicula) {
        if (usuario == null) {
            mostrarError("Debes iniciar sesión para ver los detalles de la película.");
            return;
        }
        Stage ventana = new Stage();
        ventana.setTitle(pelicula.getTitulo());
        ventana.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-radius: 15; -fx-background-color: linear-gradient(to bottom, #7b2cc9, #a34fb0);");

        HBox imageContainer = new HBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPadding(new Insets(0, 0, 10, 0));

        ImageView imageView;
        try {
            String baseUrl = "https://image.tmdb.org/t/p/w500";
            imageView = new ImageView(new Image(baseUrl + pelicula.getPathBanner(), true));
        } catch (Exception e) {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("/img/noavailable.jpg")));
        }

        imageView.setFitWidth(380);
        imageView.setPreserveRatio(true);

        imageContainer.getChildren().add(imageView);

        Label lblTitulo = new Label(pelicula.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label lblDirector = new Label("Director: " + pelicula.getDirector());
        lblDirector.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Label lblAnio = new Label("Año: " + pelicula.getAnioSalida());
        lblAnio.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Label lblGenero = new Label("Género: " + pelicula.getGenero());
        lblGenero.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Label lblValoracion = new Label("Valoración:");
        lblValoracion.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER);

        double val = pelicula.getValoracion();
        double rounded = Math.round(val * 2) / 2.0;  // redondeo a .0 o .5

        String fileName;
        if (rounded % 1 == 0) {
            fileName = "star_" + (int) rounded + ".png";
        } else {
            fileName = "star_" + ((int) rounded) + "5.png";
        }

        try {
            ImageView starImg = new ImageView(new Image(
                    getClass().getResourceAsStream("/img/" + fileName)
            ));

            starImg.setFitWidth(160);   // ajusta el tamaño si quieres
            starImg.setPreserveRatio(true);

            starsBox.getChildren().add(starImg);

        } catch (Exception e) {
            System.out.println("Error cargando estrellas: " + fileName);
        }

        Label lblValorSmall = new Label("(" + val + ")");
        lblValorSmall.setStyle("-fx-text-fill: #C4C4C4; -fx-font-size: 13px;");
        lblValorSmall.setPadding(new Insets(2, 0, 0, 3));

        HBox valoracionLinea = new HBox(10);
        valoracionLinea.setAlignment(Pos.CENTER_LEFT);
        valoracionLinea.getChildren().addAll(lblValoracion, starsBox, lblValorSmall);

        Label lblResumen = new Label(pelicula.getResumen());
        lblResumen.setWrapText(true);
        lblResumen.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        ScrollPane scrollResumen = new ScrollPane(lblResumen);
        scrollResumen.setFitToWidth(true);
        scrollResumen.setPrefViewportHeight(120);
        scrollResumen.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: white; -fx-text-fill: #7b2cc9; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> ventana.close());

        root.getChildren().addAll(
                imageContainer,
                lblTitulo,
                lblDirector,
                lblAnio,
                lblGenero,
                valoracionLinea,
                scrollResumen,
                btnCerrar
        );

        Scene scene = new Scene(root, 450, 720);
        ventana.setScene(scene);
        ventana.show();
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
