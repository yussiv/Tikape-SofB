package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.AlueDao;
import tikape.runko.database.KetjuDao;
import tikape.runko.database.ViestiDao;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:testi.db");
        database.init();

        //OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);
        AlueDao alueDao = new AlueDao(database);
        KetjuDao ketjuDao = new KetjuDao(database);
        ViestiDao viestiDao = new ViestiDao(database);
        
        
          get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
          
          get("/alue/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("ketjut", ketjuDao.findAllFromAlue(Integer.parseInt(req.params("id"))));
            
            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
          
        get("/alue", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");
            
            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
//        get("/ketju", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("viesti", "tervehdys");
//            
//            return new ModelAndView(map, "ketju");
//        }, new ThymeleafTemplateEngine());

        get("/ketju/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", viestiDao.findAllFromKetju(Integer.parseInt(req.params("id"))));
            
            return new ModelAndView(map, "ketju");
        }, new ThymeleafTemplateEngine());
        
        
        post("/", (req, res) ->{
            String nimi = req.queryParams("alue_nimi");
            alueDao.update(0, nimi); //0 ei tee mitään
            res.redirect("/");
           return null;
        });
        
        post("ketju/:id", (req, res) ->{
            
            String nimimerkki = req.queryParams("nimimerkki");
            String viesti = req.queryParams("viesti");
            int ketjuId = Integer.parseInt(req.params("id"));
            viestiDao.update(ketjuId, nimimerkki, viesti);
            res.redirect("/ketju/" + ketjuId);
           return null;
        });
    }
}
