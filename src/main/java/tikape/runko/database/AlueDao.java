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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tikape.runko.domain.Alue;
import tikape.runko.domain.Viesti;
import tikape.runko.Formatteri;

/**
 *
 * @author kari
 */
public class AlueDao implements Dao<Alue, Integer> {
    
    private Database database;
    Formatteri formatter = new Formatteri();
    
    public AlueDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Alue o = new Alue(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }
    
    @Override
    public List<Alue> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue");

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        
        ViestiDao viestiDao = new ViestiDao(database);
        
        while (rs.next()) {
            try {
                Integer id = rs.getInt("id");
                String nimi = rs.getString("nimi");
                Integer viestienMaara = viestiDao.findCountByAreaId(id);
                Viesti viimeisinViesti = viestiDao.findLastViestiByAreaId(id);
                String timestamp = viimeisinViesti == null ? "" : viimeisinViesti.getAika();
                String aika = formatter.formatoi(timestamp);
                alueet.add(new Alue(id, nimi, viestienMaara, aika));
            } catch (ParseException ex) {
                Logger.getLogger(AlueDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
    @Override
    public void update(int id, String... args) throws SQLException {
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue(nimi) VALUES(?)");
        
        String nimi = null;
        for(String s: args){
            //Alueen lisäyksessä args sisältää vain yhden arvon, alueen nimen.
            nimi = s;
        }
        stmt.setString(1, nimi);
        stmt.execute();
        
        stmt.close();
        connection.close();

    }
}
