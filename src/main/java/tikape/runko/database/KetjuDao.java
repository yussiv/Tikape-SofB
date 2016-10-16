/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.util.Formatteri;
import tikape.runko.domain.Ketju;

/**
 *
 * @author kari
 */
public class KetjuDao implements Dao<Ketju, Integer> {

    private Database database;

    public KetjuDao(Database database) {
        this.database = database;
    }

    public int create(int areaId, String name) throws SQLException {
        int id = -1;

        if (database.isPostgres()) {
            // postgressistä saa palautusarvona viimeksi lisätyn id:n
            List<Integer> ids = database.queryAndCollect("INSERT INTO Ketju (nimi,alue_id) VALUES (?, ?) RETURNING id", rs -> rs.getInt("id"), name, areaId);
            if (ids.size() == 1) {
                id = ids.get(0);
            }
        } else {
            // SQlitellä tehdään pitkän kaavan mukaan
            Connection conn = database.getConnection();
            PreparedStatement stm = conn.prepareStatement("INSERT INTO Ketju (nimi,alue_id) VALUES (?, ?)");
            stm.setString(1, name);
            stm.execute();
            // haetaan viimeksi luotu id
            ResultSet rs = conn.createStatement().executeQuery("SELECT last_insert_rowid() as id");

            if (rs.next()) {
                id = rs.getInt("id");
            }

            stm.close();
            conn.close();
        }
        return id;
    }

    @Override
    public Ketju findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Ketju WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        Integer alueId = rs.getInt("alue_id");
        String nimi = rs.getString("nimi");

        Ketju o = new Ketju(id, alueId, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<Ketju> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Ketju");

        ResultSet rs = stmt.executeQuery();
        List<Ketju> ketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer alueId = rs.getInt("alue_id");
            String nimi = rs.getString("nimi");

            ketjut.add(new Ketju(id, alueId, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return ketjut;
    }

    public List<Ketju> findAllFromAlue(Integer key) throws SQLException {

        String query;
        if (database.isPostgres()) {
            query = "SELECT k.id, k.nimi, max(vs.viestit) AS viestit, max(vs.timestamp) AS timestamp FROM (SELECT v.ketju_id,"
                    + " count(v.id) AS viestit, max(v.aika) AS timestamp FROM viesti v GROUP BY v.ketju_id) vs JOIN ketju k ON"
                    + " k.id = vs.ketju_id WHERE k.alue_id = ? GROUP BY k.id ORDER BY timestamp DESC LIMIT 10 OFFSET 0;";
        } else {
            query = "SELECT k.id as id, k.nimi as nimi, count(v.id) as viestit, max(v.aika) as timestamp FROM Ketju k, Viesti v "
                    + "WHERE k.id=v.ketju_id AND k.alue_id= ? GROUP BY k.id ORDER BY v.aika DESC;";
        }

        return database.queryAndCollect(query, rs -> new Ketju(
                rs.getInt("id"),
                key,
                rs.getString("nimi"),
                rs.getInt("viestit"),
                Formatteri.formatoi(rs.getString("timestamp"))
        ), key);
    }

    public int getNewestKetju(Integer key) throws SQLException {

        Connection connection = database.getConnection();
        // Hakee kaikki kyseisen alueen ketjut ja valitsee niistä uusimman ketjun avausta varten
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Ketju WHERE alue_id = ? ORDER BY id DESC LIMIT 1");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        int id = 0;
        while (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        //database.update("DELETE FROM Ketju WHERE id = ?", key);
    }

    @Override
    public void update(int id, String... args) throws SQLException {
        // this is not the method you are looking for
    }

    public int getPageCount(int id, int itemCount) throws SQLException {
        List<Integer> threads = database.queryAndCollect("SELECT count(id) AS ketjut FROM Ketju WHERE alue_id = ?", rs -> rs.getInt("ketjut"), id);
        if (!threads.isEmpty()) {
            int threadCount = threads.get(0);
            return itemCount > 0 ? (int) Math.ceil(1.0*threadCount / itemCount) : 1;
        }
        return 0;
    }

    public List<Ketju> getPageFromAlue(int id, int itemCount, int pageNumber) throws SQLException {
        int offset = itemCount * (pageNumber - 1);
        String query;
        if (database.isPostgres()) {
            query = "SELECT k.id, k.nimi, MAX(vs.viestit) AS viestit, MAX(vs.timestamp) AS timestamp FROM (SELECT v.ketju_id,"
                    + " count(v.id) AS viestit, MAX(v.aika) AS timestamp FROM viesti v GROUP BY v.ketju_id) vs JOIN ketju k ON"
                    + " k.id = vs.ketju_id WHERE k.alue_id = ? GROUP BY k.id ORDER BY timestamp DESC LIMIT ? OFFSET ?";
        } else {
            query = "SELECT k.id as id, k.nimi as nimi, count(v.id) as viestit, max(v.aika) as timestamp FROM Ketju k, Viesti v "
                    + "WHERE k.id=v.ketju_id AND k.alue_id= ? GROUP BY k.id ORDER BY v.aika DESC LIMIT ? OFFSET ?";
        }

        return database.queryAndCollect(query, rs -> new Ketju(
                rs.getInt("id"),
                id,
                rs.getString("nimi"),
                rs.getInt("viestit"),
                Formatteri.formatoi(rs.getString("timestamp"))
        ), id, itemCount, offset);
    }
}
