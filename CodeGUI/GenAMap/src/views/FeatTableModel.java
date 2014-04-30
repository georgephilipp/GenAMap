package views;

import BiNGO.GoItems;
import datamodel.Marker;
import datamodel.Model;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import realdata.DataManager;

/**
 * In a GO table, we will have all of the information about the significant
 * GO categories for the subset, the traittreeval, or the trait, whatever. These
 * are displayed through a table format! This model is what will actually
 * show off the values ... through the information table. 
 * @author rcurtis
 */
public class FeatTableModel extends AbstractTableModel {

    ArrayList<Marker> items;
    ArrayList<String> features;
    Map<String, Color> mapping;
    ArrayList<Double>[] featureVal;
    /**
     * The column names of the table.
     */
    private String[] columnNames;

    /**
     * The data model that we are using is going to the teh array list of
     * go items.
     * @param items
     */
    public FeatTableModel(ArrayList<Marker> items) {
        featureVal = new ArrayList[items.size()];
        this.items = items;
        int markersetid = this.items.get(0).getMarkerSetId();
        mapping = new HashMap<String, Color>();
        //get the features
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("markersetid=" + markersetid);
        ArrayList<String> features = DataManager.runSelectQuery("name", "feature", true, whereArgs, "id");
        columnNames = new String[features.size()+1];//+1
      columnNames[0]="SNP";
       mapping.put(columnNames[0], Model.colors[0]);
        int ii = 1;
        int numColors = Model.colors.length;
        for (String f : features) {
            columnNames[ii] = features.get(ii-1);
            mapping.put(columnNames[ii], Model.colors[ii % numColors]);
            ii++;
        }

        int mi=0;
        for(Marker m : items)
        {
            featureVal[mi] = m.getFeature();
            mi++;
        }

    }

    /**
     * Returns the number of items
     * @return
     */
    public int getRowCount() {
        
            return items.size();
        
    }

    /**
     * We will always have our nine columns or two columns
     * @return
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int idx) {
        
            return this.columnNames[idx];
        
    }

    /**
     * returns what will be displayed in teh table at the specified position. 
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public Object getValueAt(int rowIndex, int columnIndex) {


        if(columnIndex==0)
            return items.get(rowIndex).getName();
        else
            return featureVal[rowIndex].get(columnIndex-1);
            //return items.get(rowIndex).getFeature().get(columnIndex-1);
        
    }
}
