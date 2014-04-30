package control.itempanel;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

/**
 * A thread item can be added to the threads list and then tracks the data management
 * with the database. It can be started, stopped, have errors, etc. 
 * @author RCurtis
 */
public abstract class ThreadItem extends Observable
{
    /**
     * Returns the name of the executing thread
     * @return
     */
    public abstract String getName();
    /**
     * Returns the error that happened to cause this thread to stop.
     * @return
     */
    public abstract String getErrorText();
    /**
     * Returns true if the thread has encountered and error and has stopped.
     * @return
     */
    public final boolean isError()
    {
        return isError;
    }
    /**
     * Returns the percent complete as an integer 0-100
     * @return
     */
    public final int getValue()
    {
        return value;
    }
    /**
     * Updates the value of this currently executing task. If the task
     * is complete, it notifyes the view so that it can appropriately update. 
     * @param val
     */
    public final void setValue(int val)
    {
        this.value = val;
        if(this.value == 100)
        {
            File file = new File("refresh.txt");
            try
            {
                file.createNewFile();
            }
            catch (IOException ex)
            {
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Sets the isError value for this ThreadItem. This will call an update
     * to reassign the thread. 
     * @param error
     */
    public final void setIsError(boolean error)
    {
        if(isError == false && error == true)
        {
            isError = true;
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Returns a status message to let the user know what is happening
     * @return
     */
    public abstract String getStatus();
    /**
     * Returns a message letting the user know what happened. 
     * @return
     */
    public abstract String getSuccessMessage();
    /**
     * Can be used to store the value of percent complete of this thread
     */
    private int value;
    /**
     * Should be set in order to demonstrate that this thread is in error. 
     */
    private boolean isError = false;
    /**
     * All items have a task that they only start at the call of this method.
     */
    public abstract void start();
    /**
     * Each load should have an id
     */
    public int id = -1;

    /**
     * Returns true if the process has completed its execution or errored out.
     * @return
     */
    public boolean isFinished()
    {
        return value == 100 || isError;
    }
}
