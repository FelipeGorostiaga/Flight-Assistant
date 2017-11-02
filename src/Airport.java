import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Airport implements AirportInterface {

    private static final int DAY_TIME = 24 * 60;

    /*para saber de donde fue visitado*/
    private boolean visited;
    private Airport origin;

    /*data*/
    private String name;
    private double latitude;
    private double longitude;

    private List<Flight> flightList = new ArrayList<>();
    private HashMap<String, Flight> flights = new HashMap<>();

    public Airport(String name, double lat, double lon) {
        visited = false;
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
    }


    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Airport)) {
            return false;
        }
        Airport aux = (Airport) o;
        if (!aux.name.equals(this.name)) {
            return false;
        }
        return true;
    }


    public Airport getOrigin() {
        return origin;
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

    public HashMap<String,Flight> getFlightsMap() {
        return flights;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public void setFlights(HashMap<String, Flight> flights) {
        this.flights = flights;
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }

    /*optimized in the future*/
    public Integer getConnectionTime(Integer day, Flight arrival, Flight departure) {
        int aux = 0;
        int start = arrival.getDuration() + arrival.getDepartureTime();
        if (arrival.getDuration() + arrival.getDepartureTime() > DAY_TIME) {
            day++;
            if (day > 6) {
                day = 0;
            }
            start -= DAY_TIME;
        }
        while (arrival.getDepartureDays().contains(day)) {
            day++;
            aux++;
            if (day > 6) {
                day = 0;
            }
        }
        departure.setDepartureDay(day);
        if (aux > 0) {
            aux--;
            return DAY_TIME - start + aux * DAY_TIME + departure.getDepartureTime();
        } else {
            if (start > departure.getDepartureTime()) {
                return DAY_TIME - start + 6 * DAY_TIME + departure.getDepartureTime();
            } else {
                return departure.getDepartureTime() - start;
            }
        }

    }
}