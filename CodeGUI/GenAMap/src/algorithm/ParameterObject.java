package algorithm;

import java.util.ArrayList;

/**
 * This is an abstract class that must be instantiated.
 * A parameter object can be created in order to hold parameters for
 * interfacing with another class or the user.  It must return the
 * parameters as a string so they can be passed to the server side of
 * the application. For example, an association algorithm passing a
 * structure id to the database can store and pass it around in the
 * implementation of this class. 
 * @author rcurtis
 */
public abstract class ParameterObject
{
    public abstract String getParms();

    public ArrayList<String> getFiles()
    {
        return null;
    }
}
