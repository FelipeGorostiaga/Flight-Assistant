
import java.util.List;

/*SON LAS ARISTAS CLASE STATIC DENTRO DE FLIGHT ASSISTANTE*/
public interface Flight {

    String getAirline();

    String getFlightNumber();

    /*0 indicates monday, 1 indicates tuesday, 3 indicates wednesday,...*/
    List<Integer> getDepartureDays();

    Airport getOrigin();

    Airport getDestination();

    Integer getDepartureTime();

    Integer getDuration();

    double getPrice();

}
