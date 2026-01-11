package com.peliculas.proyecto.controllers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.peliculas.proyecto.dao.AlquilerDao;
import com.peliculas.proyecto.dao.PeliculaFavoritaDao;
import com.peliculas.proyecto.dto.Alquiler;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.PeliculaFavorita;
import com.peliculas.proyecto.dto.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class vistaPrestamos {

    @FXML private Button btnVolver;
    @FXML private ScrollPane scrollPeliculas;
    @FXML private GridPane gridPeliculas;
    @FXML private Text textoInfo;

    private PeliculaFavoritaDao peliculaFavoritaDao = new PeliculaFavoritaDao();
    private Usuario usuarioActual;

    private ArrayList<Alquiler> alquileresUsuario;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        eliminarAlquileresCaducados();
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

        ArrayList<Alquiler> alquileres;
        try {
            alquileres = AlquilerDao.getInstance().obtenerPorUsuario(usuarioActual.getIdUsuario());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        if (peliculasAlquiladas.isEmpty()){
            textoInfo.setText("NO TIENES PELICULAS ALQUILADAS");
            textoInfo.setUnderline(true);
        } else{
            ArrayList<HBox> cards = crearTarjetasAlquiladas(peliculasAlquiladas, alquileres);
            mostrarPeliculas(cards);
        }
    }

    public ArrayList<HBox> crearTarjetasAlquiladas(ArrayList<Pelicula> p, ArrayList<Alquiler> alquileres)
    {
        ArrayList<HBox> boxs = new ArrayList<>();

        Map<Integer, Alquiler> alquilerPorPelicula = new HashMap<>();

        for (Alquiler a : alquileres) {
            alquilerPorPelicula.put(a.getIdPelicula(), a);
        }

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
            resumen.setMaxWidth(200);
            resumen.setMaxHeight(75);
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
            Label alq = new Label("PELÍCULA\nALQUILADA");
            alq.setTextAlignment(TextAlignment.CENTER);
            alq.setTextFill(Color.YELLOW);
            alq.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Region spacer1 = new Region();
            spacer1.setPrefHeight(10);

            // Obtener el alquiler correspondiente
            final Alquiler alquiler = alquilerPorPelicula.get(pelicula.getIdPelicula());

            Button btnGestion = new Button("Eliminar\nalquiler");
            btnGestion.setTextAlignment(TextAlignment.CENTER);
            btnGestion.setStyle("-fx-background-color: white; -fx-text-fill: #ff6045; -fx-font-weight: bold;");
            btnGestion.setOnAction(e -> {
                e.consume();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar eliminación");
                alert.setHeaderText("¿Eliminar alquiler?");
                alert.setContentText(
                        "Estás a punto de eliminar el alquiler de:\n\n" +
                                pelicula.getTitulo() +
                                "\n\n¿Deseas continuar?"
                );

                // Aplicar estilos
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("/styles.css").toExternalForm()
                );
                alert.getDialogPane().getStyleClass().add("alert-info");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            // Eliminar alquiler
                            AlquilerDao.getInstance().eliminar(alquiler);

                            // Recargar vista
                            cargarPeliculasPrestadas();

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            });

            Label lblTiempo = new Label();
            if (alquiler != null && alquiler.getFechaDevolucion() != null) {
                lblTiempo.setText(tiempoRestante(alquiler.getFechaDevolucion()));
            } else {
                lblTiempo.setText("");
            }
            lblTiempo.setTextFill(Color.web("#FFD700"));
            lblTiempo.setWrapText(true);
            lblTiempo.setPadding(new Insets(7, 10, -25, 0));

            VBox labelBox = new VBox(3, alq, spacer1, btnGestion, lblTiempo);
            labelBox.setAlignment(Pos.CENTER);
            labelBox.setPrefWidth(150);

            // ====== ESPACIADOR PARA QUE LA ESTRELLA QUDE AL EXTREMO ======
            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);

            // ====== ARMAR HBOX ======
            box.getChildren().addAll(img, texto, spacer2, labelBox, star);

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

    private String tiempoRestante(Timestamp fechaDevolucion) {

        long ahora = System.currentTimeMillis();
        long fin = fechaDevolucion.getTime();

        long dif = fin - ahora;

        long minutos = dif / (1000 * 60);
        long horas = minutos / 60;
        long dias = horas / 24;

        horas %= 24;
        minutos %= 60;

        if (dias > 0) {
            return "⏳ Quedan " + dias + " días " + horas + " h";
        } else if (horas > 0) {
            return "⏳ Quedan " + horas + " h " + minutos + " min";
        } else {
            return "⏳ Quedan " + minutos + " min";
        }
    }

    private void eliminarAlquileresCaducados() {
        try {
            ArrayList<Alquiler> alquileres = AlquilerDao.getInstance()
                    .obtenerPorUsuario(usuarioActual.getIdUsuario());

            for (Alquiler a : alquileres) {
                Timestamp ahora = new Timestamp(System.currentTimeMillis());
                if (a.getFechaDevolucion() != null && ahora.after(a.getFechaDevolucion())) {
                    // El alquiler ya ha caducado, se elimina
                    AlquilerDao.getInstance().eliminar(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}