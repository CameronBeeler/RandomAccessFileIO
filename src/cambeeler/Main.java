package cambeeler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main
{
//    private static Map<Integer, Location> locations = new HashMap<Integer, Location>();
//    I'm going to read in two files.  One is an index file, the other is a data file'
//    This will require that the class will allow for multiple instantiations
//    The index file will be loaded into memory
//    And that we will need to retain a handle to the data file, as we will access it by request.

    private static Locations locations = new Locations();

    public static void main(String[] args)
    {
        // Change the program to allow players to type full words, or phrases, then move to the
        // correct location based upon their input.
        // The player should be able to type commands such as "Go West", "run South", or just "East"
        // and the program will move to the appropriate location if there is one.  As at present, an
        // attempt to move in an invalid direction should print a message and remain in the same place.
        //
        // Single letter commands (N, W, S, E, Q) should still be available.

	    Scanner scanner = new Scanner(System.in);

        Map<String, String> vocabulary = new HashMap<String, String>();
        vocabulary.put("QUIT", "Q");
        vocabulary.put("NORTH", "N");
        vocabulary.put("SOUTH", "S");
        vocabulary.put("WEST", "W");
        vocabulary.put("EAST", "E");
        vocabulary.put("DOWN", "D");
        vocabulary.put("UP", "U");

//        System.out.println(locations.get(0).getDescription());


        int node = 140;
        while(true) {
            Location here = Locations.getRAFLocationData(node);
            System.out.println(here.getDescription());
            Map<String, Integer> exits = here.getExits();
            System.out.print("Available exits are ");
            for(String exit: exits.keySet()) {
                System.out.print(exit + ", ");
            }
            System.out.println();

            String direction = scanner.nextLine().toUpperCase();
            if(direction.length() > 1) {
                String[] words = direction.split(" ");
                for(String word: words) {
                    if(vocabulary.containsKey(word)) {
                        direction = vocabulary.get(word);
                        break;
                    }
                }
            }

            if(exits.containsKey(direction)) {
                node = exits.get(direction);
                if(node==0)
                {
                    System.out.println("Goodbye, Thanks for playing!");
                    break;
                }

            } else {
                System.out.println("You cannot go in that direction");
            }
        }

    }
}
