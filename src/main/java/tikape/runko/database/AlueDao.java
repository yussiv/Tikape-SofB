
package tikape.runko.database;

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
        String query = "SELECT A.id, A.nimi, COUNT(V.id) as viestit, MAX(V.aika) as timestamp "
                + "FROM Alue A JOIN Ketju K ON K.alue_id = A.id JOIN Viesti V ON V.ketju_id = K.id "
                + "GROUP BY A.nimi ORDER BY timestamp DESC";
        
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
