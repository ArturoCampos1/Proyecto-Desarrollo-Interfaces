package com.peliculas.proyecto.dao;

import com.google.gson.*;
import com.peliculas.proyecto.dto.Pelicula;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TMDBDao {

    // Claves para el acceso a la API
    private static final String API_KEY = "490d5fcf9a1de19d8f7ba4c3bb2df832";
    private static final String URL_BBDD = "https://api.themoviedb.org/3/movie/";

    // Método findByName
    public Pelicula findByName(String nombre) {
        Pelicula p = null;
        try {
            URL urlAPINombre = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + nombre + "&language=es-ES");
            HttpURLConnection con = (HttpURLConnection) urlAPINombre.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);
            JsonArray results = jsonObject.getAsJsonArray("results");

            if (results != null && results.size() > 0) {
                JsonObject result = results.get(0).getAsJsonObject();
                p = getPelicula(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return p;
    }

    // Buscar películas por director (autor)
    public ArrayList<Pelicula> findByAutor(String autor) {
        ArrayList<Pelicula> arrayPelis = new ArrayList<>();
        try {
            // Buscar el ID del director/persona
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

                // Buscar películas donde la persona haya sido director
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
                        arrayPelis.add(p);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Películas del autor NO encontradas");
        }
        return arrayPelis;
    }

    // Buscar películas por género
    public ArrayList<Pelicula> findByGenre(String genero) {
        ArrayList<Pelicula> arrayPelis = new ArrayList<>();
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
            for (JsonElement genres : generos){
                JsonObject aux = genres.getAsJsonObject();
                int id = aux.get("id").getAsInt();
                String name = aux.get("name").getAsString();
                datosGeneros.put(id, name);
            }

            int idEquivalenteParametro = 0;

            for (Map.Entry<Integer, String> dGenero : datosGeneros.entrySet()) {
                if (dGenero.getValue().equalsIgnoreCase(genero)){
                    idEquivalenteParametro = dGenero.getKey();
                    break;
                }
            }

            // Buscar películas por género
            URL urlPeliculasConGen = new URL("https://api.themoviedb.org/3/discover/movie?with_genres=" + idEquivalenteParametro + "&api_key=" + API_KEY + "&language=es-ES&page=1");
            HttpURLConnection conexFinal = (HttpURLConnection) urlPeliculasConGen.openConnection();
            conexFinal.setRequestMethod("GET");
            conexFinal.setRequestProperty("Content-Type", "application/json");
            JsonObject jsonObjectArray;
            try (BufferedReader brLectura = new BufferedReader(new InputStreamReader(conexFinal.getInputStream()))) {
                jsonObjectArray = gson.fromJson(brLectura, JsonObject.class);
            }

            JsonArray results = jsonObjectArray.getAsJsonArray("results");
            for (JsonElement resultado : results){
                JsonObject result = resultado.getAsJsonObject();
                Pelicula p = getPelicula(result);
                arrayPelis.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return arrayPelis;
    }

    // Usado para la búsqueda por director, usando JsonObject ya parseado de créditos
    private static Pelicula getPelicula(JsonObject result) throws IOException {
        Pelicula p = new Pelicula();
        p.setIdPelicula(result.get("id").getAsInt());
        p.setTitulo(result.get("original_title").getAsString());
        p.setAnioSalida(result.has("release_date") ? result.get("release_date").getAsString() : "");
        p.setResumen(result.has("overview") ? result.get("overview").getAsString() : "");
        p.setPathBanner(result.has("backdrop_path") ? result.get("backdrop_path").getAsString() : "");
        if (result.has("vote_average")) {
            p.setValoracion(result.get("vote_average").getAsDouble() / 2);
        }
        try {
            Gson gson = new Gson();
            // Obtener datos completos de la película
            URL urlMovie = new URL("https://api.themoviedb.org/3/movie/" + p.getIdPelicula() + "?api_key=" + API_KEY + "&language=es-ES&append_to_response=credits");
            HttpURLConnection con = (HttpURLConnection) urlMovie.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            JsonObject results;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                results = gson.fromJson(br, JsonObject.class);
            }

            p.setIdPelicula(results.get("id").getAsInt());
            p.setTitulo(results.get("original_title").getAsString());

            // Obtener director correctamente
            if (results.has("credits")) {
                JsonObject credits = results.getAsJsonObject("credits");
                if (credits.has("crew")) {
                    p.setDirector(getDirector(credits.getAsJsonArray("crew")));
                }
            }
            return p;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDirector(JsonArray trabajadoresArray) {
        String director = "";
        for (JsonElement trabajadorPelicula : trabajadoresArray) {
            JsonObject person = trabajadorPelicula.getAsJsonObject();
            if (person.has("job") && person.get("job").getAsString().equalsIgnoreCase("Director")) {
                director = person.get("name").getAsString();
                break;
            }
        }
        return director;
    }

}
