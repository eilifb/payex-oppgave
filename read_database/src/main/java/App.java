import spark.Request;
import spark.Response;
import spark.Route;


import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static spark.Spark.*;

public class App {
    private static Connection connection;

    public static void main(String[] args) throws Exception{
        String database_file = "show_db";
        Class.forName("org.sqlite.JDBC");
        String database_location = "jdbc:sqlite:"+System.getProperty("user.dir")+"/"+database_file;
        connection = DriverManager.getConnection(database_location);

        System.out.println( "Dette er et TVserie rapporteringsprogram som på en god dag kanskje fungerer.\n"+
                            "Skriv inn 'hjelp' for en liste over rapporter som kan genereres.");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String user_input = input.readLine();
            String result = "";
            switch (user_input){
                case "hjelp":
                    print_help();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "top10":
                    result = get_top10();
                    if(!result.equals("ERROR"))
                        write_results("top10.txt",result);
                        System.out.println("Skrev rapporten til 'top10.txt'");
                    break;
                case "topnetwork":
                    result = get_top_network();
                    if(!result.equals("ERROR"))
                        write_results("top_network.txt",result);
                        System.out.println("Skrev rapporten til 'top_network.txt'");
                    break;
                default:
                    System.out.println("Kunne ikke tolke input.");
                    break;

            }
        }
    }

    public static String get_top10(){
        StringBuilder string_builder = new StringBuilder();
        try {
            String sql = "select json_extract(shows_json,'$.name') as name ,json_extract(shows_json,'$.rating.average') as rating from shows order by json_extract(shows_json,'$.rating.average') desc limit 10;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                string_builder.append(rs.getString("name") + ": " + rs.getString("rating") + "\n");
            }
        }catch (Exception e){
            System.out.println("noe gikk galt! :(");
            e.printStackTrace();
            string_builder.append("ERROR");
        }
        return string_builder.toString();
    }

    public static String get_top_network(){
        StringBuilder string_builder = new StringBuilder();
        try {
            String sql = "select \n" +
                    "                            json_extract(shows_json, '$.network.name'), \n" +
                    "                            sum(json_extract(shows_json, '$.rating.average')),\n" +
                    "                            (select json_extract(shows_json, '$.name') group by json_extract(shows_json, '$.network.name') from shows limit 1)\n" +
                    "                            from shows group by json_extract(shows_json, '$.network.name')";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                string_builder.append(rs.getString("name") + ": " + rs.getString("rating") + "\n");
            }
        }catch (Exception e){
            System.out.println("noe gikk galt! :(");
            string_builder.append("ERROR");
        }
        return string_builder.toString();
    }


    public static void print_help(){
        System.out.println(
                            "'top10' - Gir en liste over de 10 seriene med høyest rating\n\n"+
                            "'topnetwork' - Gir en liste over kanalene med høyest gjennomsnittlig rating. fungerer desverre ikke.\n\n"+
                            "'exit' - Avslutter programmet."
                            );
    }

    public static void write_results(String filename,String result) throws Exception{
        File file = new File(filename);
        file.createNewFile();
        FileWriter writer = new FileWriter(filename);
        writer.write(result);
        writer.close();
    }
}
