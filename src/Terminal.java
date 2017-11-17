import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


/**
 *  @author Santiago Swinnen
 *  The Terminal class has an array of strings. Each time a valid word is read, the string is added to the array.
 *  If the user input gets to the point when the action is executed, array elements are passed as arguments.
 *  When the while statement starts again it clears the array
 */



public class Terminal {

    private AirTrafficController atc;
    private ArrayList<Object> words;
    private String output;
    private String format;

    public Terminal() {
        this.atc = new AirTrafficController();
        this.words = new ArrayList<>();
        this.output = "stdout";
        this.format = "text";
    }
    /**
     * Creates the controller and gets ready for user input
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean exit = false;
        while(!exit) {
            words.clear();
            input = scanner.nextLine();
            if(input.equals("exit")){
                exit = true;
            } else {
                readInstruction(input);
            }
        }
    }

    /**
     * Reads user input word by word. In case an instruction is read it is executed, otherwise no action is performed
     * @param instruction string input to be analyzed
     */

    private void readInstruction(String instruction) {
        char[] chars = instruction.toCharArray();
        int i = 0;
        String action = getStringUntilChar(chars, i, ' ');
        words.add(action);
        i += action.length() + 1;
        if(action.equals("insert")) {
            checkInsertion(chars, i);
        } else if(action.equals("delete")) {
            checkDeletion(chars, i);
        } else if(action.equals("findRoute")) {
            checkRoute(chars, i);
        } else if(action.equals("worldTrip")) {
            checkWorldTrip(chars, i);
        } else if(action.equals("outputFormat")) {
            checkFormat(chars, i);
        } else {
            System.out.println("Invalid command, please try again");
        }
    }

    /**
     * if the first word of user input is "insert", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     */
    private void checkInsertion(char [] chars, int i) {
        String secondWord = getStringUntilChar(chars, i, ' ');
        words.add(secondWord);
        i += secondWord.length() + 1;
        if(secondWord.equals("airport")) {
            newAirportValidation(chars, i);
        } else if(secondWord.equals("flight")) {
            newFlightValidation(chars, i);
        } else if(secondWord.equals("all")) {
            String thirdWord = getStringUntilChar(chars,i,' ');
            i += thirdWord.length() + 1;
            words.add(thirdWord);
            if (thirdWord.equals("airports") || thirdWord.equals("flight")) {
                massiveValidation(chars, i, thirdWord);
            } else {
                System.out.println("'insert all' command only supports one of the following fmtions: " +
                        "<airports>, <flights>");
            }
        }
        else {
            System.out.println("'insert' command does not accept your entry, please try again");
        }
    }

    /**
     * Verifies if the airport expression given by the user is valid and if so, if it can de added
     * @param chars user input
     * @param i current index of char array
     */
    private boolean newAirportValidation(char[] chars, int i) {
        String airport = getStringUntilChar(chars, i, ' ');
        i += airport.length() + 1;
        words.add(airport);
        if(isAValidAirportName(airport)) {
            return checkCoordinates(chars, i);
        }
        System.out.println("Invalid airport name");
        return false;
    }

    private boolean checkCoordinates(char[] chars, int i) {
        String lat = getStringUntilChar(chars, i, ' ');
        i += lat.length() + 1;
        boolean valid = true;
        Double latitude = 0.0;
        Double longitude = 0.0;
        try {
            latitude = Double.parseDouble(lat);
        } catch (NumberFormatException e) {
            System.out.println("Latitude must be a number");
            valid = false;
        }
        if(valid && (latitude < -90.0 || latitude > 90.0)) {
            valid = false;
            System.out.println("Latitude is out of range. It must be a real number between -90 and 90)");
        }
        if(valid) {
            String lng = getStringUntilChar(chars, i, '\0');
            try {
                longitude = Double.parseDouble(lng);
            } catch(NumberFormatException f) {
                System.out.println("Longitude must be a number");
                valid = false;
            }
            if(valid && (longitude < -180.0 || longitude > 180.0)) {
                System.out.println("Longitude is out of range. It must be a real number between -180 and 180)");
            } else {
                boolean success = atc.insertAirport((String)words.get(2),latitude,longitude);
                if(success) {
                    System.out.println("Airport successfully inserted");
                } else {
                    System.out.println("Instruction ignored. Airport is already registered");
                }
                return true;
            }
        }
        return false;
    }


    /**
     * Verifies if the flight expression given by the user is valid and if so, if it can de added
     * @param chars user input
     * @param i current index of char array
     */
    private boolean newFlightValidation(char[] chars, int i) {
        String airline = getStringUntilChar(chars, i, ' ');
        i += airline.length() + 1;
        boolean valid = true;
        if(isAValidAirlineName(airline)) {
            words.add(airline);
            Integer flightNumber = 0;
            String flightNum = getStringUntilChar(chars, i, ' ');
            i += flightNum.length() + 1;
            try {
                flightNumber = Integer.parseInt(flightNum);
            } catch(NumberFormatException e) {
                valid = false;
                System.out.println("Invalid flight number");
            }
            if(valid) {
                words.add(flightNumber);
                return newFlightSecondCheck(chars, i);
            } else {
                System.out.println("Invalid flight number");
            }
        } else {
            System.out.println("Invalid airline name");
        }
        return false;
    }

    /**
     * This method is called only if three first airport insertion parameters are valid
     * @param chars input
     */

    private boolean newFlightSecondCheck(char[] chars, int i) {
        boolean valid;
        String days = getStringUntilChar(chars, i, ' ');
        i += days.length() + 1;
        ArrayList<Integer> dayList = checkWeekDays(days);
        if(dayList != null) {
            words.add(dayList);
            String origin = getStringUntilChar(chars, i, ' ');
            i += origin.length() + 1;
            String destination = getStringUntilChar(chars, i, ' ');
            i += destination.length() + 1;
            valid = isAValidAirportName(origin) && isAValidAirportName(destination);
            if (valid) {
                String depTime = getStringUntilChar(chars, i, ' ');
                i += depTime.length() + 1;
                valid = isADayTime(depTime);
                if (valid) {
                    int deptimeInt =depTimeToInt(depTime);
                    String duration = getStringUntilChar(chars, i, ' ');
                    i += duration.length() + 1;
                    String price = getStringUntilChar(chars, i, '\0');
                    if (valid) {
                        int time = durationInMinutes(duration);
                        Double doublePrice = validatePrice(price);
                        valid = valid && (time != -1) && (doublePrice != null);
                        if (valid) {
                            System.out.println((String) words.get(2) + " " +(Integer) words.get(3) +" " +
                                    dayList + " " +origin + " " +destination + " " +deptimeInt + " " +time + " " +doublePrice);
                            boolean success = atc.receiveFlightInsertion((String) words.get(2), (Integer) words.get(3),
                                    dayList, origin, destination, deptimeInt, time, doublePrice);
                            if(success) {
                                System.out.println("Flight was successfully inserted");
                            } else {
                                System.out.println("Flight could not be added. Either the flight has already been added" +
                                        "or any of airports are not registered");
                            }
                        } else {
                            System.out.println("Price format is invalid");
                        }
                    } else {
                        System.out.println("Invalid parameters for flight insertion");
                    }
                } else {
                    System.out.println("Invalid parameters for flight insertion");
                }
            } else {
                System.out.println("Invalid parameters for flight insertion");
            }
        }
        return false;
    }

    /**
     * Verifies if the file given for all airports insertion is valid
     * and if the action (replace or append) is correct
     * @param chars user input
     * @param i current index of char array
     * @param item indicates if the massive insertion involves flights or airports
     */
    private void massiveValidation(char[] chars, int i, String item) {
        String file = getStringUntilChar(chars, i, ' ');
        i += file.length() + 1;;
        System.out.println(file);
        String action = getStringUntilChar(chars, i, '\0');
        boolean fileIsValid = true;
        BufferedReader br = null;
        try{
           br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            fileIsValid = false;
        }
        if(fileIsValid) {
            if(action.equals("append") || action.equals("replace")) {
                if(item.equals("airports")) {
                    massiveInsertion(action, br, "airport");
                } else if(item.equals("flight")) {
                    massiveInsertion(action, br, "flight");
                }
            } else {
                System.out.println("Invalid massive insertion action." +
                        "Last parameter must be either <append> or <replace> ");
            }
        }
    }

    /**
     * Massive airport insertion analyze file line by line.
     * If there is an invalid line it is not added but adds but goes on with following lines.
     * @param action indicates whether airports must be appended or replaced
     * @param br Buffered Reader of the input file
     * @param type indicates if massive insertion involves flights or airports
     */
    private void massiveInsertion(String action, BufferedReader br, String type) {
        String line;
        int lineNumber = 1;
        boolean lineIsValid = true;
        if(action.equals("replace")) {
            if(type.equals("airport")) {
                atc.deleteAllAirports();
            } else {
                atc.deleteAllFlights();
            }
        }
        try{
            while((line=br.readLine())!=null) {
                if (type.equals("airport")) {
                    System.out.print("Line " + lineNumber+": ");
                    lineProcessing(line, type);
                } else if (type.equals("flight")) {
                    System.out.print("Line " + lineNumber+ ": ");
                    lineProcessing(line, type);
                    lineNumber++;
                }
            }
        } catch (IOException e) {
            System.out.println("Aborting. Unexpected input/output exception");
        }
        atc.printAirports();
        atc.printFlights();
    }

    /**
     * Individual airport/flight processing from file. This method builds an individual airport/flight insertion
     * with the terminal array. new Airport/Flight validation methods return whether insertion was valid or not
     * @param line file input
     * @return true if airport is syntactically correct, false if not.
     */
    private boolean lineProcessing(String line, String item) {
        words.clear();
        words.add("insert");
        words.add(item);
        char [] lineChars = line.toCharArray();
        for (int i = 0; i < lineChars.length; i++) {
            if(lineChars[i] == '#') {
                lineChars[i] = ' ';
            }
        }
        if(item.equals("airport")) {
            System.out.println(lineChars);
            return newAirportValidation(lineChars, 0);
        } else {
            System.out.println(lineChars);
            return newFlightValidation(lineChars, 0);
        }

        /**
         * Building a char array to seize the individual insertion coordinate checking method
         */
    }

    /**
     * if the first word of user input is "delete", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     */
    private void checkDeletion(char [] chars, int i) {
        String secondWord = getStringUntilChar(chars, i, ' ');
        i += secondWord.length() + 1;
        words.add(secondWord);
        if(secondWord.equals("airport")) {
            String airport = getStringUntilChar(chars, i, '\0');
            i += airport.length() + 1;
            words.add(airport);
            if(isAValidAirportName(airport)) {
                boolean success = atc.deleteAirport(airport);
                if(success) {
                    System.out.println("Airport has been successfully deleted");
                } else {
                    System.out.println("Action could not be completed, airport may not be registered");
                }
            } else {
                System.out.println("Airport names should contain exactly three alphabetic characters");
            }
        } else if(secondWord.equals("flight")) {
            checkFlightDeletion(chars, i);
        } else if(secondWord.equals("all")) {
            String thirdWord = getStringUntilChar(chars, i, '\0');
            if(thirdWord.equals("airport")) {
                atc.deleteAllAirports();
                System.out.println("All airports have been successfully deleted");
            } else if(thirdWord.equals("flight")) {
                atc.deleteAllFlights();
                System.out.println("All flights have been successfully deleted");
            }
        }
    }

    private void checkFlightDeletion(char[] chars, int i) {
        String airlineName = getStringUntilChar(chars, i, ' ');
        i += airlineName.length() + 1;
        String flightNum = getStringUntilChar(chars, i, '\0');
        Integer num = 0;
        boolean validNumber = true;
        try {
            num = Integer.parseInt(flightNum);
        } catch (NumberFormatException e) {
            System.out.println("Invalid flight number");
            validNumber = false;
        }
        if(validNumber) {
            if (isAValidAirlineName(airlineName)) {
                boolean success = atc.deleteFlight(airlineName, num);
                if(success) {
                    System.out.println("Flight successfully deleted");
                } else {
                    System.out.println("Instruction ignored. Flight was not found");
                }
            } else {
                System.out.println("Invalid airline name input format");
            }
        }
    }

    /**
     * if the first word of user input is "findRoute", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     */
    private void checkRoute(char [] chars, int i) {
        String origin = getStringUntilChar(chars, i, ' ');
        i += origin.length() + 1;
        words.add(origin);
        String destination = getStringUntilChar(chars, i, ' ');
        i += origin.length() + 1;
        words.add(destination);
        if(isAValidAirportName(origin) && isAValidAirportName(destination)) {
            routePriorityCheck(chars, i);
        } else {
            System.out.println("Invalid origin and destination airports. Unable to find route");
        }
    }

    /**
     * Method for validating week days
     * @param allDays string from user input containing week days
     * @return List of days if the string is valid, null if not
     */

    private ArrayList<Integer> checkWeekDays(String allDays) {
        ArrayList<Integer> days = new ArrayList<>();
        String current = new String();
        /**
         * state: 0 -> start or last read is a dash
         *        1 -> first letter read
         *        2 -> second letter read
         *        3 -> invalid input
         */
        int state = 0;
        for(int i=0; i < allDays.length() && state != 3; i++) {
            if(state == 0 || state == 1) {
                if(state == 0) {
                    current = "";
                }
                current = current + allDays.charAt(i);
                state++;
            } else if(state == 2) {
                if(!dayFactoryAdd(days,current)) {
                    state = 3;
                }
                if(allDays.charAt(i) == '-' && state != 3) {
                    state = 0;
                }
            }
        }
        /**
         * Only loop ending in state 2 are valid (last 2 chars are letters).
         * Method first verifies these last two chars and then returns.
         */
        if(state == 2) {
            if(!dayFactoryAdd(days,current)){
                state = 3;
            }
        }
        if(state != 2) {
            System.out.println("Invalid input of flight days");
        } else {
            return days;
        }
        return null;
    }
    private boolean dayFactoryAdd(List<Integer> days, String current) {
        if(current.equals("Lu")) {
            days.add(0);
        }
        else if(current.equals("Ma")) {
            days.add(1);
        }
        else if(current.equals("Mi")) {
            days.add(2);
        }
        else if(current.equals("Ju")) {
            days.add(3);
        }
        else if(current.equals("Vi")) {
            days.add(4);
        }
        else if(current.equals("Sa")) {
            days.add(5);
        }
        else if(current.equals("Do")) {
            days.add(6);
        } else {
            return false;
        }
        return true;
    }

    /**
     * if the first word of user input is "insert", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     */
    private void checkWorldTrip(char [] chars, int i) {
        String airport = getStringUntilChar(chars, i, ' ');
        i += airport.length() + 1;
        words.add(airport);
        if(isAValidAirportName(airport)) {
            routePriorityCheck(chars, i);
        }
    }

    private void checkFormat(char [] chars, int i) {
        String fmt = getStringUntilChar(chars, i, ' ');
        i += fmt.length() + 1;
        if(fmt.equals("KML") || fmt.equals("text")) {
            words.add(fmt);
            checkOutput(chars, i);
        } else {
            System.out.println("Invalid output format");
        }
    }

    private void checkOutput(char [] chars, int i) {
        String opt = getStringUntilChar(chars, i, '\0');
        output = opt;
    }


    /**
     * Multipurpose
     * assistant
     * methods
     *
     *
     *
     *
     *
     */

    private static String getStringUntilChar(char[] arr, int i, char end) {
        String ret = new String();
        while(i < arr.length && arr[i] != end) {
            ret = ret + arr[i];
            i++;
        }
        return ret;
    }

    private boolean isAValidAirportName(String word) {
        char [] airportChars =word.toCharArray();
        for (int j = 0; j < airportChars.length; j++) {
            if(!Character.isAlphabetic(airportChars[j])) {
                return false;
            }
        }
        if(word.length() == 3) {
            return true;
        }
        return false;
    }

    /**
     * Ohecks if priority input for a route is valid.
     * @param chars user input
     * @param i array index
     */

    private void routePriorityCheck(char[] chars, int i) {
        String priority = getStringUntilChar(chars, i, ' ');
        i += priority.length() + 1;
        words.add(priority);
        if(priority.equals("ft") || priority.equals("tt") || priority.equals("pr")) {
            String weekDays = getStringUntilChar(chars, i, '\0');
            List<Integer> days = checkWeekDays(weekDays);
            words.add(days);
            if(days != null) {
                RequestResult rr = null;
                if(words.get(0).equals("findRoute")) {
                    rr = atc.receiveFindRoute((String)words.get(1), (String)words.get(2), (String)words.get(3),(ArrayList<Integer>)words.get(4));
                } else if(words.get(0).equals("worldTrip")) {
                    rr = atc.receiveWorldTrip((String)words.get(1), (String)words.get(2), (ArrayList<Integer>)words.get(3));
                }
                requestResultOutput(rr);
            }
        } else {
            System.out.println("Invalid priority parameter");
        }
    }

    private void requestResultOutput(RequestResult rr) {
        if(!rr.isSuccess()) {
            System.out.println("We could not find any route for your requested departure and arival airports");
            return;
        }
        if (output.equals("stdout")) {
            if (format.equals("text")) {
                routeTextStd(rr);
            } else {
                System.out.println(KMLGenerator.createKML(rr.getRoute(), rr.getDays()));
            }
        } else {
            if(format.equals("text")) {
                routeTextFile(rr);
            } else {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(output));
                    writer.write(KMLGenerator.createKML(rr.getRoute(), rr.getDays()));
                    writer.close();
                }
                catch(IOException e) {
                    System.out.println("Unexpected output exception");
                }
            }
        }
    }

    private void routeTextStd(RequestResult rr) {
        int totalMinutes = (int)rr.getTotalTime()%60;
        int totalHours = (int)rr.getTotalTime()/60;
        int flightMinutes = (int)rr.getFlightTime()%60;
        int flightHours = (int)rr.getFlightTime()/60;
        Iterator<Integer> daysIt = rr.getDays().iterator();
        System.out.println("Your search was successful. Here is your itinerary:");
        System.out.println("");
        System.out.println("Total price: $" + rr.getPrice());
        System.out.println("Total time: " + totalHours + " hours, " + totalMinutes + " minutes");
        System.out.println("Flight time: " + flightHours + " hours, " + flightMinutes + " minutes");
        System.out.println("Your route:");
        for(Flight flight: rr.getRoute()) {
            System.out.println(flight.getOrigin().getName() + " ----> " + flight.getDestination().getName() +
                    " Flight: " + flight.getName() + ". Departure: " + day(daysIt.next()) + " at  " +
                    flight.getDepartureTime()/60 + ":" +flight.getDepartureTime()%60);
        }
    }

    private String day(int i) {
        switch (i) {
            case 0: return "Monday";
            case 1: return "Tuesday";
            case 2: return "Wednesday";
            case 3: return "Thursday";
            case 4: return "Friday";
            case 5: return "Saturday";
            case 6: return "Sunday";
            default: return null;
        }
    }

    private void routeTextFile(RequestResult rr) {
        PrintWriter fileOutput = null;
        try {
            fileOutput = new PrintWriter(new FileWriter(output));
        } catch (IOException E) {
            System.out.println("Unexpected output exception");
        }
        int totalMinutes = (int)rr.getTotalTime()%60;
        int totalHours = (int)rr.getTotalTime()/60;
        int flightMinutes = (int)rr.getFlightTime()%60;
        int flightHours = (int)rr.getFlightTime()/60;
        Iterator<Integer> daysIt = rr.getDays().iterator();
        fileOutput.println("Your search was successful. Here is your itinerary:");
        fileOutput.println("");
        fileOutput.println("Total price: $" + rr.getPrice());
        fileOutput.println("Total time: " + totalHours + " hours, " + totalMinutes + " minutes");
        fileOutput.println("Flight time: " + flightHours + " hours, " + flightMinutes + " minutes");
        fileOutput.println("Your route:");
        for(Flight flight: rr.getRoute()) {
            fileOutput.println(flight.getOrigin().getName() + " ----> " + flight.getDestination().getName() +
                    " Flight: " + flight.getName() + ". Departure: " + day(daysIt.next()) + " at  " +
                    flight.getDepartureTime()/60 + ":" +flight.getDepartureTime()%60);
        }
    }

    private boolean isADayTime(String date) {
        return date.matches("(([0-1][0-9])|(2[0-3])):[0-5][0-9]");
    }

    /**
     * Tests if duration format is valid. If so, returns total amount of miutes
     * state: 0 -> no number read
     *        1 -> one number read
     *        2 -> two numbers read
     * @param duration string
     * @return total minutes
     */
    private int durationInMinutes(String duration) {
        int state = 0;
        int minutes = 0;
        int firstDigit = -1;
        int secondDigit = -1;
        boolean hoursRead = false;
        boolean minutesRead = false;
        char current;
        for(int i = 0; i<duration.length(); i++) {
            if(minutesRead && hoursRead) {
                return -1;
            }
            current = duration.charAt(i);
            if(state == 0) {
                if(Character.isDigit(current)) {
                    firstDigit = Character.getNumericValue(current);
                    state = 1;
                }
                else {
                    return -1;
                }
            } else if(state == 1) {
                if(Character.isDigit(current)) {
                    secondDigit = Character.getNumericValue(current);
                    state = 2;
                } else if(current == 'h' && !hoursRead) {
                    hoursRead = true;
                    minutes = buildMinutes(0, firstDigit, 60);
                    state = 0;
                } else if(current == 'm' && i == duration.length() - 1 && firstDigit < 6) {
                    minutes += buildMinutes(0, secondDigit, 1);
                    minutesRead = true;
                } else {
                    return -1;
                }
            } else if(state == 2) {
                if(current == 'h' && !hoursRead) {
                    hoursRead = true;
                    minutes = buildMinutes(firstDigit, secondDigit, 60);
                    state =0;
                } else if(current == 'm' && i == duration.length() - 1 && firstDigit < 6) {
                    minutes += buildMinutes(firstDigit, secondDigit, 1);
                    minutesRead = true;
                } else {
                    return -1;
                }
            }
        }
        if(minutesRead && hoursRead) {
            return minutes;
        } else {
            return -1;
        }

    }

    private boolean isAValidAirlineName(String airline) {
        char [] airlineChars = airline.toCharArray();
        for (int j = 0; j < airlineChars.length; j++) {
            if(!Character.isAlphabetic(airlineChars[j])) {
                return false;
            }
        }
        if(!airline.isEmpty() && airline.length() <= 3 ) {
            return true;
        }
        return false;
    }

    private int buildMinutes(int first, int second, int factor) {
        return factor*(second + first * 10);
    }

    /**
     * returns price in Double format if true, null if not
     * @param price
     * @return
     */
    private Double validatePrice(String price) {
        Double priceDouble = -1.0;
        boolean valid = true;
        try {
            priceDouble = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            valid = false;
        }
        if(valid) {
            int decimalDigits = 0;
            boolean counting = false;
            for(int i = 0; i < price.length(); i++) {
                if(counting) {
                    decimalDigits++;
                }
                if(price.charAt(i) == '.') {
                    counting = true;
                }
            }
            if(decimalDigits <= 2 && priceDouble >= 0.0) {
                return priceDouble;
            }
        }
        return null;
    }

    private int depTimeToInt(String deptime) {
        String hours = new String();
        String minutes = new String();
        boolean hs = true;
        for(int i = 0; i < deptime.length(); i++) {
            if(hs) {
                if(deptime.charAt(i) == ':') {
                    hs = false;
                } else {
                    hours += deptime.charAt(i);
                }
            } else {
                minutes += deptime.charAt(i);
            }
        }
        int intH = 0;
        int intM = 0;
        try {
            intH = Integer.parseInt(hours);
            intM = Integer.parseInt(minutes);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing departure");
        }
        return intM+intH*60;
    }

}