import java.util.List;

/**
 * Flight in the air traffic system.
 *
 * Contains info about the airport from which it departs and where it arrives, it's airline and number, as well as the days of the week it leaves,
 * the price and length.
 *
 * @see Airport
 */
public class Flight implements FlightInterface {

    private String airline;
    private Integer number;
    private List<Integer> departureDays;
    private Airport destination;
    private Airport origin;
    private double price;
    private Integer departureTime;
    private Integer duration;
    private Integer departureDay = null;

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

    public Flight(Flight flight){
        this.airline = flight.airline;
        this.number = flight.number;
        this.departureDays = flight.departureDays;
        this.destination = flight.destination;
        this.price = flight.price;
        this.departureTime = flight.departureTime;
        this.duration = flight.duration;
        this.origin = flight.origin;
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

    public Airport getDestination() {
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

    public Integer getDepartureDay(){
        return departureDay;
    }

    public Airport getOrigin() {
        return origin;
    }

    public String getAirline() {
        return airline;
    }

    public Integer getNumber() {
        return number;
    }

    public void setDepartureDay(Integer day){
        this.departureDay = day;
    }




}
