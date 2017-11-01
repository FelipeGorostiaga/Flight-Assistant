import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *  @author Santiago Swinnen
 *  The Terminal class has an array of strings. Each time a valid word is read, the string is added to the array.
 *  If the user input gets to the point when the action is executed, array elements are passed as arguments.
 *  When the while statement starts again it clears the array
 */



public class Terminal {

    private AirTrafficController atc;
    private ArrayList<Object> words;

    public Terminal() {
        this.atc = new AirTrafficController();
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
                System.out.println("'insert all' command only supports one of the following options: " +
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
    private void newAirportValidation(char[] chars, int i) {
        String airport = getStringUntilChar(chars, i, '\0');
        i += airport.length() + 1;
        words.add(airport);
        if(isAThreeCharWord(airport)) {
            checkCoordinates(chars, i);
        }
    }

    private boolean checkCoordinates(char[] chars, int i) {
        String lat = getStringUntilChar(chars, i, ' ');
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
                atc.insertAirport((String)words.get(2),latitude,longitude);
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
    private void newFlightValidation(char[] chars, int i) {
        String airline = getStringUntilChar(chars, i, ' ');
        i += airline.length() + 1;
        boolean valid = true;
        if(isAThreeCharWord(airline)) {
            words.add(airline);
            Integer flightNumber = 0;
            String flightNum = getStringUntilChar(chars, i, ' ');
            try {
                flightNumber = Integer.parseInt(flightNum);
            } catch(NumberFormatException e) {
                valid = false;
                System.out.println("Invalid flight number");
            }
            if(valid) {
                words.add(flightNumber);
                newFlightSecondCheck(chars, i);
            } else {
                System.out.println("Invalid flight number");
            }
        } else {
            System.out.println("Invalid airline name");
        }
    }

    /**
     * This method is called only if three first airport insertion parameters are valid
     * @param chars input
     */

    private void newFlightSecondCheck(char[] chars, int i) {
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
            valid = isAThreeCharWord(origin) && isAThreeCharWord(destination);
            if (valid) {
                String depTime = getStringUntilChar(chars, i, ' ');
                i += depTime.length() + 1;
                valid = isADayTime(depTime);
                if (valid) {
                    String duration = getStringUntilChar(chars, i, ' ');
                    i += duration.length() + 1;
                    String price = getStringUntilChar(chars, i, '\0');
                    double priceDouble = 0.0;
                    if (valid) {
                        try {
                            priceDouble = Double.parseDouble(price);
                        } catch (NumberFormatException e) {
                            valid = false;
                        }
                        int time = durationInMinutes(duration);
                        valid = valid && durationInMinutes(duration) != -1 && validPrice(priceDouble);
                        if (valid) {
                            atc.receiveFlightInsertion((String) words.get(1), (Integer) words.get(2), dayList,
                                    origin, destination, depTime, time, priceDouble);
                        } else {
                            System.out.println("Invalid parameters for flight insertion");
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
        } else {
            System.out.println("Invalid parameters for flight insertion");
        }

    }

    /**
     * Verifies if the file given for all airports insertion is valid
     * and if the action (replace or append) is correct
     * @param chars user input
     * @param i current index of char array
     * @param item indicates if the massive insertion involves flights or airports
     */
    private void massiveValidation(char[] chars, int i, String item) {
        String fileName = getStringUntilChar(chars, i, ' ');
        String action = getStringUntilChar(chars, i, '\0');
        boolean fileIsValid = true;
        BufferedReader br = null;
        try{
           br = new BufferedReader(new FileReader("file.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            fileIsValid = false;
        }
        if(fileIsValid) {
            if(action.equals("append") || action.equals("replace")) {
                if(item.equals("airports")) {
                    massiveAirportInsertion(action, br);
                } else if(item.equals("flight")) {
                    massiveFlightInsertion(action, br);
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
     */
    private void massiveAirportInsertion(String action, BufferedReader br) {
        String line;
        int lineNumber = 1;
        boolean lineIsValid;
        if(action.equals("append")) {
            try{
                while((line=br.readLine())!=null) {
                    lineIsValid = airportLineProcessing(line);
                    if(!lineIsValid) {
                        System.out.println("Line " + lineNumber + " could not be added " +
                                "because it has an invalid format");
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                System.out.println("Aborting. Unexpected input/output exception");
            }

        }
    }

    /**
     * Individual airport processing from file. This method builds an individual airport insertion
     * with the terminal array. If argument number is correct checkCoordinates is called and if they
     * are okay, which automatically inserts the airport if these are correct.
     * @param line file input
     * @return true if airport is syntactically correct, false if not.
     */
    private boolean airportLineProcessing(String line) {
        words.clear();
        words.add("insert");
        words.add("airport");
        ArrayList<String> lineWords = new ArrayList<>();
        char [] lineChars = line.toCharArray();
        int i = 0;
        String current;
        while(i < lineChars.length) {
            current = getStringUntilChar(lineChars, i, '#');
            i += current.length() + 1;
            words.add(current);
        }
        if(lineWords.size() != 5) {
            return false;
        }
        /**
         * Building a char array to seize the individual insertion coordinate checking method
         */
        String coordinates = (String)words.get(3);
        coordinates += ' ';
        coordinates += (String)words.get(4);
        char [] coordChars =  coordinates.toCharArray();
        if(isAThreeCharWord(lineWords.get(0))) {
            return checkCoordinates(coordChars, 0);
        }
        return false;
    }

    private void massiveFlightInsertion(String action, BufferedReader br) {
        //To be implemented
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
            if(isAThreeCharWord(airport)) {
                atc.deleteAirport(airport);
            } else {
                System.out.println("Airport names should contain exactly three alphabetic characters");
            }
        } else if(secondWord.equals("flight")) {
            checkFlightDeletion(chars, i);
        } else if(secondWord.equals("all")) {
            String thirdWord = getStringUntilChar(chars, i, '\0');
            if(thirdWord.equals("airport")) {
                atc.deleteAllAirports();
            } else if(thirdWord.equals("flight")) {
                atc.deleteAllFlights();
            }
        }
    }

    private void checkFlightDeletion(char[] chars, int i) {
        String airlineName = getStringUntilChar(chars, i, ' ');
        String flightNum = getStringUntilChar(chars, i, '\0');
        Integer num = 0;
        boolean validNumber = true;
        try {
            num = Integer.parseInt(flightNum);
        } catch (NumberFormatException e) {
            System.out.println("Invalid flight number");
            validNumber = false;
        }
        if(validNumber && isAThreeCharWord(airlineName)) {
            atc.deleteFlight(airlineName, num);
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
        if(isAThreeCharWord(origin) && isAThreeCharWord(destination)) {
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
        for(int i=0; i<allDays.length() && state != 3; i++) {
            if(state == 0 || state == 1) {
                current = current + allDays.charAt(i);
                state++;
            }
            if(state == 2) {
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
        if(isAThreeCharWord(airport)) {
            routePriorityCheck(chars, i);
        }
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

    private boolean isAThreeCharWord(String word) {
        boolean allLetters = true;
        char [] airportChars =word.toCharArray();
        for (int j = 0; j <= airportChars.length; j++) {
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
            if(days != null) {
                if(words.get(0).equals("findRoute")) {
                    atc.receiveFindRoute((String)words.get(1), (String)words.get(2), (String)words.get(3),(ArrayList<Integer>)words.get(4));
                } else if(words.get(0).equals("worldTrip")) {
                    atc.receiveWorldTrip((String)words.get(1), (String)words.get(2), (ArrayList<Integer>)words.get(3));
                }
            }
        } else {
            System.out.println("Invalid priority parameter");
        }
    }

    private boolean isADayTime(String date) {
        if(Pattern.matches("^[0-2][0-3]h:[0-5][0-9]m$",date)){
            return true;
        }
        return false;
    }

    /**
     * Tests if duration format is valid. If so, returns total amount of miutes
     * state: 0 -> no number read
     *        1 -> one number read
     *        2 -> two numbers read
     * @param duration
     * @return
     */

    private int durationInMinutes(String duration) {
        int state = 0;
        int minutes = 0;
        int firstDigit = -1;
        int secondDigit = -1;
        boolean hoursRead = false;
        for(int i = 0; i<duration.length(); i++) {
            char current = duration.charAt(i);
            if(state == 0) {
                if(Character.isDigit(current)) {
                    firstDigit = Character.getNumericValue(current);
                    state = 1;
                }
                else {
                    return -1;
                }
            }
            if(state == 1) {
                if(Character.isDigit(current)) {
                    secondDigit = Character.getNumericValue(current);
                    state = 2;
                } else if(current == 'h' && !hoursRead) {
                    hoursRead = true;
                    minutes = buildMinutes(0, firstDigit, 60);
                    state = 0;
                } else {
                    return -1;
                }
            }
            if(state == 2) {
                if(current == 'h' && !hoursRead) {
                    hoursRead = true;
                    minutes = buildMinutes(firstDigit, secondDigit, 60);
                    state =0;
                } else if(current == 'm' && i == duration.length() - 1 && firstDigit < 6) {
                    minutes += buildMinutes(firstDigit, secondDigit, 1);
                } else {
                    return -1;
                }
            }
        }
        return minutes;
    }

    private int buildMinutes(int first, int second, int factor) {
        return factor*(second + first * 10);
    }

    private boolean validPrice(Double price) {
        Double fractionPart = price - price.longValue();
        int decimalDigits = fractionPart.toString().length() - 2;
        if(decimalDigits == 2 && price >= 0.0) {
            return true;
        }
        return false;
    }
}