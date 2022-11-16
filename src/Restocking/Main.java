package Restocking;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            File infile = new File(args[0]);
            Scanner reader = new Scanner(infile);

            // Read first input line for #Locations, #Highways and Time.
            String[] l1 = reader.nextLine().split(" ");
            Graph g = new Graph(Integer.parseInt(l1[0]), Integer.parseInt(l1[2]));

            // Create highways
            while (reader.hasNextLine()) {
                String[] input = reader.nextLine().split(" ");
                g.makeHighway(Integer.parseInt(input[0]),
                            Integer.parseInt(input[1]),
                            Integer.parseInt(input[2]),
                            Integer.parseInt(input[3]));
            }

            System.out.println(g.toString());

            new PathFinder(g).run();

        } catch(IOException e) {
            System.out.println("An IOException occurred");
            e.printStackTrace();
        }
    }
}