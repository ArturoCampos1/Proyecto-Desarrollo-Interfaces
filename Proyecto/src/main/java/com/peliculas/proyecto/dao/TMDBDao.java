package com.peliculas.proyecto.dao;

import com.google.gson.*;
import com.peliculas.proyecto.dto.Pelicula;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO encargado de la comunicación con la API externa TMDB (The Movie Database).
 * Permite buscar películas por nombre, autor (director), género y obtener
 * películas populares.
 *
 * Esta clase NO interactúa con la base de datos local, únicamente consume la API.
 *
 * @author Arturo Campos
 */
public class TMDBDao {

    /**
     * Clave privada para el acceso a la API de TMDB.
     */
    private static final String API_KEY = "490d5fcf9a1de19d8f7ba4c3bb2df832";

    /**
     * Busca películas en TMDB a partir de su nombre.
     *
     * @param nombre Nombre de la película a buscar
     * @return Lista de películas que coinciden con el nombre indicado
     * @throws RuntimeException si ocurre un error durante la conexión o lectura de datos
     * @author Arturo Campos
     */
    public ArrayList<Pelicula> findByName(String nombre) {
        ArrayList<Pelicula> arrayPeliculas = new ArrayList<>();
        try {
            URL urlAPINombre = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + nombre + "&language=es-ES");
            HttpURLConnection con = (HttpURLConnection) urlAPINombre.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);

            JsonArray results = jsonObject.getAsJsonArray("results");
            for (JsonElement resultado : results) {
                JsonObject result = resultado.getAsJsonObject();
                Pelicula p = getPelicula(result);

                if (!revisarPelicula(p)) {
                    arrayPeliculas.add(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return arrayPeliculas;
    }

    /**
     * Busca películas dirigidas por un autor (director) concreto.
     *
     * @param autor Nombre del director
     * @return Lista de películas dirigidas por el autor indicado
     * @throws RuntimeException si ocurre un error durante la consulta a la API
     * @author Arturo Campos
     */
    public ArrayList<Pelicula> findByAutor(String autor) {
        ArrayList<Pelicula> arrayPelis = new ArrayList<>();
        try {
            URL urlAutor = new URL("https://api.themoviedb.org/3/search/person?api_key=" + API_KEY + "&query=" + autor + "&language=es-ES");
            HttpURLConnection con = (HttpURLConnection) urlAutor.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);

            JsonArray results = jsonObject.getAsJsonArray("results");
            if (results != null && results.size() > 0) {
                JsonObject persona = results.get(0).getAsJsonObject();
                int personId = persona.get("id").getAsInt();

                URL urlCredits = new URL("https://api.themoviedb.org/3/person/" + personId + "/movie_credits?api_key=" + API_KEY + "&language=es-ES");
                HttpURLConnection creditsCon = (HttpURLConnection) urlCredits.openConnection();
                creditsCon.setRequestMethod("GET");
                creditsCon.setRequestProperty("Content-Type", "application/json");
                BufferedReader brCredits = new BufferedReader(new InputStreamReader(creditsCon.getInputStream()));
                JsonObject creditsObject = gson.fromJson(brCredits, JsonObject.class);

                JsonArray crewArray = creditsObject.getAsJsonArray("crew");
                for (JsonElement elem : crewArray) {
                    JsonObject crew = elem.getAsJsonObject();
                    if (crew.get("job").getAsString().equalsIgnoreCase("Director")) {
                        Pelicula p = getPelicula(crew);
                        if (!revisarPelicula(p)) {
                            arrayPelis.add(p);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Películas del autor NO encontradas");
        }

        return arrayPelis;
    }

    /**
     * Busca películas en TMDB filtrando por género.
     *
     * @param genero Nombre del género a buscar
     * @return Lista de películas pertenecientes al género indicado
     * @throws RuntimeException si ocurre un error durante la consulta
     * @author Arturo Campos
     */
    public ArrayList<Pelicula> findByGenre(String genero) {
        ArrayList<Pelicula> arrayPelis = new ArrayList<>();
        int numero = (int) (Math.random() * 300) + 1;

        try {
            URL urlIdGenres = new URL("https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY + "&language=es-ES");
            HttpURLConnection con = (HttpURLConnection) urlIdGenres.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            JsonObject jsonObject;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                jsonObject = gson.fromJson(br, JsonObject.class);
            }

            JsonArray generos = jsonObject.getAsJsonArray("genres");
            HashMap<Integer, String> datosGeneros = new HashMap<>();
            for (JsonElement genres : generos) {
                JsonObject aux = genres.getAsJsonObject();
                int id = aux.get("id").getAsInt();
                String name = aux.get("name").getAsString();
                datosGeneros.put(id, name);
            }

            int idEquivalenteParametro = 0;

            for (Map.Entry<Integer, String> dGenero : datosGeneros.entrySet()) {
                if (dGenero.getValue().equalsIgnoreCase(genero)) {
                    idEquivalenteParametro = dGenero.getKey();
                    break;
                }
            }

            URL urlPeliculasConGen = new URL("https://api.themoviedb.org/3/discover/movie?with_genres=" + idEquivalenteParametro + "&api_key=" + API_KEY + "&language=es-ES&page=" + numero);
            HttpURLConnection conexFinal = (HttpURLConnection) urlPeliculasConGen.openConnection();
            conexFinal.setRequestMethod("GET");
            conexFinal.setRequestProperty("Content-Type", "application/json");
            JsonObject jsonObjectArray;
            try (BufferedReader brLectura = new BufferedReader(new InputStreamReader(conexFinal.getInputStream()))) {
                jsonObjectArray = gson.fromJson(brLectura, JsonObject.class);
            }

            JsonArray results = jsonObjectArray.getAsJsonArray("results");
            for (JsonElement resultado : results) {
                JsonObject result = resultado.getAsJsonObject();
                Pelicula p = getPelicula(result);

                if (!revisarPelicula(p)) {
                    arrayPelis.add(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return arrayPelis;
    }

    /**
     * Obtiene una lista de películas populares (trending) desde TMDB.
     *
     * @return Lista de películas populares
     * @throws RuntimeException si ocurre un error durante la conexión
     *
     * @author Arturo Campos
     */
    public ArrayList<Pelicula> findTrendingFilms() {
        ArrayList<Pelicula> peliculasPopulares = new ArrayList<>();
        int numero = (int) (Math.random() * 500) + 1;

        URL url = null;
        try {
            url = new URL("https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=es-ES&page=" + numero);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setRequestProperty("Content-Type", "application/json");
        Gson gson = new Gson();
        JsonObject jsonObject;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jsonObject = gson.fromJson(br, JsonObject.class);

        JsonArray results = jsonObject.get("results").getAsJsonArray();
        for (JsonElement element : results) {
            JsonObject resultado = element.getAsJsonObject();
            Pelicula p = null;
            try {
                p = getPelicula(resultado);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (!revisarPelicula(p)) {
                peliculasPopulares.add(p);
            }
        }
        return peliculasPopulares;
    }

    /**
     * Busca una película concreta por su nombre exacto.
     * Método utilizado desde el panel de administración.
     *
     * Realiza una llamada a la API de TMDB y devuelve la última coincidencia
     * encontrada con el nombre proporcionado.
     *
     * @param nombre Nombre de la película a buscar
     * @return Película encontrada o null si no se encuentra ninguna
     * @throws IOException si ocurre un error de entrada/salida durante la conexión
     *
     * @author Arturo Campos
     */
    public Pelicula findBySpecificName(String nombre) throws IOException {
        Pelicula p = null;

        URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY
                + "&query=" + URLEncoder.encode(nombre, StandardCharsets.UTF_8)
                + "&language=es-ES");
        HttpURLConnection con = null;
        con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        JsonObject jsonObject = gson.fromJson(br, JsonObject.class);
        JsonArray arrayJson = jsonObject.get("results").getAsJsonArray();
        for (JsonElement element : arrayJson) {
            JsonObject resultados = element.getAsJsonObject();
            p = getPelicula(resultados);
        }

        return p;
    }

    /**
     * Construye un objeto {@link Pelicula} a partir de un {@link JsonObject}
     * obtenido desde la API de TMDB.
     *
     * Se encarga de:
     *
     * Asignar datos básicos de la película
     * Consultar el director
     * Resolver los géneros a partir de sus IDs
     *
     *
     * Este método es utilizado internamente por otras búsquedas.
     *
     * @param result Objeto JSON con los datos de la película
     * @return Objeto {@link Pelicula} completamente construido
     * @throws IOException si ocurre un error durante las llamadas a la API
     *
     * @author Arturo Campos
     */
    private static Pelicula getPelicula(JsonObject result) throws IOException {
        Pelicula p = new Pelicula();

        p.setIdPelicula(result.get("id").getAsInt());
        p.setTitulo(result.get("original_title").getAsString());
        p.setResumen(result.has("overview") && !result.get("overview").isJsonNull()
                ? result.get("overview").getAsString()
                : "");
        p.setPathBanner(result.has("backdrop_path") && !result.get("backdrop_path").isJsonNull()
                ? result.get("backdrop_path").getAsString()
                : "src/main/resources/img/noavailable.jpg");
        p.setAnioSalida(result.has("release_date") && !result.get("release_date").isJsonNull()
                ? result.get("release_date").getAsString()
                : "");
        p.setValoracion(result.has("vote_average") && !result.get("vote_average").isJsonNull()
                ? result.get("vote_average").getAsDouble() / 2
                : 0.0);
        p.setAnioSalida(result.has("release_date") && !result.get("release_date").isJsonNull()
                ? result.get("release_date").getAsString()
                : "");
        URL urlDirector = new URL("https://api.themoviedb.org/3/movie/" + p.getIdPelicula() + "/credits?api_key=" + API_KEY + "&language=es-ES");
        URL urlGenres = new URL("https://api.themoviedb.org/3/genre/movie/list?language=es-ES&api_key=" + API_KEY);

        //Conexión Directores
        HttpURLConnection con = (HttpURLConnection) urlDirector.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(br, JsonObject.class);
        JsonArray cast = jsonObject.getAsJsonArray("crew");

        String director = "";
        for (JsonElement trabajadorPelicula : cast) {
            JsonObject person = trabajadorPelicula.getAsJsonObject();
            if (person.get("job").getAsString().equalsIgnoreCase("director")) {
                director = person.get("name").getAsString();
                break;
            }
        }
        p.setDirector(director);

        //Conexion Genres
        JsonArray genres = result.getAsJsonArray("genre_ids");
        ArrayList<Integer> genresInt = new ArrayList<>();
        ArrayList<String> genresString = new ArrayList<>();
        for (JsonElement g : genres) {
            genresInt.add(g.getAsInt());
        }

        HttpURLConnection conex = (HttpURLConnection) urlGenres.openConnection();
        conex.setRequestMethod("GET");
        conex.setRequestProperty("Content-Type", "application/json");

        BufferedReader br2 = new BufferedReader(new InputStreamReader(conex.getInputStream()));
        Gson gson2 = new Gson();
        JsonObject jsonObject2 = gson2.fromJson(br2, JsonObject.class);
        JsonArray genresJson = jsonObject2.getAsJsonArray("genres");

        HashMap<Integer, String> datosGeneros = new HashMap<>();
        for (JsonElement genresObject : genresJson) {
            JsonObject aux = genresObject.getAsJsonObject();
            int id = aux.get("id").getAsInt();
            String name = aux.get("name").getAsString();
            datosGeneros.put(id, name);
        }

        int i = 0;

        for (Integer genreId : genresInt) {
            if (datosGeneros.containsKey(genreId)) {
                genresString.add(datosGeneros.get(genreId));
            }
        }

        String generos = String.join(", ", genresString);
        p.setGenero(generos);

        return p;
    }

    /**
     * Comprueba si una película contiene todos los datos necesarios.
     *
     * @param pelicula Objeto {@link Pelicula} a validar
     * @return true si faltan datos obligatorios, false si la película es válida
     *
     * @author Arturo Campos
     */
    public boolean revisarPelicula(Pelicula pelicula) {

        boolean faltanDatos = false;

        if (pelicula.getTitulo() == null || pelicula.getTitulo().isEmpty()
                || pelicula.getResumen() == null || pelicula.getResumen().isEmpty()
                || pelicula.getPathBanner() == null || pelicula.getPathBanner().isEmpty()
                || pelicula.getAnioSalida() == null || pelicula.getAnioSalida().isEmpty()
                || pelicula.getDirector() == null || pelicula.getDirector().isEmpty()
                || pelicula.getGenero() == null || pelicula.getGenero().isEmpty()
                || pelicula.getIdPelicula() == 0
                || pelicula.getValoracion() == 0.0) {
            faltanDatos = true;
        }
        ;

        return faltanDatos;
    }
}