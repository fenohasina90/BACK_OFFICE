package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.framework.ModelyAndView;
import main.java.com.entity.Lieu;
import main.java.com.service.LieuService;

import java.util.List;

@Controllera
@RequestMapping("/lieu")
public class LieuController {

    private final LieuService service = new LieuService();

    @GETY("/")
    public ModelyAndView index() {
        return new ModelyAndView("lieu-index");
    }

    @GETY("/liste")
    public ModelyAndView liste() {
        ModelyAndView mv = new ModelyAndView("lieu-liste");
        try {
            List<Lieu> lieux = service.getAllLieux();
            mv.addObject("lieux", lieux);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement des lieux: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/formulaire")
    public ModelyAndView showForm() {
        return new ModelyAndView("lieu-form");
    }

    @GETY("/edit")
    public ModelyAndView showEditForm(@RequestParam("id") String id) {
        ModelyAndView mv = new ModelyAndView("lieu-form");
        try {
            int lieuId = Integer.parseInt(id);
            Lieu lieu = service.getLieuById(lieuId);
            mv.addObject("lieu", lieu);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement du lieu: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/create")
    public ModelyAndView create(@RequestParam("lieu") String libelle) {
        ModelyAndView mv = new ModelyAndView("lieu-result");
        try {
            Lieu lieu = service.createLieu(libelle);
            mv.addObject("lieu", lieu);
            mv.addObject("message", "Lieu créé avec succès!");
            mv.addObject("operation", "create");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors de la création: " + e.getMessage());
            mv.addObject("operation", "create");
        }
        return mv;
    }

    @POSTA("/update")
    public ModelyAndView update(@RequestParam("id") String id, @RequestParam("lieu") String libelle) {
        ModelyAndView mv = new ModelyAndView("lieu-result");
        try {
            int lieuId = Integer.parseInt(id);
            service.updateLieu(lieuId, libelle);
            Lieu lieu = service.getLieuById(lieuId);
            mv.addObject("lieu", lieu);
            mv.addObject("message", "Lieu mis à jour avec succès!");
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
        ModelyAndView mv = new ModelyAndView("lieu-result");
        try {
            int lieuId = Integer.parseInt(id);
            service.deleteLieu(lieuId);
            mv.addObject("message", "Lieu supprimé avec succès!");
            mv.addObject("operation", "delete");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors de la suppression: " + e.getMessage());
            mv.addObject("operation", "delete");
        }
        return mv;
    }
}
