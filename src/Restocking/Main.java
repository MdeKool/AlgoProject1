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
                g.makeHighway(input[0] + ';' + input[1] + ';' + input[2], Integer.valueOf(input[3]));
            }



        } catch(IOException e) {
            System.out.println("An IOException occurred");
            e.printStackTrace();
        }
    }
}