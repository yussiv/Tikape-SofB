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
public class Viesti {
    private int id;
    private int ketjuId;
    private String sisalto;
    private String aika;
    private String nimimerkki;
    
    public Viesti(int id, int ketjuId, String sisalto, String aika, String nimimerkki) {
        this.id = id;
        this.ketjuId = ketjuId;
        this.sisalto = sisalto;
        this.aika = aika;
        this.nimimerkki = nimimerkki;
    }
    
    public Integer getId(){
        return id;
    }
    
    public void setId(Integer id){
        this.id = id;
    }
    
    public Integer getKetjuId(){
        return ketjuId;
    }
    
    public void setKetjuId(Integer alueId){
        this.ketjuId = alueId;
    }
    
    public String getSisalto(){
        return sisalto;
    }
    
    public void setSisalto(String sisalto){
        this.sisalto = sisalto;
    }
    
    public String getAika(){
        return aika;
    }
    
    public void setAika(String aika){
        this.aika = aika;
    }
    
    public String getNimimerkki(){
        return nimimerkki;
    }
    
    public void setNimimerkki(String nimimerkki){
        this.nimimerkki = nimimerkki;
    }
    
}
