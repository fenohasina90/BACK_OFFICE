package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.framework.ModelyAndView;
import main.java.com.entity.Distance;
import main.java.com.entity.Lieu;
import main.java.com.service.DistanceService;
import main.java.com.service.LieuService;

import java.util.List;

@Controllera
@RequestMapping("/distance")
public class DistanceController {

    private final DistanceService service = new DistanceService();
    private final LieuService lieuService = new LieuService();

    @GETY("/")
    public ModelyAndView index() {
        return new ModelyAndView("distance-index");
    }

    @GETY("/liste")
    public ModelyAndView liste() {
        ModelyAndView mv = new ModelyAndView("distance-liste");
        try {
            List<Distance> distances = service.getAllDistances();
            mv.addObject("distances", distances);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement des distances: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/formulaire")
    public ModelyAndView showForm() {
        ModelyAndView mv = new ModelyAndView("distance-form");
        try {
            List<Lieu> lieux = lieuService.getAllLieux();
            mv.addObject("lieux", lieux);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement des lieux: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/edit")
    public ModelyAndView showEditForm(@RequestParam("id") String id) {
        ModelyAndView mv = new ModelyAndView("distance-form");
        try {
            int distanceId = Integer.parseInt(id);
            Distance distance = service.getDistanceById(distanceId);
            List<Lieu> lieux = lieuService.getAllLieux();
            mv.addObject("distance", distance);
            mv.addObject("lieux", lieux);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement de la distance: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/create")
    public ModelyAndView create(@RequestParam("fromLieu") String fromLieu,
                                @RequestParam("toLieu") String toLieu,
                                @RequestParam("distanceKm") String distanceKm) {
        ModelyAndView mv = new ModelyAndView("distance-result");
        try {
            int fromId = Integer.parseInt(fromLieu);
            int toId = Integer.parseInt(toLieu);
            double km = Double.parseDouble(distanceKm);
            Distance d = service.createDistance(fromId, toId, km);
            mv.addObject("distance", d);
            mv.addObject("message", "Distance créée avec succès!");
            mv.addObject("operation", "create");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors de la création: " + e.getMessage());
            mv.addObject("operation", "create");
        }
        return mv;
    }

    @POSTA("/update")
    public ModelyAndView update(@RequestParam("id") String id,
                                @RequestParam("fromLieu") String fromLieu,
                                @RequestParam("toLieu") String toLieu,
                                @RequestParam("distanceKm") String distanceKm) {
        ModelyAndView mv = new ModelyAndView("distance-result");
        try {
            int distanceId = Integer.parseInt(id);
            int fromId = Integer.parseInt(fromLieu);
            int toId = Integer.parseInt(toLieu);
            double km = Double.parseDouble(distanceKm);
            service.updateDistance(distanceId, fromId, toId, km);
            Distance d = service.getDistanceById(distanceId);
            mv.addObject("distance", d);
            mv.addObject("message", "Distance mise à jour avec succès!");
            mv.addObject("operation", "update");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors de la mise à jour: " + e.getMessage());
            mv.addObject("operation", "update");
        }
        return mv;
    }

    @POSTA("/delete")
    public ModelyAndView delete(@RequestParam("id") String id) {
        ModelyAndView mv = new ModelyAndView("distance-result");
        try {
            int distanceId = Integer.parseInt(id);
            service.deleteDistance(distanceId);
            mv.addObject("message", "Distance supprimée avec succès!");
            mv.addObject("operation", "delete");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors de la suppression: " + e.getMessage());
            mv.addObject("operation", "delete");
        }
        return mv;
    }
}
