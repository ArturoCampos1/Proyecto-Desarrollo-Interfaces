package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dao.PeliculasDisponiblesDao;
import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dao.UsuarioDao;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private TextField campoPrecio;

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

    /**
     * @author Arturo Campos
     * Método de inicialización que carga usuarios, peliculas disponibles y todas las peliculas en general
     * Se declaran los eventos para que los botones pasen los parametros a los métodos
     */
    @FXML
    public void initialize() {

        cargarUsuarios();
        obtenerPeliculasDisponibles();
        obtenerTodasPeliculas();

        botonVolver.setOnMouseClicked(event -> volverAMain());
        btnAñadir.setOnMouseClicked(event -> {
            buscarYAgregarPelicula(campoNombre, campoCantidad, campoPrecio);
        });

    }

    /**
     * @author Arturo Campos
     * Método que activa todos las peliculas de la bd en el grid
     */
    public void obtenerTodasPeliculas() {
        ArrayList<Pelicula> peliculas = peliculaDao.obtenerPeliculas();
        ArrayList<VBox> boxs = returnPeliculaConFormatoArrayTodas(peliculas);
        mostrarPeliculasTodas(boxs);
    }
    /**
     * @author Arturo Campos
     * Método que activa todos las peliculas disponibles en el grid
     */
    public void obtenerPeliculasDisponibles(){
        ArrayList<Pelicula> peliculas = peliculasDisponiblesDao.obtenerPeliculasDispos();
        ArrayList<VBox> boxs = returnPeliculaConFormatoArrayDispos(peliculas);
        mostrarPeliculas(boxs);
    }

    /**
     * @author Arturo Campos
     * Método que agrega pelicula dispobible mediante un buscador de coincidencias y asigna cantidad y precio.
     * Si la pelicula existe la actualizará
     * @param campoNombre
     * @param campoCantidad
     * @param campoPrecio
     */
    //Método copiado de vistaListaPeliculas (Iker)
    public void buscarYAgregarPelicula(TextField campoNombre, TextField campoCantidad, TextField campoPrecio) {
        String nombre = campoNombre.getText();
        String cantidad = campoCantidad.getText();
        String precio = campoPrecio.getText();

        if (nombre == null || nombre.isEmpty() || cantidad.isEmpty() || cantidad == null || precio == null || precio.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Escribe datos para agregarla.");
            return;
        }

        int cantidadInt = Integer.valueOf(Math.round(Float.parseFloat(cantidad.replace(",", "."))));
        double precioDouble = Double.valueOf(precio.replace(",", "."));

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
                    pelicula.setPrecio(precioDouble);
                    Pelicula peliculaEncontrada = peliculaDao.buscarPorNombre(pelicula.getTitulo());

                    if (peliculaEncontrada != null) {
                        pelicula.setIdPelicula(peliculaEncontrada.getIdPelicula());
                        peliculaDao.actualizar(pelicula);
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización", "💡 Película actualizada");
                    } else {
                        peliculaDao.crear(pelicula);
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "✅ Película añadida con éxito");
                    }


                    obtenerTodasPeliculas();
                    obtenerPeliculasDisponibles();
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

    /**
     * @author Arturo Campos
     * Método que crea un arraylist de vbox y va recorriendo el arraylist de peliculas para crear cartas con formato
     * @param p (arrayList de peliculas que se obtendra con el método de obtenerPeliculasDisponibles)
     * @return ArrayList de VBOX que se imprime en el gridPane
     */
    public ArrayList<VBox> returnPeliculaConFormatoArrayDispos(ArrayList<Pelicula> p) {
        ArrayList<VBox> boxs = new ArrayList<>();

        for (Pelicula pelicula : p) {
            VBox box = new VBox(5);
            box.setPrefWidth(480);

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
    /**
     * @author Arturo Campos
     * Método que crea un arraylist de vbox y va recorriendo el arraylist de peliculas para crear cartas con formato. La diferencia
     * con el anterior es que este metodo devuelve los vbox de todas las peliculas que se encuentren en la bd sean dispobibles o no
     * @param p (arrayList de peliculas que se obtendra con el método de obtenerTodas)
     * @return ArrayList de VBOX que se imprime en el gridPane
     */
    public ArrayList<VBox> returnPeliculaConFormatoArrayTodas(ArrayList<Pelicula> p) {
        ArrayList<VBox> boxs = new ArrayList<>();
        for (Pelicula pelicula : p) {
            VBox box = new VBox(5);
            box.setPrefWidth(415);
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
            if (pelicula.getDisponible() < 1){
                disponible.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            } else disponible.setStyle("-fx-font-size: 14px; -fx-text-fill: purple;");

            Label precio = new Label("Precio: " + pelicula.getPrecio() + " €");
            precio.setStyle("-fx-font-size: 14px; -fx-text-fill: grey;");

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
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmación");
                alert.setContentText("Esto podría eliminar la película de alguna lista de usuarios. ¿Estás seguro?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        peliculaDao.eliminar(pelicula.getIdPelicula());
                        if (pelicula.getDisponible() > 0) {
                            PeliculasDisponiblesDao.eliminarPelicula(pelicula.getIdPelicula());
                        }
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "✅ Película eliminada");
                        obtenerTodasPeliculas();
                        obtenerPeliculasDisponibles();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


            box.getChildren().addAll(titulo, generos, disponible, precio, btnEliminar);
            box.setPadding(new Insets(10));

            boxs.add(box);
        }
        return boxs;
    }

    /**
     * @author Arturo Campos
     * Método que simplemente consulta los usuarios en bd y creamos con un metodo filas
     */
    public void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDao.consultarUsuarios();

            contenedorUsuarios.getChildren().clear();

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

    /**
     * @author Arturo Campos
     * Método que inserta los usuarios en un HBOX
     * @param usuario
     * @param index
     * @return un HBox que muestra los usuarios en las lista estilizada
     */
    private HBox crearFilaUsuarioEstilizada(Usuario usuario, int index) {
        HBox fila = new HBox(10);
        fila.setPrefWidth(240);
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro de que quieres eliminar al usuario " + usuario.getNombreUsuario() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    usuarioDao.eliminar(usuario.getIdUsuario());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "✅ El usuario " + usuario.getNombreUsuario() +
                            " e id " + usuario.getIdUsuario() + " ha sido eliminado.");
                    cargarUsuarios();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        vboxInfo.getChildren().addAll(lineaNombre, lblCorreo, lblTelefono, btnEliminar);

        fila.getChildren().addAll(icono, vboxInfo);

        return fila;
    }

    /**
     * @author Arturo Campos
     * Método que organiza las películas
     * @param peliculas
     */
    void mostrarPeliculas(ArrayList<VBox> peliculas) {
        gridPeliculas.getChildren().clear();
        int row = 0;
        for (VBox p : peliculas) {
            gridPeliculas.add(p, 1, row);
            row++;
        }
    }
    /**
     * @author Arturo Campos
     * Método que organiza las películas
     * @param peliculas
     */
    void mostrarPeliculasTodas(ArrayList<VBox> peliculas) {
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
    /**
     * @author Arturo Campos
     * Error en caso de que no carguen los usuarios
     */
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

    /**
     * @author Arturo Campos
     * Volver al inicio
     */
    private void volverAMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaMain.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) scrollPanePerfiles.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cineverse");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver a la pantalla principal");
        }
    }
    /**
     * Método para mostrar alertas personalizadas
     * @param tipo
     * @param titulo
     * @param mensaje
     *
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