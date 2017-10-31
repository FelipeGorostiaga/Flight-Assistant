import sun.reflect.generics.tree.Tree;

import java.sql.Time;
import java.util.*;


public class AirTrafficController implements AirTrafficControllerInterface{

    private static final int DAY_TIME = 24 * 60;

    private List<Airport> airportList = new ArrayList<>();
    private HashMap<String,Airport> airports = new HashMap<>();

    public boolean insertFlight(String airline, Integer number, Airport origin, Airport destination, List<Integer> departureDays, Integer duration, Integer departureTime, double price) {
        Airport start = airports.get(origin.name);
        Airport end = airports.get(destination.name);
        if(start != null && end != null && !start.equals(end)){
            if(start.flights.containsKey(airline + number)){
                return false;
            }
            Flight nFlight = new Flight(airline, number, origin, destination, departureDays, duration, departureTime, price);
            start.flightList.add(nFlight);
            start.flights.put(nFlight.airline + nFlight.number, nFlight);
            return true;
        }
        return false;
    }

    public boolean insertAirport(String name, double latitude, double longitude) {
        if(!airports.containsKey(name)){
            Airport airport = new Airport(name, latitude, longitude);
            airports.put(name, airport);
            airportList.add(airport);
            return true;
        }
        return false;
    }

    public boolean deleteAirport(String name) {
        Airport aux = airports.get(name);
        if(aux == null){
            return false;
        }
        for(Airport airport : airportList){
            if(!airport.equals(aux)){
                for(int i = 0; i< airport.flights.size(); i++){
                    Flight flight = airport.flights.get(i);
                    if(flight.destination.name.equals(name)){
                        airport.flights.remove(i);
                        break;
                    }
                }
            }
        }
        airports.remove(name);
        airportList.remove(aux);
        return true;
    }

    private void clearMarks(){
        for(Airport airport : airportList){
            airport.visited = false;
        }
    }


    public boolean deleteFlight(String airline, Integer number) {
        String name = airline + number;
        for(Airport airport: airportList){
            if(airport.flights.containsKey(name)){
                airport.flights.remove(name);
                for(int i = 0 ; i< airport.flightList.size(); i++){
                    if(airport.flightList.get(i).equals(name)){
                        airport.flightList.remove(i);
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public void insertAllAirports(List<Airport> airports) {
        for(Airport airport: airports){
            insertAirport(airport.name, airport.latitude, airport.longitude);
        }
    }

    public void deleteAllAirports() {
        airports = new HashMap<>();
        airportList = new ArrayList<>();
    }

    public void replaceAllAirports(List<Airport> airports) {
        deleteAllAirports();
        insertAllAirports(airports);
    }

    public void insertAllFlights(List<Flight> flights) {
        for(Flight flight : flights){
            insertFlight(flight.airline, flight.number,  flight.origin , flight.destination, flight.departureDays, flight.duration, flight.departureTime,  flight.price);
        }
    }

    public void deleteAllFlights() {
        for(Airport airport : airportList){
            airport.flights = new HashMap<>();
            airport.flightList = new ArrayList<>();
        }
    }

    public void replaceAllFlights(List<Flight> flights) {
        deleteAllFlights();
        insertAllFlights(flights);

    }

    public List<Flight> findRouteMinFlightTime(Airport origin, Airport destination, List<Integer> departureDays) {
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        clearMarks();
        origin.visited = true;
          /*check if we route can be started on requested days*/
        for(Flight flight : origin.flightList){
            for(Integer day: departureDays){
                if(flight.departureDays.get(day) != null){
                    pq.offer(new PQNode(flight, null, 0 + flight.duration)); break;
                }
            }
        }
        while(!pq.isEmpty()){
            PQNode pqnode = pq.poll();
            Airport currentAirport = pqnode.flight.destination;
            if(!currentAirport.visited){
                currentAirport.visited = true;
                if(currentAirport.equals(destination)){
                    return routePlanner(pqnode, origin);
                }
                for(Flight flight : currentAirport.flightList){
                    if(!flight.destination.visited){
                        pq.offer(new PQNode(flight, pqnode, pqnode.distance + flight.duration));
                    }
                }
            }
        }
        return null;
    }

    public List<Flight> findRouteMinPrice(Airport origin, Airport destination, List<Integer> departureDays) {
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        clearMarks();
        origin.visited = true;
          /*check if we route can be started on requested days*/
        for(Flight flight : origin.flightList){
            for(Integer day: departureDays){
                if(flight.departureDays.get(day) != null){
                    pq.offer(new PQNode(flight, null, 0 + flight.price)); break;
                }
            }
        }
        while(!pq.isEmpty()){
            PQNode pqnode = pq.poll();
            Airport currentAirport = pqnode.flight.destination;
            if(!currentAirport.visited){
                currentAirport.visited = true;
                if(currentAirport.equals(destination)){
                    return routePlanner(pqnode, origin);
                }
                for(Flight flight : currentAirport.flightList){
                    if(!flight.destination.visited){
                        pq.offer(new PQNode(flight, pqnode, pqnode.distance + flight.price));
                    }
                }
            }
        }
        return null;
    }

    public List<Flight> findRouteMinTotalTime(Airport origin, Airport destination, List<Integer> departureDays) {
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        Integer departureDay = new Integer(0);
        clearMarks();
        origin.visited = true;
         /*check if we route can be started on requested days*/
        for(Flight flight : origin.flightList){
            for(Integer day: departureDays){
                if(flight.departureDays.get(day) != null){
                    departureDay = day;
                    pq.offer(new PQNode(flight, null, 0 + flight.duration)); break;
                }
            }
        }
        while(!pq.isEmpty()){
            PQNode pqnode = pq.poll();
            Airport currentAirport = pqnode.flight.destination;
            if(!currentAirport.visited){
               currentAirport.visited = true;
                if(currentAirport.equals(destination)){
                    return routePlanner(pqnode, origin);
                }
                for(Flight flight : currentAirport.flightList){
                    if(!flight.destination.visited){
                        Integer waitingTime = currentAirport.getConnectionTime(((int)(departureDay + pqnode.distance/(DAY_TIME)) % 7), pqnode.flight, flight);
                        pq.offer(new PQNode(flight, pqnode, pqnode.distance + flight.duration + waitingTime));
                    }
                }
            }
        }
        return null;
    }

    List<Flight> routePlanner(PQNode node, Airport origin){
        LinkedList<Flight> route = new LinkedList<>();
        while(!node.flight.origin.equals(origin)){
            route.push(node.flight);
            node = node.previous;
        }
        route.push(node.flight);
        return route;
    }





    public List<Flight> worldTripMinFlightTime() {
        /*
        HashMap<Integer,List<Flight>> possibleRoutes = new HashMap<>();
        for(Airport airpot : airportList){
            clearMarks();
            hamiltonianPath(airport, airportList.size());
        }
        return possibleRoutes.get(possibleRoutes.keySet().iterator().next());
        */
        return null;
    }




    public List<Flight> worldTripMinPrice() {
        return null;
    }

    public List<Flight> worldTripMinTotalTime() {
        return null;
    }


    protected class Airport implements AirportInterface{
        private boolean visited;
        private String name;
        private double latitude;
        private double longitude;

        private List<Flight> flightList = new ArrayList<>();
        private HashMap<String, Flight> flights = new HashMap<>();

        public Airport(String name, double lat, double lon){
            visited = false;
            this.name = name;
            this.latitude = lat;
            this.longitude = lon;
        }

        public int hashCode(){
            return name.hashCode();
        }

        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(!(o instanceof Airport)){
                return false;
            }
            Airport aux =(Airport)o;
            if(!aux.name.equals(this.name)){
                return false;
            }
            return true;
        }


        public boolean isVisited() {
            return visited;
        }


        public String getName() {
            return name;
        }


        public double getLatitude() {
            return latitude;
        }


        public double getLongitude() {
            return longitude;
        }

        public List<Flight> getFlights() {
            return flightList;
        }

        /*optimized in the future*/
        public Integer getConnectionTime(Integer day, Flight arrival, Flight departure){
            int aux = 0;
            int start = arrival.duration + arrival.departureTime;
            if(arrival.duration + arrival.departureTime > DAY_TIME) {
                day++;
                if (day > 6) {
                    day = 0;
                }
                start -= DAY_TIME;
            }
            while(arrival.departureDays.get(day) != null){
                day++;
                aux++;
                if(day > 6){
                    day = 0;
                }
            }
            if(aux > 0){
                aux--;
                return  DAY_TIME - start + aux * DAY_TIME + departure.departureTime;
            }
            else{
                if(start > departure.departureTime){
                    return DAY_TIME - start + 6 * DAY_TIME +departure.departureTime;
                } else {
                    return departure.departureTime - start;
                }
            }

        }
    }




    protected class Flight implements FlightInterface {

        private String airline;
        private Integer number;
        private List<Integer> departureDays;
        private Airport destination;
        private Airport origin;
        private double price;
        private Integer departureTime;
        private Integer duration;

        public Flight(String airline, Integer number, Airport origin, Airport destination, List<Integer> departureDays, Integer duration, Integer departureTime, double price) {
            this.airline = airline;
            this.number = number;
            this.departureDays = departureDays;
            this.destination = destination;
            this.price = price;
            this.departureTime = departureTime;
            this.duration = duration;
            this.origin = origin;
        }


        public int hashCode(){
            return (airline + number).hashCode();
        }

        public boolean equals(Object o){
            if(o == null) {
                return false;
            }
            if(!(o instanceof Flight)){
                return false;
            }
            Flight aux = (Flight)o;
            if(!(aux.airline + aux.number).equals(this.airline + this.number)){
                return false;
            }
            return true;
        }

        public String getName() {
            return airline + number;
        }

        public List<Integer> getDepartureDays() {
            return departureDays;
        }


        public AirportInterface getDestination() {
            return destination;
        }

        public Integer getDepartureTime() {
            return departureTime;
        }

        public Integer getDuration() {
            return duration;
        }

        public double getPrice() {
            return price;
        }
    }

    private class PQNode implements Comparable<PQNode> {
        PQNode previous;
        Flight flight;
        double distance;

        public PQNode(Flight flight, PQNode previous, double distance) {
            this.flight = flight;
            this.previous = previous;
            this.distance = distance;
        }

        public int compareTo(PQNode o) {
            return Double.valueOf(distance).compareTo(o.distance);
        }
    }

}
