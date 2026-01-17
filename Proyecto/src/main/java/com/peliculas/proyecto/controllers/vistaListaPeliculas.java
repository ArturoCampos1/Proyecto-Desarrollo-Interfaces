package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.ListaPeliculaDao;
import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class vistaListaPeliculas {

    @FXML private ListView<String> listaPeliculas;
    @FXML private Button btnBuscarAgregar;
    @FXML private Button btnQuitar;
    @FXML private Button btnVolver;
    @FXML private Text txtTituloLista;
    @FXML private TextField campoBusqueda;

    private Lista lista;
    private Usuario usuario;
    private ArrayList<Pelicula> peliculasLista;

    PeliculaDao peliculaDao = new PeliculaDao();

    // Recibe datos desde vistaListas
    public void setData(Usuario usuario, Lista lista) {
        this.usuario = usuario;
        this.lista = lista;
        txtTituloLista.setText("Lista : " + lista.getNombreLista());
        txtTituloLista.setStyle("-fx-alignment: center; -fx-font-size: 28px;");

        cargarPeliculas();
    }

    @FXML
    private void initialize() {
        btnVolver.setOnAction(e -> volverAListas());
        btnBuscarAgregar.setOnAction(e -> buscarYAgregarPelicula());
        btnQuitar.setOnAction(e -> quitarPelicula());

        // 🔹 Personalizar las celdas de las películas para que se vean más grandes
        listaPeliculas.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        // Texto más grande y con padding
                        setStyle("-fx-font-size: 16px; -fx-padding: 8px;");
                    }
                }
            };
            cell.setPrefHeight(40);
            return cell;
        });
    }

    // 🔹 Cargar películas de la lista desde BD
    private void cargarPeliculas() {
        try {
            peliculasLista = PeliculaDao.getInstance().obtenerPeliculasDeLista(lista.getIdLista());
            listaPeliculas.getItems().clear();
            for (Pelicula p : peliculasLista) {
                listaPeliculas.getItems().add(p.getTitulo() + " | Director: " + p.getDirector());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Buscar en TMDB y añadir a BD + lista
    private void buscarYAgregarPelicula() {
        String nombre = campoBusqueda.getText();
        if (nombre == null || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Escribe un nombre para buscar.");
            return;
        }

        try {
            TMDBDao tmdb = new TMDBDao();
            ArrayList<Pelicula> coincidencias = tmdb.findByName(nombre);

            if (coincidencias.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sin resultados", "No se encontraron películas en TMDB.");
                return;
            }

            ChoiceDialog<Pelicula> selector = new ChoiceDialog<>(coincidencias.get(0), coincidencias);
            selector.setTitle("Selecciona película");
            selector.setHeaderText("Coincidencias encontradas en TMDB");
            selector.setContentText("Película:");

            selector.showAndWait().ifPresent(pelicula -> {
                try {
                    // Insertar en BD
                    Pelicula pEncontrada = peliculaDao.buscarPorNombre(pelicula.getTitulo());
                    if (pEncontrada == null){
                        PeliculaDao.getInstance().crear(pelicula);
                    } else{
                        ListaPeliculaDao.getInstance().agregarPelicula(lista, pEncontrada);
                    }

                    ListaPeliculaDao.getInstance().agregarPelicula(lista, pelicula);

                    cargarPeliculas();

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

    // 🔹 Quitar película de la lista
    private void quitarPelicula() {
        int index = listaPeliculas.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Selecciona una película para quitar.");
            return;
        }
        Pelicula pelicula = peliculasLista.get(index);
        try {
            ListaPeliculaDao.getInstance().quitarPelicula(lista, pelicula);
            cargarPeliculas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Volver a la vista de listas
    private void volverAListas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListas.fxml"));
            Scene scene = new Scene(loader.load());
            vistaListas controller = loader.getController();
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