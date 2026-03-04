package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.annote.JSON;
import main.java.com.framework.ModelyAndView;
import main.java.com.entity.Voiture;
import main.java.com.dto.VoitureDTO;
import main.java.com.service.VoitureService;
import main.java.com.service.TokenService;

import java.util.List;
import java.util.ArrayList;

@Controllera
@RequestMapping("/voiture/api")
public class VoitureAPIController {
    
    private VoitureService voitureService = new VoitureService();
    private TokenService tokenService = new TokenService();

    // Méthode pour vérifier le token
    private boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        try {
            return tokenService.isTokenValid(token);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GETY("/list")
    @JSON
    public ModelyAndView listAPI(@RequestParam("token") String token) {
        ModelyAndView mv = new ModelyAndView("api");
        
        // Vérification du token
        if (!validateToken(token)) {
            mv.addObject("success", false);
            mv.addObject("error", "Token invalide ou expiré");
            mv.addObject("code", 401);
            return mv;
        }
        
        try {
            List<Voiture> voitures = voitureService.getAllVoitures();
            List<VoitureDTO> dtos = convertToDTO(voitures);
            mv.addObject("voitures", dtos);
            mv.addObject("success", true);
            mv.addObject("count", dtos.size());
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    @GETY("/get")
    @JSON
    public ModelyAndView getAPI(@RequestParam("id") String id, @RequestParam("token") String token) {
        ModelyAndView mv = new ModelyAndView("api");
        
        // Vérification du token
        if (!validateToken(token)) {
            mv.addObject("success", false);
            mv.addObject("error", "Token invalide ou expiré");
            mv.addObject("code", 401);
            return mv;
        }
        
        try {
            int voitureId = Integer.parseInt(id);
            Voiture voiture = voitureService.getVoitureById(voitureId);
            
            if (voiture != null) {
                VoitureDTO dto = convertToDTO(voiture);
                mv.addObject("voiture", dto);
                mv.addObject("success", true);
            } else {
                mv.addObject("success", false);
                mv.addObject("error", "Voiture non trouvée");
                mv.addObject("code", 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    @POSTA("/create")
    @JSON
    public ModelyAndView createAPI(
            @RequestParam("typeCarburant") String typeCarburant,
            @RequestParam("nbPlace") String nbPlace,
            @RequestParam("token") String token) {
        
        ModelyAndView mv = new ModelyAndView("api");
        
        // Vérification du token
        if (!validateToken(token)) {
            mv.addObject("success", false);
            mv.addObject("error", "Token invalide ou expiré");
            mv.addObject("code", 401);
            return mv;
        }
        
        try {
            int places = Integer.parseInt(nbPlace);
            Voiture voiture = voitureService.createVoiture(typeCarburant, places);
            VoitureDTO dto = convertToDTO(voiture);
            
            mv.addObject("voiture", dto);
            mv.addObject("success", true);
            mv.addObject("message", "Voiture créée avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    @POSTA("/update")
    @JSON
    public ModelyAndView updateAPI(
            @RequestParam("id") String id,
            @RequestParam("typeCarburant") String typeCarburant,
            @RequestParam("nbPlace") String nbPlace,
            @RequestParam("token") String token) {
        
        ModelyAndView mv = new ModelyAndView("api");
        
        // Vérification du token
        if (!validateToken(token)) {
            mv.addObject("success", false);
            mv.addObject("error", "Token invalide ou expiré");
            mv.addObject("code", 401);
            return mv;
        }
        
        try {
            int voitureId = Integer.parseInt(id);
            int places = Integer.parseInt(nbPlace);
            voitureService.updateVoiture(voitureId, typeCarburant, places);
            
            Voiture voiture = voitureService.getVoitureById(voitureId);
            VoitureDTO dto = convertToDTO(voiture);
            
            mv.addObject("voiture", dto);
            mv.addObject("success", true);
            mv.addObject("message", "Voiture mise à jour avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    @POSTA("/delete")
    @JSON
    public ModelyAndView deleteAPI(@RequestParam("id") String id, @RequestParam("token") String token) {
        ModelyAndView mv = new ModelyAndView("api");
        
        // Vérification du token
        if (!validateToken(token)) {
            mv.addObject("success", false);
            mv.addObject("error", "Token invalide ou expiré");
            mv.addObject("code", 401);
            return mv;
        }
        
        try {
            int voitureId = Integer.parseInt(id);
            voitureService.deleteVoiture(voitureId);
            
            mv.addObject("success", true);
            mv.addObject("message", "Voiture supprimée avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    private List<VoitureDTO> convertToDTO(List<Voiture> voitures) {
        List<VoitureDTO> dtos = new ArrayList<>();
        for (Voiture voiture : voitures) {
            dtos.add(convertToDTO(voiture));
        }
        return dtos;
    }

    private VoitureDTO convertToDTO(Voiture voiture) {
        VoitureDTO dto = new VoitureDTO();
        dto.setId(voiture.getId());
        dto.setTypeCarburant(voiture.getTypeCarburant());
        dto.setTypeCarburantLibelle(voiture.getTypeCarburantLibelle());
        dto.setNbPlace(voiture.getNbPlace());
        return dto;
    }
}
