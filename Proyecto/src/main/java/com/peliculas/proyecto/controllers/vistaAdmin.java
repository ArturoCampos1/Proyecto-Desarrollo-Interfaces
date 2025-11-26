package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class vistaAdmin {

    @FXML
    private ScrollPane scrollPanePerfiles;

    @FXML
    private VBox contenedorUsuarios;

    private UsuarioDao usuarioDao;

    @FXML
    public void initialize() {
        usuarioDao = new UsuarioDao();

        // Estilo del ScrollPane
        scrollPanePerfiles.setStyle("-fx-background: white; -fx-border-color: transparent;");

        cargarUsuarios();
    }

    public void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDao.consultarUsuarios();

            contenedorUsuarios.getChildren().clear();

            // Añadir cada usuario con estilo mejorado
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario usuario = usuarios.get(i);
                HBox fila = crearFilaUsuarioEstilizada(usuario, i);
                contenedorUsuarios.getChildren().add(fila);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError();
        }
    }

    private HBox crearFilaUsuarioEstilizada(Usuario usuario, int index) {
        HBox fila = new HBox(10);
        fila.setPadding(new Insets(12, 15, 12, 15));
        fila.setStyle(
                "-fx-background-color: " + (index % 2 == 0 ? "#f8f9fa" : "white") + ";" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: #e9ecef;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8px;"
        );

        // Icono decorativo
        Label icono = new Label("👤");
        icono.setStyle("-fx-font-size: 16px;");

        // VBox para contener las tres líneas
        VBox vboxInfo = new VBox(4);

        // === LÍNEA 1: Nombre + ID ===
        HBox lineaNombre = new HBox(10);

        Label lblNombre = new Label(usuario.getNombreUsuario());
        lblNombre.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;"
        );

        Label lblId = new Label("(" + usuario.getIdUsuario() + ")");
        lblId.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #7044ea;" +
                        "-fx-font-weight: bold;"
        );

        lineaNombre.getChildren().addAll(lblNombre, lblId);

        // === LÍNEA 2: Correo ===
        Label lblCorreo = new Label("📧 " + usuario.getCorreo());
        lblCorreo.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #6c757d;"
        );

        // === LÍNEA 3: Teléfono ===
        Label lblTelefono = new Label("📱 " + usuario.getNumTelef());
        lblTelefono.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #6c757d;"
        );

        vboxInfo.getChildren().addAll(lineaNombre, lblCorreo, lblTelefono);

        fila.getChildren().addAll(icono, vboxInfo);
        
        return fila;
    }



    private void mostrarError() {
        Label lblError = new Label("❌ Error al cargar usuarios");
        lblError.setStyle(
                "-fx-text-fill: #e74c3c;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 15;" +
                        "-fx-background-color: #fee;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-font-weight: bold;"
        );
        contenedorUsuarios.getChildren().add(lblError);
    }
}
