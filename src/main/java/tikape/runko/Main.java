package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.database.AlueDao;
import tikape.runko.database.KetjuDao;

public class Main {

    public static void main(String[] args) throws Exception {
        //Database database = new Database("jdbc:sqlite:opiskelijat.db");
        Database database = new Database("jdbc:sqlite:testi.db");
        database.init();

        //OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);
        AlueDao alueDao = new AlueDao(database);
        KetjuDao ketjuDao = new KetjuDao(database);
        
        
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
        
        get("/ketju", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");
            
            return new ModelAndView(map, "ketju");
        }, new ThymeleafTemplateEngine());
        
        
        post("/uusi/alue", (req, res) ->{
            String nimi = req.queryParams("alue_nimi");
            System.out.println(nimi);
            alueDao.update(nimi);
            res.redirect("/");
           return 0;
        });
    }
}
