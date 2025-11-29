package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.ListaPeliculaDao;
import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Pelicula;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    // Recibe datos desde vistaListas
    public void setData(Usuario usuario, Lista lista) {
        this.usuario = usuario;
        this.lista = lista;
        txtTituloLista.setText("Películas de: " + lista.getNombreLista());
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
            cell.setPrefHeight(40); // 🔹 cada celda ocupa más espacio que lo normal
            return cell;
        });
    }

    // 🔹 Auxiliar: extraer solo el año
    private String extraerAnio(String fechaCompleta) {
        if (fechaCompleta == null || fechaCompleta.isEmpty()) return "2000";
        if (fechaCompleta.length() >= 4) return fechaCompleta.substring(0, 4);
        return "2000";
    }

    // 🔹 Auxiliar: mapear género TMDB → ENUM BD
    private String mapearGenero(String generoTMDB) {
        if (generoTMDB == null || generoTMDB.isEmpty()) return "ACCION";
        generoTMDB = generoTMDB.toUpperCase();
        switch (generoTMDB) {
            case "ACTION": return "ACCION";
            case "ADVENTURE": return "AVENTURA";
            case "ANIMATION": return "ANIMACION";
            case "COMEDY": return "COMEDIA";
            case "CRIME": return "CRIMEN";
            case "DOCUMENTARY": return "DOCUMENTAL";
            case "DRAMA": return "DRAMA";
            case "FAMILY": return "FAMILIAR";
            case "FANTASY": return "FANTASIA";
            case "HISTORY": return "HISTORIA";
            case "HORROR": return "TERROR";
            case "MUSIC": return "MUSICA";
            case "MYSTERY": return "MISTERIO";
            case "ROMANCE": return "ROMANCE";
            case "SCIENCE FICTION": return "CIENCIA_FICCION";
            case "TV MOVIE": return "PELICULA_DE_TV";
            case "THRILLER": return "SUSPENSO";
            case "WAR": return "BELICA";
            case "WESTERN": return "OESTE";
            default: return "ACCION";
        }
    }

    // 🔹 Cargar películas de la lista desde BD
    private void cargarPeliculas() {
        try {
            peliculasLista = PeliculaDao.getInstance().obtenerPeliculasDeLista(lista.getIdLista());
            listaPeliculas.getItems().clear();
            for (Pelicula p : peliculasLista) {
                listaPeliculas.getItems().add(p.getTitulo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Buscar en TMDB y añadir a BD + lista
    private void buscarYAgregarPelicula() {
        String nombre = campoBusqueda.getText();
        if (nombre == null || nombre.isEmpty()) {
            mostrarAlerta("Error", "Escribe un nombre para buscar.", Alert.AlertType.ERROR);
            return;
        }

        try {
            TMDBDao tmdb = new TMDBDao();
            ArrayList<Pelicula> coincidencias = tmdb.findByName(nombre);

            if (coincidencias.isEmpty()) {
                mostrarAlerta("Sin resultados", "No se encontraron películas en TMDB.", Alert.AlertType.INFORMATION);
                return;
            }

            ChoiceDialog<Pelicula> selector = new ChoiceDialog<>(coincidencias.get(0), coincidencias);
            selector.setTitle("Selecciona película");
            selector.setHeaderText("Coincidencias encontradas en TMDB");
            selector.setContentText("Película:");

            selector.showAndWait().ifPresent(pelicula -> {
                try {
                    pelicula.setAnioSalida(extraerAnio(pelicula.getAnioSalida()));
                    pelicula.setGenero(mapearGenero(pelicula.getGenero()));
                    pelicula.setDisponible(1);

                    PeliculaDao.getInstance().crear(pelicula); // ahora p tiene id_pelicula correcto
                    ListaPeliculaDao.getInstance().agregarPelicula(lista, pelicula);

                    cargarPeliculas();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    mostrarAlerta("Error", "No se pudo añadir la película.", Alert.AlertType.ERROR);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo buscar la película en TMDB.", Alert.AlertType.ERROR);
        }
    }

    // 🔹 Quitar película de la lista
    private void quitarPelicula() {
        int index = listaPeliculas.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            mostrarAlerta("Error", "Selecciona una película para quitar.", Alert.AlertType.ERROR);
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

    // 🔹 Mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
