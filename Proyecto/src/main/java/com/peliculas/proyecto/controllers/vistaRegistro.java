package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controlador de la vista de registro de usuarios.
 * Permite crear nuevas cuentas validando los datos introducidos
 * y comprobando duplicados en la base de datos.
 *
 * @author Iker Sillero y Kevin Mejías
 */
public class vistaRegistro {

    @FXML
    private Pane paneRegistro;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoCorreo;
    @FXML
    private TextField campoTelefono;
    @FXML
    private PasswordField campoContraseña;
    @FXML
    private Button botonRegistro;
    @FXML
    private Button botonVolver;

    /**
     * Inicializa la vista de registro.
     * Asigna eventos a los botones, configura el cursor
     * y aplica estilos visuales.
     *
     * @author Iker Sillero
     */
    @FXML
    private void initialize() {
        botonRegistro.setOnMouseClicked(event -> procesarRegistro());
        botonVolver.setOnMouseClicked(event -> volverAMain());

        botonRegistro.setCursor(Cursor.HAND);
        botonVolver.setCursor(Cursor.HAND);

        botonRegistro.getStyleClass().add("btnMorado");
    }

    /**
     * Procesa el registro de un nuevo usuario.
     * Realiza validaciones de los campos, comprueba
     * la existencia de datos duplicados y crea el usuario.
     *
     * @author Iker Sillero
     */
    private void procesarRegistro() {
        System.out.println("Click en Crear Cuenta");

        String nombreUsuario = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String contrasena = campoContraseña.getText().trim();

        if (nombreUsuario.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Todos los campos son obligatorios");
            return;
        }

        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo inválido", "Introduce un correo válido");
            return;
        }

        if (!telefono.matches("\\d{9}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido",
                    "El teléfono debe contener exactamente 9 dígitos");
            return;
        }

        if (contrasena.length() < 4) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña débil",
                    "La contraseña debe tener al menos 4 caracteres");
            return;
        }

        try {
            UsuarioDao usuarioDao = UsuarioDao.getInstance();

            boolean usuarioExiste = usuarioDao.existeUsuario(nombreUsuario);
            boolean correoExiste = usuarioDao.existeCorreo(correo);
            boolean telefonoExiste = usuarioDao.existeTelefono(telefono);

            if (usuarioExiste && correoExiste && telefonoExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Usuario, correo y teléfono ya están registrados");
                return;
            }

            if (usuarioExiste && correoExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Usuario y correo ya están registrados");
                return;
            }

            if (usuarioExiste && telefonoExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Usuario y teléfono ya están registrados");
                return;
            }

            if (correoExiste && telefonoExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Correo y teléfono ya están registrados");
                return;
            }

            if (usuarioExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Ese nombre de usuario ya está registrado");
                return;
            }

            if (correoExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Ese correo ya está registrado");
                return;
            }

            if (telefonoExiste) {
                mostrarAlerta(Alert.AlertType.ERROR, "Registro inválido",
                        "Ese teléfono ya está registrado");
                return;
            }

            Usuario nuevoUsuario = new Usuario(nombreUsuario, correo, telefono, contrasena);
            usuarioDao.crear(nuevoUsuario);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso",
                    "Usuario creado correctamente");
            abrirVistaInicioSesion();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD",
                    "No se pudo registrar el usuario:\n" + e.getMessage());
        }
    }

    /**
     * Abre la vista de inicio de sesión tras completar
     * correctamente el registro.
     *
     * @author Iker Sillero
     */
    private void abrirVistaInicioSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaInicioSesion.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneRegistro.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo abrir la pantalla de inicio de sesión");
        }
    }

    /**
     * Vuelve a la pantalla principal de la aplicación.
     *
     * @author Iker Sillero
     */
    private void volverAMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneRegistro.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo volver a la pantalla principal");
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