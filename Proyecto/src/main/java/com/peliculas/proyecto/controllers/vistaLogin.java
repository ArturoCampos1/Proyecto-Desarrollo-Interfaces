package com.peliculas.proyecto.controllers;

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
import java.sql.*;
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
    private void initialize(){
        botonLogin.setOnMouseClicked(event -> procesarLogin());
    }

    private void procesarLogin(){
        String nombreUsuario = campoUsuario.getText();
        String contrasena = campoContraseña.getText();

        Usuario usuario = verificarCredenciales(nombreUsuario, contrasena);

        if (usuario != null){
            mostrarAlerta(Alert.AlertType.INFORMATION, "Login correcto", "Bienvenido " + usuario.getNombreUsuario());
            abrirVistaMain();
        }else {
            mostrarAlerta(Alert.AlertType.ERROR, "Login fallido", "Usuario o contraseña incorrectos");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVistaMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) paneLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la vista principal");
        }
    }

    private Usuario verificarCredenciales(String nombreUsuario, String contrasena){
        String url = "jdbc:mysql://localhost:3306/cineverse";
        String user = "root";
        String password = "root";

        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("correo"),
                        rs.getString("num_tel"),
                        rs.getString("contrasena"),
                        new ArrayList<Pelicula>()
                );
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
