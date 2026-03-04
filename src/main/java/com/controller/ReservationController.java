package main.java.com.controller;

import main.java.com.annote.Controllera;
import main.java.com.annote.GETY;
import main.java.com.annote.POSTA;
import main.java.com.annote.RequestMapping;
import main.java.com.annote.RequestParam;
import main.java.com.annote.JSON;
import main.java.com.framework.ModelyAndView;
import main.java.com.entity.Client;
import main.java.com.entity.Hotel;
import main.java.com.entity.Reservation;
import main.java.com.dto.ClientDTO;
import main.java.com.dto.HotelDTO;
import main.java.com.dto.ReservationDTO;
import main.java.com.service.ReservationService;
import main.java.com.service.TokenService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

@Controllera
@RequestMapping("/reservation")
public class ReservationController {
    
    private ReservationService service = new ReservationService();
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

    @GETY("/formulaire")
    public ModelyAndView showForm() {
        ModelyAndView mv = new ModelyAndView("reservation-form");
        try {
            List<Client> clients = service.getAllClients();
            List<Hotel> hotels = service.getAllHotels();
            mv.addObject("clients", clients);
            mv.addObject("hotels", hotels);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("error", "Erreur lors du chargement des données: " + e.getMessage());
        }
        return mv;
    }

    @POSTA("/create")
    public ModelyAndView createReservation(
            @RequestParam("idClient") String idClient,
            @RequestParam("idHotel") String idHotel,
            @RequestParam("dateReservation") String dateReservation,
            @RequestParam("heureReservation") String heureReservation,
            @RequestParam("nbPersonnes") String nbPersonnes) {
        
        ModelyAndView mv = new ModelyAndView("reservation-result");
        try {
            LocalDate date = LocalDate.parse(dateReservation);
            LocalTime heure = LocalTime.parse(heureReservation);
            int hotelId = Integer.parseInt(idHotel);
            int nbPers = Integer.parseInt(nbPersonnes);
            
            Reservation reservation = service.createReservation(idClient, hotelId, date, heure, nbPers);
            
            // Récupérer la réservation complète avec les détails
            Reservation fullReservation = service.getReservationById(reservation.getId());
            
            mv.addObject("reservation", fullReservation);
            mv.addObject("success", true);
            mv.addObject("message", "Réservation créée avec succès!");
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", "Erreur lors de la création de la réservation: " + e.getMessage());
        }
        return mv;
    }

    @GETY("/")
    public ModelyAndView index() {
        ModelyAndView mv = new ModelyAndView("reservation-index");
        return mv;
    }

    @GETY("/api/list")
    @JSON
    public ModelyAndView listReservationsAPI(@RequestParam("token") String token) {
        ModelyAndView mv = new ModelyAndView("api");
        
        // Vérification du token
        if (!validateToken(token)) {
            mv.addObject("success", false);
            mv.addObject("error", "Token invalide ou expiré");
            mv.addObject("code", 401);
            return mv;
        }
        
        try {
            List<Reservation> reservations = service.getAllReservations();
            List<ReservationDTO> dtos = convertToDTO(reservations);
            mv.addObject("reservations", dtos);
            mv.addObject("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    @GETY("/api/list/filter")
    @JSON
    public ModelyAndView listReservationsByDateAPI(
            @RequestParam("date") String date,
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
            LocalDate filterDate = LocalDate.parse(date);
            List<Reservation> reservations = service.getReservationsByDate(filterDate);
            List<ReservationDTO> dtos = convertToDTO(reservations);
            mv.addObject("reservations", dtos);
            mv.addObject("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("success", false);
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }

    private List<ReservationDTO> convertToDTO(List<Reservation> reservations) {
        List<ReservationDTO> dtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        for (Reservation reservation : reservations) {
            ReservationDTO dto = new ReservationDTO();
            dto.setId(reservation.getId());
            dto.setDateReservation(reservation.getDateReservation().format(formatter));
            if (reservation.getHeureReservation() != null) {
                dto.setHeureReservation(reservation.getHeureReservation().toString());
            }
            dto.setNbPersonnes(reservation.getNbPersonnes());
            dto.setIdClient(reservation.getIdClient());
            dto.setIdHotel(reservation.getIdHotel());
            
            if (reservation.getClient() != null) {
                ClientDTO clientDTO = new ClientDTO(
                    reservation.getClient().getId(),
                    reservation.getClient().getNom(),
                    reservation.getClient().getPrenom(),
                    reservation.getClient().getEmail()
                );
                dto.setClient(clientDTO);
            }
            
            if (reservation.getHotel() != null) {
                HotelDTO hotelDTO = new HotelDTO(
                    reservation.getHotel().getId(),
                    reservation.getHotel().getNom(),
                    reservation.getHotel().getAdresse(),
                    reservation.getHotel().getVille(),
                    reservation.getHotel().getPays(),
                    reservation.getHotel().getIdLieu()
                );
                dto.setHotel(hotelDTO);
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }
}
