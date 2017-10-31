import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *  @author Santiago Swinnen
 */

public class Terminal {

    private AirTrafficController atc;

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
        i += secondWord.length() + 1;
        if(secondWord.equals("airport")) {
            newAirportValidation(chars, i);
        } else if(secondWord.equals("flight")) {
            newFlightValidation(chars, i);
        } else if(secondWord.equals("all")) {
            String thirdWord = getStringUntilChar(chars,i,' ');
            i += secondWord.length() + 1;
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
        //To be implemented
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
        if(secondWord.equals("airport")) {
            String airport = getStringUntilChar(chars, i, ' ');
            i += airport.length() + 1;
            if(airportNameisValid(airport)) {
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
            String thirdWord = getStringUntilChar(chars, i, ' ');
            i += secondWord.length() + 1;
        }
    }



    private void deleteAirport(String airport) {

    }

    private void deleteFlight(String flight) {

    }

    /**
     * if the first word of user input is "checkRoute", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     */
    private void checkRoute(char [] chars, int i) {

    }

    /**
     * if the first word of user input is "insert", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     */
    private void checkWorldTrip(char [] chars, int i) {

    }



    private static String getStringUntilChar(char[] arr, int i, char end) {
        String ret = new String();
        while(i < arr.length && arr[i] != end) {
            ret = ret + arr[i];
            i++;
        }
        return ret;
    }
    private boolean airportNameisValid(String airport) {
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
}