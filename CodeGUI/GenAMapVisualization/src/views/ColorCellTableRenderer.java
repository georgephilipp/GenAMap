package views;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * We want to display color for each of the rows of the GO table. We do
 * this through this class. 
 * @author rcurtis
 */
public class ColorCellTableRenderer extends JLabel implements TableCellRenderer
{
    /**
     * Constructor
     */
    public ColorCellTableRenderer()
    {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        setBackground((Color)value);
        setBorder(BorderFactory.createEmptyBorder());
        return this;
    }
}
