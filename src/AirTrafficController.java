

import javax.xml.transform.Result;
import java.lang.ref.ReferenceQueue;
import java.nio.channels.IllegalChannelGroupException;
import java.sql.Time;
import java.util.*;

/**
 * Handler for all airports and flights, controls air traffic system.
 *
 * Keeps track of all airports and flights, as well as finds the optimal route between two
 * airports given a priority constraint
 *
 * @see Airport
 * @see Flight
 * @see RequestResult
 */
public class AirTrafficController implements AirTrafficControllerInterface {

    private static final int DAY_TIME = 24 * 60;
    private static final int FLIGHT_TIME = 0;
    private static final int PRICE = 1;
    private static final int TOTAL_TIME = 2;

    private List<Airport> airportList = new ArrayList<>();
    private HashMap<String,Airport> airports = new HashMap<>();

    //Node for use in PriorityQueues while finding optimal routes
    //Contains info about flights and the search
    private class PQNode implements Comparable<PQNode> {
        private PQNode previous;
        private Flight flight;
        private double distance;
        private double[] info = new double[3];
        private int priority;
        private int departureDay;

        public PQNode( Flight flight, PQNode previous, double flightTime, double price, double totalTime, int priority, int day) {
            this.flight = flight;
            this.previous = previous;
            this.info[FLIGHT_TIME] = flightTime;
            this.info[PRICE] = price;
            this.info[TOTAL_TIME] = totalTime;
            this.priority = priority;
            this.distance = info[priority];
            this.departureDay = day;
        }

        public int compareTo(PQNode o) {
            return Double.valueOf(distance).compareTo(o.distance);
        }
    }





//****************************************   SYSTEM MANAGEMENT FUNCTIONS ************************************************


    /**
     * Adds a flight to its origin airports' flight hashmap
     *
     * @param airline   name of airline
     * @param number    flight number
     * @param origin    origin City
     * @param destination   destination city
     * @param departureDays days of the week the flight leaves the origin city
     * @param duration  length of flight
     * @param departureTime time flight leaves from origin city
     * @param price price of flight
     * @return  true if flight was successfully inserted into system
     */
    public boolean insertFlight(String airline, Integer number, Airport origin, Airport destination, List<Integer> departureDays, Integer duration, Integer departureTime, double price) {
        Airport start = airports.get(origin.getName());
        Airport end = airports.get(destination.getName());
        if(start != null && end != null && !start.equals(end)){
            if(start.getFlightsMap().containsKey(airline + number)){
                return false;
            }
            Flight nFlight = new Flight(airline, number, origin, destination, departureDays, duration, departureTime, price);
            start.getFlights().add(nFlight);
            start.getFlightsMap().put(nFlight.getAirline() + nFlight.getNumber(), nFlight);
            return true;
        }
        return false;
    }

    /**
     * Inserts airport into existing list of airports
     *
     * @param name  three letter name of airport
     * @param latitude
     * @param longitude
     * @return  true if the airport was successfully inserted into the list
     */
    public boolean insertAirport(String name, double latitude, double longitude) {
        if(!airports.containsKey(name)){
            Airport airport = new Airport(name, latitude, longitude);
            airports.put(name, airport);
            airportList.add(airport);
            return true;
        }
        return false;
    }

    /**
     * Removes an airport from the list of airports
     *
     * @param name  three letter name of airport
     * @return  true if removal was successful
     */
    public boolean deleteAirport(String name) {
        Airport aux = airports.get(name);
        if(aux == null){
            return false;
        }
        for(Airport airport : airportList){
            if(!airport.equals(aux)){
                for(int i = 0; i< airport.getFlights().size(); i++){
                    Flight flight = airport.getFlights().get(i);
                    if(flight.getDestination().getName().equals(name)){
                        airport.getFlights().remove(i);
                        break;
                    }
                }
            }
        }
        airports.remove(name);
        airportList.remove(aux);
        return true;
    }

    /**
     * Removes all temporary marking variables used in searches from airports.
     */
    private void clearMarks(){
        for(Airport airport : airportList){
            airport.setVisited(false);
            airport.setOrigin(null);
        }
    }

    /**
     * Removes a flight from the list of airports
     *
     * @param airline  name of airline
     * @param number   flight number
     * @return  true if removal was successful
     */
    public boolean deleteFlight(String airline, Integer number) {
        String name = airline + number;
        for(Airport airport: airportList){
            if(airport.getFlightsMap().containsKey(name)){
                airport.getFlightsMap().remove(name);
                for(int i = 0 ; i< airport.getFlights().size(); i++){
                    if(airport.getFlights().get(i).equals(name)){
                        airport.getFlights().remove(i);
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Inserts a list of airports into master list
     * @param airports List of airports to be added
     */
    public void insertAllAirports(List<Airport> airports) {
        for(Airport airport: airports){
            insertAirport(airport.getName(), airport.getLatitude(), airport.getLongitude());
        }
    }

    /**
     * Clears all airports from list by creating a new empty hashmap
     */
    public void deleteAllAirports() {
        airports = new HashMap<>();
        airportList = new ArrayList<>();
    }

    /**
     * Replaces all existing airports by first clearing the existing list and setting the new list to be the master
     * @param airports New list of airports to be made into the master list
     */
    public void replaceAllAirports(List<Airport> airports) {
        deleteAllAirports();
        insertAllAirports(airports);
    }

    /**
     * Inserts a list of flights into the hashmaps of their respective origin airports
     * @param flights
     */
    public void insertAllFlights(List<Flight> flights) {
        for(Flight flight : flights){
            insertFlight(flight.getAirline(), flight.getNumber(),  flight.getOrigin() , flight.getDestination(),
                    flight.getDepartureDays(), flight.getDuration(), flight.getDepartureTime(),  flight.getPrice());
        }
    }

    /**
     * Deletes all flights by clearing the flight list and hashmap of all airports
     */
    public void deleteAllFlights() {
        for(Airport airport : airportList){
            airport.setFlights(new HashMap<>());
            airport.setFlightList(new ArrayList<>());
        }
    }

    /**
     * Replaces all flights by clearing all flight information from airports, then inserting the new flights
     * @param flights
     */
    public void replaceAllFlights(List<Flight> flights) {
        deleteAllFlights();
        insertAllFlights(flights);

    }

    public void printAirports(){
        System.out.println("AIRPORT LIST:");
        for(Airport airport : airportList){
            System.out.println(airport.getName());
        }
    }

    public void printFlights(){
        System.out.println("FLIGHT LIST:");
        for(Airport airport : airportList){
            for(Flight flight : airport.getFlights()){
                System.out.println(flight);
            }
        }
    }

//****************************************   SYSTEM TASK FUNCTIONS ************************************************

    /**
     * Kicks off the search for an optimum route between two airports, given a priority of total time, flight time, or cost of the trip.
     *
     * @param origin    airport from which the trip begins
     * @param destination   airport at which the trip ends
     * @param priority  the variable for which the optimal route will be found
     * @param weekDays  days of the week the trip can begin
     * @return  a Request Result describing the trip's status, route, cost, and times
     *
     * @see RequestResult
     */
    public RequestResult receiveFindRoute(String origin, String destination, String priority, List<Integer> weekDays) {
        RequestResult ret = new RequestResult();
        Airport start = airports.get(origin);
        Airport finish = airports.get(destination);
        if(start == null || finish == null) return ret;
        switch(priority){
            case "ft": findRouteMinPriority(start, finish, FLIGHT_TIME, weekDays, ret); break;
            case "pr": findRouteMinPriority(start, finish, PRICE, weekDays, ret); break;
            case "tt": findRouteMinPriority(start, finish, TOTAL_TIME, weekDays, ret); break;
            default: throw new IllegalArgumentException("Not a valid priority");
        }
        return ret;
    }
    
    public RequestResult receiveWorldTrip(String origin, String priority, List<Integer> weekDays) {

        Airport start = airports.get(origin);

        if(start == null) new RequestResult(); //con flag seteado en false.

        switch(priority) {
            case "ft": return worldTrip(start, FLIGHT_TIME, weekDays);
            case "pr": return worldTrip(start, PRICE, weekDays);
            case "tt": return worldTrip(start, TOTAL_TIME, weekDays);
            default: throw new IllegalArgumentException("Not a valid priority");
        }

    }


    public boolean receiveFlightInsertion(String airline, int flightNum, List<Integer> weekDays, String origin, String destination,
                                   int departureTime, int duration, double price) {
        Airport orig = airports.get(origin);
        Airport dest = airports.get(destination);
        if(orig == null || dest == null) { return false; }
        else {
            return insertFlight(airline, flightNum, orig, dest, weekDays, duration, departureTime, price);
        }
    }

    /**
     * Implements a search for an optimal route between two airports by using a priority queue and backtracking
     *
     * @param origin    airport from which the trip begins
     * @param destination   airport at which the trip ends
     * @param priority  the variable for which the optimal route will be found
     * @param departureDays days on which the trip can begin
     * @param ret   the working RequestResult of the trip
     * @return  The
     */
    public RequestResult findRouteMinPriority(Airport origin, Airport destination,
                                                int priority, List<Integer> departureDays, RequestResult ret) {
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        clearMarks();
        if(priority != TOTAL_TIME)
            origin.setVisited(true);
          /*check if the route can be started on requested days*/
        for(Flight flight : origin.getFlights()){
            for(Integer day: departureDays){
                if(flight.getDepartureDays().contains(day)){
                    pq.offer(new PQNode(flight , null, flight.getDuration(),flight.getPrice(), flight.getDuration(), priority, day));
                    if(priority != TOTAL_TIME){
                        break;
                    }
                }
            }
        }
        while(!pq.isEmpty()){
            PQNode pqnode = pq.poll();
           // System.out.println("-------------Priority " + pqnode.priority + "Flight ");
           // System.out.println( pqnode.flight + "Price " + pqnode.info[PRICE] + "TotalTIme "+ pqnode.info[TOTAL_TIME] + "FlightTime" + pqnode.info[FLIGHT_TIME]);
           // System.out.println("DISTANCE " + pqnode.distance);
            Airport currentAirport = pqnode.flight.getDestination();
            if(!currentAirport.isVisited()){
                currentAirport.setOrigin(pqnode.flight.getOrigin());
                if(priority == TOTAL_TIME)
                    pqnode.flight.getOrigin().setVisited(true);
                else
                    currentAirport.setVisited(true);
                if(currentAirport.equals(destination)){
                    return routePlanner(pqnode, origin, ret);
                }
                for(Flight flight : currentAirport.getFlights()){
                    if(!flight.getDestination().isVisited()){
                        /*gets time waiting for connection*/
                        Integer waitingTime = currentAirport.getConnectionTime(pqnode.departureDay, pqnode.flight, flight);
                        /*inserts Priority Queue Node with copy of flight */
                        pq.offer(new PQNode(flight, pqnode, flight.getDuration() + pqnode.info[FLIGHT_TIME],
                                flight.getPrice() + pqnode.info[PRICE], pqnode.info[TOTAL_TIME] + waitingTime + flight.getDuration(), priority, flight.getDepartureDay()));
                    }
                }
            }
        }
        ret.setSuccess(false);
        return ret;
    }


    /**
     * Recreates the optimal route by stepping through the priority queue of routes, then creating a RequestResult and giving it the route
     *
     * @param node  the node at which the destination airport was reached
     * @param origin    the original origin airport of the trip
     * @param ret   the working RequestResult of the route
     * @return  the final RequestResult
     */
    RequestResult routePlanner(PQNode node, Airport origin, RequestResult ret){
        LinkedList<Flight> route = new LinkedList<>();
        ret.setTotalTime(node.info[TOTAL_TIME]);
        ret.setFlightTime(node.info[FLIGHT_TIME]);
        ret.setPrice(node.info[PRICE]);
        LinkedList<Integer> stack = new LinkedList<>();
        while(!node.flight.getOrigin().equals(origin)){
            route.push(node.flight);
            stack.push(node.departureDay);
            node = node.previous;
        }
        route.push(node.flight);
        stack.push(node.departureDay);
        ret.setSuccess(true);
        ret.setRoute(route);
        ret.setDays(stack);
        return ret;
    }
    
     public RequestResult worldTrip(Airport origin, int priority, List<Integer> departureDays) {

        int size = airportList.size();

        RequestResult optimalResult = new RequestResult();


        for(Flight flight : origin.getFlights()) {
            for(Integer day: flight.getDepartureDays()) {

                if(departureDays.contains(day)) {

                    RequestResult rr = new RequestResult();
                    rr.setFlightTime(flight.getDuration());
                    rr.setPrice(flight.getPrice());
                    rr.setTotalTime(flight.getDuration());
                    rr.getRoute().add(flight);
                    rr.getDays().add(day);

                    WeekTime wt = new WeekTime(day,flight.getDepartureTime()); //dia y minuto en el que comienza la vuelta al mundo.

                    wt.addMinutes(flight.getDuration());    //dia y minuto despues de tomar el primer vuelo

                    worldTrip(flight.getDestination(),origin,size-1,wt,priority,rr,optimalResult);
                }
            }
        }

        return optimalResult;
    }

    private void worldTrip(Airport current, Airport origin, int size, WeekTime wt, int priority, RequestResult result, RequestResult optimal ) {

        if(size == 0 && current.equals(origin)) { //el equals esta de mas, si esta bien hecho solo podria ser el nodo origen.
            if( (!optimal.isSuccess()) || foundOptimal(result,optimal,priority)) {
                
                optimal.cloneRR(result);
                return;
            }
        }

        if(size == 1)
            origin.setVisited(false);

        current.setVisited(true);

        for(Flight flight : current.getFlights()) {
            if (!flight.getDestination().isVisited()) {
                for (Integer day : flight.getDepartureDays()) {

                        WeekTime dayAfterFlight = new WeekTime(wt.getDay(), wt.getMinute());

                        int cantMin = wt.calcMinutes(day, flight.getDepartureTime()) + flight.getDuration();  //cantidad de minutos entre que se aterriza hasta llegar al proximo destino
                        dayAfterFlight.addMinutes(cantMin);

                        if ((!optimal.isSuccess()) || validPriorityCondition(result, optimal, priority, flight, cantMin)) {

                            result.getRoute().add(flight);
                            result.getDays().add(day);
                            result.addData(flight.getPrice(), flight.getDuration(), cantMin);
                            
                            worldTrip(flight.getDestination(), origin, size - 1, dayAfterFlight, priority, result, optimal);

                            result.getRoute().remove(flight); //Backtracking
                            result.getDays().remove(day);
                            result.removeData(flight.getPrice(), flight.getDuration(), cantMin);

                        }
                }
            }
        }

        current.setVisited(false); //Backtracking
    }



    private boolean validPriorityCondition(RequestResult result, RequestResult optimal, int priority, Flight flight, int cantMin) {

            switch(priority) {
                case FLIGHT_TIME: return (result.getFlightTime() + flight.getDuration()) < optimal.getFlightTime();
                case PRICE: return (result.getPrice() + flight.getPrice()) < optimal.getPrice();
                case TOTAL_TIME: return (result.getTotalTime() + cantMin) < optimal.getTotalTime();
                default: return false;
            }
    }

    private boolean foundOptimal(RequestResult result, RequestResult optimal, int priority) {

        switch(priority) {
            case FLIGHT_TIME: return result.getFlightTime() < optimal.getFlightTime();
            case PRICE: return result.getPrice() < optimal.getPrice();
            case TOTAL_TIME: return result.getTotalTime() < optimal.getTotalTime();
            default:return false;
        }

    }

}
   
