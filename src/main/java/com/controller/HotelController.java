package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.framework.ModelyAndView;
import main.java.com.entity.Hotel;
import main.java.com.entity.Lieu;
import main.java.com.service.HotelService;
import main.java.com.service.LieuService;

import java.util.List;

@Controllera
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService service = new HotelService();
    private final LieuService lieuService = new LieuService();

    @GETY("/")
    public ModelyAndView index() {
        return new ModelyAndView("hotel-index");
    }

    @GETY("/liste")
    public ModelyAndView liste() {
        ModelyAndView mv = new ModelyAndView("hotel-liste");
        try {
            List<Hotel> hotels = service.getAllHotels();
            mv.addObject("hotels", hotels);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement des hôtels: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/formulaire")
    public ModelyAndView showForm() {
        ModelyAndView mv = new ModelyAndView("hotel-form");
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
        ModelyAndView mv = new ModelyAndView("hotel-form");
        try {
            int hotelId = Integer.parseInt(id);
            Hotel hotel = service.getHotelById(hotelId);
            List<Lieu> lieux = lieuService.getAllLieux();
            mv.addObject("hotel", hotel);
            mv.addObject("lieux", lieux);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement de l'hôtel: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/create")
    public ModelyAndView create(@RequestParam("nom") String nom,
                                @RequestParam("adresse") String adresse,
                                @RequestParam("ville") String ville,
                                @RequestParam("pays") String pays,
                                @RequestParam("idLieu") String idLieu) {
        ModelyAndView mv = new ModelyAndView("hotel-result");
        try {
            int lieuId = Integer.parseInt(idLieu);
            Hotel hotel = service.createHotel(nom, adresse, ville, pays, lieuId);
            mv.addObject("hotel", hotel);
            mv.addObject("message", "Hôtel créé avec succès!");
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
                                @RequestParam("nom") String nom,
                                @RequestParam("adresse") String adresse,
                                @RequestParam("ville") String ville,
                                @RequestParam("pays") String pays,
                                @RequestParam("idLieu") String idLieu) {
        ModelyAndView mv = new ModelyAndView("hotel-result");
        try {
            int hotelId = Integer.parseInt(id);
            int lieuId = Integer.parseInt(idLieu);
            service.updateHotel(hotelId, nom, adresse, ville, pays, lieuId);
            Hotel hotel = service.getHotelById(hotelId);
            mv.addObject("hotel", hotel);
            mv.addObject("message", "Hôtel mis à jour avec succès!");
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
        ModelyAndView mv = new ModelyAndView("hotel-result");
        try {
            int hotelId = Integer.parseInt(id);
            service.deleteHotel(hotelId);
            mv.addObject("message", "Hôtel supprimé avec succès!");
            mv.addObject("operation", "delete");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors de la suppression: " + e.getMessage());
            mv.addObject("operation", "delete");
        }
        return mv;
    }
}
