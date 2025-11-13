package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Pelicula;
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
import java.util.ArrayList;

public class vistaLogin {

    Pelicula peliculaDto;
    Usuario usuarioDto;

    @FXML
    private Pane paneLogin;
    @FXML
    private TextField campoUsuario;
    @FXML
    private PasswordField campoContraseña;
    @FXML
    private Button botonLogin;
    @FXML
    private Button botonLogin1;

    @FXML
    private void initialize(){
        botonLogin.setOnMouseClicked(event -> procesarLogin());
        if (botonLogin1 != null) {
            botonLogin1.setOnMouseClicked(event -> volverAlMain());
        }
    }

    private void procesarLogin(){
        String nombreUsuario = campoUsuario.getText();
        String contrasena = campoContraseña.getText();

        Usuario usuario = verificarCredenciales(nombreUsuario, contrasena);

        if (usuario != null){
            mostrarAlerta(Alert.AlertType.INFORMATION, "Login correcto", "Bienvenido " + usuario.getNombreUsuario());
            abrirVistaMain(usuario); // pasamos el usuario
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Login fallido", "Usuario o contraseña incorrectos");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVistaMain(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());

            vistaMain controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) paneLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista principal");
        }
    }

    private void volverAlMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver a la vista principal");
        }
    }

    private Usuario verificarCredenciales(String nombreUsuario, String contrasena){
        Usuario usuario = null;
        try{
            UsuarioDao usuarioDao = UsuarioDao.getInstance();
            usuario = usuarioDao.login(nombreUsuario, contrasena);
        }catch (SQLException e){
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudo verificar el usuario");
        }
        return usuario;
    }
}
