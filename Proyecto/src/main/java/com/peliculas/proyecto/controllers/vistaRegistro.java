package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class vistaRegistro {

    @FXML
    private Pane paneRegistro;
    @FXML
    private TextField campoNombre;       // Nombre Usuario
    @FXML
    private TextField campoCorreo;       // Correo
    @FXML
    private TextField campoTelefono;     // Número TLF
    @FXML
    private PasswordField campoContraseña; // Contraseña
    @FXML
    private Button botonRegistro;
    @FXML
    private Button botonVolver;

    @FXML
    private void initialize() {
        botonRegistro.setOnMouseClicked(event -> procesarRegistro());
        botonVolver.setOnMouseClicked(event -> volverAMain());
    }

    private void procesarRegistro() {
        System.out.println("Click en Crear Cuenta");

        String nombreUsuario = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String contrasena = campoContraseña.getText().trim();

        // ✅ Validación de campos vacíos
        if (nombreUsuario.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Todos los campos son obligatorios");
            return;
        }

        // ✅ Validación de correo
        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo inválido", "Introduce un correo válido");
            return;
        }

        // ✅ Validación de teléfono (solo números)
        if (!telefono.matches("\\d+")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido", "El teléfono solo puede contener números");
            return;
        }

        // ✅ Validación de longitud de contraseña
        if (contrasena.length() < 4) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña débil", "La contraseña debe tener al menos 4 caracteres");
            return;
        }

        try {
            UsuarioDao usuarioDao = UsuarioDao.getInstance();

            // ✅ Comprobar si el usuario ya existe
            if (usuarioDao.existeUsuario(nombreUsuario)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Usuario existente", "Ese nombre de usuario ya está registrado");
                return;
            }

            // ✅ Crear usuario
            Usuario nuevoUsuario = new Usuario(nombreUsuario, correo, telefono, contrasena);

            // ✅ Insertar en BD
            usuarioDao.insert(nuevoUsuario);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "Usuario creado correctamente");
            volverAMain();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudo registrar el usuario:\n" + e.getMessage());
        }
    }


    private void volverAMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneRegistro.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver a la pantalla principal");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
