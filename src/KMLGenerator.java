import java.util.List;

/**
 * Created by tferrer on 16/11/17.
 */
public class KMLGenerator {

    private static final String[] week = new String[]{"Lu", "Ma", "Mi", "Ju",
            "Vi", "Sa", "Do"};

    public static String createKML(List<Flight> flights, List<Integer> days) {
        if(flights.isEmpty())
            return "NotFound";
        StringBuilder kml = new StringBuilder();
        StringBuilder route = new StringBuilder();
        kml.append(initializeKML());
        route.append(initializeRoute());
        Flight first = flights.get(0);
        kml.append(createCamera(first.getOrigin().getLongitude(),
                first.getOrigin().getLatitude()));
        int i = 0;
        for(Flight flight : flights) {
            boolean isLast = i == flights.size()-1;
            kml.append(createPlacemark(flight, days, i));
            route.append(addRoutePoint(flight, isLast));
            if(isLast)
                kml.append(finalizePlacemarks(flight.getDestination()));
            i++;
        }
        route.append("\t\t\t\t</coordinates>\n" +
                "\t\t\t</LineString>\n" +
                "\t\t</Placemark>\n");
        kml.append(route);
        kml.append("\t</Folder>\n" +
                "</Document>\n" +
                "</kml>");
        return kml.toString();
    }

    private static String initializeKML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
                "<Document>\n" +
                "\t<name>Flight</name>\n" +
                "\t<open>1</open>\n" +
                "\t<Folder>\n" +
                "\t\t<name>Flights</name>\n" +
                "\t\t<open>1</open>\n" +
                "\t\t<Style id=\"sn_noicon\">\n" +
                "\t\t\t<IconStyle>\n" +
                "\t\t\t\t<Icon></Icon>\n" +
                "\t\t\t</IconStyle>\n" +
                "\t\t\t<LabelStyle>\n" +
                "\t\t\t\t<color>00ffffff</color>\n" +
                "\t\t\t</LabelStyle>\n" +
                "\t\t</Style>\n" +
                "\t\t<Style id=\"yellowLineGreenPoly\">\n" +
                "\t\t\t<LineStyle>\n" +
                "\t\t\t\t<color>7f00ffff</color>\n" +
                " \t\t\t\t<width>4</width>\n" +
                "\t\t\t</LineStyle>\n" +
                " \t\t\t<PolyStyle>\n" +
                " \t\t\t\t<color>7f00ff00</color>\n" +
                " \t\t\t</PolyStyle>\n" +
                "\t\t</Style>\n";
    }

    private static String initializeRoute() {
        return "\n\t\t<Placemark>\n" +
                " \t\t\t<name>route</name>\n" +
                " \t\t\t<description></description>\n" +
                " \t\t\t<styleUrl>#yellowLineGreenPoly</styleUrl>\n" +
                " \t\t\t<LineString>\n" +
                " \t\t\t\t<extrude>1</extrude>\n" +
                " \t\t\t\t<tessellate>1</tessellate>\n" +
                " \t\t\t\t<altitudeMode>absoluto</altitudeMode>\n" +
                " \t\t\t\t<coordinates>";
    }

    private static String createCamera(double longitude, double latitude) {
        return "\t\t<Camera id=\"ID\"> \n" +
                "  \t\t\t<gx:ViewerOptions>\n" +
                "    \t\t\t<option> name=\" \" type=\"boolean\"></option>\n" +
                "  \t\t\t</gx:ViewerOptions>\n" +
                "  \t\t\t<longitude>" + longitude + "</longitude>\n" +
                "  \t\t\t<latitude>" + latitude + "</latitude>\n" +
                "  \t\t\t<altitude>5000.00</altitude>\n" +
                "  \t\t\t<heading>0</heading>\n" +
                "  \t\t\t<tilt>0</tilt>\n" +
                "  \t\t\t<roll>0</roll>\n" +
                "  \t\t\t<altitudeMode>relativeToGround</altitudeMode>\n" +
                "\t\t</Camera>";
    }


    private static String createPlacemark(Flight flight, List<Integer> days, int i) {
        Airport airp = flight.getOrigin();
        return "\n\t\t<Placemark id=\"flight" + i +"\">\n" +
                "\t\t\t\t<name>" + airp.getName() + " " + flight.getName() +
                "</name>\n" + "\t\t\t\t<description> Destination: " +
                flight.getDestination().getName() +
                " Departure: " + week[days.get(i)] + " " +
                flight.getDepartureTime() +
                " Flight time: " + ((flight.getDuration() > 60)?
                (flight.getDuration()/60 + "h") : "") +
                String.format("%02d", flight.getDuration()%60)+ "m</description>\n" +
                "\t\t\t\t<LookAt>\n" +
                "\t\t\t\t\t<longitude>" + airp.getLongitude() +"</longitude>\n" +
                "\t\t\t\t\t<latitude>" + airp.getLatitude() + "</latitude>\n" +
                "\t\t\t\t\t<altitude>0</altitude>\n" +
                "\t\t\t\t\t<heading>0</heading>\n" +
                "\t\t\t\t\t<tilt>0</tilt>\n" +
                "\t\t\t\t\t<range>518.7551361289096</range>\n" +
                "\t\t\t\t\t<altitudeMode>relativeToGround</altitudeMode>\n" +
                "\t\t\t\t</LookAt>\n" +
                "\t\t\t\t<styleUrl>#sn_noicon</styleUrl>\n" +
                "\t\t\t\t<Point>\n" +
                "\t\t\t\t\t<coordinates>" + airp.getLongitude() + "," +
                airp.getLatitude() + ",0</coordinates>\n" +
                "\t\t\t\t</Point>\n" +
                "\t\t\t\t<gx:balloonVisibility>" + ((i != 0) ? 0 : 1) +
                "</gx:balloonVisibility>\n" +
                "\t\t</Placemark>";
    }

    private static String finalizePlacemarks(Airport dest) {
        return "\n\t\t<Placemark id=\"flightEnd\">\n" +
                "\t\t\t\t<name>" + dest.getName() + "</name>\n" +
                "\t\t\t\t<description>End!</description>\n" +
                "\t\t\t\t<LookAt>\n" +
                "\t\t\t\t\t<longitude>" + dest.getLongitude() +"</longitude>\n" +
                "\t\t\t\t\t<latitude>" + dest.getLatitude() + "</latitude>\n" +
                "\t\t\t\t\t<altitude>0</altitude>\n" +
                "\t\t\t\t\t<heading>0</heading>\n" +
                "\t\t\t\t\t<tilt>0</tilt>\n" +
                "\t\t\t\t\t<range>518.7551361289096</range>\n" +
                "\t\t\t\t\t<altitudeMode>relativeToGround</altitudeMode>\n" +
                "\t\t\t\t</LookAt>\n" +
                "\t\t\t\t<styleUrl>#sn_noicon</styleUrl>\n" +
                "\t\t\t\t<Point>\n" +
                "\t\t\t\t\t<coordinates>" + dest.getLongitude() + "," +
                dest.getLatitude() + ",0</coordinates>\n" +
                "\t\t\t\t</Point>\n" +
                "\t\t\t\t<gx:balloonVisibility>0</gx:balloonVisibility>\n" +
                "\t\t</Placemark>";
    }

    private static String addRoutePoint(Flight flight, boolean isLast) {
        Airport orig = flight.getOrigin();
        String ret = "\n\t\t\t\t\t" + orig.getLongitude() + "," + orig.getLatitude() + ",0";
        if(isLast)
            ret += "\n\t\t\t\t\t" + flight.getDestination().getLongitude() + "," +
                    flight.getDestination().getLatitude() + ",0\n";
        return ret;
    }


}
