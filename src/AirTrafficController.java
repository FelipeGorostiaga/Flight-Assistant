

import javax.xml.transform.Result;
import java.lang.ref.ReferenceQueue;
import java.nio.channels.IllegalChannelGroupException;
import java.sql.Time;
import java.util.*;


public class AirTrafficController /*implements AirTrafficControllerInterface*/{

    private static final int DAY_TIME = 24 * 60;
    private static final int FLIGHT_TIME = 0;
    private static final int PRICE = 1;
    private static final int TOTAL_TIME = 2;

    private List<Airport> airportList = new ArrayList<>();
    private HashMap<String,Airport> airports = new HashMap<>();

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

    private void clearMarks(){
        for(Airport airport : airportList){
            airport.setVisited(false);
            airport.setOrigin(null);
        }
    }


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

    public void insertAllAirports(List<Airport> airports) {
        for(Airport airport: airports){
            insertAirport(airport.getName(), airport.getLatitude(), airport.getLongitude());
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
            insertFlight(flight.getAirline(), flight.getNumber(),  flight.getOrigin() , flight.getDestination(),
                    flight.getDepartureDays(), flight.getDuration(), flight.getDepartureTime(),  flight.getPrice());
        }
    }

    public void deleteAllFlights() {
        for(Airport airport : airportList){
            airport.setFlights(new HashMap<>());
            airport.setFlightList(new ArrayList<>());
        }
    }

    public void replaceAllFlights(List<Flight> flights) {
        deleteAllFlights();
        insertAllFlights(flights);

    }


    public RequestResult receiveFindRoute(String origin, String destination, String priority, List<Integer> weekDays) {
        RequestResult ret = new RequestResult();
        switch(priority){
            case "ft": findRouteMinPriority(airports.get(origin), airports.get(destination), FLIGHT_TIME, weekDays, ret); break;
            case "pr": findRouteMinPriority(airports.get(origin), airports.get(destination), PRICE, weekDays, ret); break;
            case "tt": findRouteMinPriority(airports.get(origin), airports.get(destination), TOTAL_TIME, weekDays, ret); break;
            default: throw new IllegalArgumentException("Not a valid priority");
        }
        return ret;
    }


    public RequestResult findRouteMinPriority(Airport origin, Airport destination,
                                                int priority, List<Integer> departureDays, RequestResult ret) {
        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        clearMarks();
        origin.setVisited(true);
          /*check if we route can be started on requested days*/
        for(Flight flight : origin.getFlights()){
            for(Integer day: departureDays){
                if(flight.getDepartureDays().contains(day)){
                    flight.setDepartureDay(day);
                    pq.offer(new PQNode(new Flight(flight) , null, 0, flight.getDuration(),flight.getPrice(), 0, priority));
                    if(priority != TOTAL_TIME){
                        break;
                    }
                }
            }
        }
        while(!pq.isEmpty()){
            PQNode pqnode = pq.poll();
            Airport currentAirport = pqnode.flight.getDestination();
            if(!currentAirport.isVisited() || priority == TOTAL_TIME){
                currentAirport.setOrigin(pqnode.flight.getOrigin());
                currentAirport.setVisited(true);
                if(currentAirport.equals(destination)){
                    return routePlanner(pqnode, origin, ret);
                }
                for(Flight flight : currentAirport.getFlights()){
                    if(!flight.getDestination().isVisited() || (priority == TOTAL_TIME && flight.getOrigin().equals(flight.getDestination().getOrigin()))){
                        /*gets time waiting for connection*/
                        Integer waitingTime = currentAirport.getConnectionTime(pqnode.flight.getDepartureDay(), pqnode.flight, flight);
                        /*inserts Priority Queue Node with copy of flight */
                        pq.offer(new PQNode(new Flight(flight), pqnode, pqnode.distance , flight.getDuration() + pqnode.info[FLIGHT_TIME],
                                flight.getPrice() + pqnode.info[PRICE], pqnode.info[TOTAL_TIME] + waitingTime + flight.getDuration(), priority));
                    }
                }
            }
        }
        ret.setSuccess(false);
        return ret;
    }



    RequestResult routePlanner(PQNode node, Airport origin, RequestResult ret){
        LinkedList<Flight> route = new LinkedList<>();
        ret.setTotalTime(node.info[TOTAL_TIME]);
        ret.setFlightTime(node.info[FLIGHT_TIME]);
        ret.setPrice(node.info[PRICE]);
        while(!node.flight.getOrigin().equals(origin)){
            route.push(node.flight);
            node = node.previous;
        }
        route.push(node.flight);
        ret.setSuccess(true);
        ret.setRoute(route);
        return ret;
    }

    public List<Flight> worldTripMinPrice() {
        return null;
    }

    public List<Flight> worldTripMinTotalTime() {
        return null;
    }



    private class PQNode implements Comparable<PQNode> {
        PQNode previous;
        Flight flight;
        double distance;
        double[] info = new double[3];
        int priority;

        public PQNode( Flight flight, PQNode previous, double prevTotal, double flightTime, double price, double totalTime, int priority) {
            this.flight = flight;
            this.previous = previous;
            this.info[FLIGHT_TIME] = flightTime;
            this.info[PRICE] = price;
            this.info[TOTAL_TIME] = totalTime;
            this.priority = priority;
            this.distance = info[priority] + prevTotal;
        }

        public int compareTo(PQNode o) {
            return Double.valueOf(distance).compareTo(o.distance);
        }
    }













    //estas listas se usan para no tener que recorrer cada vez que quiero saber el precio total del viaje almacenado
    private class PriceList extends ArrayList<Flight> {

        private double price;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.setPrice(price);
        }

    }
    private class DurationList extends ArrayList<Flight> {

        private int duration;

        public int getDuration() {
            return duration;
        }

        public void setDuration(double price) {
            this.duration = duration;
        }

    }


    public PriceList worldTripPrice(List<Integer> departureDays) {

        clearMarks();
        int size = airportList.size();

        List<Flight> route = Arrays.asList(new Flight[size]);   // lista con tamano igual al numero de aeropuertos para guardar el camino por nivel
        PriceList minList = new PriceList();                 //lista que alamcena el minimo recorrido de los minimos recorridos por nodo
        PriceList minRoute = new PriceList();  //lista que se almacena la mejor vuelta al mundo para ese nodo.

        //Necesito un metodo que agarre todos los aeropuertos que tengan vuelos que salgan en algunos de los que estan en departureDays
        //y itero en el for-each con estos aeropuertos, no con todos.

        for (Airport airport : airportList) {

            minRoute.clear();
            worldTripPrice(airport, airport, 0, size, route, minRoute, 0);

            //Si se consiguio una vuelta al mundo valida con este nodo y su precio es menor a alguno encontrado previamente, se le asigna a minList.
            if(!minRoute.isEmpty()) {
                if(minList.isEmpty() || minRoute.getPrice() < minList.getPrice()) {
                    minList.clear();
                    minList.addAll(minRoute);
                    minList.setPrice(minRoute.getPrice());
                }
            }

        }
        if(minList.isEmpty()) //ver por comodidad de la terminal si prefiere una lista vacia o null.
            return null;
        else
            return minList;
    }
       /*
            size: es el total de nodos en el grafo, se va restando uno en cada recursion.
            level: es la profundidad de la recursion para saber donde escribir en la lista
            route: camino actual en la recursion
            minRoute: camino optimo encontrado
         */

    private void worldTripPrice(Airport current, Airport origin, int level, int size, List<Flight> route, PriceList minRoute, double price) {

        if (size == 1)
            origin.setVisited(false);

        current.setVisited(true);

        for (Flight flight : current.getFlights()) {

            if (!flight.getDestination().isVisited()) {

                //se completa el ciclo hamiltoniano
                if (flight.getDestination().equals(origin)) {
                    if (minRoute.isEmpty() || ((price + flight.getPrice()) < minRoute.getPrice())) {

                        minRoute.clear();
                        route.add(level + 1, flight);  //se agrega el vuelo que vuelve al nodo origen.
                        minRoute.addAll(route);
                        minRoute.setPrice(price + flight.getPrice());
                    }
                }
                //la lista que almacena la mejor ruta esta vacia o no esta vacia y todavia no me pase del minimo que tengo
                else {
                    if((minRoute.isEmpty()) || (minRoute.getPrice() > (price + flight.getPrice()))) {

                    route.add(level, flight);
                    worldTripPrice(flight.getDestination(), origin, level + 1, size - 1, route, minRoute, price + flight.getPrice());
                    }
                }


            }
        }

        current.setVisited(false);

    }


    public DurationList worldTripFlightTime(List<Integer> departureDays) {

        clearMarks();
        int size = airportList.size();

        List<Flight> route = Arrays.asList(new Flight[size]);
        DurationList minList = new DurationList();
        DurationList minRoute = new DurationList();

        for (Airport airport : airportList) {

            minRoute.clear();
            worldTripFlightTime(airport, airport, 0, size, route, minRoute, 0);

            if(!minRoute.isEmpty()) {
                if(minList.isEmpty() || minRoute.getDuration() < minList.getDuration()) {
                    minList.clear();
                    minList.addAll(minRoute);
                    minList.setDuration(minRoute.getDuration());
                }
            }

        }
        if(minList.isEmpty()) //ver por comodidad de la terminal si prefiere una lista vacia o null.
            return null;
        else
            return minList;
    }


    private void worldTripFlightTime(Airport current, Airport origin, int level, int size, List<Flight> route, DurationList minRoute, Integer duration) {

        if (size == 1)
            origin.setVisited(false); //habilito que se pueda volver al nodo origen en caso de que exista el vuelo

        current.setVisited(true);

        for (Flight flight : current.getFlights()) { //recorro la lista de vuelos del aeropuerto (DFS)

            if (!flight.getDestination().isVisited()) {

                if (flight.getDestination().equals(origin)) {

                    if(minRoute.isEmpty() || ((duration + flight.getDuration()) < minRoute.getDuration()) ) {

                        minRoute.clear();
                        route.add(level + 1, flight);  //se agrega el vuelo que vuelve al nodo origen.
                        minRoute.addAll(route);
                        minRoute.setDuration(duration + flight.getDuration());
                    }
                }
                else {
                    if (minRoute.isEmpty() || minRoute.getDuration() > (duration + flight.getDuration())) {
                        route.add(level, flight);
                        worldTripFlightTime(flight.getDestination(), origin, level + 1, size - 1, route, minRoute, duration + flight.getDuration());
                    }
                }

            }
        }
        current.setVisited(false);
    }
}
