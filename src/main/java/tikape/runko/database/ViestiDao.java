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
import tikape.runko.domain.Ketju;
import tikape.runko.domain.Viesti;
/**
 *
 * @author kari
 */
public class ViestiDao implements Dao<Viesti, Integer>  {
    
    private Database database;
    
    public ViestiDao(Database database){
        this.database = database;
    }
    
    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        Integer ketjuId = rs.getInt("ketju_id");
        String sisalto = rs.getString("sisalto");
        String aika = rs.getString("aika");
        String nimimerkki = rs.getString("nimimerkki");

        Viesti o = new Viesti(id, ketjuId, sisalto, aika, nimimerkki);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }
    
    @Override
    public List<Viesti> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti");

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer ketjuId = rs.getInt("ketju_id");
            String sisalto = rs.getString("sisalto");
            String aika = rs.getString("aika");
            String nimimerkki = rs.getString("nimimerkki");

            viestit.add(new Viesti(id, ketjuId, sisalto, aika, nimimerkki));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }
    
    public List<Viesti> findAllFromKetju(Integer key) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE ketju_id = ?");
        stmt.setObject(1, key);
        
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer ketjuId = rs.getInt("ketju_id");
            String sisalto = rs.getString("sisalto");
            String aika = rs.getString("aika");
//            String aika = "joku aika";
            String nimimerkki = rs.getString("nimimerkki");

            viestit.add(new Viesti(id, ketjuId, sisalto, aika, nimimerkki));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }

    @Override
    public void update(String nimi) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
