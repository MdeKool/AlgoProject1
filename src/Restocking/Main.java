package Restocking;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            File infile = new File(args[0]);
            Scanner reader = new Scanner(infile);

            while (reader.hasNextLine()) {
                System.out.println(reader.nextLine());
            }
        } catch(IOException e) {
            System.out.println("An IOException occurred");
            e.printStackTrace();
        }
    }
}