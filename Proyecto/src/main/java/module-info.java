module com.peliculas.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires java.sql;
    requires com.google.gson;
    requires javafx.graphics;
    requires java.desktop;
    requires stripe.java;

    opens com.peliculas.proyecto.controllers to javafx.fxml;
    exports com.peliculas.proyecto;
}