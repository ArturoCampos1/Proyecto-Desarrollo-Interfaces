package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.ListaDao;
import com.peliculas.proyecto.dto.Lista;
import com.peliculas.proyecto.dto.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Controlador de la vista de gestión de listas del usuario.
 * Permite crear, eliminar y acceder a listas de películas,
 * así como volver al panel principal del usuario.
 *
 * @author Iker Sillero
 */
public class vistaListas {

    @FXML private ListView<String> listaListas;
    @FXML private Button btnCrearLista;
    @FXML private Button btnEliminarLista;
    @FXML private Button btnVolver;

    private Usuario usuario;
    private ArrayList<Lista> listasUsuario;

    /**
     * Establece el usuario actual y carga sus listas.
     *
     * @param usuario Usuario autenticado
     * @author Iker Sillero
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarListas();
    }

    /**
     * Inicializa la vista de listas.
     * Configura los eventos de los botones, la interacción con la lista
     * y el estilo visual de las celdas.
     *
     * @author Iker Sillero
     */
    @FXML
    private void initialize() {
        btnVolver.setOnAction(e -> volverAlPanelUsuario());
        btnCrearLista.setOnAction(e -> crearLista());
        btnEliminarLista.setOnAction(e -> eliminarLista());

        listaListas.setCursor(Cursor.HAND);
        listaListas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                abrirListaPeliculas();
            }
        });

        // 🔹 Personalizar las celdas para que se vean más grandes
        listaListas.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        // Texto un poco más grande y con menos padding
                        setStyle("-fx-font-size: 16px; -fx-padding: 8px;");
                    }
                }
            };
            cell.setPrefHeight(40); // 🔹 más grande que lo normal (~24px), pero más pequeño que los 60px anteriores
            return cell;
        });
    }

    /**
     * Carga las listas del usuario desde la base de datos
     * y las muestra en el ListView.
     *
     * @author Iker Sillero
     */
    private void cargarListas() {
        try {
            listasUsuario = ListaDao.getInstance().obtenerPorNombreUsuario(usuario.getNombreUsuario());
            listaListas.getItems().clear();
            for (Lista l : listasUsuario) {
                listaListas.getItems().add(l.getNombreLista());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo para crear una nueva lista
     * y la guarda en la base de datos.
     *
     * @author Iker Sillero
     */
    private void crearLista() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva lista");
        dialog.setHeaderText("Crear nueva lista");
        dialog.setContentText("Nombre de la lista:");
        dialog.showAndWait().ifPresent(nombre -> {
            try {
                Lista nueva = new Lista();
                nueva.setNombreLista(nombre);
                nueva.setUsuario(usuario);
                ListaDao.getInstance().crear(nueva);
                cargarListas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Elimina la lista seleccionada por el usuario
     * y actualiza la vista.
     *
     * @author Iker Sillero
     */
    private void eliminarLista() {
        int index = listaListas.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Selecciona una lista para eliminar.");
            return;
        }
        Lista lista = listasUsuario.get(index);
        try {
            ListaDao.getInstance().eliminar(lista);
            cargarListas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la vista de películas asociadas a la lista seleccionada.
     *
     * @author Iker Sillero
     */
    private void abrirListaPeliculas() {
        int index = listaListas.getSelectionModel().getSelectedIndex();
        if (index == -1) return;
        Lista listaSeleccionada = listasUsuario.get(index);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/vistaListaPeliculas.fxml"));
            Scene scene = new Scene(loader.load());
            vistaListaPeliculas controller = loader.getController();
            controller.setData(usuario, listaSeleccionada);
            Stage stage = (Stage) listaListas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve al panel principal del usuario.
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
