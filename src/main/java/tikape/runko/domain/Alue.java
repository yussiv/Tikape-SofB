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
    
    private Integer id;
    private String nimi;
    private Integer viestienMaara;
    private String viimeisinViesti;
    
    public Alue(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public Alue(Integer id, String nimi, Integer viestienMaara, String viimeisimmanViestinAika) {
        this(id, nimi);
        this.viestienMaara = viestienMaara;
        this.viimeisinViesti = viimeisimmanViestinAika;
    }

    public void setViestienMaara(Integer viestienMaara) {
        this.viestienMaara = viestienMaara;
    }

    public void setViimeisinViesti(String viimeisinViesti) {
        this.viimeisinViesti = viimeisinViesti;
    }

    public Integer getViestienMaara() {
        return viestienMaara;
    }

    public String getViimeisinViesti() {
        return viimeisinViesti;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
}
