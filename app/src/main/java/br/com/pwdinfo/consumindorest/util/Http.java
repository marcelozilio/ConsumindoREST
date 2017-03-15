package br.com.pwdinfo.consumindorest.util;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Marcelo on 31/10/2016.
 */
public class Http {

    public static final String INSERIR = "Http://192.168.25.60:8080/CrudWS/controle/produto/inserir";
    public static final String LISTAR = "Http://192.168.25.60:8080/CrudWS/controle/produto/listar";
    public static String DELETAR = "Http://192.168.25.60:8080/CrudWS/controle/produto/deletar/";
    public static final String ALTERAR = "Http://192.168.25.60:8080/CrudWS/controle/produto/alterar";
    public static final String LOGIN = "Http://192.168.25.60:8080/CrudWS/controle/usuario/login";

    public Boolean post(String urlChamada, String json, String metodo) throws IOException {

        URL url = new URL(urlChamada);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(metodo);

        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Accept", "text/plain"/*"application/json"*/);

        connection.setDoOutput(true);

        PrintStream printStream = new PrintStream(connection.getOutputStream());
        printStream.println(json);

        connection.connect();

        String jsonResposta = new Scanner(connection.getInputStream()).next();

        return Boolean.parseBoolean(jsonResposta);
    }

    public String get(String urlChamada, String metodo) throws IOException {

        URL url = new URL(urlChamada);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(metodo);

        connection.setRequestProperty("Content-type", "application/json");
        //connection.setRequestProperty("Accept", /*"application/json"*/"text/plain");

        connection.connect();

        String jsonResposta = new Scanner(connection.getInputStream()).next();

        return jsonResposta;
    }
}
