import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Return object used in searching an optimal route.
 *
 * Contains useful information about the success of the search, the route it took, price, time in the air and total time.
 *
 * @see Flight
 */
public class RequestResult {
    private boolean success;
    private List<Flight> route;
    private double price;
    private double totalTime;
    private double flightTime;
    private List<Integer> days;

    public RequestResult() {
        success = false;
        route = new LinkedList<Flight>();
        price = 0;
        totalTime = 0;
        flightTime = 0;
        days = new LinkedList<Integer>();

    }

     public void cloneRR(RequestResult result) {

        this.setTotalTime(result.getTotalTime());
        this.setFlightTime(result.getFlightTime());
        this.setPrice(result.getPrice());
        this.setSuccess(true);

        this.setRoute(new LinkedList<Flight>(result.getRoute()));
        this.setDays(new LinkedList<Integer>(result.getDays()));

    }

    public void addData(double price, int fduration, int ttime) {


        this.price  += price;
        this.flightTime += fduration;
        this.totalTime += ttime;

    }

    public void removeData(double price, int fduration, int ttime) {

        this.price  -= price;
        this.flightTime -= fduration;
        this.totalTime -= ttime;
    }

    public List<Integer> getDays() { return days;  }

    public boolean isSuccess() {
        return success;
    }

    public List<Flight> getRoute() {
        return route;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getFlightTime() {
        return flightTime;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setRoute(List<Flight> route) {
        this.route = route;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public void setFlightTime(double flightTime) {
        this.flightTime = flightTime;
    }

    public void setDays(List<Integer> days) { this.days = days; }
}
