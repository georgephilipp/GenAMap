/*     */ package BiNGO.GOlorize;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ 
/*     */ class ColorRenderer extends JLabel
/*     */   implements TableCellRenderer
/*     */ {
/*     */   HashMap goColor;
/*     */   boolean isBordered;
/* 175 */   Color color = Color.getColor("white");
/*     */   ResultAndStartPanel result;
/*     */ 
/*     */   public ColorRenderer(boolean isBordered, HashMap goColor)
/*     */   {
/* 178 */     this.isBordered = isBordered;
/* 179 */     this.goColor = goColor;
/* 180 */     setOpaque(true);
/*     */   }
/*     */   public ColorRenderer(boolean isBordered, HashMap goColor, ResultAndStartPanel result) {
/* 183 */     this.isBordered = isBordered;
/* 184 */     this.goColor = goColor;
/* 185 */     this.result = result;
/* 186 */     setOpaque(true);
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object label, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/* 193 */     Color newColor = this.color;
/* 194 */     String text = ((JLabel)table.getValueAt(row, this.result.getDescriptionColumn())).getText();
/* 195 */     Color color = (Color)this.goColor.get(table.getValueAt(row, this.result.getGoTermColumn()));
/* 196 */     setBackground(color);
/*     */ 
/* 200 */     setText(text);
/*     */ 
/* 205 */     return this;
/*     */   }
/*     */ }

/* Location:           C:\Users\ARCurtis\.cytoscape\2.8\plugins\BiNGO-2.44\BiNGO.jar
 * Qualified Name:     BiNGO.GOlorize.ColorRenderer
 * JD-Core Version:    0.6.0
 */