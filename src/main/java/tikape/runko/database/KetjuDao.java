
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
            String query = "INSERT INTO Ketju (nimi,alue_id) VALUES (?, ?) RETURNING id";
            List<Integer> ids = database.queryAndCollect(query, rs -> rs.getInt("id"), name, areaId);
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
    public Ketju findOne(Integer id) throws SQLException {
        String query = "SELECT * FROM Ketju WHERE id = ?";
        List<Ketju> threads = database.queryAndCollect(query, rs -> new Ketju(rs.getInt("id"), rs.getInt("alue_id"), rs.getString("nimi")), id);
        if(threads.isEmpty())
            return null;
        
        return threads.get(0);
    }

    @Override
    public List<Ketju> findAll() throws SQLException {
        return database.queryAndCollect("SELECT * FROM Ketju", rs -> new Ketju(rs.getInt("id"), rs.getInt("alue_id"), rs.getString("nimi")));
    }

    public List<Ketju> findAllFromAlue(int id) throws SQLException {
        return getPageFromAlue(id, -1, -1);
    }

    public List<Ketju> getPageFromAlue(int id, int itemCount, int pageNumber) throws SQLException {
        int offset = itemCount * (pageNumber - 1);
        String query = "SELECT K.id, K.nimi, count(V.id) AS viestit, max(V.aika) AS timestamp FROM Ketju K "
                + "LEFT JOIN Viesti V ON K.id=V.ketju_id WHERE K.alue_id = ? "
                + "GROUP BY K.id ORDER BY timestamp DESC LIMIT ? OFFSET ?";

        // näytetään "kaikki" ketjut
        if (itemCount < 1) {
            offset = 0;
            itemCount = 10000;
        }

        return database.queryAndCollect(query, rs -> new Ketju(
                rs.getInt("id"),
                id,
                rs.getString("nimi"),
                rs.getInt("viestit"),
                Formatteri.formatoi(rs.getString("timestamp"))
        ), id, itemCount, offset);
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
        String query = "SELECT count(id) AS ketjut FROM Ketju WHERE alue_id = ?";
        List<Integer> threads = database.queryAndCollect(query, rs -> rs.getInt("ketjut"), id);
        if (!threads.isEmpty()) {
            int threadCount = threads.get(0);
            return itemCount > 0 ? (threadCount + itemCount - 1) / itemCount : 1;
        }
        return 0;
    }
}
