import java.util.Arrays;
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

    public RequestResult clone() {

        RequestResult ret = new RequestResult();

        List<Flight> flightList = new LinkedList<Flight>(this.getRoute());
        List<Integer> daysList = new LinkedList<Integer>(this.getDays());
        ret.setPrice(this.getPrice());
        ret.setFlightTime(this.flightTime);
        ret.setTotalTime(this.totalTime);

        return ret;
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
