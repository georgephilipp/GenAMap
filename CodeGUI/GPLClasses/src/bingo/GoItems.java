package BiNGO;

/**
 * To store what we need to know about a GO enrichment ...
 * @author rcurtis
 */
public class GoItems
{
    /**
     * The GO Id of the enriched category
     */
    public String GO_ID;
    /**
     * The description of the enriched category
     */
    public String descr;
    /**
     * The p-value of the enrichment
     */
    public double pval;
    /**
     * The corrected pvalue of the enrichment
     */
    public double correctedpval;
    /**
     * little x - number of genes in the overlap
     */
    public int x;
    /**
     * little n - number of genes in the subset
     */
    public int n;
    /**
     * Big x - number of genes in the category
     */
    public int X;
    /**
     * Big n - number of total genes. 
     */
    public int N;

    public String getStringRepresentation()
    {
        descr = descr.replace("-", "");
        descr = descr.replace('\'', 'p');
        descr = descr.replace(",", "");
        return GO_ID + "*" + descr + "*" + pval + "*" + correctedpval + "*" + x + "*" + n + "*" + X + "*" + N;
    }

    public static GoItems parse(String s)
    {
        GoItems ret = new GoItems();
        try
        {
            String[] codes = s.split("\\*");
            if (codes.length == 8)
            {
                ret.GO_ID = codes[0];
                ret.descr = codes[1];
                ret.pval = Double.parseDouble(codes[2]);
                ret.correctedpval = Double.parseDouble(codes[3]);
                ret.x = Integer.parseInt(codes[4]);
                ret.n = Integer.parseInt(codes[5]);
                ret.X = Integer.parseInt(codes[6]);
                ret.N = Integer.parseInt(codes[7]);
            }
            else
            {
                ret.GO_ID = codes[5];
                ret.descr = codes[6];
                ret.pval = Double.parseDouble(codes[4]);
                ret.correctedpval = 1.0;
                ret.x = Integer.parseInt(codes[0]);
                ret.n = Integer.parseInt(codes[1]);
                ret.X = Integer.parseInt(codes[2]);
                ret.N = Integer.parseInt(codes[3]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }
}
