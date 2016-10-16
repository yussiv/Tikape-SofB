package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.AlueDao;
import tikape.runko.database.KetjuDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Ketju;
import tikape.runko.util.InputScrubber;

public class Main {

    public static void main(String[] args) throws Exception {
        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:testi.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("JDBC_DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("JDBC_DATABASE_URL");
        } 
        Database database = new Database(jdbcOsoite);
        
        Spark.staticFileLocation("/public");
        AlueDao alueDao = new AlueDao(database);
        KetjuDao ketjuDao = new KetjuDao(database);
        ViestiDao viestiDao = new ViestiDao(database);

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        port(getHerokuAssignedPort());
        
        // Session luonti ennen jokaista kutsua
        before("*", (req, res) -> {
            Session session = req.session(true);
            // lisätään nimimerkki sessioon, niin käyttäjän ei tarvitse manuaalisesti syöttää sitä joka kerta
            if (req.queryParams().contains("nimimerkki")) {
                session.attribute("nimimerkki", InputScrubber.clean(req.queryParams("nimimerkki")));
            }
        });

        // Listaa alueet
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        // Listaa Alueen ketjut
        get("/alue/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            HashMap map = new HashMap<>();
            map.put("ketjut", ketjuDao.findAllFromAlue(id));
            map.put("alue", alueDao.findOne(id));
            map.put("nimimerkki", req.session().attribute("nimimerkki"));
            map.put("sivu", 0);
            map.put("sivut", 0);

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());

        // Listaa ketjun viestit
        get("/ketju/:id/page/:pg", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            int sivu = Integer.parseInt(req.params("pg"));
            int alueId = ketjuDao.findOne(id).getAlueId();
            int pc = viestiDao.getPageCount(id);
            HashMap map = new HashMap<>();
            map.put("viestit", viestiDao.findAllFromKetju(id, sivu));
            map.put("alue", alueDao.findOne(alueId));
            map.put("ketju", ketjuDao.findOne(id));
            map.put("sivu", sivu);
            map.put("sivut", pc);
            map.put("nimimerkki", req.session().attribute("nimimerkki"));


            return new ModelAndView(map, "ketju");
        }, new ThymeleafTemplateEngine());

        // Lisää alueen
        post("/", (req, res) -> {
            String nimi = InputScrubber.clean(req.queryParams("alue_nimi"));
            if(!nimi.isEmpty()) {
                int alueId = alueDao.create(nimi);
                if (alueId > 0) {
                    res.redirect("/alue/" + alueId);
                }
            }
            res.redirect("/");
            return null;
        });
        
        // Lisää ketjun
        post("/alue/:id", (req, res) -> {
            String nimimerkki = InputScrubber.clean(req.queryParams("nimimerkki"));
            String otsikko = InputScrubber.clean(req.queryParams("otsikko"));
            String viesti = InputScrubber.clean(req.queryParams("viesti"));
            int alueId = Integer.parseInt(req.params("id"));
            
            if(!nimimerkki.isEmpty() && !otsikko.isEmpty() && !viesti.isEmpty()) {
                int ketjuId = ketjuDao.create(alueId, otsikko);
                if(ketjuId > 0) {
                    // lisätään ensimmäinen viesti
                    viestiDao.create(ketjuId, nimimerkki, viesti);
                    res.redirect("/ketju/" + ketjuId);
                    return "";
                } 
            } 
            res.redirect("/alue/" + alueId);
            
            return "";
        });

        // Lisää viestin
        post("/ketju/:id/page/:pg", (req, res) -> {
            String nimimerkki = InputScrubber.clean(req.queryParams("nimimerkki"));
            String viesti = InputScrubber.clean(req.queryParams("viesti"));
            int ketjuId = Integer.parseInt(req.params("id"));
            int sivu = Integer.parseInt(req.params("pg"));
            if(!nimimerkki.isEmpty() && !viesti.isEmpty())
                viestiDao.create(ketjuId, nimimerkki, viesti);
            
            res.redirect("/ketju/" + ketjuId + "/page/" + sivu);
            return null;
        });
        
        // Poistaa alueen
        post("/alue/:id/delete", (req, res) -> {
            int alueId = Integer.parseInt(req.params("id"));
            alueDao.delete(alueId);
            res.redirect("/");
            return null;
        });

        // Poistaa ketjun
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

        // Poistaa viestin
        post("/viesti/:id/delete", (req, res) -> {
            int viestiId = Integer.parseInt(req.params("id"));
            viestiDao.delete(viestiId);
            res.redirect("/");
            return null;
        });
    }
    // herokun porttinumeron palautus
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
