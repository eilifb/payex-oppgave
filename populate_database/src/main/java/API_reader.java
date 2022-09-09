import static spark.Spark.get;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.net.HttpURLConnection;

public class API_reader {
    public static void main(String[] args) throws Exception { //TODO: handle exception
        URL url = new URL("https://api.tvmaze.com/search/shows?q=girls");
        HttpURLConnection url_connection = (HttpURLConnection) url.openConnection();

        url_connection.setRequestMethod("GET");
        url_connection.setRequestProperty("UserAgent","Mozilla/5.0");

        int responseCode = url_connection.getResponseCode(); //TODO: handle 429 error
        JsonElement json_raw = JsonParser.parseReader(new InputStreamReader((InputStream) url_connection.getContent()));
        JsonObject json = json_raw.getAsJsonObject();
        get("/hello", (req, res) -> "<h1>Hello</h1>");
        System.out.println("Code: "+responseCode+ ", JSON as string: "+json.toString());

    }
}
