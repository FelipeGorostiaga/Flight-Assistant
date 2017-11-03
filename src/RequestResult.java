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

    public RequestResult() {
        success = false;
        route = null;
        price = 0;
        totalTime = 0;
        flightTime = 0;
    }

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
}
