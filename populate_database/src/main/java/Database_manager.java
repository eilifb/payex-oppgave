import com.google.gson.JsonObject;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database_manager {
    private Connection connection;

    public void init(String database_file) throws SQLException, ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");
        String database_location = "jdbc:sqlite:" + System.getProperty("user.dir") + "/" + database_file;
        connection = DriverManager.getConnection(database_location);
    }

    public void create_table(String name, String columns) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "CREATE TABLE shows(show_id integer NOT NULL, shows_json JSON NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void insert_json(String tablename, JsonObject json) throws SQLException {
        Statement stmt = connection.createStatement();
        int id = json.get("id").getAsInt();
        String sql = "INSERT INTO " + tablename + " (show_id, shows_json) values (" + id + ", '" + json.toString() + "')";
        stmt.executeUpdate(sql);
        stmt.close();
    }
}
