package com.peliculas.proyecto.dao;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.peliculas.proyecto.dto.Genero;
import com.peliculas.proyecto.dto.Pelicula;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TMDBDao {

    private static final String API_KEY = "490d5fcf9a1de19d8f7ba4c3bb2df832";
    private static final String URL_BBDD = "https://api.themoviedb.org/3/movie/";

    public Pelicula findById(String id){

        try {
            URL urlAPI = new URL(URL_BBDD + id + "?api_key=" + API_KEY);
            HttpURLConnection con = (HttpURLConnection) urlAPI.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(br, JsonObject.class);

            Pelicula p = null;

            String idP = jsonObject.get("id").getAsString();;
            String titulo = jsonObject.get("original_title").getAsString(); ;
            String anioSalida = jsonObject.get("release_date").getAsString();;
            String director;
            String resumen = jsonObject.get("release_date").getAsString();;
            Genero genero;
            Double valoracion = jsonObject.get("average_rate").getAsDouble();
            Double valoracionAdaptada = valoracion / 2;
        } catch (Exception e) {
            throw new RuntimeException(e + ": Película NO encontrada");
        }

    }


}
