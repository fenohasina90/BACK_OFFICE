package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.framework.ModelyAndView;
import main.java.com.service.PlanificationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controllera
@RequestMapping("/planification")
public class PlanificationController {

    private final PlanificationService service = new PlanificationService();

    @GETY("/")
    public ModelyAndView index() {
        ModelyAndView mv = new ModelyAndView("planification-form");
        return mv;
    }

    @POSTA("/run")
    public ModelyAndView run(@RequestParam("date") String date) {
        ModelyAndView mv = new ModelyAndView("planification-form");
        try {
            LocalDate d = LocalDate.parse(date);
            PlanificationService.PlanificationResult result = service.planifier(d);
            mv.addObject("success", true);
            mv.addObject("result", result);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", "Erreur lors de la planification: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/runV2")
    public ModelyAndView runV2(@RequestParam("date") String date) {
        ModelyAndView mv = new ModelyAndView("planification-form");
        try {
            LocalDate d = LocalDate.parse(date);
            PlanificationService.PlanificationResult result = service.planifierV2(d);
            mv.addObject("success", true);
            mv.addObject("result", result);
            mv.addObject("mode", "V2");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", "Erreur lors de la planification V2: " + e.getMessage());
            mv.addObject("mode", "V2");
        }
        return mv;
    }

    @GETY("/liste")
    public ModelyAndView liste(@RequestParam("date") String date,
                              @RequestParam("dateDebut") String dateDebut,
                              @RequestParam("dateFin") String dateFin) {
        ModelyAndView mv = new ModelyAndView("planification-liste");
        try {
            LocalDate start;
            LocalDate end;

            if (dateDebut != null && !dateDebut.trim().isEmpty()) {
                start = LocalDate.parse(dateDebut);
            } else if (date != null && !date.trim().isEmpty()) {
                start = LocalDate.parse(date);
            } else {
                throw new IllegalArgumentException("Paramètre date manquant");
            }

            if (dateFin != null && !dateFin.trim().isEmpty()) {
                end = LocalDate.parse(dateFin);
            } else {
                end = start;
            }

            if (end.isBefore(start)) {
                LocalDate tmp = start;
                start = end;
                end = tmp;
            }

            List<Map<String, Object>> rows = service.getReservationsPlanifiees(start, end);
            mv.addObject("dateDebut", start);
            mv.addObject("dateFin", end);
            mv.addObject("rows", rows);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/voyages")
    public ModelyAndView voyages(@RequestParam("dateDebut") String dateDebut,
                                 @RequestParam("dateFin") String dateFin) {
        ModelyAndView mv = new ModelyAndView("planification-voyages");
        try {
            LocalDate start;
            LocalDate end;

            if (dateDebut != null && !dateDebut.trim().isEmpty()) {
                start = LocalDate.parse(dateDebut);
            } else {
                start = LocalDate.now();
            }

            if (dateFin != null && !dateFin.trim().isEmpty()) {
                end = LocalDate.parse(dateFin);
            } else {
                end = start;
            }

            if (end.isBefore(start)) {
                LocalDate tmp = start;
                start = end;
                end = tmp;
            }

            mv.addObject("dateDebut", start);
            mv.addObject("dateFin", end);
            mv.addObject("voyages", service.getVoyages(start, end));
            mv.addObject("voyageCountsByVoiture", service.getVoyageCountsByVoiture(start, end));
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("voyages", new ArrayList<>());
            mv.addObject("error", "Erreur lors du chargement des voyages: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/voyage")
    public ModelyAndView voyage(@RequestParam("id") String id) {
        ModelyAndView mv = new ModelyAndView("planification-voyage-detail");
        try {
            int voyageId = Integer.parseInt(id);
            mv.addObject("voyageId", voyageId);
            mv.addObject("stops", service.getStops(voyageId));
            mv.addObject("distanceTotalKm", service.getVoyageDistanceTotalKm(voyageId));

            PlanificationService.VoyageTiming timing = service.getVoyageTiming(voyageId);
            mv.addObject("arrivalByStopId", timing.getArrivalAtDestinationByStopId());
            mv.addObject("arrivalAeroport", timing.getArrivalAtAeroport());
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement du voyage: " + e.getMessage());
            mv.addObject("stops", new ArrayList<>());
        }
        return mv;
    }
}
