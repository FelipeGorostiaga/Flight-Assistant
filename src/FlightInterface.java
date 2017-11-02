
import java.util.HashSet;
import java.util.List;

/*SON LAS ARISTAS CLASE STATIC DENTRO DE FLIGHT ASSISTANTE*/
public interface FlightInterface {

    String getName();

    /*0 indicates monday, 1 indicates tuesday, 2 indicates wednesday,...*/
    List<Integer> getDepartureDays();


    AirportInterface getDestination();

    Integer getDepartureTime();

    Integer getDuration();

    double getPrice();

    Integer getDepartureDay();

}
