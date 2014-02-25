package views.heatmap;

import heatchart.HeatChart;
import java.awt.Color;

/**
 * We want to have a scale next to heat charts and color-coded parts of GenAMap
 * so that we can determine the high and low values of association. This class
 * simply returns a chart that can be added next to different parts of the code.
 * @author Anuj Goyal
 */
public class HeatChartScale
{
    /**
     * Creates a continuum chart that can be displayed on the screen.
     */
    public static HeatChart DrawScale(double min, double max, int height, double colorScale,
            Color minColor, Color maxColor, int numYvals)
    {
        return DrawScale(min, max, height, colorScale, minColor, maxColor, numYvals, new java.awt.Color(246, 248, 254));
    }

    /**
     * Creates a continuum chart that can be displayed on the screen.
     */
    public static HeatChart DrawScale(double min, double max, int height, double colorScale,
            Color minColor, Color maxColor, int numYvals, Color backColor)
    {
        int factor = 100;
        double difference = max - min;
        double addition = difference / (double) factor;
        double[][] newZValues = new double[factor][1];
        Double[] yvals = new Double[factor];
        double current = min;
        for (int i = 0; i < factor; i++)
        {
            newZValues[i][0] = current;
            yvals[i] = ((int) (current * 1000)) / 1000.0;
            current += addition;
        }
        HeatChart curChart = new HeatChart(newZValues);
        curChart.setXAxisValuesFrequency(1);
        curChart.setYAxisValuesFrequency(numYvals);
        curChart.setShowXAxisValues(false);
        curChart.setChartHeight(height);
        curChart.setChartWidth(100);
        curChart.setHighValueColour(maxColor);
        curChart.setLowValueColour(minColor);
        curChart.setBackgroundColour(backColor);
        curChart.setColourScale(colorScale);
        curChart.setXValuesHorizontal(true);

        curChart.setDataMax(max);
        curChart.setDataMin(min);
        curChart.setYValues(yvals);

        return curChart;
    }
}
