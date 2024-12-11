//Launches the InbetWordle game
public class GameLauncher{
   
   
   //Controls if the secret word is displayed in the game window (true) or not (false)
   public static final boolean DEBUG_SHOW_SECRET = true;
   
   //Controls if the hard coded word is used as the secret word (true) or a random
   //word read from the text file (false)
   public static final boolean DEBUG_USE_PRESET_SECRET = false;
   
   
   
   public static void main(String[] args){
      
      IBWGraphics.launchGame();
      
   }
   
   
   
   
   
}
