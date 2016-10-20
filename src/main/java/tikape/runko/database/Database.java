package tikape.runko.database;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database<T> {

    private boolean debug;
    private String address;

    public Database(String address) throws Exception {
        this.address = address;
        init();
    }
    
    public boolean isPostgres() {
        return this.address.contains("postgres");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(address);
    }

    public void setDebugMode(boolean d) {
        debug = d;
    }

    public int update(String updateQuery, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(updateQuery);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        int changes = stmt.executeUpdate();

        if (debug) {
            System.out.println("---");
            System.out.println(updateQuery);
            System.out.println("Changed rows: " + changes);
            System.out.println("---");
        }

        stmt.close();
        conn.close();

        return changes;
    }

    public List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
        Connection conn = getConnection();
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }

        List<T> rows = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            if (debug) {
                System.out.println("---");
                System.out.println(query);
                debug(rs);
                System.out.println("---");
            }

            rows.add(col.collect(rs));
        }

        rs.close();
        stmt.close();
        conn.close();

        return rows;
    }

    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(
                    rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }

    private void init() {
        List<String> lauseet = null;
        if (this.address.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("CREATE TABLE Alue (id SERIAL PRIMARY KEY NOT NULL, nimi VARCHAR(200) NOT NULL);");
        lista.add("CREATE TABLE Ketju (id SERIAL PRIMARY KEY NOT NULL, alue_id INT NOT NULL, nimi VARCHAR(200) NOT NULL, FOREIGN KEY (alue_id) REFERENCES Alue(id));");
        lista.add("CREATE TABLE Viesti (id SERIAL PRIMARY KEY NOT NULL, ketju_id INT NOT NULL, sisalto TEXT NOT NULL, aika TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL, nimimerkki VARCHAR(30) NOT NULL, FOREIGN KEY (ketju_id) REFERENCES Ketju(id));");

        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        lista.add("CREATE TABLE Alue (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nimi VARCHAR(200) NOT NULL);");
        lista.add("CREATE TABLE Ketju (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, alue_id INTEGER NOT NULL, nimi VARCHAR(200) NOT NULL);");
        lista.add("CREATE TABLE Viesti (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ketju_id INTEGER NOT NULL, sisalto TEXT NOT NULL, aika TIMESTAMP NOT NULL, nimimerkki VARCHAR(30) NOT NULL);");

        return lista;
    }
}
