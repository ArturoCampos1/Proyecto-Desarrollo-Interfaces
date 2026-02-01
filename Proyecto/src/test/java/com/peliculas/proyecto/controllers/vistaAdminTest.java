package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dao.PeliculaDao;
import com.peliculas.proyecto.dao.PeliculasDisponiblesDao;
import com.peliculas.proyecto.dao.TMDBDao;
import com.peliculas.proyecto.dto.Pelicula;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.*;
        import org.mockito.Mockito;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@DisplayName("Tests de AdminDao")
public class vistaAdminTest {

    @BeforeEach
    void setUp() {
        System.out.println("Preparando test...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finalizado.");
    }

    @Test
    @DisplayName("obtenerTodasPeliculasTest")
    public void obtenerTodasPeliculasTest() throws SQLException {

        //Método obtenerPeliculas()
        PeliculaDao peliculaDao = Mockito.mock(PeliculaDao.class);
        ArrayList<Pelicula> lista = peliculaDao.obtenerPeliculas();
        Pelicula p = peliculaDao.buscarPorNombre("Predator");
        lista.add(p);
        // Simulamos la llamada al primer método para comprobar que funciona y devolverá
        // nuestra lista que acabamos de crear con tan solo una película
        Mockito.when(peliculaDao.obtenerPeliculas()).thenReturn(lista);

        //Método returnPeliculaConFormatoArrayTodas()
        vistaAdmin vistaAdmin = Mockito.mock(vistaAdmin.class);
        VBox vbox = new VBox();
        ArrayList<VBox> listaVbox = new ArrayList<>();
        listaVbox.add(vbox);
        Mockito.when(vistaAdmin.returnPeliculaConFormatoArrayTodas(lista)).thenReturn(listaVbox);

    }

    @Test
    @DisplayName("obtenerPeliculasDisponiblesTest")
    public void obtenerPeliculasDisponiblesTest() throws SQLException {

        PeliculasDisponiblesDao peliculasDisponiblesDao = Mockito.mock(PeliculasDisponiblesDao.class);
        PeliculaDao peliculaDao = Mockito.mock(PeliculaDao.class);
        ArrayList<Pelicula> lista = peliculaDao.obtenerPeliculas();
        Pelicula p = peliculaDao.buscarPorNombre("Predator");
        lista.add(p);
        Mockito.when(peliculasDisponiblesDao.obtenerPeliculasDispos()).thenReturn(lista);

        //Método returnPeliculaConFormatoArrayTodas()
        vistaAdmin vistaAdmin = Mockito.mock(vistaAdmin.class);
        VBox vbox = new VBox();
        ArrayList<VBox> listaVbox = new ArrayList<>();
        listaVbox.add(vbox);
        Mockito.when(vistaAdmin.returnPeliculaConFormatoArrayDispos(lista)).thenReturn(listaVbox);

    }

    @Test
    @DisplayName("buscarYAgregarPelicula")
    public void buscarYAgregarPelicula() throws IOException, SQLException {

        TMDBDao tmdbDaoMock = Mockito.mock(TMDBDao.class);

        Pelicula p = new Pelicula();
        p.setTitulo("Predator");

        Mockito.when(tmdbDaoMock.findBySpecificName("Predator"))
                .thenReturn(p);

        PeliculaDao peliculaDao = new PeliculaDao();

        Pelicula resultado = peliculaDao.buscarPorNombre("Predator");

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Predator", resultado.getTitulo());
    }


    @Test
    @DisplayName("buscarPeliculaEnTMDBTest")
    public void buscarPeliculaEnTMDBTest() throws IOException {

        TMDBDao tmdbDao = new TMDBDao();
        String nombre = "The Iron Man";

        Pelicula pelicula = tmdbDao.findBySpecificName(nombre);

        Assertions.assertNotNull(pelicula);
    }

}
