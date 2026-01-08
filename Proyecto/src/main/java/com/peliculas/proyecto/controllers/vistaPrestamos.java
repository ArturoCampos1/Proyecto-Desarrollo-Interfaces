package com.peliculas.proyecto.controllers;

import java.sql.SQLException;
import java.util.ArrayList;

import com.peliculas.proyecto.dao.AlquilerDao;
import com.peliculas.proyecto.dao.PeliculaFavoritaDao;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.PeliculaFavorita;
import com.peliculas.proyecto.dto.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

public class vistaPrestamos {

    @FXML private Button btnVolver;
    @FXML private ScrollPane scrollPeliculas;
    @FXML private GridPane gridPeliculas;

    private PeliculaFavoritaDao peliculaFavoritaDao = new PeliculaFavoritaDao();
    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarPeliculasPrestadas();
    }

    @FXML
    public void initialize() {
        btnVolver.setOnAction(e -> volverAlPanelUsuario());
        gridPeliculas.setMaxWidth(Double.MAX_VALUE);
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

    public void cargarPeliculasPrestadas() {
        ArrayList<Pelicula> peliculasAlquiladas;
        try {
            peliculasAlquiladas = AlquilerDao.getInstance().obtenerPeliculasAlquiladasPorUsuario(usuarioActual.getIdUsuario());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ArrayList<HBox> cards = crearTarjetasAlquiladas(peliculasAlquiladas);
        mostrarPeliculas(cards);
    }

    public ArrayList<HBox> crearTarjetasAlquiladas(ArrayList<Pelicula> p) {
        ArrayList<HBox> boxs = new ArrayList<>();

        // Cargar favoritos UNA sola vez
        ArrayList<PeliculaFavorita> infoFavs = peliculaFavoritaDao.mostrarFavoritosPorUsuario(usuarioActual.getIdUsuario());

        for (Pelicula pelicula : p) {
            HBox box = new HBox(15);
            box.setCursor(Cursor.HAND);
            box.setPadding(new Insets(15));
            box.setMaxWidth(Double.MAX_VALUE);

            // ====== FONDO ======
            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#a34fb0")),
                    new Stop(1, Color.web("#7b2cc9"))
            );
            box.setBackground(new Background(
                    new BackgroundFill(gradient, new CornerRadii(12), Insets.EMPTY)
            ));
            box.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

            // ====== IMAGEN ======
            String baseUrl = "https://image.tmdb.org/t/p/w500";
            ImageView img;
            try {
                img = new ImageView(new Image(baseUrl + pelicula.getPathBanner(), true));
            } catch (IllegalArgumentException e) {
                img = new ImageView();
            }
            img.setFitWidth(220);
            img.setFitHeight(250);
            img.setPreserveRatio(true);

            // ====== TEXTOS ======
            Label titulo = new Label(pelicula.getTitulo());
            titulo.setStyle("-fx-font-weight: bold;");
            titulo.setTextFill(Color.WHITE);
            titulo.setWrapText(true);

            Label director = new Label("Director: " + pelicula.getDirector());
            director.setTextFill(Color.WHITE);

            Label resumen = new Label(pelicula.getResumen());
            resumen.setTextFill(Color.web("#F0F0F0"));
            resumen.setWrapText(true);
            resumen.setMaxWidth(260);
            resumen.setMaxHeight(60);
            resumen.setStyle("""
            -fx-font-size: 13px;
            -fx-text-overrun: ellipsis;
               """);


            Label valoracion = new Label("Valoración: " + pelicula.getValoracion());
            valoracion.setTextFill(Color.WHITE);

            VBox texto = new VBox(5, titulo, director, resumen, valoracion);
            texto.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(texto, Priority.ALWAYS);

            // ====== ESTRELLA FAVORITO ======
            boolean esFavorita = false;
            for (PeliculaFavorita pFaf : infoFavs) {
                if (pelicula.getIdPelicula() == pFaf.getId_pelicula()) {
                    esFavorita = true;
                    break;
                }
            }

            ImageView starIcon = new ImageView(new Image(
                    getClass().getResource(
                            esFavorita ? "/img/starRellena.png" : "/img/starVacia.png"
                    ).toExternalForm()
            ));
            starIcon.setFitWidth(24);
            starIcon.setFitHeight(24);
            starIcon.setPreserveRatio(true);

            Button star = new Button();
            star.setGraphic(starIcon);
            star.setStyle("-fx-background-color: transparent;");
            star.setPrefSize(30, 30);

            final boolean[] estadoFavorito = {esFavorita};

            star.setOnMouseClicked(event -> {
                event.consume();

                if (estadoFavorito[0]) {
                    peliculaFavoritaDao.eliminarPeliculaFav(usuarioActual, pelicula);
                    starIcon.setImage(new Image(
                            getClass().getResource("/img/starVacia.png").toExternalForm()
                    ));
                } else {
                    peliculaFavoritaDao.añadirPeliculaFav(usuarioActual, pelicula);
                    starIcon.setImage(new Image(
                            getClass().getResource("/img/starRellena.png").toExternalForm()
                    ));
                }

                estadoFavorito[0] = !estadoFavorito[0];
            });

            // ====== LABEL DE PELÍCULA ALQUILADA ======
            Label linea1 = new Label("PELÍCULA");
            linea1.setTextFill(Color.YELLOW);
            linea1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label linea2 = new Label("ALQUILADA");
            linea2.setTextFill(Color.YELLOW);
            linea2.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            VBox labelBox = new VBox(2, linea1, linea2);
            labelBox.setAlignment(Pos.CENTER);

            // ====== ESPACIADOR PARA QUE LA ESTRELLA QUDE AL EXTREMO ======
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // ====== ARMAR HBOX ======
            box.getChildren().addAll(img, texto, spacer, star, labelBox);

            // ====== CLICK TARJETA ======
            box.setOnMouseClicked(e -> abrirCartaPelicula(pelicula));

            boxs.add(box);
        }

        return boxs;
    }


    private void mostrarPeliculas(ArrayList<HBox> peliculas) {
        gridPeliculas.getChildren().clear();
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        cc.setFillWidth(true);
        gridPeliculas.getColumnConstraints().add(cc);

        int col = 0;
        int row = 0;

        final int COLUMNAS_POR_FILA = 1;

        for (HBox p : peliculas) {
            HBox.setHgrow(p, Priority.ALWAYS);
            gridPeliculas.add(p, col, row);
            col++;
            if (col >= COLUMNAS_POR_FILA) {
                col = 0;
                row++;
            }
        }
    }

    private void abrirCartaPelicula(Pelicula pelicula) {
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
}