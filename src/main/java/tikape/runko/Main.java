package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.AlueDao;
import tikape.runko.database.KetjuDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Ketju;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:testi.db");
        //database.init();

        Spark.staticFileLocation("/public");
        AlueDao alueDao = new AlueDao(database);
        KetjuDao ketjuDao = new KetjuDao(database);
        ViestiDao viestiDao = new ViestiDao(database);

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        port(getHerokuAssignedPort());

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
            int alueId = alueDao.create(nimi);
            if (alueId > 0) {
                res.redirect("/alue/" + alueId);
            }
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

        // Ketjun poisto
        post("/ketju/:id/delete", (req, res) -> {
            int ketjuId = Integer.parseInt(req.params("id"));
            Ketju ketju = ketjuDao.findOne(ketjuId);
            if (ketju != null) {
                int alueId = ketju.getAlueId();
                ketjuDao.delete(ketjuId);
                res.redirect("/alue/" + alueId);
            }
            res.redirect("/");
            return "";
        });

        post("/alue/:id", (req, res) -> {

            String nimimerkki = req.queryParams("nimimerkki");
            String otsikko = req.queryParams("otsikko");
            String viesti = req.queryParams("viesti");
            int alueId = Integer.parseInt(req.params("id"));
            //uuden ketjun avaus
            ketjuDao.update(alueId, otsikko);
            //haetaan äsken avatun ketjun id
            int ketjuId = ketjuDao.getNewestKetju(alueId);
            //avataan uusi ketju
            viestiDao.update(ketjuId, nimimerkki, viesti);
            res.redirect("/alue/" + alueId);
            return null;
        });

        // Alueen poisto
        post("/alue/:id/delete", (req, res) -> {
            int alueId = Integer.parseInt(req.params("id"));
            alueDao.delete(alueId);
            res.redirect("/");
            return null;
        });
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
