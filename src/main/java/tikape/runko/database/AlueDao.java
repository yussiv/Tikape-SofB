
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import tikape.runko.domain.Alue;
import tikape.runko.Formatteri;

/**
 *
 * @author kari
 */
public class AlueDao implements Dao<Alue, Integer> {
    
    private Database database;
    
    public AlueDao(Database database) {
        this.database = database;
    }
    
    public int create(String name) throws SQLException {
        Connection conn = database.getConnection();
        
        int id = -1;
        
        if(database.isPostgres()) {
            List<Integer> ids = database.queryAndCollect("INSERT INTO Alue (nimi) VALUES (?) RETURNING id", rs -> rs.getInt("id"), name);
            if(ids.size() == 1)
                id = ids.get(0);
        }
        else {
            PreparedStatement stm = conn.prepareStatement("INSERT INTO Alue (nimi) VALUES (?)");
            stm.setString(1, name);
            stm.execute();
            // haetaan viimeksi luotu id
            ResultSet rs = conn.createStatement().executeQuery("SELECT last_insert_rowid() as id");

            if(rs.next())
                id = rs.getInt("id");

            stm.close();
            conn.close();
        }
        
        return id;
    }
    
    @Override
    public Alue findOne(Integer key) throws SQLException {
        String query = "SELECT * FROM Alue WHERE id = ?";
        List<Alue> areas = database.queryAndCollect(query, 
                rs -> new Alue(
                        rs.getInt("id"), 
                        rs.getString("nimi")
                ), key);
        
        if(areas.size() != 1)
            return null;
        
        return areas.get(0);
    }
    
    @Override
    public List<Alue> findAll() throws SQLException {
        String query = "SELECT A.id, A.nimi, KV.viestit, KV.timestamp "
                + "FROM (SELECT K.alue_id, count(V.id) as viestit, max(V.aika) as timestamp FROM Viesti V JOIN Ketju K ON K.id = V.ketju_id GROUP BY K.alue_id) KV "
                + "JOIN Alue A ON A.id = KV.alue_id "
                + "GROUP BY A.id ORDER BY A.nimi ASC;";
        
        return database.queryAndCollect(query, 
                rs -> new Alue(
                    rs.getInt("id"),
                    rs.getString("nimi"),
                    rs.getInt("viestit"),
                    Formatteri.formatoi(rs.getString("timestamp"))
                )
        );
    }

    @Override
    public void delete(Integer key) throws SQLException {
        database.update("DELETE FROM Alue WHERE id = ?", key);
    }
    
    @Override
    public void update(int id, String... args) throws SQLException {
        // Not implemented
    }
    
    public void updateName(int id, String name) throws SQLException {
        database.update("UPDATE Alue SET nimi = ? WHERE id = ?", id, name);
    }
}
