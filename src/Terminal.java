import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *  @author Santiago Swinnen
 *  The Terminal class has an array of strings. Each time a valid word is read, the string is added to the array.
 *  If the user input gets to the point when the action is executed, array elements are passed as arguments.
 *  When the while statement starts again it clears the array
 */

public class Terminal {

    private AirTrafficController atc;
    private ArrayList<String> words;

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
     * @param instruction string input to be analized
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
            if (thirdWord.equals("airports")) {
                allAirportsValidation(chars, i);
            } else if(thirdWord.equals("flights")) {
                allFlightsValidation(chars, i);
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
        if(airportNameIsValid(airport)) {
            checkCoordinates(chars, i);
        }
    }

    private void checkCoordinates(char[] chars, int i) {

    }

    /**
     * Verifies if the flight expression given by the user is valid and if so, if it can de added
     * @param chars user input
     * @param i current index of char array
     */
    private void newFlightValidation(char[] chars, int i) {
        //To be implemented
    }

    /**
     * Verifies if the file given for all airports insertion is valid
     * and if the action (replace or append) is correct
     * @param chars user input
     * @param i current index of char array
     */
    private void allAirportsValidation(char[] chars, int i) {
        //To be implemented
    }

    /**
     * Verifies if the file given for all flights insertion is valid
     * and if the action (replace or append) is correct
     * @param chars user input
     * @param i current index of char array
     */
    private void allFlightsValidation(char[] chars, int i) {
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
            if(airportNameIsValid(airport)) {
                deleteAirport(airport);
            } else {
                System.out.println("Airport names should contain exactly three alphabetic characters");
            }
        } else if(secondWord.equals("flight")) {
            String flight = getStringUntilChar(chars, i, '\0');
            if(flightIsValid(flight)) {
                deleteFlight(flight);
            }
        } else if(secondWord.equals("all")) {
            String thirdWord = getStringUntilChar(chars, i, '\0');
            if(thirdWord.equals("airport")) {
                atc.deleteAllAirports();
            } else if(thirdWord.equals("flight")) {
                atc.deleteAllFlights();
            }
        }
    }



    private void deleteAirport(String airport) {

    }

    private void deleteFlight(String flight) {

    }

    private void deleteAllFlights() {

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
        if(airportNameIsValid(origin) && airportNameIsValid(destination)) {
            routePriorityCheck(chars, i);
        } else {
            System.out.println("Invalid origin and destination airports. Unable to find route");
        }
    }

    private void checkWeekDays(char[] chars, int i) {

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
        if(airportNameIsValid(airport)) {
            routePriorityCheck(chars, i);
        }
    }


    /**
     * Multipurpose assistant methods
     *
     *
     *
     *
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
    private boolean airportNameIsValid(String airport) {
        boolean allLetters = true;
        char [] airportChars =airport.toCharArray();
        for (int j = 0; j <= airportChars.length; j++) {
            if(!Character.isAlphabetic(airportChars[j])) {
                return false;
            }
        }
        if(airport.length() == 3) {
            return true;
        }
        return false;
    }

    /**
     * Takes a String and checks if it is a flight expression .Only checks syntax
     * @param flight
     * @return returns whether it is a flight expression or not.
     */

    private boolean flightIsValid(String flight) {
        //To be implemented
        return true;
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
            checkWeekDays(chars, i);
        } else {
            System.out.println("Invalid priority parameter");
        }
    }
}