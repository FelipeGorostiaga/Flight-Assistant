import java.util.List;

/*GUARDA EL GRAFO DE LISTA DE ADYACENCIA (DIGRAFO)*/
public interface AirTrafficControllerInterface {


    boolean insertFlight(String airline, Integer number, Airport origin, Airport destination, List<Integer> departureDays, Integer duration, Integer departureTime, double price);
    boolean insertAirport(String name, double latitude, double longitude);

    boolean deleteAirport(String name);
    boolean deleteFlight(String airlineName, Integer Number);

    boolean insertAllAirports(List<Airport> airports);
    boolean deleteAllAirports();
    boolean replaceAllAirports(List<Airport> airport);


    boolean insertAllFlights(List<Flight> flights);
    boolean deleteAllFlights();
    boolean replaceAllFlights(List<Airport> flights);



    List<Flight> findRouteMinFlightTime(Airport origin, Airport destiny, List<Integer> departureDays);
    List<Flight> findRouteMinPrice(Airport origin, Airport destiny, List<Integer> departureDays);
    List<Flight> findRouteMinTotalTime(Airport origin, Airport destiny, List<Integer> departureDays);

    List<Flight> worldTripMinFlightTime();
    List<Flight> worldTripMinPrice();
    List<Flight> worldTripMinTotalTime();
}
