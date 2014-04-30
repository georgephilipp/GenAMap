package views.snp;

import javax.swing.table.AbstractTableModel;

/**
 * In the case of case/control data, we can display a frequency table that
 * shows the counts of the cases and controls according to SNP. This
 * class is used as the model for the java table display. 
 * @author rcurtis
 */
public class FrequencyTableModel extends AbstractTableModel
{
    /**
     * The values in the table model
     */
    private double[][] values;
    
    /**
     * The column names of the table.
     */
    private String[] columnNames =
    {
        "# minor alleles", "controls (1)", "cases (2)", ""
    };

    /**
     * The data model that we are using is going to the the table of values
     * @param items
     */
    public FrequencyTableModel(double[][] table)
    {
        this.values = table;
    }

    /**
     * Returns the number of rows
     * @return
     */
    public int getRowCount()
    {
        return values.length + 1;
    }

    /**
     * Returns number of columns
     * @return
     */
    public int getColumnCount()
    {
        return values[0].length + 2;
    }

    @Override
    public String getColumnName(int idx)
    {
        return columnNames[idx];
    }

    /**
     * returns what will be displayed in teh table at the specified position. 
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if(columnIndex == 0)
        {
            if(rowIndex < 3)
                return rowIndex;
            else
                return "";
        }
        else
        {
            if(rowIndex == this.getRowCount() -1 &&
                    columnIndex == this.getColumnCount() -1)
            {
                double val = 0.0;
                for(int i = 0; i < values.length; i ++)
                {
                    for(int j = 0; j < values[i].length; j++)
                    {
                        val += values[i][j];
                    }
                }
                return val;
            }
            if(rowIndex == this.getRowCount()-1)
            {
                if(columnIndex == 0)
                    return "";
                double val = 0.0;
                for(int i = 0; i < this.getRowCount()-1; i ++)
                {
                    val += values[i][columnIndex-1];
                }
                return val;
            }
            if(columnIndex == this.getColumnCount() -1)
            {
                double val = 0.0;
                for(int i = 1; i < this.getColumnCount()-1; i ++)
                {
                    val += values[rowIndex][i-1];
                }
                return val;
            }
            return values[rowIndex][columnIndex - 1];
        }
    }
}
