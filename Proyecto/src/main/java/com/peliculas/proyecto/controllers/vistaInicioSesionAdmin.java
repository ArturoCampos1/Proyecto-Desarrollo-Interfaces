package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.AdministradorDao;
import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Administrador;
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

public class vistaInicioSesionAdmin {

    AdministradorDao administradorDao = new AdministradorDao();

    @FXML
    private Pane paneLogin;
    @FXML
    private TextField campoAdmin;
    @FXML
    private PasswordField campoContraseña;
    @FXML
    private Button botonLogin;
    @FXML
    private Button botonVolver;

    @FXML
    private void initialize() {
        botonLogin.setOnMouseClicked(event -> procesarLogin());
        botonVolver.setOnMouseClicked(event -> volverAMain());
    }

    @FXML
    private void procesarLogin() {
        String nombreUsuario = campoAdmin.getText();
        String contrasena = campoContraseña.getText();

        try {
            Administrador admin = administradorDao.login(nombreUsuario, contrasena);

            if (admin != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Login correcto", "Bienvenido " + admin.getUsuario());
                abrirPanelAdminDesdeLogin(admin);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Login fallido", "Usuario o contraseña incorrectos");
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudo verificar el administrador. Error: " + e.getErrorCode());
        }
    }

    private void abrirPanelAdminDesdeLogin(Administrador admin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaAdmin.fxml"));
            Scene scene = new Scene(loader.load());

            vistaAdmin controller = loader.getController();

            Stage stage = (Stage) paneLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el panel de admin");
        }
    }


    @FXML
    private void volverAMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneLogin.getScene().getWindow();
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
