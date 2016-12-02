/**
 * Created by Mike on 10/8/2016.
 */

import java.io.*;
import com.scitools.understand.Database;
import com.scitools.understand.Understand;


public class DataFile {


    // temporary variable to hold database that will be opened until it is returned to main() in analyzer.java
    static Database db;


    //
    // method that takes the filepath of a database as a string and opens the database(or at least attempts to...)
    //
    public static Database openDatabase(String filepath)
    {
        // holds the name of the database
        String filename = getFileName(filepath);

        try
        {
            // try to open database given by the String parameter
            db = Understand.open(filepath);
            //System.out.println("Database with name " + filename + " opened successfully.");

        }
        catch(Exception openError)
        {
            // show error message and exit the program
            System.out.println("\nFailed to open database with name: " + filename + "\n");
            //System.exit(0);
        }

        return db;

    }// end of openDatabase()


    //
    // method that takes a database and closes it(or at least attempts to...)
    //
    public static void closeDatabase(Database db)
    {
        // holds the name of the database
        String filename = getFileName(db.name());

        try
        {
            // attempt to close the database given as the parameter
            db.close();
            //System.out.println("Database with name " +  filename + " closed successfully.");
        }
        catch(Exception closeError)
        {
            // print message declaring failure to close the database
            System.out.println("Database with name " + filename + " closed unsuccessfully.");
            System.exit(0);
        }

    } // end of closeDatabase()


    //
    // method that takes a full filepath and returns just the name of the file (i.e. something.udb)
    //
    public static String getFileName(String path)
    {
        String name;

        File filename = new File(path);
        name = filename.getName();

        return name;

    }// end of getFileName


} // end of DataFile class