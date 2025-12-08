package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class vistaPerfilUsuario {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarDatos();
    }

    private void cargarDatos() {
        if (usuario != null) {
            txtNombre.setText(usuario.getNombreUsuario());
            txtCorreo.setText(usuario.getCorreo());
            txtTelefono.setText(usuario.getNumTelef());
        }
    }

    @FXML
    private void initialize() {

        btnVolver.setOnAction(e -> volverAlPanelUsuario());
        btnGuardar.setOnAction(e -> guardarCambios());

        // ✅ Cursor HAND en botones
        btnVolver.setCursor(Cursor.HAND);
        btnGuardar.setCursor(Cursor.HAND);

        // ✅ Botón Guardar morado
        btnGuardar.getStyleClass().add("btnMorado");

        // ✅ Botón Volver también morado (para mantener coherencia visual)
        btnVolver.getStyleClass().add("btnMorado"); // ✅ CAMBIO
    }

    private void guardarCambios() {

        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Nombre y correo son obligatorios.");
            return;
        }

        usuario.setNombreUsuario(txtNombre.getText());
        usuario.setCorreo(txtCorreo.getText());
        usuario.setNumTelef(txtTelefono.getText());

        if (!txtContrasena.getText().isEmpty()) {
            usuario.setContrasena(txtContrasena.getText());
        }

        boolean actualizado = UsuarioDao.getInstance().actualizarUsuario(usuario);

        if (actualizado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos actualizados correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los cambios.");
        }
    }

    private void volverAlPanelUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaPanelUsuario.fxml"));
            Scene scene = new Scene(loader.load());

            vistaPanelUsuario controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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