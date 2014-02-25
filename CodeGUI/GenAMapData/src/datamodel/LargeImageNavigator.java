package datamodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * When a network is greater than 200 nodes, or an association plot has more than
 * 200 markers/traits, then it becomes very time consuming to query the database
 * for each value ... especially when you are looking for over 5600x5600 values!
 * 
 * In order to account for this problem, we create resolution data on the local
 * file system - up to 5 different stored file resolutions. After that, we query
 * the database for the values. This is done elsewhere, and this file is called
 * once the resolution data is ready.
 * 
 * Given the location of the files, this class acts as the front end to the different
 * resolutions present in the data.
 *
 * The large network navigator will allow the user to browse through the large
 * network, including zooming, panning, etc.
 *
 * This acts as a front end to the file system which stores the different resolution
 * data.  It also knows the labels for the axes.
 *
 * This class is abstract, and should be inherited by a class specific to what kind
 * of data it will be dealing with -association or network. For example, network si
 * trait by trait, while association is marker by trait. 
 *
 * @author rcurtis
 */
public abstract class LargeImageNavigator
{
    /**
     * The main directory where the data for the different resolutions is stored
     */
    protected String directory;
    /**
     * The number of traits in this data
     */
    protected int traitSize;
    /**
     * Pointers to objects representing the traits - and their database values.
     */
    protected ArrayList<Trait> traits;
    /**
     * An array list that stores the number of traits per cell for each resolution -
     * that is, how many traits does the one value in the cell represent??
     */
    protected ArrayList<Integer> noTraitsPerCell;
    /**
     * The number of resolutions found in the file system - this can range from 1 to 5.
     */
    protected int noResolutions;
    /**
     * This array list stores the index of the trait for each cell to know where it begins. For example,
     * if cell one contains 10 traits, then cell 2 will start on trait 11. 
     */
    protected ArrayList<ArrayList<Integer>> starts;
    /**
     * The array that represents the values that are going to be stored in the heat
     * chart on the screen. The maximum preferred size is 200 x 200, although smaller
     * z matrices are allowed.
     */
    protected double[][] zvals;
    /**
     * This array stores the values for each cell for the x axis. If there are 5 traits per
     * cell, then this array will go something like 1,6,11, etc.
     */
    protected Object[] xaxis;
    /**
     * This array stores the values for each cell for the y axis. If there are 5 traits per
     * cell then this array will go something like 1,6,11, etc.
     */
    protected Object[] yaxis;
    /**
     * This value stores the resolution (1,2,3,4...) that the x axis is currently on.
     */
    protected int curResX;
    /**
     * This value stores the resolution (1,2,3,4...) that the y axis is on.
     */
    protected int curResY;
    /**
     * This value stores the current cell that the x axis begins at in the overall image.
     */
    protected int curCellX;
    /**
     * This value stores the current cell that the y axis begins at in the overall image. 
     */
    protected int curCellY;
    /**
     * This hashmap will map the database ids to the current index of that id in the image.
     */
    protected HashMap<Integer,Integer> mapIdtoIdx;
    /**
     * This is an array that stores how many traits are in each file for each resolution.
     * For example, resolution 1 may have 6000 traits per file. Then res 2 would have 3000
     * traits per file, res 3 would have 1500 traits per file, and so on. 
     */
    protected ArrayList<Integer> noTraitsPerFile;

    /**
     * Returns the zvalue matrix for this matrix as it is currently zoomed in.
     * This is what will display on the screen.
     */
    public double[][] getZVals()
    {
        return zvals;
    }

    /**
     * Returns the lables for each current x cell in the zvalue matrix - this is
     * the count order of the cells, it is the sum total of all traits represented
     * by the cell.
     */
    public Object[] getXAxisLabels()
    {
        return this.xaxis;
    }

    /**
     * Returns the labels for each current y cell in the zvalue matrix - this is
     * the count order fo the cells, it is the sum total of all traits represented
     * by the cell. 
     */
    public Object[] getYAxisLabels()
    {
        return this.yaxis;
    }

    /**
     * Abstrat method that must be implemented by inheriting class. This looks
     * in the current directory and finds out how many resolutions are present for this
     * heat map.
     * @param dir The directory that the resolutions are stored in.
     * @return an integer representing the number of resolution for this heat chart. 
     */
    protected abstract int findNoResolutions(String dir);

    /**
     * Callibrate takes parameters from the old image and determines where to draw the new
     * image. It also calls the methods to create the zvalue map of the new image.
     * This is where zooming, panning, etc take place. It is where the current resolution
     * is decided, and what to do with the z value matrix is determined.
     *
     * Once this method has been called, the zvalue matrix and the axis labels are ready
     * to be retrieved by the calling class. This method should not be called in the case
     * that the user is zooming out.
     * @param xmin the coordinate in the z value matrix of the new x value that is the minimum to display.
     * @param xmax the coordinate in teh z value matrix of the new x value that is the maximum to display.
     * @param ymin the coordinate in the z value matrix of the new y value taht is the minimum to display.
     * @param ymax the coordinate in the z value matrix of the new y value that is the maximum to display.
     * @param maxThresh This parameter is passed in representing the color scale that should be used in coloring the nodes - the maximum value that is represented with the max color scale value.
     * @param minThresh This parameter is passed in representing the color scale that should be used in coloring the nodes - the minimum value that is represented with the min color scale value.
     */
    public abstract void callibrate(int xmin, int xmax, int ymin, int  ymax, double maxThresh, double minThresh);

    /**
     * This method is called instead of callibrate when the user performs a zoom out
     * action. This will callibrate the image and get it ready for display as well. 
     * @param maxThresh This parameter is passed in representing the color scale that should be used in coloring the nodes - the maximum value that is represented with the max color scale value.
     * @param minThresh This parameter is passed in representing the color scale that should be used in coloring the nodes - the minimum value that is represented with the min color scale value.
     */
    public void zoomOut(double maxThresh, double minThresh)
    {
        //find out how many cells are displayed on the screen
        //double that.
        int noCellsShownX = (int)Math.ceil(zvals[0].length/2);
        if(noCellsShownX <= 1)
            noCellsShownX = 2;
        int noCellsShownY = (int)Math.ceil(zvals.length/2);
        if(noCellsShownY <= 1)
            noCellsShownY = 2;
        int xmin = -1 * noCellsShownX;
        int xmax = noCellsShownX*3;
        int ymin = -1 * noCellsShownY;
        int ymax = noCellsShownY*3;

        if(xmin*this.noTraitsPerCell.get(curResX-1) + this.noTraitsPerCell.get(curResX-1)*curCellX < 0)
        {
            xmin = -1 * curCellX;
        }
        ymin = checkYBounds(ymin);

        //ensure that I am not dropping below 0 or over the max.
        this.callibrate(xmin, xmax, ymin, ymax, maxThresh, minThresh);
    }

    /**
     * This method is called as a quick way to access the full, zoomed out image.
     * It returns a matrix that could be assigned to the z matrix of values. 
     * @return
     */
    protected abstract double[][] loadOriginalResolution();

    /**
     * This method is called when the user performs a panning motion. It tells
     * the heat chart image how far to pan the image to the right, left, up, or down.
     * @param xMoved How far the image should be moved in the x direction.
     * @param yMoved How far the image should be moved in teh y direction.
     * @param maxThresh This parameter is passed in representing the color scale that should be used in coloring the nodes - the maximum value that is represented with the max color scale value.
     * @param minThresh This parameter is passed in representing the color scale that should be used in coloring the nodes - the minimum value that is represented with the min color scale value.
     */
    public abstract void pan(int xMoved, int yMoved, double minThresh, double maxThresh);

    /**
     * Given the current location in the matrix, along with the height, width,
     * and resolution, figure out which files to open and open them.
     *
     * Then, fill in the values in the matrix.  
     * @param x the current x location in the matrix.
     * @param y the current y location in the matrix.
     * @param useDB A quick reference of whether the resolution selected is the database resolution.
     * @param width The width of the matrix.
     * @param height The height of the matrix.
     * @param resToUse The index specifying which resolution to use. 
     */
    protected abstract void defineMatrix(int x, int y, boolean useDB, int width, int height, int resToUse);


    /**
     * If the user wants to find out the indeces of the traits currently on the screen,
     * they can call this method and those indeces will be placed in the subset object passed
     * in.
     * @param subset The object that will store the indeces of the new subset. 
     */
    public abstract void getSubSet(ArrayList<Integer> subset);

    /**
     * Checks the y axis to ensure that the user isn't panning off of the image.
     * @param ymin the y min position selected
     * @return the y min position if it doesn't go off the screen, or the zero value if it does.
     */
    protected abstract int checkYBounds(int ymin);
}
