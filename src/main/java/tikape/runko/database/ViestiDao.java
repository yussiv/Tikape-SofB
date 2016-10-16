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
import tikape.runko.domain.Viesti;
import tikape.runko.util.Formatteri;

/**
 *
 * @author kari
 */
public class ViestiDao implements Dao<Viesti, Integer>  {
    
    private Database database;
    
    public ViestiDao(Database database){
        this.database = database;
    }
    
    public void create(int ketjuId, String nimimerkki, String viesti) throws SQLException {
        database.update("INSERT INTO Viesti(ketju_id, sisalto, aika, nimimerkki) VALUES(?, ?, CURRENT_TIMESTAMP, ?)", ketjuId, viesti, nimimerkki);
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
    
    public List<Viesti> findAllFromKetju(Integer key, Integer page) throws SQLException {
        int sivu = page * 10 - 10;
        Connection connection = database.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE ketju_id = ? LIMIT 10 OFFSET ? ");
        stmt.setObject(1, key);
        stmt.setObject(2, sivu);
        
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer ketjuId = rs.getInt("ketju_id");
            String sisalto = rs.getString("sisalto");
            String aika = rs.getString("aika");
            String nimimerkki = rs.getString("nimimerkki");

            viestit.add(new Viesti(id, ketjuId, sisalto, Formatteri.formatoi(aika), nimimerkki));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }
    
    public int getPageCount(Integer ketjuId) throws SQLException {
        int count = -1;
        List<Integer> counts = database.queryAndCollect("SELECT count(v.id) AS c FROM Viesti v, Ketju k WHERE k.id=v.ketju_id AND ketju_id= ? ", rs -> rs.getInt("c"), ketjuId);
            if(counts.size() == 1)
                count = counts.get(0);
        int pgs = count / 10 + 1;
        return pgs;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
    
    @Override
    public void update(int ketjuId, String... args) throws SQLException {
       // nope, not here
    }
}