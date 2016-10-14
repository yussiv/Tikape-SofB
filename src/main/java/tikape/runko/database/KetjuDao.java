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
import tikape.runko.Formatteri;
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

        Connection connection = database.getConnection();
//        PreparedStatement count = connection.prepareStatement("select count(*) from alue a, ketju k where k.alue_id=a.id and  k.alue_id= ? ;");
        PreparedStatement stmt = connection.prepareStatement("SELECT k.id as id, k.nimi as nimi, count(v.id) as viestit, max(v.aika) as timestamp FROM Ketju k, Viesti v WHERE k.id=v.ketju_id AND k.alue_id= ? GROUP BY k.id ORDER BY v.aika DESC;");
        stmt.setObject(1, key);
        
//        ResultSet ketjutCount = count.executeQuery();
        ResultSet rs = stmt.executeQuery();
        List<Ketju> ketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer alueId = key;
            String nimi = rs.getString("nimi");
            Integer viestienMaara = rs.getInt("viestit");
            String timestamp = rs.getString("timestamp");

            ketjut.add(new Ketju(id, alueId, nimi, viestienMaara, Formatteri.formatoi(timestamp)));
        }
        
        rs.close();
        stmt.close();
        connection.close();

        return ketjut;
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
        database.update("DELETE FROM Ketju WHERE id = ?", key);
    }

   

    @Override
    public void update(int id, String... args) throws SQLException {
        
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Ketju(alue_id, nimi) VALUES(?, ?)");
        
        int alue_id = id;
        String nimi = null;
        for(String s: args){
            //Ketjun avauksessa args sisältää vain yhden arvon, ketjun nimen.
            nimi = s;
        }
        stmt.setInt(1, alue_id);
        stmt.setString(2, nimi);
        stmt.execute();
        
        stmt.close();
        connection.close();

    }
}
