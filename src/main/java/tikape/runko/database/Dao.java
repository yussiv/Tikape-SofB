package tikape.runko.database;

import java.sql.*;
import java.util.*;

public interface Dao<T, K> {

    T findOne(K key) throws SQLException;

    List<T> findAll() throws SQLException;

    void delete(K key) throws SQLException;
    
    //String... jotta voidaan käyttää samaa metodia viesteillä, alueilla ja ketjuilla.
    void update(int id, String... args) throws SQLException;
}
