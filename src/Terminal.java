import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *  @author Santiago Swinnen
 */

public class Terminal {
    /**
     * Creates the controller and gets ready for user input
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        AirTrafficController atc = new AirTrafficController();
        String input;
        boolean exit = false;
        while(!exit) {
            input = scanner.nextLine();
            if(input.equals("exit")){
                exit = true;
            } else {
                readInstruction(input, atc);
            }
        }

    }

    /**
     * Reads user input word by word. In case an instruction is read it is executed, otherwise no action is performed
     * @param instruction string input to be analized
     * @param atc current air traffic controller
     */

    private void readInstruction(String instruction, AirTrafficController atc) {
        char[] chars = instruction.toCharArray();
        int i = 0;
        String action = getStringUntilChar(chars, i, ' ');
        i += action.length() + 1;
        if(action.equals("insert")) {
            checkInsertion(chars, i, atc);
        } else if(action.equals("delete")) {
            checkDeletion(chars, i, atc);
        } else if(action.equals("findRoute")) {
            checkRoute(chars, i, atc);
        } else if(action.equals("worldTrip")) {
            checkWorldTrip(chars, i, atc);
        } else {
            System.out.println("Invalid instruction, please try again");
        }
    }

    /**
     * if the first word of user input is "insert", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     * @param atc air traffic controller
     */
    private void checkInsertion(char [] chars, int i, AirTrafficController atc) {
        //To be implemented
    }

    /**
     * if the first word of user input is "delete", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     * @param atc air traffic controller
     */
    private void checkDeletion(char [] chars, int i, AirTrafficController atc) {
        //To be implemented
    }

    /**
     * if the first word of user input is "checkRoute", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     * @param atc air traffic controller
     */
    private void checkRoute(char [] chars, int i, AirTrafficController atc) {

    }

    /**
     * if the first word of user input is "insert", this method is called for further checking
     * @param chars user input
     * @param i current index of char array
     * @param atc air traffic controller
     */
    private void checkWorldTrip(char [] chars, int i, AirTrafficController atc) {

    }



    private static String getStringUntilChar(char[] arr, int i, char end) {
        String ret = new String();
        while(i < arr.length && arr[i] != end) {
            ret = ret + arr[i];
            i++;
        }
        return ret;
    }
}