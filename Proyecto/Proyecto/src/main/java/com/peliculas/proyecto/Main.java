package com.peliculas.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("vistaMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CineVerse");
        stage.setScene(scene);

        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
