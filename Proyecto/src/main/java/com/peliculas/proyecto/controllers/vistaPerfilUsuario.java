package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador de la vista de perfil del usuario.
 * Permite visualizar y modificar los datos personales del usuario,
 * como nombre, correo, teléfono y contraseña.
 *
 * @author Iker Sillero y Kevin Mejías
 */
public class vistaPerfilUsuario {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private Usuario usuario;

    /**
     * Recibe el usuario actual y carga sus datos en la vista.
     *
     * @param usuario Usuario que ha iniciado sesión
     * @author Iker Sillero
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarDatos();
    }

    /**
     * Carga los datos del usuario en los campos del formulario.
     *
     * @author Iker Sillero
     */
    private void cargarDatos() {
        if (usuario != null) {
            txtNombre.setText(usuario.getNombreUsuario());
            txtCorreo.setText(usuario.getCorreo());
            txtTelefono.setText(usuario.getNumTelef());
        }
    }

    /**
     * Inicializa la vista del perfil asignando eventos,
     * estilos y cursores a los componentes.
     *
     * @author Iker Sillero
     */
    @FXML
    private void initialize() {

        btnVolver.setOnAction(e -> volverAlPanelUsuario());
        btnGuardar.setOnAction(e -> guardarCambios());

        btnVolver.setCursor(Cursor.HAND);
        btnGuardar.setCursor(Cursor.HAND);

        btnGuardar.getStyleClass().add("btnMorado");
        btnVolver.getStyleClass().add("btnMorado");
    }

    /**
     * Guarda los cambios realizados en el perfil del usuario.
     * Valida los campos obligatorios y actualiza los datos
     * en la base de datos.
     *
     * @author Iker Sillero
     */
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

    /**
     * Vuelve al panel principal del usuario manteniendo su sesión activa.
     *
     * @author Iker Sillero
     */
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
