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

        AlueDao alueDao = new AlueDao(database);
        KetjuDao ketjuDao = new KetjuDao(database);
        ViestiDao viestiDao = new ViestiDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/alue/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            HashMap map = new HashMap<>();
            map.put("ketjut", ketjuDao.findAllFromAlue(id));
            map.put("alue", alueDao.findOne(id));

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());

        get("/ketju/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            int alueId = ketjuDao.findOne(id).getAlueId();
            HashMap map = new HashMap<>();
            map.put("viestit", viestiDao.findAllFromKetju(id));
            map.put("alue", alueDao.findOne(alueId));
            map.put("ketju", ketjuDao.findOne(id));
//            map.put("viesti", viestiDao.findOne(id));

            return new ModelAndView(map, "ketju");
        }, new ThymeleafTemplateEngine());

        post("/", (req, res) -> {
            String nimi = req.queryParams("alue_nimi");
            alueDao.update(0, nimi); //0 ei tee mitään
            res.redirect("/");
            return null;
        });

        post("/ketju/:id", (req, res) -> {

            String nimimerkki = req.queryParams("nimimerkki");
            String viesti = req.queryParams("viesti");
            int ketjuId = Integer.parseInt(req.params("id"));
            viestiDao.update(ketjuId, nimimerkki, viesti);
            res.redirect("/ketju/" + ketjuId);
            return null;
        });
        
        post("/alue/:id", (req, res) -> {

            String nimimerkki = req.queryParams("nimimerkki");
            String otsikko = req.queryParams("otsikko");
            String viesti = req.queryParams("viesti");
            int alueId = Integer.parseInt(req.params("id"));
            ketjuDao.update(alueId, otsikko);
            int ketjuId = ketjuDao.getNewestKetju(alueId);
            viestiDao.update(ketjuId, nimimerkki, viesti);
            res.redirect("/alue/" + alueId);
            return null;
        });
    }
}
