module com.peliculas.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires java.sql;
    requires gson;

    opens com.peliculas.proyecto.controllers to javafx.fxml;
    exports com.peliculas.proyecto;
}