package views;

import BiNGO.GoItems;
import datamodel.Model;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * In a GO table, we will have all of the information about the significant
 * GO categories for the subset, the traittreeval, or the trait, whatever. These
 * are displayed through a table format! This model is what will actually
 * show off the values ... through the information table. 
 * @author rcurtis
 */
public class GoTableModel extends AbstractTableModel
{
    ArrayList<GoItems> items;
    ArrayList<String> gocats;
    Map<String, Color> mapping;
    /**
     * The column names of the table.
     */
    private String[] columnNames =
    {
        "", "GO ID", "Description", "p-value", "corrected",
        "x", "n", "X", "N"
    };
    private String[] columnNames2 =
    {
        "", "Description"
    };
    private String[] columnNames3 =
    {
        "", "GO ID / SNP name", "Description / Chr(loc)", "p-value", "corrected",
        "x", "n", "X", "N"
    };
    private int colIdx;

    /**
     * The data model that we are using is going to the teh array list of
     * go items.
     * @param items
     */
    public GoTableModel(ArrayList<GoItems> items, ArrayList<String> gocats, Map<String, Color> gocolormap,
            int colIdx)
    {
        this.items = items;
        this.gocats = gocats;
        this.mapping = gocolormap;
        this.colIdx = colIdx;
    }

    /**
     * Returns the number of items
     * @return
     */
    public int getRowCount()
    {
        if (gocats == null)
        {
            return items.size();
        }
        else
        {
            return gocats.size();

        }
    }

    /**
     * We will always have our nine columns or two columns
     * @return
     */
    public int getColumnCount()
    {
        if ((gocats == null || gocats.size() == 0))
        {
            return 9;
        }
        return 2;
    }

    @Override
    public String getColumnName(int idx)
    {
        if (gocats == null && colIdx < 3)
        {
            return this.columnNames[idx];
        }
        if (colIdx == 2)
        {
            return this.columnNames2[idx];
        }
        else
        {
            return this.columnNames3[idx];
        }
    }

    /**
     * returns what will be displayed in teh table at the specified position. 
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (gocats == null)
        {
            switch (columnIndex)
            {
                case 1:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).GO_ID;
                case 2:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).descr;
                case 3:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).pval;
                case 4:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    double corrpval = items.get(rowIndex).correctedpval;
                    if (corrpval == 1.0)
                    {
                        try
                        {
                            Integer.parseInt(items.get(rowIndex).GO_ID);
                        }
                        catch (Exception ep)
                        {
                            return "n/a";
                        }
                    }
                    return items.get(rowIndex).correctedpval;
                case 5:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).x;
                case 6:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).n;
                case 7:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).X;
                case 8:
                    if (items.get(rowIndex).GO_ID.equals("-1"))
                    {
                        return "";
                    }
                    return items.get(rowIndex).N;
            }
            if (mapping == null)
            {
                if (items.get(rowIndex).GO_ID.equals("-1"))
                {
                    return Color.WHITE;
                }
                else if (colIdx == 3)
                {
                    try
                    {
                        Integer.parseInt(items.get(rowIndex).GO_ID);
                        return Model.colors[0];
                    }
                    catch (Exception ep)
                    {
                        return Model.colors[2];
                    }
                }
                else if (rowIndex > 22)
                {
                    return Color.WHITE;
                }

                return Model.colors[rowIndex];
            }
            Color c = mapping.get(items.get(rowIndex).descr);
            if (c != null)
            {
                return c;
            }
        }
        else
        {
            switch (columnIndex)
            {
                case 1:
                    return gocats.get(rowIndex);
            }
            if (mapping == null)
            {
                if (rowIndex > 22)
                {
                    return Color.WHITE;
                }
                return Model.colors[rowIndex];
            }
            Color c = mapping.get(gocats.get(rowIndex));
            if (c != null)
            {
                return c;
            }
        }

        if ((items != null && items.size() > 0) || (gocats != null && gocats.size() > 0))
        {
            return Color.LIGHT_GRAY;
        }
        return Color.BLACK;
    }
}
