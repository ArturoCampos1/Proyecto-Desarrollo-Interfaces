module com.peliculas.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires static lombok;

    opens com.peliculas.proyecto to javafx.fxml;
    exports com.peliculas.proyecto;
}