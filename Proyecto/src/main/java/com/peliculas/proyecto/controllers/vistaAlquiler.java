package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.*;
import com.peliculas.proyecto.dto.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de la vista de alquiler de películas.
 * Permite al usuario visualizar las películas disponibles,
 * marcarlas como favoritas y realizar el proceso de alquiler.
 *
 * @author Kevin Mejías
 */
public class vistaAlquiler {

    @FXML private Button btnVolver;
    @FXML private ScrollPane scrollPeliculas;
    @FXML private GridPane gridPeliculas;

    private PeliculaFavoritaDao peliculaFavoritaDao = new PeliculaFavoritaDao();
    private ListaDao listaDao = new ListaDao();
    private ListaPeliculaDao listaPeliculaDao = new ListaPeliculaDao();
    private AlquilerDao alquilerDao = new AlquilerDao();
    private Usuario usuarioActual;

    /**
     * Recibe el usuario actual desde la vista del panel de usuario
     * y carga las películas disponibles para alquiler.
     *
     * @param usuario Usuario que ha iniciado sesión
     * @author Kevin Mejías
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarPeliculasParaAlquiler();
    }

    /**
     * Método de inicialización de la vista.
     * Asigna la acción al botón de volver.
     *
     * @author Kevin Mejías
     */
    @FXML
    public void initialize() {
        btnVolver.setOnAction(e -> volverAlPanelUsuario());
    }

    /**
     * Carga las películas disponibles para alquiler desde la base de datos
     * y las muestra en la vista.
     *
     * @author Kevin Mejías
     */
    public void cargarPeliculasParaAlquiler() {
        ArrayList<Pelicula> peliculasDisponibles = PeliculasDisponiblesDao.obtenerPeliculasDispos();
        ArrayList<VBox> cards = crearTarjetasAlquiler(peliculasDisponibles);
        mostrarPeliculas(cards);
    }

    /**
     * Crea tarjetas gráficas para las películas disponibles para alquiler.
     * Cada tarjeta contiene información, control de favoritos y opción de alquiler.
     *
     * @param p Lista de películas disponibles
     * @return ArrayList de VBox con las tarjetas formateadas
     * @author Kevin Mejías
     */
    //Método original de Arturo pero modificado
    public ArrayList<VBox> crearTarjetasAlquiler(ArrayList<Pelicula> p) {

        ArrayList<VBox> boxs = new ArrayList<>();

        ArrayList<PeliculaFavorita> infoFavs =
                peliculaFavoritaDao.mostrarFavoritosPorUsuario(usuarioActual.getIdUsuario());

        for (Pelicula pelicula : p) {

            VBox box = new VBox(5);
            box.setPrefWidth(180);

            LinearGradient gradient = new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#a34fb0")),
                    new Stop(1, Color.web("#7b2cc9"))
            );
            box.setBackground(new Background(
                    new BackgroundFill(gradient, new CornerRadii(12), Insets.EMPTY)
            ));
            box.setPadding(new Insets(10));
            box.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");

            ImageView img;
            try {
                img = new ImageView(new Image(
                        "https://image.tmdb.org/t/p/w500" + pelicula.getPathBanner(), true
                ));
            } catch (Exception e) {
                img = new ImageView();
            }
            img.setFitWidth(180);
            img.setFitHeight(250);
            img.setPreserveRatio(true);

            Label titulo = new Label(pelicula.getTitulo());
            titulo.setStyle("-fx-font-weight: bold;");
            titulo.setTextFill(Color.WHITE);
            titulo.setWrapText(true);

            Label director = new Label("Director: " + pelicula.getDirector());
            director.setTextFill(Color.WHITE);

            Label resumen = new Label("Resumen: " + pelicula.getResumen());
            resumen.setTextFill(Color.WHITE);

            Label valoracion = new Label("Valoración: " + pelicula.getValoracion());
            valoracion.setTextFill(Color.WHITE);

            Label disponible = new Label("Disponible: " + pelicula.getDisponible());
            disponible.setTextFill(Color.WHITE);

            Label precio = new Label("Precio: " + pelicula.getPrecio() + " €");
            precio.setTextFill(Color.PINK);
            precio.setStyle("-fx-font-weight: bold;");

            Region spacer1 = new Region();
            VBox.setVgrow(spacer1, Priority.ALWAYS);

            boolean esFavorita = false;
            for (PeliculaFavorita pFaf : infoFavs){
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

            final boolean[] estadoFavorito = { esFavorita };

            star.setOnMouseClicked(event -> {
                event.consume();

                ArrayList<Lista> listasUsuario = new ArrayList<>();
                try {
                    listasUsuario = listaDao.obtenerPorNombreUsuario(usuarioActual.getNombreUsuario());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                boolean creada = false;
                for (Lista lista : listasUsuario){
                    if (lista.getNombreLista()
                            .equalsIgnoreCase("Películas Favoritas - " + usuarioActual.getNombreUsuario())){
                        creada = true;
                        break;
                    }
                }

                Lista listaFav;
                if (!creada){
                    listaFav = new Lista("Películas Favoritas - " + usuarioActual.getNombreUsuario(), usuarioActual);
                } else{
                    Lista listaFavoritos;
                    try {
                        listaFavoritos = listaDao.encontrarPorNombre(
                                usuarioActual.getIdUsuario(),
                                "Películas Favoritas - " + usuarioActual.getNombreUsuario()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

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

            Button btnAlquilar = new Button("Alquilar Película");
            btnAlquilar.setPrefWidth(180);
            btnAlquilar.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            LinearGradient btnGradient = new LinearGradient(
                    0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#dd4698")),
                    new Stop(1, Color.web("#7b2cc9"))
            );
            btnAlquilar.setBackground(new Background(
                    new BackgroundFill(btnGradient, new CornerRadii(8), Insets.EMPTY)
            ));

            try {
                for (Alquiler a : alquilerDao.obtenerTodos()) {
                    if (a.getIdPelicula() == pelicula.getIdPelicula()
                            && a.getIdUsuario() == usuarioActual.getIdUsuario()) {
                        btnAlquilar.setText("Ya alquilada");
                        btnAlquilar.setDisable(true);
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            btnAlquilar.setOnAction(e -> {
                e.consume();

                try {
                    ArrayList<Alquiler> alquileresUsuario =
                            alquilerDao.obtenerPorUsuario(usuarioActual.getIdUsuario());

                    long ahora = System.currentTimeMillis();
                    alquileresUsuario.removeIf(a ->
                            a.getFechaDevolucion() != null &&
                                    a.getFechaDevolucion().getTime() <= ahora
                    );

                    if (alquileresUsuario.size() >= 3) {
                        mostrarAlerta(
                                Alert.AlertType.ERROR,
                                "Alquiler máximo alcanzado",
                                "Elimina una de tus préstamos para alquilar otra."
                        );
                        return;
                    }

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/vistas/vistaPayment.fxml")
                    );
                    Scene scene = new Scene(loader.load());

                    vistaPayment controller = loader.getController();
                    controller.setUsuario(usuarioActual);
                    controller.setPelicula(pelicula);
                    controller.cargarDatos();

                    Stage stage = (Stage) btnVolver.getScene().getWindow();
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();

                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                }
            });

            box.setOnMouseClicked(e -> abrirCartaPelicula(pelicula));

            box.getChildren().addAll(
                    img, titulo, director, resumen, valoracion,
                    disponible, precio, spacer1, star, btnAlquilar
            );
            boxs.add(box);
        }

        return boxs;
    }

    /**
     * Organiza las tarjetas de películas dentro del GridPane.
     *
     * @param peliculas Lista de tarjetas a mostrar
     * @author Kevin Mejías
     */
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

    /**
     * Vuelve al panel principal del usuario manteniendo su sesión.
     *
     * @author Kevin Mejías
     */
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

    /**
     * Abre una ventana emergente con la información detallada de la película.
     *
     * @param pelicula Película seleccionada
     * @author Kevin Mejías
     */
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

    /**
     * Muestra una alerta personalizada con estilos CSS según el tipo de mensaje.
     * Se utiliza para informar al usuario de errores, avisos o mensajes informativos.
     *
     * @param tipo Tipo de alerta (ERROR, INFORMATION, etc.)
     * @param titulo Título que se mostrará en la ventana
     * @param mensaje Mensaje principal de la alerta
     * @author Kevin Mejías
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        switch (tipo) {
            case ERROR:
                alert.getDialogPane().getStyleClass().add("alert-error");
                break;
            case INFORMATION:
                alert.getDialogPane().getStyleClass().add("alert-info");
                break;
            default:
                alert.getDialogPane().getStyleClass().add("alert-info");
                break;
        }
        alert.showAndWait();
    }
}
