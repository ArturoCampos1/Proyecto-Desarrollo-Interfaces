package com.peliculas.proyecto.dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.peliculas.proyecto.dto.Genero;
import com.peliculas.proyecto.dto.Pelicula;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMDBDao {

    //Claves para el acceso a la API
    private static final String API_KEY = "490d5fcf9a1de19d8f7ba4c3bb2df832";
    private static final String URL_BBDD = "https://api.themoviedb.org/3/movie/";

    //Método findById
    public Pelicula findById(int id){

        //Creamos objeto fuera del try para devolver algo seguro (si es null ya haremos el control en el controller)
        Pelicula p = new Pelicula();
        try {
            //URL que vamos a usar para hacer la consulta para el id
            URL urlAPI = new URL(URL_BBDD + id + "?api_key=" + API_KEY + "&language=es-ES"); //Endpoint general
            URL urlAPICreditos = new URL(URL_BBDD + id +
                    "/credits?api_key=" + API_KEY + "&language=es-ES"); //Endpoint créditos

            //Abrimos conexión y seteamos el metodo GET y definimos sobre lo que va a trabajar (Json)
            HttpURLConnection con = (HttpURLConnection) urlAPI.openConnection();
            HttpURLConnection conCreditos = (HttpURLConnection) urlAPICreditos.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            conCreditos.setRequestMethod("GET");
            conCreditos.setRequestProperty("Content-Type", "application/json");

            //Obtenemos la respuesta de la API
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            BufferedReader brCreditos = new BufferedReader(new InputStreamReader(conCreditos.getInputStream()));

            //Creamos un objeto Gson para tratar con los archivos JSon y definimos un
            //objeto Json para leer el BufferedReader
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);
            JsonObject jsonObjectCreditos = gson.fromJson(brCreditos, JsonObject.class);

            //Obtenemos todos los datos en nuestras respectivas variables
            int idP = jsonObject.get("id").getAsInt();
            String titulo = jsonObject.get("original_title").getAsString();
            String anioSalida = jsonObject.get("release_date").getAsString();
            String director = "";
            JsonArray trabajadoresArray = jsonObjectCreditos.getAsJsonArray("crew");

            //Bucle que lee todos los miembros que han participado a la pelicula y si el job es
            //directos lo definimos a nuestra variable
            for (JsonElement trabajadorPelicula : trabajadoresArray) {
                JsonObject person = trabajadorPelicula.getAsJsonObject();
                if (person.get("job").getAsString().equalsIgnoreCase("Director")) {
                    director = person.get("name").getAsString();
                    break;
                }
            }
            String resumen = jsonObject.get("overview").getAsString();

            String pathBanner = jsonObject.get("backdrop_path").getAsString();

            String genero = "";
            JsonArray generos = jsonObject.getAsJsonArray("genres");

            //Si devuelve géneros setea el 1ro de la lista
            if (generos.size() > 0) {
                genero = generos.get(0).getAsJsonObject().get("name").getAsString();
            }

            //Normalizamos el genero en mayuscula y replazamos las tildes y las _
            String normalizado = genero.toUpperCase()
                    .replace(" ", "_")
                    .replace("Á", "A")
                    .replace("É", "E")
                    .replace("Í", "I")
                    .replace("Ó", "O")
                    .replace("Ú", "U")
                    .replace("Ñ", "N");

            //Lo tipamos
            Genero generoTipado = Genero.valueOf(normalizado);

            //Nosotros trabajamos sobrre valoración de 0 - 5 y ellos de 0 - 10 por lo que simplemente
            //dividimos / 2
            double valoracion = jsonObject.get("vote_average").getAsDouble();
            double valoracionAdaptada = valoracion / 2;

            //Seteamos datos
            p.setIdPelicula(idP);
            p.setTitulo(titulo);
            p.setAnioSalida(anioSalida);
            p.setDirector(director);
            p.setResumen(resumen);
            p.setPathBanner(pathBanner);
            p.setGenero(generoTipado);
            p.setValoracion(valoracionAdaptada);

        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return p;
    }

    //Método findByName
    public Pelicula findByName(String nombre){

        //Creamos objeto fuera del try para devolver algo seguro (si es null ya haremos el control en el controller)
        Pelicula p = new Pelicula();
        try {
            //URL que vamos a usar para hacer la consulta para el nombre
            URL urlAPINombre = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + nombre + "&language=es-ES");

            //Abrimos conexión y seteamos el metodo GET y definimos sobre lo que va a trabajar (Json)
            HttpURLConnection con = (HttpURLConnection) urlAPINombre.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);

            JsonArray results = jsonObject.get("results").getAsJsonArray();

            if (results.size() > 0){
                JsonObject leerResultados = results.get(0).getAsJsonObject();
                int id = leerResultados.get("id").getAsInt();
                p = findById(id);
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return p;
    }

    public ArrayList<Pelicula> findByGenre(String genero) {
        ArrayList<Pelicula> arrayPelis = new ArrayList<>();
        try {

            URL urlIdGenres = new URL("https://api.themoviedb.org/3/genre/movie/list?api_key=490d5fcf9a1de19d8f7ba4c3bb2df832&language=es-ES");

            HttpURLConnection con = (HttpURLConnection) urlIdGenres.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);

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

            URL urlPeliculasConGen = new URL("https://api.themoviedb.org/3/discover/movie?with_genres=" + idEquivalenteParametro + "&api_key=" + API_KEY + "&language=es-ES&page=2");

            HttpURLConnection conexFinal = (HttpURLConnection) urlPeliculasConGen.openConnection();
            conexFinal.setRequestMethod("GET");
            conexFinal.setRequestProperty("Content-Type", "application/json");

            BufferedReader brLectura = new BufferedReader(new InputStreamReader(conexFinal.getInputStream()));
            JsonObject jsonObjectArray = gson.fromJson(brLectura, JsonObject.class);

            JsonArray results = jsonObjectArray.getAsJsonArray("results");
            for (JsonElement resultado : results){
                JsonObject result = resultado.getAsJsonObject();
                int idBusqueda = result.get("id").getAsInt();
                Pelicula p = findById(idBusqueda);
                arrayPelis.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }
        return arrayPelis;
    }
}
