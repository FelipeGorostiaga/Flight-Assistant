import java.util.List;

/*SON LOS NODOS CLASE STATIC DENTRO DE FLIGHTASSISTANT*/
public interface Airport {

    String getName();

    double getLatitude();

    double getLongitude();

    List<Flight> getFlights();




}