package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.*;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class vistaAdmin {

    @FXML
    private ScrollPane scrollPanePerfiles;

    @FXML
    private ScrollPane scrollPaneTodas;

    @FXML
    private Button botonVolver;

    @FXML
    private VBox contenedorUsuarios;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoCantidad;

    @FXML
    private Button btnAñadir;

    @FXML
    private Text textoInfo;

    @FXML
    private GridPane gridPeliculas;

    @FXML
    private GridPane gridTodasPeliculas;

    PeliculasDisponiblesDao peliculasDisponiblesDao = new PeliculasDisponiblesDao();

    UsuarioDao usuarioDao = new UsuarioDao();

    PeliculaDao peliculaDao = new PeliculaDao();

    TMDBDao tmdbDao = new TMDBDao();

    @FXML
    public void initialize() {

        cargarUsuarios();
        obtenerPeliculasDisponibles();
        obtenerTodasPeliculas();

        botonVolver.setOnMouseClicked(event -> volverAMain());
        btnAñadir.setOnMouseClicked(event -> {
            buscarYAgregarPelicula(campoNombre, campoCantidad);
        });

    }

    public void obtenerTodasPeliculas() {
        ArrayList<Pelicula> peliculas = peliculaDao.obtenerPeliculas();
        ArrayList<VBox> boxs = returnPeliculaConFormatoArrayTodas(peliculas);
        mostrarPeliculasTodas(boxs);
    }

    public void obtenerPeliculasDisponibles(){
        ArrayList<Pelicula> peliculas = peliculasDisponiblesDao.obtenerPeliculasDispos();
        ArrayList<VBox> boxs = returnPeliculaConFormatoArrayDispos(peliculas);
        mostrarPeliculas(boxs);
    }

    //Método copiado de vistaListaPeliculas (Iker)
    private void buscarYAgregarPelicula(TextField campoNombre, TextField campoCantidad) {
        String nombre = campoNombre.getText();
        String cantidad = campoCantidad.getText();

        int cantidadInt = Integer.valueOf(cantidad);

        if (nombre == null || nombre.isEmpty() || cantidad.isEmpty() || cantidad == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Escribe un nombre para buscar.");
            return;
        }

        try {
            ArrayList<Pelicula> coincidencias = tmdbDao.findByName(nombre);

            if (coincidencias.isEmpty()) {
                mostrarAlerta( Alert.AlertType.INFORMATION, "Sin resultados", "No se encontraron películas en TMDB.");
                return;
            }

            ChoiceDialog<Pelicula> selector = new ChoiceDialog<>(coincidencias.get(0), coincidencias);
            selector.setTitle("Selecciona película");
            selector.setHeaderText("Coincidencias encontradas en TMDB");
            selector.setContentText("Película:");

            selector.showAndWait().ifPresent(pelicula -> {
                try {
                    pelicula.setDisponible(cantidadInt);
                    peliculaDao.crearPeliculaDisponible(pelicula);
                    obtenerPeliculasDisponibles();
                    obtenerTodasPeliculas();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo añadir la película.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo buscar la película en TMDB.");
        }
    }

    public ArrayList<VBox> returnPeliculaConFormatoArrayDispos(ArrayList<Pelicula> p) {
        ArrayList<VBox> boxs = new ArrayList<>();

        for (Pelicula pelicula : p) {
            VBox box = new VBox(5);
            box.setPrefWidth(400);

            // Estilos CSS directamente en el VBox
            box.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #ffffff, #e0e0e0);" +
                            "-fx-border-color: #333;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
            );

            Label titulo = new Label(pelicula.getTitulo() + " (" + pelicula.getIdPelicula() + ")");
            titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label generos = new Label(pelicula.getGenero());
            generos.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label disponible = new Label("Disponible: " + pelicula.getDisponible());
            disponible.setStyle("-fx-font-size: 14px; -fx-text-fill: purple;");

            box.getChildren().addAll(titulo, generos, disponible);
            box.setPadding(new Insets(10));

            boxs.add(box);
        }

        return boxs;
    }

    public ArrayList<VBox> returnPeliculaConFormatoArrayTodas(ArrayList<Pelicula> p) {
        ArrayList<VBox> boxs = new ArrayList<>();
        for (Pelicula pelicula : p) {
            VBox box = new VBox(5);
            box.setPrefWidth(350);
            box.setStyle(
                    "-fx-background-radius: 8px;" +
                            "-fx-background-color: #ffffff;" +
                            "-fx-border-color: #dcdcdc;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 8px;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 4);"
            );

            Label titulo = new Label(pelicula.getTitulo() + " (" + pelicula.getIdPelicula() + ")");
            titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label generos = new Label(pelicula.getGenero());
            generos.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label disponible = new Label("Disponible: " + pelicula.getDisponible());
            disponible.setStyle("-fx-font-size: 14px; -fx-text-fill: purple;");

            box.getChildren().addAll(titulo, generos, disponible);
            box.setPadding(new Insets(10));

            boxs.add(box);
        }
        return boxs;
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

        // === LÍNea 3: Btn Eliminar ==
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-text-fill: #8e44ad;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 16;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #8e44ad;" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;"
        );

        btnEliminar.setOnMouseClicked(event -> {
            try {
                usuarioDao.eliminar(usuario.getIdUsuario());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "✅ Usuario eliminado.");
                cargarUsuarios();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


        vboxInfo.getChildren().addAll(lineaNombre, lblCorreo, lblTelefono, btnEliminar);

        fila.getChildren().addAll(icono, vboxInfo);

        return fila;
    }

    private void mostrarPeliculas(ArrayList<VBox> peliculas) {
        gridPeliculas.getChildren().clear();
        int row = 0;
        for (VBox p : peliculas) {
            gridPeliculas.add(p, 1, row);
            row++;
        }
    }

    private void mostrarPeliculasTodas(ArrayList<VBox> peliculas) {
        gridTodasPeliculas.getChildren().clear();
        int col = 0;
        int row = 0;
        for (VBox p : peliculas) {
            gridTodasPeliculas.add(p, col, row);
            col++;
            if (col > 1) { // 2 columnas: 0 y 1
                col = 0;
                row++;
            }
        }
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

    private void volverAMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) scrollPanePerfiles.getScene().getWindow();
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
