import java.util.HashSet;
import java.util.List;

/*GUARDA EL GRAFO DE LISTA DE ADYACENCIA (DIGRAFO)*/
public interface AirTrafficControllerInterface {


    boolean insertFlight(String airline, Integer number, Airport origin, Airport destination, List<Integer> departureDays, Integer duration, Integer departureTime, double price);
    boolean insertAirport(String name, double latitude, double longitude);

    boolean deleteAirport(String name);
    boolean deleteFlight(String airlineName, Integer Number);

    void insertAllAirports(List<Airport> airports);
    void deleteAllAirports();
    void replaceAllAirports(List<Airport> airport);


    void insertAllFlights(List<Flight> flights);
    void deleteAllFlights();
    void replaceAllFlights(List<Flight> flights);


    List<Flight> findRouteMinFlightTime(Airport origin, Airport destiny, List<Integer> departureDays);
    List<Flight> findRouteMinPrice(Airport origin, Airport destiny, List<Integer> departureDays);
    List<Flight> findRouteMinTotalTime(Airport origin, Airport destiny, List<Integer> departureDays);

    List<Flight> worldTripMinFlightTime();
    List<Flight> worldTripMinPrice();
    List<Flight> worldTripMinTotalTime();

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
                           String departureTime, int duration, double price);

    /**
     * Receives 'findRoute' instruction with params form terminal
     * @param origin orig airport string
     * @param destination dest airport string
     * @param priority priority string
     * @param weekDays List of integers representing days
     * @return String representing whether rhe action could be performed of not.
     * Terminal will print whatever it gets
     */
    RequestResult receiveFindRoute(String origin, String destination, String priority, List<Integer> weekDays);

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
