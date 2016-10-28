/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

/**
 *
 * @author kari
 */
public class Alue {
    
    private int id;
    private String nimi;
    private int viestienMaara;
    private int ketjujenMaara;
    private String viimeisinViesti;
    
    public Alue(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public Alue(int id, String nimi, int ketjujenMaara, int viestienMaara, String viimeisimmanViestinAika) {
        this(id, nimi);
        this.viestienMaara = viestienMaara;
        this.ketjujenMaara = ketjujenMaara;
        this.viimeisinViesti = viimeisimmanViestinAika;
    }

    public void setKetjujenMaara(int ketjujenMaara) {
        this.ketjujenMaara = ketjujenMaara;
    }

    public void setViestienMaara(int viestienMaara) {
        this.viestienMaara = viestienMaara;
    }

    public void setViimeisinViesti(String viimeisinViesti) {
        this.viimeisinViesti = viimeisinViesti;
    }

    public int getKetjujenMaara() {
        return ketjujenMaara;
    }

    public int getViestienMaara() {
        return viestienMaara;
    }

    public String getViimeisinViesti() {
        return viimeisinViesti;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
}
