import java.util.List;

/*GUARDA EL GRAFO DE LISTA DE ADYACENCIA (DIGRAFO)*/
public interface AirTrafficControllerInterface {


    boolean insertFlight(String airline, Integer number, AirTrafficController.Airport origin, AirTrafficController.Airport destination, List<Integer> departureDays, Integer duration, Integer departureTime, double price);
    boolean insertAirport(String name, double latitude, double longitude);

    boolean deleteAirport(String name);
    boolean deleteFlight(String airlineName, Integer Number);

    void insertAllAirports(List<AirTrafficController.Airport> airports);
    void deleteAllAirports();
    void replaceAllAirports(List<AirTrafficController.Airport> airport);


    void insertAllFlights(List<AirTrafficController.Flight> flights);
    void deleteAllFlights();
    void replaceAllFlights(List<AirTrafficController.Flight> flights);



    List<AirTrafficController.Flight> findRouteMinFlightTime(AirTrafficController.Airport origin, AirTrafficController.Airport destiny, List<Integer> departureDays);
    List<AirTrafficController.Flight> findRouteMinPrice(AirTrafficController.Airport origin, AirTrafficController.Airport destiny, List<Integer> departureDays);
    List<AirTrafficController.Flight> findRouteMinTotalTime(AirTrafficController.Airport origin, AirTrafficController.Airport destiny, List<Integer> departureDays);

    List<AirTrafficController.Flight> worldTripMinFlightTime();
    List<AirTrafficController.Flight> worldTripMinPrice();
    List<AirTrafficController.Flight> worldTripMinTotalTime();
}
