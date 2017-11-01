import java.util.HashSet;
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

    /**
     * Receives flight insert instruccion from terminal
     * @param airline
     * @param flightNum
     * @param weekDays
     * @param origin
     * @param destination
     * @param departureTime
     * @param duration
     * @param price
     * @return String inicating whether the insertion could be done or not
     */
    String receiveFlightInsertion(String airline, int flightNum, List<Integer> weekDays, String origin, String destination,
                           String departureTime, String duration, double price);

    /**
     * Receives 'findRoute' instruction with params form terminal
     * @param origin orig airport string
     * @param destination dest airport string
     * @param priority priority string
     * @param weekDays List of integers representing days
     * @return String representing whether rhe action could be performed of not.
     * Terminal will print whatever it gets
     */
    String receiveFindRoute(String origin, String destination, String priority, List<Integer> weekDays);

    /**
     * Receives 'worldTrip' instruction from terminal and calls the right method according to the priority
     * @param origin origin airport
     * @param priority priority chosen by user
     * @param weekDays List of integers representing days
     * @return String representing whether rhe action could be performed of not.
     * Terminal will print whatever it gets
     */
    String receiveWorldTrip(String origin, String priority, List<Integer> weekDays);
}
