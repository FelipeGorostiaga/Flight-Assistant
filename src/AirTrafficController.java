import java.sql.Time;
import java.util.List;


public class AirTrafficController implements AirTrafficControllerInterface{

    private List<Airport> airports;

    private class Airport implements AirportInterface{

        private String name;
        private double lat;
        private double lon;
        private List<Flight> flights;
    }

    private class Flight implments FlightInterface{

        private String name;
        private Integer num;
        private String days;
        private Airport origin;
        private Airport destination;
        private Double price;
        private Time departureTime;
        private String duration;
    }

}
