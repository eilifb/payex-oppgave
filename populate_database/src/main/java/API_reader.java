
import static spark.Spark.get;

import netscape.javascript.JSObject;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import  javax.net.ssl.HostnameVerifier;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class API_reader {
    public static void populate_database() throws Exception {

        File_reader fr = new File_reader();
        fr.Read_file("populate_database\\src\\main\\resources\\config.txt");
        URL[] API_request_urls = fr.get_request_URLs();
        ArrayList<JsonObject> shows_json = get_json_from_API(API_request_urls);

        Database_manager db = new Database_manager();
        db.init("show_db");
        db.create_table("shows",("shows_json json"));

        for(JsonObject j : shows_json){

            if(j.get("name").toString().contains("\'")){
                String new_name = j.get("name").getAsString();
                new_name = new_name.replaceAll("'","''");
                j.remove("name");
                j.addProperty("name",new_name);
            }
            if(j.get("summary").toString().contains("\'")){
                String new_summary = j.get("summary").getAsString();
                new_summary = new_summary.replaceAll("'","''");
                j.remove("summary");
                j.addProperty("summary",new_summary);
            }
            db.insert_json("shows",j);
        }
    }

    public static ArrayList<JsonObject> get_json_from_API(URL[] request_urls) throws IOException, InterruptedException {
        ArrayList<JsonObject> shows = new ArrayList<JsonObject>();
        int counter = 0;
        for(URL request_url : request_urls){
            int max_retries = 0;
            String show_url = "";
            while(max_retries < 10){
                try{
                    HttpsURLConnection url_connection = (HttpsURLConnection) request_url.openConnection();
                    url_connection.setRequestMethod("GET");
                    url_connection.setRequestProperty("UserAgent","Mozilla/5.0");

                    int response_code = url_connection.getResponseCode();

                    if(response_code != 200){
                        throw new Exception();
                    }

                    InputStreamReader read_content = new InputStreamReader((InputStream) url_connection.getContent());
                    JsonElement json_raw = JsonParser.parseReader(read_content);
                    read_content.close();
                    JsonObject json = json_raw.getAsJsonObject();


                    shows.add(json);

                    TimeUnit.MILLISECONDS.sleep(50);
                    counter++;
                    break;
                }catch(Exception e){
                    e.printStackTrace();
                    TimeUnit.SECONDS.sleep(12);
                    max_retries++;
                }
            }
        }

        return shows;
    }
}

