package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    // ✅ Recibe el usuario desde el panel
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarDatos();
    }

    // ✅ Cargar datos del usuario en los campos
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
    }

    // ✅ Guardar cambios en BD
    private void guardarCambios() {

        // Validaciones básicas
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Error", "Nombre y correo son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        // ✅ Actualizar datos en el objeto usuario
        usuario.setNombreUsuario(txtNombre.getText());
        usuario.setCorreo(txtCorreo.getText());
        usuario.setNumTelef(txtTelefono.getText());

        // ✅ Contraseña: solo cambiar si el campo NO está vacío
        if (!txtContrasena.getText().isEmpty()) {
            usuario.setContrasena(txtContrasena.getText());
        }

        // ✅ Guardar en BD
        boolean actualizado = UsuarioDao.getInstance().actualizarUsuario(usuario);

        if (actualizado) {
            mostrarAlerta("Éxito", "Datos actualizados correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudieron guardar los cambios.", Alert.AlertType.ERROR);
        }
    }

    // ✅ Volver al panel usuario manteniendo sesión
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

    // ✅ Método para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
