package datamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import realdata.DataManager;

/**
 * The LargeAssocNavigator will serve as a navigator to go through a large
 * association image.  It extends the LargeImageNavigator, which acts as a template to this
 * and the LargeNetworkNavigator
 *
 * An Association Heat chart has the markers ordered on the left axis, with the
 * traits ordered on the x axis. The traits on the x axis can be ordered by some clustering,
 *  whiel the markers on the y axis are fixed.
 *
 * @author RCurtis
 */
public class LargeAssocNavigator extends LargeImageNavigator
{
    /**
     * The association set that this heat chart represents.
     */
    protected AssociationSet assoc;
    /**
     * The number of markers to display on the x axis.
     */
    private int markerSize;
    /**
     * An ArrayList of pointers to all of the markers dispalyed in this heat chart.
     */
    private ArrayList<Marker> markers;
    /**
     * The number of markers represented by a particular cell at the different
     * iterations. 
     */
    protected ArrayList<Integer> noMarkersPerCell;
    /**
     * This list of indexes regarding the trait start indeces
     */
    protected ArrayList<ArrayList<Integer>> startTraits;
    /**
     * This list of indexes of where we start the markers
     */
    protected ArrayList<ArrayList<Integer>> startMarkers;
    /**
     * The number of markers per file. 
     */
    protected ArrayList<Integer> noMarkersPerFile;

    /**
     * Constructor
     */
    public LargeAssocNavigator(ArrayList<Trait> traitNodes, String dir, AssociationSet assoc)
    {
        super();
        this.assoc = assoc;
        directory = dir;
        traits = traitNodes;
        traitSize = traitNodes.size();
        markerSize = assoc.getMarkerSet().getMarkers().size();
        markers = assoc.getMarkerSet().getMarkers();
        noTraitsPerCell = new ArrayList<Integer>();
        noMarkersPerCell = new ArrayList<Integer>();
        noResolutions = findNoResolutions(dir);
        curResX = 1;
        curResY = 1;
        curCellX = 0;
        curCellY = 0;

        mapIdtoIdx = new HashMap<Integer,Integer>();
        for(Trait t: traits)
        {
            mapIdtoIdx.put(t.getId(), t.getSortIdx());
        }

        noTraitsPerFile = new ArrayList<Integer>();
        noMarkersPerFile = new ArrayList<Integer>();

        int i = 1;
        for(int j = 1; j <= noResolutions; j ++)
        {
            noTraitsPerFile.add((int)Math.ceil((double)traitSize / (double)i));
            i*=2;
        }
        i = 1;
        for(int j = 1; j <= noResolutions; j ++)
        {
            noMarkersPerFile.add((int)Math.ceil((double)markerSize / (double)i));
            i*=2;
        }

        for(i = 0; i < noTraitsPerFile.size(); i ++)
        {
            noTraitsPerCell.add((int)Math.ceil((double)noTraitsPerFile.get(i) / 200.0));
        }
        for(i = 0; i < noMarkersPerFile.size(); i ++)
        {
            noMarkersPerCell.add((int)Math.ceil((double)noMarkersPerFile.get(i) / 200.0));
        }
        
        noTraitsPerCell.add(1);
        noMarkersPerCell.add(1);

        int startID = 0;

        startTraits = new ArrayList<ArrayList<Integer>>();
        for(int j =0; j < noTraitsPerFile.size(); j ++)
        {
            startTraits.add(new ArrayList<Integer>());
            int lim = (int) (Math.pow(2, j) * (Math.ceil(noTraitsPerFile.get(j) / noTraitsPerCell.get(j))))+1;
            lim += (noTraitsPerFile.get(j) % noTraitsPerCell.get(j) == 0) ? 0 : 1;
            for(int k = 0; k < lim; k ++)
            {
                int s = startID + k * noTraitsPerCell.get(j);
                startTraits.get(j).add(s);
            }
            startTraits.get(j).set(startTraits.get(j).size()-1, startID + traits.size()+10);
        }

        startMarkers = new ArrayList<ArrayList<Integer>>();
        for(int j = 0; j < noMarkersPerFile.size(); j ++)
        {
            startMarkers.add(new ArrayList<Integer>());
            int lim = (int) (Math.pow(2,j) * (Math.ceil(noMarkersPerFile.get(j) / noMarkersPerCell.get(j)))) + 1;
            lim += (noMarkersPerFile.get(j) % noMarkersPerCell.get(j) == 0) ? 0 : 1;
            for(int k = 0; k < lim; k ++)
            {
                int s = startID + k * noMarkersPerCell.get(j);
                startMarkers.get(j).add(s);
            }
            startMarkers.get(j).set(startMarkers.get(j).size()-1, startID + markers.size() + 10);
        }

        zvals = loadOriginalResolution();

        if(zvals != null){
        this.xaxis = new Object[zvals[0].length];
        for(i = 0; i < zvals[0].length; i ++)
        {
            int a =  0 + i * noTraitsPerCell.get(curResX-1);
            xaxis[i] = a+1;
            if(a > traitSize)
                xaxis[i] = traitSize;
        }

        this.yaxis = new Object[zvals.length];
//        boolean isMax = traitSize > traitSize;
//        if(isMax)
//            traitSize = zvals.length * noTraitsPerCell.get(curResY-1);
        for(i = 0; i < zvals.length; i ++)
        {
            int b = i * noMarkersPerCell.get(curResY-1);
            yaxis[i] = b+1;
        }
        }
//        if(isMax)
//            yaxis[0] = traitSize;
    }

    @Override
    protected void defineMatrix(int x, int y, boolean useDB, int width, int height, int resToUse)
    {
        System.out.println(resToUse);
        //TODO:
        // 1) load in other resolutions
        int resX = this.noTraitsPerCell.get(this.curResX-1);
        int resY = this.noMarkersPerCell.get(this.curResY-1);

        if(useDB)
        {
            //y = this.traitSidze - 1 - y - height;
            String filterx = "traitid IN (";
            for(int i = 0; i < width*resX; i ++)
            {
                if(i+x*resX < traitSize)
                {
                    filterx += traits.get(i+x*resX).getId() + (i == width*resX-1?")":",");
                }
            }
            String filtery = "markerid IN (";
            for(int i = 0; i < height*resY; i ++)
            {
                if(i+y*resY < markerSize)
                {
                    filtery += markers.get(markerSize - 1 - (i+y*resY)).getId() + (i == height*resY-1?")":",");
                }
            }

            ArrayList<String> whereArgs = new ArrayList<String>();
            whereArgs.add(filterx);
            whereArgs.add(filtery);
            whereArgs.add("assocsetid = " + assoc.getId());

            ArrayList<String> cols = new ArrayList<String>();
            cols.add("markerid");
            cols.add("traitid");
            cols.add("value");

            ArrayList<HashMap<String,String>> res =
                    DataManager.runMultiColSelectQuery(cols, "association", true, whereArgs, null);

            whereArgs.clear();
            whereArgs.add("id=" + assoc.getId());

            boolean ispval = DataManager.runSelectQuery("ispval", "assocset", true, whereArgs, null).get(0).equals("1");

            fillInMatrixWithVals(res, resX, resY, x, width, y, height, ispval);
        }
        else
        {
            String fileheader = directory + "/assoc" + resToUse + "_";
            String ext = ".txt";

            if(resToUse == 1)
            {
                double[][] ztemp = this.loadOriginalResolution();
                //zvals = this.loadOriginalResolution();
                y = ztemp.length - y - height;
                for(int i = y; i < y+height; i ++)
                {
                    for(int j = x; j < x+width; j ++)
                    {
                        if(i-y < zvals.length && j-x < zvals[0].length)
                            zvals[i-y][j-x] = ztemp[i][j];
                    }
                }
            }
            else
            {
                int markerNo = (int) Math.ceil((double)markerSize/(double)resY);
                y = markerNo - y - height;
                double[][] ztemp = loadValuesFromFile(x, y, width, height, fileheader, ext, resX, resY, resToUse);
                for(int i=0;i<height;i++)
                {
                    for(int j=0; j<width;j++)
                    {
                        //if(i<zvals.length && j<zvals[0].length)
                        zvals[i][j] = ztemp[i][j];
                    }
                }
            }
        }
    }

    @Override
    protected int findNoResolutions(String dir)
    {
        noResolutions = 0;
        for(int i = 0; i < 6; i ++)
        {
            if(new File(dir + "/assoc" + i + "_1.txt").exists())
            {
                noResolutions ++;
            }
        }
        return noResolutions;
    }

    @Override
    protected double[][] loadOriginalResolution()
    {
        try
        {
            Scanner checker = null;
            try{
                checker = new Scanner(new File(directory + "/complete.txt"), "DEFAULT");
            }
            catch(Exception ex){
                return null;
            }

            if(checker != null){
            ArrayList<Edge> edges = new ArrayList<Edge>();
            Scanner scanner = new Scanner(new File(directory + "/assoc1_1.txt"), "DEFAULT");
            while (scanner.hasNextLine())
            {
                String s = scanner.nextLine();
                String[] vals = s.split(" ");
                if(vals.length == 3)
                {
                    edges.add(new Edge(Integer.parseInt(vals[0])
                            , Integer.parseInt(vals[1]), Double.parseDouble(vals[2])));
                }
            }

            int noNodesX = (int) Math.ceil((double)this.traitSize / (double)this.noTraitsPerCell.get(0));
            int noNodesY = (int) Math.ceil((double)this.markerSize/(double)this.noMarkersPerCell.get(0));
            double[][] toRet = new double[noNodesY][noNodesX];
            for(Edge ne : edges)
            {
                toRet[ne.getT1Idx()][ne.getT2Idx()] = ne.getWeight();
            }
            return toRet;
            }
            return null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

    }

        @Override
    public void callibrate(int xmin, int xmax, int ymin, int ymax, double maxThresh, double minThresh)
    {
       if(zvals != null){
        //determine the size of the current image
        int curSizeY = this.noMarkersPerCell.get(this.curResY-1) * zvals.length;
        int curSizeX = this.noTraitsPerCell.get(this.curResX-1) * zvals[0].length;

        //determine the size of the new image
        int newSizeY = this.noMarkersPerCell.get(this.curResY-1) * (ymax - ymin + 1);
        int newSizeX = this.noTraitsPerCell.get(this.curResX-1) * (xmax - xmin + 1);

        if(newSizeY <= 0)
        {
            newSizeY = curSizeY;
            ymin = 0;
            ymax = zvals.length;
        }
        if(newSizeX <= 0)
        {
            newSizeX = curSizeX;
            xmin = 0;
            xmax = zvals[0].length;
        }

        if(curSizeX > traitSize && newSizeX > curSizeX)
            newSizeX = curSizeX;
        else if(newSizeX > traitSize)
        {
            int maxNoTraitPerCell = this.noTraitsPerCell.get(0);
            newSizeX = maxNoTraitPerCell * (int)
                    (Math.ceil((double)traitSize / (double)maxNoTraitPerCell));
        }
        if(curSizeY > markerSize && newSizeY > curSizeY)
            newSizeY = curSizeY;
        else if(newSizeY > markerSize)
        {
            int maxNoMarkerPerCell = this.noMarkersPerCell.get(0);
            newSizeY = maxNoMarkerPerCell * (int)
                    (Math.ceil((double)markerSize / (double)maxNoMarkerPerCell));
        }

        if(curSizeX == newSizeX && curSizeY == newSizeY && xmin == 0 && ymin == 0 && zvals != null)
            return;

        //the smaller resolution is going to determine what files we do.
        boolean useXRes = newSizeX < newSizeY;
        int totCellsX = newSizeX;
        int totCellsY = newSizeY;
        int pastResX = curResX;
        int pastResY = curResY;

        for(int i = this.noTraitsPerCell.size()-1; i > -1; i --)
        {
            double imageSize = (double) totCellsX / noTraitsPerCell.get(i);
            if(imageSize < 220)
            {
                curResX = i + 1;
                break;
            }
        }
        for(int i = this.noMarkersPerCell.size()-1; i > -1; i --)
        {
            double imageSize = (double) totCellsY / noMarkersPerCell.get(i);
            if(imageSize < 220)
            {
                curResY = i + 1;
                break;
            }
        }

        curResY = Math.min(curResY, curResX);
        curResX = Math.min(curResY, curResX);

        boolean isUseDB = !(new File(directory + "/assoc" + (!useXRes ? curResX : curResY) + "_1.txt").exists());

        //we need to keep track what traits we are on!  This is the next step - keep
        //track of what traits we are displaying.  We could even just return a blank
        //matrix for now.
        int curTraitX = noTraitsPerCell.get(pastResX-1) * this.curCellX;
        int curTraitY = noMarkersPerCell.get(pastResY-1) * this.curCellY;

        int newXMin = curTraitX +  xmin * noTraitsPerCell.get(pastResX-1);
        int newXMax = curTraitX + xmax * noTraitsPerCell.get(pastResX-1);
        int newYMin = curTraitY + ymin * noMarkersPerCell.get(pastResY-1);
        int newYMax = curTraitY + ymax * noMarkersPerCell.get(pastResY-1);

        if(newXMax > traitSize)
            newXMax = traitSize;
        if(newYMax > markerSize)
            newYMax = markerSize;

        int startCellX = (int) Math.floor((double)newXMin / (double) noTraitsPerCell.get(curResX-1));
        int endCellX = (int) Math.ceil((double)newXMax / (double) noTraitsPerCell.get(curResX-1));
        int startCellY = (int) Math.floor((double)newYMin / (double) noMarkersPerCell.get(curResY-1));
        int endCellY = (int) Math.ceil((double)newYMax / (double) noMarkersPerCell.get(curResY-1));
        curCellX = startCellX;
        curCellY = startCellY;

        newXMin = startCellX * noTraitsPerCell.get(curResX-1);
        newXMax = endCellX * noTraitsPerCell.get(curResX-1);
        newYMin = startCellY * noMarkersPerCell.get(curResY-1);
        newYMax = endCellY * noMarkersPerCell.get(curResY-1);

        if(newXMax > traitSize)
            newXMax = traitSize;
        if(newYMax > markerSize)
            newYMax = markerSize;

        System.out.println("Displaying: " + newXMin + "-" + newXMax + "," + newYMin + "-" + newYMax);
        System.out.println("Displaying: " + startCellX + "-" + endCellX + "," + startCellY + "-" + endCellY);

        int noCellsX = endCellX - startCellX ;
        int noCellsY = endCellY - startCellY;

        zvals = new double[noCellsY][noCellsX];

        defineMatrix(curCellX, curCellY, isUseDB, noCellsX, noCellsY, (!useXRes?curResX:curResY));

        this.xaxis = new Object[zvals[0].length];
        for(int i = 0; i < zvals[0].length; i ++)
        {
            int a =  newXMin + i * noTraitsPerCell.get(curResX-1);
            xaxis[i] = a+1;
            if(a >= newXMax)
                xaxis[i] = newXMax;
        }

        this.yaxis = new Object[zvals.length];
        boolean isMax = newYMax > markerSize;
        if(isMax)
            newYMax = zvals.length * noMarkersPerCell.get(curResY-1);
        for(int i = 0; i < zvals.length; i ++)
        {
            int b = (markerSize-newYMax) + i * noMarkersPerCell.get(curResY-1);
            yaxis[i] = b+1;
        }
        if(isMax)
            yaxis[yaxis.length-1] = markerSize;
        }
    }

    @Override
    public void getSubSet(ArrayList<Integer> subset)
    {
        int x = this.curCellX*this.noTraitsPerCell.get(this.curResX-1);
        int width = zvals[0].length * this.noTraitsPerCell.get(this.curResX-1);
        for(int i = x; i < width + x; i ++)
        {
            subset.add(traits.get(i).getId());
        }
    }

    private void fillInMatrixWithVals(ArrayList<HashMap<String, String>> res, int resX, int resY, int x, int width, int y, int height, boolean ispval)
    {
        int ytemp = y;
        y = markerSize - y - 1 - height;

        int xorig = x;
        int yorig = y;
        x *= resX;
        y *= resY;

        for (HashMap hm : res)
        {
            int t = (int) Math.round((double)mapIdtoIdx.get(Integer.parseInt((String)hm.get("traitid"))));
            int m = Integer.parseInt((String)hm.get("markerid")) - this.markers.get(0).getId();
            Double w = Math.abs(Double.parseDouble((String) hm.get("value")));

            if(ispval)
            {
                w = -Math.log10(w);
            }

            int idxy = Math.round((float)m/(float)resY) - yorig - 1;//zvals.length - 1 - Math.round((float)m/(float)resY) + yorig;
            int idxx = Math.round((float)t/(float)resX)-xorig;

            String hi;
            if(idxy >= zvals.length)
                continue;

            if(idxx >= zvals[0].length)
                continue;

            if(zvals[idxy][idxx] < w)
                    zvals[idxy][idxx] = w;
        }
    }

    @Override
    protected int checkYBounds(int ymin)
    {
        if (ymin * this.noMarkersPerCell.get(curResY - 1) + this.noMarkersPerCell.get(curResY - 1) * curCellY < 0)
        {
            ymin = -1 * curCellY;
        }
        return ymin;
    }

    @Override
    public void pan(int xMoved, int yMoved, double minThresh, double maxThresh)
    {
        if(xMoved == 0 && yMoved == 0)
            return;
        int noCellsShownX = zvals[0].length;//-1;
        int noCellsShownY = zvals.length;//-1;
        int xmin = 0 - xMoved;
        int xmax = noCellsShownX - xMoved;
        int ymin = 0 + yMoved;
        int ymax = noCellsShownY + yMoved;

        System.out.println(xMoved + " "+yMoved);

        if(xmin*this.noTraitsPerCell.get(curResX-1) + this.noTraitsPerCell.get(curResX-1)*curCellX < 0)
        {
            xmin = 0 - curCellX;
            xmax = noCellsShownX - curCellX;
        }
        if(ymin*this.noMarkersPerCell.get(curResY-1) + this.noMarkersPerCell.get(curResY-1)*curCellY < 0)
        {
            ymin = 0 - curCellY;//we are at an edge!
            ymax = noCellsShownY - curCellY;
        }

        int resY = this.noMarkersPerCell.get(curResY-1);
        int resX = this.noTraitsPerCell.get(curResX-1);
        int ymaxloc = curCellY + ymax;
        int xmaxloc = curCellX + xmax;
        int ycurloc = curCellY + noCellsShownY;
        int xcurloc = curCellX + noCellsShownX;
        int ytoploc = (int)Math.ceil((double)markerSize / (double)resY);
        int xtoploc = (int)Math.ceil((double)traitSize / (double)resX);

        if(ycurloc > markerSize && ymaxloc > ycurloc)
        {
            ymin = 0;
            ymax = noCellsShownY;
        }
        else if(ymaxloc > ycurloc && ymaxloc > ytoploc)
        {
            int diff = ytoploc - ymaxloc;
            ymax += diff;
            ymin += diff;
        }
        if(xcurloc > traitSize && xmaxloc > xcurloc)
        {
            xmin = 0;
            xmax = noCellsShownX;
        }
        else if(xmaxloc > xcurloc && xmaxloc > xtoploc)
        {
            int diff = xtoploc - xmaxloc;
            xmax += diff;
            xmin += diff;
        }

        this.callibrate(xmin, xmax, ymin, ymax, maxThresh, minThresh);
    }

    private double[][] loadValuesFromFile(int x, int y, int width, int height, String fileheader, String ext, int resX, int resY, int curRes)
    {
        int numColsPerRow = 1;
        int temp = curRes;
        while(temp-->1)
        {
            numColsPerRow *= 2;
        }

        double[][] toRet = new double[height][width];

        int posX = x * resX;
        int posY = y * resY;

        int notraitspf = 200*resX;//noTraitsPerFile.get(curRes-1);
        int filenoX = 1;

        while(notraitspf * filenoX < posX)
        {
            filenoX ++;
        }

        int nomarkerspf = 200*resY;// noMarkersPerFile.get(curRes-1);
        int filenoY = 1;

        while(nomarkerspf * filenoY < posY)
        {
            filenoY ++;
        }

        int endfilenoX = 1;
        int endfilenoY = 1;

        while(notraitspf * endfilenoX < posX + width * resX)
        {
            endfilenoX ++;
        }
        while(nomarkerspf * endfilenoY < posY + height * resY)
        {
            endfilenoY ++;
        }

        System.out.println(filenoX + "to" + endfilenoX);

        //we now know which files we are going to need to read in order to fill in our matrix.
        for(int i = filenoY; i <= endfilenoY; i ++)
        {
            for(int j = filenoX; j <= endfilenoX; j ++)
            {
                int idx = j;
                idx += (i-1)*numColsPerRow;

                String filename = fileheader + idx + ext;

                int startfiY = y;
                int startfiX = x;
                int startMatY = 0;
                int startMatX = 0;
                //where do we start in this file??
                if(i <= filenoY)
                {
                    startfiY = y;
                }
                else
                {
                    startMatY = 200 - (y % 200);
                }
                if(j <= filenoX)
                {
                    startfiX = x;
                }
                else
                {
                    startMatX = 200 - (x % 200);
                }
                
                try
                {
                    ArrayList<Edge> edges = new ArrayList<Edge>();
                    Scanner scanner = new Scanner(new File(filename));
                    while(scanner.hasNextLine())
                    {
                        String s= scanner.nextLine();
                        String[] vals = s.split(" ");
                        if(vals.length == 3)
                        {
                            edges.add(new Edge(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2])));
                        }
                    }
                    for(Edge ne : edges)
                    {
                        int yy = translateFileIndex(ne.getT1Idx(), startfiY, startMatY);
                        int xx = translateFileIndex(ne.getT2Idx(), startfiX, startMatX);

                        if(xx >= 0 && xx < toRet[0].length && yy >= 0 && yy < toRet.length)
                            toRet[yy][xx] = ne.getWeight();
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                    //return null;
                }
                

            }
        }

        return toRet;
    }

    private int translateFileIndex(int idx, int startfi, int startMat)
    {
        return idx - startfi;
    }
}
