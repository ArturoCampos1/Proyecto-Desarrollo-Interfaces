package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class vistaMain {

    @FXML
    private Pane paneBusqueda;
    @FXML
    private Label lblBienvenida;

    private Usuario usuario;

    /**
     * Inicializa la vista principal.
     * Configura el evento de clic en la tarjeta de búsqueda para abrir
     * el buscador de películas.
     * @author Arturo Campos
     */
    @FXML
    private void initialize() {
        // Click en la tarjeta de búsqueda abre el buscador
        if (paneBusqueda != null) {
            paneBusqueda.setOnMouseClicked(event -> abrirVentana());
        }
    }
    /**
     * Abre la ventana del buscador de películas.
     * Carga la vista FXML del buscador y reemplaza la escena actual.
     * Muestra una alerta si ocurre un error al cargar la vista.
     * @author Arturo Campos
     */
    private void abrirVentana() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaBuscadorPeliculas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el buscador");
        }
    }
    /**
     * Abre la ventana de inicio de sesión de usuarios.
     * Carga la vista FXML correspondiente y reemplaza la escena actual.
     * Muestra una alerta si ocurre un error al cargar la vista.
     * @author Arturo Campos
     */
    @FXML
    private void abrirVistaInicioSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaInicioSesion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de inicio de sesión");
        }
    }
    /**
     * Abre la ventana de inicio de sesión para administradores.
     * Carga la vista FXML correspondiente y reemplaza la escena actual.
     * Muestra una alerta si ocurre un error al cargar la vista.
     * @author Arturo Campos
     */
    @FXML
    private void abrirAccesoAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaInicioSesionAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de inicio de sesión admin");
        }
    }
    /**
     * Abre la ventana de registro de nuevos usuarios.
     * Carga la vista FXML correspondiente y reemplaza la escena actual.
     * Muestra una alerta si ocurre un error al cargar la vista.
     * @author Arturo Campos
     */
    @FXML
    private void abrirVistaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaRegistro.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) paneBusqueda.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista de registro " + e.getMessage());
        }
    }
    /**
     * Establece el usuario actual que ha iniciado sesión.
     * Actualiza la etiqueta de bienvenida si el usuario no es nulo.
     *
     * @param usuario Usuario autenticado
     * @author Iker Sillero
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblBienvenida != null && usuario != null) {
            lblBienvenida.setText("Bienvenida " + usuario.getNombreUsuario());
        }
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