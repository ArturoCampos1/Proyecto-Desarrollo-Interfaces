package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.AlquilerDao;
import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dto.Alquiler;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import com.peliculas.proyecto.service.PaymentService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class vistaPayment {

    private Pelicula pelicula;
    private Usuario usuarioActual;

    AlquilerDao alquilerDao = new AlquilerDao();
    PeliculaDao peliculaDao = new PeliculaDao();

    @FXML
    private Button btnVolver;

    @FXML
    private WebView webView;

    @FXML
    private Button btnPagar;

    @FXML
    private ImageView imgBackdrop;

    @FXML
    private Label lblPrecio;

    @FXML
    private Label lblTitulo;

    private final PaymentService paymentService = new PaymentService();

    /**
     * Inicializa la vista de pago.
     * Configura los eventos de los botones y verifica que el WebView
     * esté correctamente inyectado desde el FXML.
     * Este método se ejecuta automáticamente al cargar la vista.
     *
     * @author Arturo Campos
     */
    @FXML
    public void initialize() {

        if (webView == null) {
            System.err.println("ERROR: WebView no se inyectó correctamente desde el FXML");
        }

        btnVolver.setOnAction(actionEvent -> volverAlquiler());
    }
    /**
     * Maneja el proceso de pago de la película seleccionada.
     * Inicia la sesión de pago mediante PaymentService, carga
     * la URL de Stripe Checkout en el WebView y detecta cambios
     * en la URL para determinar si el pago fue exitoso o cancelado.
     * Muestra alertas de éxito o error según corresponda.
     *
     * @author Arturo Campos
     */
    @FXML
    private void handlePagar() {
        try {
            if (webView == null) {
                mostrarError("Error: WebView no está disponible");
                return;
            }

            String checkoutUrl = paymentService.iniciarPago(pelicula.getPrecio());
            if (checkoutUrl == null) {
                mostrarError("No se pudo crear la sesión de pago");
                return;
            }

            WebEngine webEngine = webView.getEngine();

            // Detectar cambios de URL para saber si el pago fue exitoso o cancelado
            webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("URL cambió a: " + newValue);

                if (newValue.contains("localhost/success")) {
                    Platform.runLater(() -> {
                        mostrarExito("¡Pago completado con éxito!");
                        redirigirPrestamos();
                    });
                } else if (newValue.contains("localhost/cancel")) {
                    Platform.runLater(() -> {
                        mostrarError("Pago cancelado");
                    });
                }
            });

            // Cargar la URL de Stripe Checkout
            webEngine.load(checkoutUrl);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error procesando el pago: " + e.getMessage());
        }
    }
    /**
     * Redirige al usuario a la vista de préstamos después de un pago exitoso
     * Crea un registro de alquiler en la base de datos, actualiza la
     * disponibilidad de la película y carga la vista de alquiler con
     * la información del usuario actual.
     *
     * @author Arturo Campos
     */
    private void redirigirPrestamos() {
        // Fecha y hora EXACTAS del alquiler
        Timestamp fechaAlquiler = new Timestamp(System.currentTimeMillis());

        Alquiler alquiler = new Alquiler(
                usuarioActual.getIdUsuario(),
                pelicula.getIdPelicula(),
                fechaAlquiler
        );

        try {
            alquilerDao.crear(alquiler);
            peliculaDao.actualizarDisponibilidad(pelicula, 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaAlquiler.fxml"));
            Scene scene = new Scene(loader.load());
            vistaAlquiler controller = loader.getController();
            controller.setUsuario(usuarioActual);
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve a la vista de alquiler sin procesar un pago.
     * Carga la vista de alquiler y mantiene la información
     * del usuario actual.
     *
     * @author Arturo Campos
     */
    private void volverAlquiler() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaAlquiler.fxml"));
            Scene scene = new Scene(loader.load());
            vistaAlquiler controller = loader.getController();
            controller.setUsuario(usuarioActual);
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Muestra una alerta de error con un mensaje personalizado.
     * Se utiliza para notificar al usuario sobre fallos durante
     * el proceso de pago.
     *
     * @param mensaje Mensaje de error a mostrar en la alerta
     *
     * @author Arturo Campos
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error en el pago");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    /**
     * Muestra una alerta de éxito con un mensaje personalizado.
     * Se utiliza para notificar al usuario que el pago se completó
     * correctamente.
     *
     * @param mensaje Mensaje de éxito a mostrar en la alerta
     *
     * @author Arturo Campos
     */
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText("Pago completado con éxito");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    /**
     * Carga los datos de la película en la vista.
     * <p>
     * Muestra el título, la imagen de fondo y el precio de la película.
     * También inicia el proceso de pago llamando a handlePagar().
     *
     * @author Arturo Campos
     */
    public void cargarDatos() {
        lblTitulo.setText(pelicula.getTitulo());
        imgBackdrop.setImage(new Image("https://image.tmdb.org/t/p/w500" + pelicula.getPathBanner(), true));
        lblPrecio.setText("Precio: " + pelicula.getPrecio() + " €");
        handlePagar();
    }
    /**
     * Establece la película que se va a pagar.
     *
     * @param pelicula Película seleccionada para el pago
     *
     * @author Arturo Campos
     */
    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }
    /**
     * Establece el usuario que está realizando el pago.
     *
     * @param usuario Usuario que va a alquilar la película
     *
     * @author Arturo Campos
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }
}