import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.io.*;

public class IBWLogic {


    //Name of file containing all the possible "secret words"
    private static final String SECRET_WORDS_FNAME = "secret_words.txt";

    //Name of file containing all the valid guess words
    private static final String VALID_GUESSES_FNAME = "valid_words.txt";

    //Use for generating random numbers!
    private static final Random rand = new Random();

    //Dimensions of the game grid in the game window
    public static final int MAX_ROWS = 5;
    public static final int MAX_COLS = 6;

    //Character codes for the enter and backspace key press
    public static final char ENTER_KEY = KeyEvent.VK_ENTER; //(numeric value is: 10)
    public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE; //(numeric value is: 8)

    //The null character value (used to represent an "empty" value for a spot on the game grid)
    public static final char NULL_CHAR = 0;

    //Various Color Values
    private static final Color COLOR_GREEN = new Color(53, 209, 42); //(Green... right letter right place)
    private static final Color COLOR_YELLOW = new Color(235, 216, 52); //(Yellow... right letter wrong place)
    private static final Color COLOR_DGRAY = Color.DARK_GRAY; //(Dark Gray)
    private static final Color COLOR_LGRAY = new Color(160, 163, 168); //(Light Gray)

    //ints representing different potential arrow directions (drawn at the end of a letter row)
    public static final int ARROW_LEFT = -1;
    public static final int ARROW_RIGHT = 1;
    public static final int ARROW_BLANK = 0;

    //A preset, hard-coded secret word to be use when the resepective debug is enabled
    private static final char[] DEBUG_SECRET = {
        'R',
        'A',
        'I',
        'D',
        'E',
        'R'
    };


    //...Feel free to add more **PRIMITIVE FINAL** variables of your own!
    private static final int WINNING_SCORE = 6;

    private static final int NUM_OF_VALID_WORDS = 15055;

    private static final int NUM_OF_SECRET_WORDS = 843;





    //******************   NON-FINAL GLOBAL VARIABLES   ******************
    //********  YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS!  ********


    //Array storing all valid guesses read out of the respective file
    private static String[] dictionary;

    //The active row/col where the user left off typing
    private static int activeRow, activeCol;

    //The indices out of the dictionary where the current "leftmost" and
    //"rightmost" (ie alphabetically earliest and latest, respetively) occur
    private static int leftmostWordIdx, rightmostWordIdx;

    //*******************************************************************



    //Short for "initilaize".  This function gets called ONCE when the game is
    //very first launched before the user has the opportunity to do anything.
    //
    //Should perform any initialization that needs to happen at the start of the game,
    //and return the randomly chosen "secret word" as a char array
    //
    //If either of the valid guess or secret words files cannot be read, or are
    //missing the word count in the first line) this function returns null.
    public static char[] init() {
        leftmostWordIdx = -1;
        rightmostWordIdx = -1;
        String[] words = new String[NUM_OF_SECRET_WORDS];
        dictionary = new String[NUM_OF_VALID_WORDS];

        try{
            Scanner input = new Scanner(new File(SECRET_WORDS_FNAME));
            Scanner inputTwo = new Scanner(new File(VALID_GUESSES_FNAME));

            for(int i = 0; i < words.length; i++){
                String word = input.nextLine().trim();
                if (word.length() == MAX_COLS){
                    words[i] = word;
                }
            }

            for(int i = 0; i < dictionary.length; i++){
                String word = inputTwo.nextLine().trim();
                dictionary[i] = word;
            }

            input.close();
            inputTwo.close();

            int randNum = rand.nextInt(words.length);

            if (GameLauncher.DEBUG_USE_PRESET_SECRET)
                return DEBUG_SECRET;

            return words[randNum].toCharArray();
        }
        catch(FileNotFoundException e){
            System.exit(1);
        }
        return DEBUG_SECRET; //Part 3.1.1 step 1!
    }



    //Complete your warmup task (Section 3.1.1 step 2) here by calling the requisite
    //functions out of IBWGraphics.
    //This function gets called ONCE after the graphics window has been
    //initialized and init has been called.
    public static void warmup() {
        System.out.println("Hellooooo!");
        System.out.println("DEBUG: The secret word is: " +IBWGraphics.getSecretWordStr());
        //All of your warmup code will go in here except for the
        //"wiggle" task (3.1.1 step 3)... where will that go?
        /*
          IBWGraphics.setCellChar(0,0,'C');
          IBWGraphics.setCellColor(0,0, COLOR_YELLOW);
          IBWGraphics.setCellChar(1,3, 'O');
          IBWGraphics.setCellChar(3,5, 'S');
          IBWGraphics.setCellColor(3,5,COLOR_GREEN);
          IBWGraphics.setCellChar(4,5, 'C');
          IBWGraphics.setCellColor(4,5,COLOR_DGRAY);
          IBWGraphics.setArrowDirection(1,ARROW_LEFT);
          IBWGraphics.setArrowDirection(2,ARROW_RIGHT);
        */

    }


    //This function gets called everytime the user types a valid key on the
    //keyboard (alphabetic character, enter, or backspace) or clicks one of the
    //keys on the graphical keyboard interface.
    //
    //The key pressed is passed in as a char value.
    public static void keyReact(char key) {
        //placeholder debug print message
        System.out.println("key pressed: '" + key + "' (int value: " + (int) key + ")");

        /* if (key == 'P'){
          IBWGraphics.triggerWiggle(3);
          }
        */

        System.out.println("Active Col: " + activeCol);
        System.out.println("Active Row: " + activeRow);

        processBackspace(key);

        if (key != ENTER_KEY && key != BACKSPACE_KEY && activeCol < MAX_COLS) {
            IBWGraphics.setCellChar(activeRow, activeCol, key);
            activeCol++;
        }
        if (activeCol == MAX_COLS && key == ENTER_KEY) {
            if(isValidGuess(getGuess(activeRow))){
                validateGuess();
                activeCol = 0;
                activeRow++;
            }
            else {
                IBWGraphics.triggerWiggle(activeRow);
            }
        } else if (activeCol != MAX_COLS && key == ENTER_KEY) {
            IBWGraphics.triggerWiggle(activeRow);
        }
    }


    static boolean checkIfExists(char c, char[] secretWordString, int[] letterUse) {
        for (int i = 0; i < secretWordString.length; i++) {
            if (secretWordString[i] == c&& letterUse[i] == 0) {
                return true;
            }
        }
        return false;
    }

    static void setKeyboardColors(){
        for (int i = 0; i < MAX_ROWS; i++){
            for(int j = 0; j < MAX_COLS; j++){
                char activeKey = IBWGraphics.getCellChar(i,j);
                if (activeKey != NULL_CHAR && activeKey != ' '){
                Color color = IBWGraphics.getKeyboardColor(activeKey);
                    if (color != COLOR_LGRAY && color != COLOR_GREEN ){
                        IBWGraphics.setKeyboardColor(activeKey,IBWGraphics.getCellColor(i,j));
                    }
                }
            }
        }
    }
    static void validateGuess(){
        String guesses = getGuess(activeRow);
        String secretWordString = IBWGraphics.getSecretWordStr();
        int count = validateColors();
        setKeyboardColors();

        if (guesses.compareTo(secretWordString) > 0 && count!= WINNING_SCORE){
            IBWGraphics.setArrowDirection(activeRow, ARROW_LEFT);

        } else if (count != WINNING_SCORE){
            IBWGraphics.setArrowDirection(activeRow, ARROW_RIGHT);

        }

        if (count == WINNING_SCORE) {
            IBWGraphics.endGame(true);
        } else if (activeCol == MAX_COLS && activeRow == MAX_ROWS-1) {
            IBWGraphics.endGame(false);
        }

    }

    static String getGuess(int row){
        String guess = "";
        for (int i = 0; i < MAX_COLS; i++) {
            char currentChar = IBWGraphics.getCellChar(row, i);
            guess+=currentChar;
            }
        return guess;
    }

    static boolean isValidGuess(String guesses){
        guesses = guesses.toLowerCase();
        int currentWordIdx = -1;

        for (int i = 0; i < dictionary.length; i++){
            if(dictionary[i] != null && dictionary[i].equals(guesses)){
                currentWordIdx = i;
                break;
            }
        }

        if (currentWordIdx == -1){
            return false;
        }


        if (leftmostWordIdx == -1 && rightmostWordIdx == -1){
            leftmostWordIdx = currentWordIdx;
            rightmostWordIdx = currentWordIdx;
            return true;
        }
        String leftBound = dictionary[leftmostWordIdx];
        String rightBound = dictionary[rightmostWordIdx];

        if (IBWGraphics.getArrowDirection(activeRow-1) == 1){
            if (guesses.compareTo(leftBound) <= 0){
                return false;
            }
        }

        else{
            if (guesses.compareTo(rightBound)>= 0){
                return false;
            }
        }


        if (currentWordIdx < leftmostWordIdx){
            leftmostWordIdx = currentWordIdx;
        }

        if (currentWordIdx > rightmostWordIdx){
            rightmostWordIdx = currentWordIdx;
        }

        return true;
        }

    static void processBackspace(char key){
        if (key == BACKSPACE_KEY){
            if (activeCol > 0) {
                activeCol--;
                IBWGraphics.setCellChar(activeRow, activeCol, NULL_CHAR);
            }
        }
    }
    static int countAppearances(char[] secretWordChar, char c){
        int count = 0;
        for (char letter: secretWordChar){
            if (letter == c){
                count++;
            }
        }
        return count;
    }
    static int validateColors(){
        String secretWordString = IBWGraphics.getSecretWordStr();
        char[] secretWordChar = IBWGraphics.getSecretWordArr();
        String guesses = getGuess(activeRow);
        int[] letterUseSecretWord = new int[MAX_COLS];
        int[] letterUseGuess = new int[MAX_COLS];
        boolean[] green = new boolean[MAX_COLS];
        int count = 0;

        for (int i = 0; i < MAX_COLS; i++) {
            char currentChar = IBWGraphics.getCellChar(activeRow, i);
            if (currentChar == secretWordChar[i]) {
                IBWGraphics.setCellColor(activeRow, i, COLOR_GREEN);
                letterUseSecretWord[i]++;
                green[i] = true;
                count++;
            }
        }

        for (int i = 0; i < MAX_COLS; i++) {
        char currentChar = IBWGraphics.getCellChar(activeRow, i);
        if (!green[i] && checkIfExists(currentChar, secretWordChar, letterUseSecretWord)) {
            int secretWordAppearances = countAppearances(secretWordChar, currentChar);
            if (letterUseGuess[i] < secretWordAppearances) {
            IBWGraphics.setCellColor(activeRow, i, COLOR_YELLOW);
            letterUseGuess[i]++;
            for (int j = 0; j < MAX_COLS; j++) {
                if (secretWordChar[j] == currentChar && letterUseSecretWord[j] == 0) {
                    letterUseSecretWord[j]++;
                    break;
                        }
                    }
                }
            } else if (!green[i]) {
            IBWGraphics.setCellColor(activeRow, i, COLOR_DGRAY);
            }
        }
        return count;
    }
}
