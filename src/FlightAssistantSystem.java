import java.sql.Time;


public class FlightAssistantSystem implements FlightAssistant {

    private List<Aeropuerto> aeropuertos;

    private class Aeropuerto {

        private String name;
        private double lat;
        private double lon;
        private List<Vuelo> vuelos;
    }

    private class Vuelo {

        private String name;
        private Integer num;
        private String days;
        private Aeropuerto origin;
        private Aeropuerto destination;
        private Double price;
        private Time departureTime;
        private String duration;
    }

}
