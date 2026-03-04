package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.framework.ModelyAndView;
import main.java.com.entity.Voiture;
import main.java.com.service.VoitureService;

import java.util.List;

@Controllera
@RequestMapping("/voiture")
public class VoitureController {
    
    private VoitureService service = new VoitureService();

    @GETY("/")
    public ModelyAndView index() {
        ModelyAndView mv = new ModelyAndView("voiture-index");
        return mv;
    }

    @GETY("/liste")
    public ModelyAndView liste() {
        ModelyAndView mv = new ModelyAndView("voiture-liste");
        try {
            List<Voiture> voitures = service.getAllVoitures();
            mv.addObject("voitures", voitures);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement des voitures: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/formulaire")
    public ModelyAndView showForm() {
        ModelyAndView mv = new ModelyAndView("voiture-form");
        return mv;
    }

    @GETY("/edit")
    public ModelyAndView showEditForm(@RequestParam("id") String id) {
        ModelyAndView mv = new ModelyAndView("voiture-form");
        try {
            int voitureId = Integer.parseInt(id);
            Voiture voiture = service.getVoitureById(voitureId);
            mv.addObject("voiture", voiture);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement de la voiture: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/create")
    public ModelyAndView create(
            @RequestParam("typeCarburant") String typeCarburant,
            @RequestParam("nbPlace") String nbPlace) {
        
        ModelyAndView mv = new ModelyAndView("voiture-result");
        try {
            int places = Integer.parseInt(nbPlace);
            Voiture voiture = service.createVoiture(typeCarburant, places);
            
            mv.addObject("voiture", voiture);
            mv.addObject("success", true);
            mv.addObject("message", "Voiture créée avec succès!");
            mv.addObject("action", "create");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", "Erreur lors de la création: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/update")
    public ModelyAndView update(
            @RequestParam("id") String id,
            @RequestParam("typeCarburant") String typeCarburant,
            @RequestParam("nbPlace") String nbPlace) {
        
        ModelyAndView mv = new ModelyAndView("voiture-result");
        try {
            int voitureId = Integer.parseInt(id);
            int places = Integer.parseInt(nbPlace);
            service.updateVoiture(voitureId, typeCarburant, places);
            
            Voiture voiture = service.getVoitureById(voitureId);
            mv.addObject("voiture", voiture);
            mv.addObject("success", true);
            mv.addObject("message", "Voiture mise à jour avec succès!");
            mv.addObject("action", "update");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", "Erreur lors de la mise à jour: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/delete")
    public ModelyAndView delete(@RequestParam("id") String id) {
        ModelyAndView mv = new ModelyAndView("voiture-result");
        try {
            int voitureId = Integer.parseInt(id);
            service.deleteVoiture(voitureId);
            
            mv.addObject("success", true);
            mv.addObject("message", "Voiture supprimée avec succès!");
            mv.addObject("action", "delete");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return mv;
    }
}
