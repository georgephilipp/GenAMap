package datamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import realdata.DataManager;

/**
 * The LargeNetworkNavigator will serve as a navigator to go through a large
 * network.  It extends the LargeImageNavigator, which acts as a template to this
 * and the LargeAssocNavigator. It handles zoom, pan, resolution, etc.
 * @author RCurtis
 */
public class LargeNetworkNavigator extends LargeImageNavigator
{
    /**
     * A pointer to the network that this largeNetworkNavigator is viewing.
     */
    protected Network net;

    /**
     * Constructor for the LargeNetworkNavigator.
     * @param traitNodes a list of all the traits that are displayed in this network view - used for clustering information
     * @param dir the directory that stores the resolution data for this large network
     * @param net the pointer to the network object for this network navigator.
     */
    public LargeNetworkNavigator(ArrayList<Trait> traitNodes, String dir, Network net)
    {
        super();
        this.net = net;
        directory = dir;
        traits = traitNodes;
        traitSize = traitNodes.size();
        noTraitsPerCell = new ArrayList<Integer>();
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

        ArrayList<Integer> noTraitsPerFile = new ArrayList<Integer>();
        int i = 1;
        for(int j = 1; j <= noResolutions; j ++)
        {
            noTraitsPerFile.add((int)Math.ceil((double)traitSize / (double)i));
            i*=2;
        }

        for(i = 0; i < noResolutions; i ++)
        {
            noTraitsPerCell.add((int)Math.ceil((double)noTraitsPerFile.get(i) / 200.0));
        }
        noTraitsPerCell.add(1);

        int startID = 0;//traits.get(0).getId();

        starts = new ArrayList<ArrayList<Integer>>();
        for(int j =0; j < noTraitsPerFile.size(); j ++)
        {
            starts.add(new ArrayList<Integer>());
            int lim = (int) (Math.pow(2, j) * (Math.ceil(noTraitsPerFile.get(j) / noTraitsPerCell.get(j))))+1;
            lim += (noTraitsPerFile.get(j) % noTraitsPerCell.get(j) == 0) ? 0 : 1;
            //lim++;
            for(int k = 0; k < lim; k ++)
            {
                int s = startID + k * noTraitsPerCell.get(j);
                starts.get(j).add(s);
            }
            starts.get(j).set(starts.get(j).size()-1, startID + traits.size()+10);
        }
        zvals = loadOriginalResolution();

        this.xaxis = new Object[zvals[0].length];
        for(i = 0; i < zvals[0].length; i ++)
        {
            int a =  0 + i * noTraitsPerCell.get(curResX-1);
            xaxis[i] = a;
            if(a > traitSize)
                xaxis[i] = traitSize;
        }

        this.yaxis = new Object[zvals.length];
        for(i = 0; i < zvals.length; i ++)
        {
            int b = traitSize - i * noTraitsPerCell.get(curResY-1);
            yaxis[i] = b;
        }
    }

    @Override
    protected void defineMatrix(int x, int y, boolean useDB, int width, int height, int resToUse)
    {
        int resX = this.noTraitsPerCell.get(this.curResX-1);
        int resY = this.noTraitsPerCell.get(this.curResY-1);

        if(useDB)
        {
            //y = this.traitSize - 1 - y - height;
            String filterx = "trait1 IN (";
            String filterx_a = "trait2 IN(";
            for(int i = 0; i < width*resX; i ++)
            {
                if(i+x*resX < traitSize)
                {
                    filterx += traits.get(i+x*resX).getId() + (i == width*resX-1?")":",");
                    filterx_a+=traits.get(i+x*resX).getId() + (i == width*resX-1?")":",");
                }
            }
            String filtery = "trait2 IN (";
            String filtery_a = "trait1 IN (";
            for(int i = 0; i < height*resY; i ++)
            {
                if(i+y*resY < traitSize)
                {
                    filtery += traits.get(i+y*resY).getId() + (i == height*resY-1?")":",");
                    filtery_a+=traits.get(i+y*resY).getId() + (i == height*resY-1?")":",");
                }
            }

            ArrayList<String> whereArgs = new ArrayList<String>();
            whereArgs.add(filterx);
            whereArgs.add(filtery);
            whereArgs.add("netid = " + net.getId());

            ArrayList<String> cols = new ArrayList<String>();
            cols.add("trait1");
            cols.add("trait2");
            cols.add("weight");

            ArrayList<HashMap<String,String>> res =
                    DataManager.runMultiColSelectQuery(cols, "networkval", true, whereArgs, null);
            fillInMatrixWithVals(res, resX, resY, x, width, y, height);

            whereArgs.clear();
            whereArgs.add(filterx_a);
            whereArgs.add(filtery_a);
            whereArgs.add("netid = " + net.getId());

            res =
                    DataManager.runMultiColSelectQuery(cols, "networkval", true, whereArgs, null);

            fillInMatrixWithVals(res, resX, resY, x, width, y, height);
        }
        else
        {
            String fileheader = directory + "/net" + resToUse + "_";
            String ext = ".txt";

            if(resToUse == 1)
            {
                double[][] ztemp = this.loadOriginalResolution();
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
                int traitNo = (int) Math.ceil((double)traitSize/(double)resY);
                y = traitNo - y - height;
                double[][] ztemp = loadValuesFromFile(x, y, width, height, fileheader, ext, resX, resY, resToUse);
                for(int i=0;i<height;i++)
                {
                    for(int j=0; j<width;j++)
                    {
                        zvals[height-i-1][j] = ztemp[i][j];
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
            if(new File(dir + "/net" + i + "_1.txt").exists())
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
            Scanner scanner = new Scanner(new File(directory + "/net1_1.txt"), "DEFAULT");
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

            int noNodes = (int) Math.ceil((double)this.traitSize / (double)this.noTraitsPerCell.get(0));
            double[][] toRet = new double[noNodes][noNodes];
            for(Edge ne : edges)
            {
                toRet[noNodes - 1 - ne.getT1Idx()][ne.getT2Idx()] = ne.getWeight();
                toRet[noNodes - 1 - ne.getT2Idx()][ne.getT1Idx()] = ne.getWeight();
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
        //System.out.println(xmin + " " + xmax + ", " + ymin + " " + ymax);

        //determine the size of the current image
        int curSizeY = this.noTraitsPerCell.get(this.curResY-1) * zvals.length;
        int curSizeX = this.noTraitsPerCell.get(this.curResX-1) * zvals[0].length;

        //determine the size of the new image
        int newSizeY = this.noTraitsPerCell.get(this.curResY-1) * (ymax - ymin + 1);
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
        if(curSizeY > traitSize && newSizeY > curSizeY)
            newSizeY = curSizeY;
                else if(newSizeX > traitSize)
        {
            int maxNoTraitPerCell = this.noTraitsPerCell.get(0);
            newSizeY = maxNoTraitPerCell * (int)
                    (Math.ceil((double)traitSize / (double)maxNoTraitPerCell));
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
        for(int i = this.noTraitsPerCell.size()-1; i > -1; i --)
        {
            double imageSize = (double) totCellsY / noTraitsPerCell.get(i);
            if(imageSize < 220)
            {
                curResY = i + 1;
                break;
            }
        }

        curResY = Math.min(curResY, curResX);
        curResX = Math.min(curResY, curResX);

        boolean isUseDB = !(new File(directory + "/net" + (!useXRes ? curResX : curResY) + "_1.txt").exists());

        //we need to keep track what traits we are on!  This is the next step - keep
        //track of what traits we are displaying.  We could even just return a blank
        //matrix for now.
        int noCellsDisplayedX = zvals[0].length;
        int noCellsDisplayedY = zvals.length;
        int curTraitX = noTraitsPerCell.get(pastResX-1) * this.curCellX;
        int curTraitY = noTraitsPerCell.get(pastResY-1) * this.curCellY;

        /*if(xmax+1 == zvals[0].length)
            xmax++;
        if(ymax+1 == zvals.length)
            ymax++;*/

        int newXMin = curTraitX +  xmin * noTraitsPerCell.get(pastResX-1);
        int newXMax = curTraitX + xmax * noTraitsPerCell.get(pastResX-1);
        int newYMin = curTraitY + ymin * noTraitsPerCell.get(pastResY-1);
        int newYMax = curTraitY + ymax * noTraitsPerCell.get(pastResY-1);

        if(newXMax > traitSize)
            newXMax = traitSize;
        if(newYMax > traitSize)
            newYMax = traitSize;

        int startCellX = (int) Math.floor((double)newXMin / (double) noTraitsPerCell.get(curResX-1));
        int endCellX = (int) Math.ceil((double)newXMax / (double) noTraitsPerCell.get(curResX-1));
        int startCellY = (int) Math.floor((double)newYMin / (double) noTraitsPerCell.get(curResY-1));
        int endCellY = (int) Math.ceil((double)newYMax / (double) noTraitsPerCell.get(curResY-1));
        curCellX = startCellX;
        curCellY = startCellY;

        newXMin = startCellX * noTraitsPerCell.get(curResX-1);
        newXMax = endCellX * noTraitsPerCell.get(curResX-1);
        newYMin = startCellY * noTraitsPerCell.get(curResY-1);
        newYMax = endCellY * noTraitsPerCell.get(curResY-1);

        if(newXMax > traitSize)
            newXMax = traitSize;
        if(newYMax > traitSize)
            newYMax = traitSize;

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
        boolean isMax = newYMax > traitSize;
        if(isMax)
            newYMax = zvals.length * noTraitsPerCell.get(curResY-1);
        for(int i = 0; i < zvals.length; i ++)
        {
            int b = newYMax - i * noTraitsPerCell.get(curResY-1);
            yaxis[i] = b;
        }
        if(isMax)
            yaxis[0] = traitSize;
    }

    @Override
    public void getSubSet(ArrayList<Integer> subset)
    {
        int x = this.curCellX*this.noTraitsPerCell.get(this.curResX-1);
        int y = this.curCellY*this.noTraitsPerCell.get(this.curResY-1);
        int width = zvals[0].length * this.noTraitsPerCell.get(this.curResX-1);
        int height = zvals.length * this.noTraitsPerCell.get(this.curResY-1);
        for(int i = x; i < width + x; i ++)
        {
            subset.add(traits.get(i).getId());
        }
        for(int j = y; j < height + y; j ++)
        {
            if(!subset.contains(traits.get(j).getId()))
                subset.add(traits.get(j).getId());
        }
    }

    /**
     * The results from a SQL query are passed into this method and the zvalue matrix
     * is filled with the values from the results.
     * @param res the results from the SQL query
     * @param resX the current x resolution
     * @param resY the current y resolution
     * @param x the current x location in the matrix
     * @param width the width
     * @param y the current y location in the matrix
     * @param height the height
     * @throws NumberFormatException
     */
    protected void fillInMatrixWithVals(ArrayList<HashMap<String, String>> res, int resX, int resY, int x, int width, int y, int height) throws NumberFormatException
    {
        int xorig = x;
        int yorig = y;
        x *= resX;
        y *= resY;
        for (HashMap hm : res)
        {
            int t1 = (int) Math.round((double)mapIdtoIdx.get(Integer.parseInt((String)hm.get("trait1"))));
            int t2 = (int) Math.round((double)mapIdtoIdx.get(Integer.parseInt((String)hm.get("trait2"))));
            Double w = Math.abs(Double.parseDouble((String) hm.get("weight")));
            boolean ist1x = t1 >= x && t1 < width*resX + x;
            boolean ist2x = t2 >= x && t2 < width*resX + x;
            boolean ist1y = t1 >= y && t1 < height*resY + y;
            boolean ist2y = t2 >= y && t2 < height*resY + y;

            if (ist1x && ist2y)
            {
                int idxy = zvals.length - 1 - Math.round((float)t2/(float)resY) + yorig;
                int idxx = Math.round((float)t1/(float)resX)-xorig;
                if(zvals[idxy][idxx] < w)
                    zvals[idxy][idxx] = w;
            }
            if (ist2x && ist1y)
            {
                int idxy = zvals.length - 1 - Math.round((float)t1/(float)resY) + yorig;
                int idxx = Math.round((float)t2/(float)resX)-xorig;
                if(zvals[idxy][idxx] < w)
                    zvals[idxy][idxx] = w;
            }
        }
    }

    @Override
    protected int checkYBounds(int ymin)
    {
        if (ymin * this.noTraitsPerCell.get(curResY - 1) + this.noTraitsPerCell.get(curResY - 1) * curCellY < 0)
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
        if(ymin*this.noTraitsPerCell.get(curResY-1) + this.noTraitsPerCell.get(curResY-1)*curCellY < 0)
        {
            ymin = 0 - curCellY;//we are at an edge!
            ymax = noCellsShownY - curCellY;
        }

        int resY = this.noTraitsPerCell.get(curResY-1);
        int resX = this.noTraitsPerCell.get(curResX-1);
        int ymaxloc = curCellY + ymax;
        int xmaxloc = curCellX + xmax;
        int ycurloc = curCellY + noCellsShownY;
        int xcurloc = curCellX + noCellsShownX;
        int ytoploc = (int)Math.ceil((double)traitSize / (double)resY);
        int xtoploc = (int)Math.ceil((double)traitSize / (double)resX);

        if(ycurloc > traitSize && ymaxloc > ycurloc)
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

    /**
     * If it is determined that an intermediate resolution file is needed, this method
     * is called to load the values into the zvalue matrix. This can also fill the matrix
     * across different files, if necessary. The most files that would ever need to
     * be explored is 4.
     * @param x current x position
     * @param y current y position
     * @param width width to display
     * @param height height to display
     * @param fileheader the resolution file header of the files to load in.
     * @param ext .txt
     * @param resX the current x resolution
     * @param resY the current y resolution
     * @param curRes the current overall resolution
     * @return a matrix that has the values to be included in the zvalue matrix. 
     */
    private double[][] loadValuesFromFile(int x, int y, int width, int height, String fileheader, String ext, int resX, int resY, int curRes)
    {
        System.out.println(curRes + "th rez");

        int traitNo = (int) Math.ceil((double)traitSize/(double)resY);
        y = traitNo - y - height;

        int numColsPerRow = 1;
        int temp = curRes;
        while(temp-->1)
        {
            numColsPerRow *= 2;
        }

        double[][] toRet = new double[height][width];

        int posX = x * resX;
        int posY = y * resY;

        int notraitspf = 200*resX;
        int filenoX = 1;

        while(notraitspf * filenoX < posX)
        {
            filenoX ++;
        }

        int nomarkerspf = 200*resY;
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

        //we now know which files we are going to need to read in order to fill in our matrix.
        for(int i = filenoY; i <= endfilenoY; i ++)
        {
            for(int j = filenoX; j <= endfilenoX; j ++)
            {
                int idx = Math.max(i,j);
                idx += (Math.min(j, i)-1)*numColsPerRow;

                if(Math.min(j,i)>1)
                {
                    temp = Math.min(j,i);
                    while(temp-->1)
                    {
                        idx -= temp;
                    }
                }

                String filename = fileheader + idx + ext;

                int startfiY = y;
                int startfiX = x;
                int startMatY = 0;
                int startMatX = 0;
                //where do we start in this file??
                if(i == filenoY)
                {
                    startfiY = y;
                }
                else
                {
                }
                if(j == filenoX)
                {
                    startfiX = x;
                }
                else
                {
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
                        int yy = translateFileIndex(ne.getT1Idx(), startfiY);
                        int xx = translateFileIndex(ne.getT2Idx(), startfiX);

                        int qq = translateFileIndex(ne.getT1Idx(), startfiX);
                        int pp = translateFileIndex(ne.getT2Idx(), startfiY);

                        {
                            if(xx >= 0 && xx < toRet[0].length && yy >= 0 && yy < toRet.length)
                                toRet[yy][xx] = ne.getWeight();

                            if(qq >= 0 && qq < toRet[0].length && pp >= 0 && pp < toRet.length)
                                toRet[pp][qq] = ne.getWeight();
                        }
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }

        return toRet;
    }

    /**
     * Translates the trait index into coordinates to use in the zvalue matrix. The index
     * comes from the trait file that was read in - if the zvalue matrix overlaps a few
     * displays, then this will need to be translated from the file's label of the edge
     * to the zvalue matrix's label. 
     * @param idx the index of the trait
     * @param startfi the index where the file started in the overall picture.
     * @return
     */
    private int translateFileIndex(int idx, int startfi)
    {
        return idx - startfi;
    }
}
