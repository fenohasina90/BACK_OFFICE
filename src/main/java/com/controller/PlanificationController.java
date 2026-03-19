package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.framework.ModelyAndView;
import main.java.com.service.PlanificationService;
import main.java.com.entity.Voyage;
import main.java.com.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            int waitMinutes = service.getParametreActif().getTempsAttenteMin();

            Map<Integer, Voyage> voyageById = new HashMap<>();
            if (rows != null) {
                for (Map<String, Object> r : rows) {
                    Integer voyageId = (Integer) r.get("voyageId");
                    if (voyageId != null && !voyageById.containsKey(voyageId)) {
                        Voyage v = service.getVoyageById(voyageId);
                        if (v != null) {
                            voyageById.put(voyageId, v);
                        }
                    }
                }
            }

            Map<LocalDate, Map<LocalTime, Map<Integer, Integer>>> totalByDateSlotReservation = new HashMap<>();
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                List<Reservation> reservations = service.getReservationsByDate(d);
                Map<LocalTime, Map<Integer, Integer>> totalBySlotRes = new HashMap<>();
                if (reservations != null) {
                    for (Reservation res : reservations) {
                        LocalTime slotStart = service.getCreneauStart(res.getHeureReservation());
                        totalBySlotRes.computeIfAbsent(slotStart, k -> new HashMap<>());
                        Map<Integer, Integer> byRes = totalBySlotRes.get(slotStart);
                        byRes.put(res.getId(), byRes.getOrDefault(res.getId(), 0) + res.getNbPersonnes());
                    }
                }
                totalByDateSlotReservation.put(d, totalBySlotRes);
            }

            Map<String, Map<Integer, Integer>> assignedBySlotKeyReservation = new HashMap<>();
            if (rows != null) {
                for (Map<String, Object> r : rows) {
                    Integer voyageId = (Integer) r.get("voyageId");
                    if (voyageId == null) {
                        continue;
                    }
                    Voyage v = voyageById.get(voyageId);
                    if (v == null || v.getDateVoyage() == null || v.getHeureDepart() == null) {
                        continue;
                    }
                    LocalTime slotStart = v.getHeureDepart().minusMinutes(waitMinutes);
                    String slotKey = v.getDateVoyage().toString() + "|" + slotStart.toString();

                    Integer reservationId = (Integer) r.get("reservationId");
                    Integer nbAffectees = (Integer) r.get("nbPersonnesAffectees");
                    if (reservationId == null || nbAffectees == null) {
                        continue;
                    }

                    assignedBySlotKeyReservation.computeIfAbsent(slotKey, k -> new HashMap<>());
                    Map<Integer, Integer> byRes = assignedBySlotKeyReservation.get(slotKey);
                    byRes.put(reservationId, byRes.getOrDefault(reservationId, 0) + nbAffectees);
                }
            }

            Map<String, List<String>> nonAssignesBySlotKey = new HashMap<>();
            for (Map.Entry<LocalDate, Map<LocalTime, Map<Integer, Integer>>> e : totalByDateSlotReservation.entrySet()) {
                LocalDate d = e.getKey();
                for (Map.Entry<LocalTime, Map<Integer, Integer>> se : e.getValue().entrySet()) {
                    LocalTime slotStart = se.getKey();
                    String slotKey = d.toString() + "|" + slotStart.toString();
                    Map<Integer, Integer> totals = se.getValue();
                    Map<Integer, Integer> assigned = assignedBySlotKeyReservation.getOrDefault(slotKey, new HashMap<>());

                    List<String> restes = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> tr : totals.entrySet()) {
                        int rid = tr.getKey();
                        int total = tr.getValue();
                        int aff = assigned.getOrDefault(rid, 0);
                        int reste = total - aff;
                        if (reste > 0) {
                            restes.add("R" + rid + " (" + reste + " pers)");
                        }
                    }
                    nonAssignesBySlotKey.put(slotKey, restes);
                }
            }

            Map<String, Map<String, Object>> grouped = new HashMap<>();
            if (rows != null) {
                for (Map<String, Object> r : rows) {
                    Integer voyageId = (Integer) r.get("voyageId");
                    if (voyageId == null) {
                        continue;
                    }
                    Voyage v = voyageById.get(voyageId);
                    if (v == null || v.getDateVoyage() == null || v.getHeureDepart() == null) {
                        continue;
                    }

                    LocalTime slotEnd = v.getHeureDepart();
                    LocalTime slotStart = slotEnd.minusMinutes(waitMinutes);
                    String slotLabel = slotStart.toString().substring(0, 5) + " - " + slotEnd.toString().substring(0, 5);

                    String groupKey = v.getDateVoyage().toString() + "|" + slotStart.toString() + "|" + v.getIdVoiture() + "|" + v.getId();
                    grouped.computeIfAbsent(groupKey, k -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("date", v.getDateVoyage());
                        m.put("creneau", slotLabel);
                        m.put("voitureId", v.getIdVoiture());
                        m.put("heureDepart", slotEnd.toString().substring(0, 5));
                        m.put("dureeMinutes", v.getDureeMinutes());
                        try {
                            LocalDateTime retour = service.getVoyageTimingDateTime(v.getId()).getArrivalAtAeroport();
                            m.put("heureRetour", retour != null ? retour.toLocalTime().toString().substring(0, 5) : "");
                        } catch (Exception ex) {
                            m.put("heureRetour", "");
                        }
                        m.put("voyageId", v.getId());
                        m.put("planifications", new ArrayList<String>());
                        m.put("hotels", new HashSet<String>());
                        m.put("reservations", new HashSet<String>());
                        m.put("nbPlace", null);
                        m.put("typeCarburant", null);
                        String slotKey = v.getDateVoyage().toString() + "|" + slotStart.toString();
                        List<String> na = nonAssignesBySlotKey.get(slotKey);
                        m.put("nonAssignes", na != null ? na : new ArrayList<String>());
                        return m;
                    });

                    Map<String, Object> g = grouped.get(groupKey);
                    Integer reservationId = (Integer) r.get("reservationId");
                    Integer nbAffectees = (Integer) r.get("nbPersonnesAffectees");
                    Integer nbPersonnes = (Integer) r.get("nbPersonnes");
                    String hotelNom = (String) r.get("hotelNom");
                    Integer nbPlace = (Integer) r.get("nbPlace");
                    String typeCarburant = (String) r.get("typeCarburant");

                    if (g.get("nbPlace") == null && nbPlace != null) {
                        g.put("nbPlace", nbPlace);
                    }
                    if (g.get("typeCarburant") == null && typeCarburant != null) {
                        g.put("typeCarburant", typeCarburant);
                    }

                    if (reservationId != null && nbAffectees != null) {
                        if (nbPersonnes != null) {
                            ((List<String>) g.get("planifications")).add("R" + reservationId + " (" + nbAffectees + "/" + nbPersonnes + " pers)");
                            ((Set<String>) g.get("reservations")).add("R" + reservationId + " (" + nbPersonnes + " pers)");
                        } else {
                            ((List<String>) g.get("planifications")).add("R" + reservationId + " (" + nbAffectees + " pers)");
                            ((Set<String>) g.get("reservations")).add("R" + reservationId);
                        }
                    }
                    if (hotelNom != null) {
                        ((Set<String>) g.get("hotels")).add(hotelNom);
                    }
                }
            }

            List<Map<String, Object>> creneauRows = new ArrayList<>();
            for (Map<String, Object> g : grouped.values()) {
                List<String> planifs = (List<String>) g.get("planifications");
                StringBuilder sb = new StringBuilder();
                if (planifs != null) {
                    for (int i = 0; i < planifs.size(); i++) {
                        if (i > 0) sb.append(" + ");
                        sb.append(planifs.get(i));
                    }
                }
                g.put("planificationLabel", sb.toString());

                Set<String> hotels = (Set<String>) g.get("hotels");
                g.put("hotelsLabel", hotels != null ? String.join("<br>", hotels) : "");

                Set<String> resSet = (Set<String>) g.get("reservations");
                g.put("reservationsLabel", resSet != null ? String.join("<br>", resSet) : "");

                List<String> nonAssignes = (List<String>) g.get("nonAssignes");
                g.put("nonAssignesLabel", nonAssignes != null ? String.join("<br>", nonAssignes) : "");

                creneauRows.add(g);
            }

            mv.addObject("creneauRows", creneauRows);
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
            Voyage voyage = service.getVoyageById(voyageId);
            mv.addObject("voyage", voyage);

            mv.addObject("stops", service.getStops(voyageId));
            mv.addObject("distanceTotalKm", service.getVoyageDistanceTotalKm(voyageId));

            if (voyage != null) {
                Map<Integer, Integer> counts = service.getVoyageCountsByVoiture(voyage.getDateVoyage(), voyage.getDateVoyage());
                mv.addObject("nbVoyagesVoitureDate", counts != null ? counts.getOrDefault(voyage.getIdVoiture(), 0) : 0);
                mv.addObject("departAeroport", LocalDateTime.of(voyage.getDateVoyage(), voyage.getHeureDepart()));
            }

            PlanificationService.VoyageTimingDateTime timingDT = service.getVoyageTimingDateTime(voyageId);
            mv.addObject("arrivalByStopIdDT", timingDT.getArrivalAtDestinationByStopId());
            mv.addObject("retourAeroport", timingDT.getArrivalAtAeroport());

            mv.addObject("legs", service.getVoyageLegs(voyageId));
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement du voyage: " + e.getMessage());
            mv.addObject("stops", new ArrayList<>());
        }
        return mv;
    }
}
