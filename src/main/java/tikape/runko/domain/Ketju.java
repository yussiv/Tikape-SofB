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
public class Ketju {
    
    private Integer id;
    private Integer alueId;
    private String nimi;
    private Integer viestienMaara;
    private String viimeisinViesti;
    
    public Ketju(Integer id, Integer alueId, String nimi) {
        this.id = id;
        this.alueId = alueId;
        this.nimi = nimi;
    }

    public Ketju(Integer id, Integer alueId, String nimi, Integer viestienMaara, String viimeisimmanViestinAika) {
        this(id, alueId, nimi);
        this.viestienMaara = viestienMaara;
        this.viimeisinViesti = viimeisimmanViestinAika;
    }

    public Integer getViestienMaara() {
        return viestienMaara;
    }

    public String getViimeisinViesti() {
        return viimeisinViesti;
    }

    public void setViestienMaara(Integer viestienMaara) {
        this.viestienMaara = viestienMaara;
    }

    public void setViimeisinViesti(String viimeisinViesti) {
        this.viimeisinViesti = viimeisinViesti;
    }
    
    public Integer getId(){
        return id;
    }
    
    public void setId(Integer id){
        this.id = id;
    }
    
    public Integer getAlueId(){
        return alueId;
    }
    
    public void setAlueId(Integer alueId){
        this.alueId = alueId;
    }
    
    public String getNimi(){
        return nimi;
    }
    
    public void setNimi(String nimi){
        this.nimi = nimi;
    }
}
