package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

/**
 * Controlador del panel principal del usuario.
 * Permite acceder a las distintas funcionalidades de la aplicación,
 * como perfil, buscador, listas, listas públicas, alquiler y préstamos.
 *
 * @author Iker Sillero y Kevin Mejías
 */
public class vistaPanelUsuario {

    @FXML private ImageView iconUser;
    @FXML private ImageView iconSearch;

    @FXML private Pane paneUser;
    @FXML private Pane paneSearch;

    @FXML private StackPane cardListas;
    @FXML private StackPane cardListasPublicas;
    @FXML private StackPane cardPrestamos;
    @FXML private StackPane cardAlquilar;

    @FXML private Button btnVolver;

    @FXML private Label lblUsername;
    @FXML private Label lblCorreo;

    private Usuario usuario;

    /**
     * Recibe el usuario actual y muestra su información básica
     * en el panel principal.
     *
     * @param usuario Usuario que ha iniciado sesión
     * @author Iker Sillero
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (lblUsername != null && usuario != null) {
            lblUsername.setText(usuario.getNombreUsuario().toUpperCase());
            lblCorreo.setText(usuario.getCorreo());
        }
    }

    /**
     * Abre la vista del perfil del usuario.
     *
     * @author Iker Sillero
     */
    private void abrirVistaPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPerfilUsuario.fxml"));
            Scene scene = new Scene(loader.load());

            vistaPerfilUsuario controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) iconUser.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la vista del buscador de películas.
     *
     * @author Iker Sillero
     */
    private void abrirBuscador() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaBuscadorPeliculas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaBuscadorPeliculas controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) iconSearch.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la vista de listas privadas del usuario.
     *
     * @author Iker Sillero
     */
    private void abrirVistaListas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaListas controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) cardListas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la vista de listas públicas.
     *
     * @author Iker Sillero
     */
    private void abrirVistaListasPublicas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListasPublicas.fxml"));
            Scene scene = new Scene(loader.load());

            vistaListasPublicas controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) cardListasPublicas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la vista de alquiler de películas.
     *
     * @author Kevin Mejías
     */
    private void abrirVistaAlquilar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaAlquiler.fxml"));
            Scene scene = new Scene(loader.load());

            vistaAlquiler controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) iconSearch.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la vista de préstamos del usuario.
     *
     * @author Kevin Mejías
     */
    private void abrirVistaPrestamos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPrestamos.fxml"));
            Scene scene = new Scene(loader.load());

            vistaPrestamos controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) iconSearch.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve a la vista principal de la aplicación.
     *
     * @param event Evento del botón volver
     * @author Iker Sillero
     */
    private void volverAMain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa el panel del usuario asignando eventos
     * y cursores a los distintos componentes de la interfaz.
     *
     * @author Iker Sillero
     */
    @FXML
    private void initialize() {

        iconUser.setOnMouseClicked(e -> abrirVistaPerfil());
        iconSearch.setOnMouseClicked(e -> abrirBuscador());

        paneUser.setOnMouseClicked(e -> abrirVistaPerfil());
        paneSearch.setOnMouseClicked(e -> abrirBuscador());

        cardListas.setOnMouseClicked(e -> abrirVistaListas());
        cardListasPublicas.setOnMouseClicked(e -> abrirVistaListasPublicas());
        cardPrestamos.setOnMouseClicked(e -> abrirVistaPrestamos());
        cardAlquilar.setOnMouseClicked(e -> abrirVistaAlquilar());

        iconUser.setCursor(Cursor.HAND);
        iconSearch.setCursor(Cursor.HAND);
        paneUser.setCursor(Cursor.HAND);
        paneSearch.setCursor(Cursor.HAND);

        cardListas.setCursor(Cursor.HAND);
        cardListasPublicas.setCursor(Cursor.HAND);
        cardPrestamos.setCursor(Cursor.HAND);
        cardAlquilar.setCursor(Cursor.HAND);
        btnVolver.setCursor(Cursor.HAND);

        btnVolver.setOnAction(this::volverAMain);
    }
}
