package it.dev;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import Quotes.HistoricalData;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class WeekGraphActivity extends Activity 
{
	private LinearLayout week_LL;
	private ArrayList<HistoricalData> data;
	
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.week_graph_activity);
        
        week_LL = (LinearLayout) findViewById(R.id.week_LL);
        
        Intent intent = getIntent(); // l'intent di questa activity
        String pkg = getPackageName();
        
        data = (ArrayList<HistoricalData>)intent.getSerializableExtra(pkg+".lists");
        
        plotGraph();
    }
	
	private void plotGraph()
	{
		String[] titles = new String[] {""};
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		
		//------------------retreaving data from ArrayList--------------------//
		Date[] dateTmp = new Date[data.size()];
		double[] valueTmp = new double[data.size()];
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		ArrayList<Double> listaPerOrdinareY = new ArrayList<Double>();
		
	    for (int i = 0; i < data.size(); i++) 
		{
	    	try 
	    	{
	    		System.out.println(data.get(i).getDate());
				dateTmp[i] = (Date)formatter.parse(data.get(i).getDate());
			} 
	    	catch (Exception e) 
			{
				e.printStackTrace();
			}
//			valueTmp[i] = Double.valueOf(String.valueOf(toolHistoricalData.get(i).getValue()));
	    	valueTmp[i] = Double.parseDouble(data.get(i).getValue());
			listaPerOrdinareY.add(valueTmp[i]);
		}
	    
	    dates.add(dateTmp);
	    values.add(valueTmp);
        //------------------------------------------------------------------//
	    
	    int[] colors = new int[] {Color.BLUE};
	    PointStyle[] styles = new PointStyle[] {PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    
	    
	    Collections.sort(listaPerOrdinareY);
	    
	    setChartSettings(renderer, "History Diagram", "Date", "Price", dates.get(0)[0].getTime(),dates.get(0)[data.size()-1].getTime(), 0, listaPerOrdinareY.get(listaPerOrdinareY.size()-1)*1.05, Color.GRAY, Color.LTGRAY);
	    renderer.setXLabels(5);
	    renderer.setYLabels(10);
	    int length = renderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) 
	    {
	        SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
	        seriesRenderer.setDisplayChartValues(true);
	    }
	    View mChartView = ChartFactory.getTimeChartView(WeekGraphActivity.this, buildDateDataset(titles, dates, values),
	            renderer, "MM/dd/yyyy");
        
		LayoutParams params = new LayoutParams(400, 400);
		
		week_LL.addView(mChartView, 0, params);
	}
	
	//METHODSSS

			protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
					List<double[]> yValues) {
				XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
				addXYSeries(dataset, titles, xValues, yValues, 0);
				return dataset;
			}

			public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
					List<double[]> yValues, int scale) {
				int length = titles.length;
				for (int i = 0; i < length; i++) {
					XYSeries series = new XYSeries(titles[i], scale);
					double[] xV = xValues.get(i);
					double[] yV = yValues.get(i);
					int seriesLength = xV.length;
					for (int k = 0; k < seriesLength; k++) {
						series.add(xV[k], yV[k]);
					}
					dataset.addSeries(series);
				}
			}

			/**
			 * Builds an XY multiple series renderer.
			 * 
			 * @param colors the series rendering colors
			 * @param styles the series point styles
			 * @return the XY multiple series renderers
			 */
			protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
				XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
				setRenderer(renderer, colors, styles);
				return renderer;
			}

			protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
				renderer.setAxisTitleTextSize(16);
				renderer.setChartTitleTextSize(20);
				renderer.setLabelsTextSize(15);
				renderer.setLegendTextSize(15);
				renderer.setPointSize(5f);
				renderer.setMargins(new int[] { 20, 30, 15, 20 });
				int length = colors.length;
				for (int i = 0; i < length; i++) {
					XYSeriesRenderer r = new XYSeriesRenderer();
					r.setColor(colors[i]);
					r.setPointStyle(styles[i]);
					renderer.addSeriesRenderer(r);
				}
			}

			/**
			 * Sets a few of the series renderer settings.
			 * 
			 * @param renderer the renderer to set the properties to
			 * @param title the chart title
			 * @param xTitle the title for the X axis
			 * @param yTitle the title for the Y axis
			 * @param xMin the minimum value on the X axis
			 * @param xMax the maximum value on the X axis
			 * @param yMin the minimum value on the Y axis
			 * @param yMax the maximum value on the Y axis
			 * @param axesColor the axes color
			 * @param labelsColor the labels color
			 */
			protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
					String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
					int labelsColor) {
				renderer.setChartTitle(title);
				renderer.setXTitle(xTitle);
				renderer.setYTitle(yTitle);
				renderer.setXAxisMin(xMin);
				renderer.setXAxisMax(xMax);
				renderer.setYAxisMin(yMin);
				renderer.setYAxisMax(yMax);
				renderer.setAxesColor(axesColor);
				renderer.setLabelsColor(labelsColor);
			}

			/**
			 * Builds an XY multiple time dataset using the provided values.
			 * 
			 * @param titles the series titles
			 * @param xValues the values for the X axis
			 * @param yValues the values for the Y axis
			 * @return the XY multiple time dataset
			 */
			protected XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
					List<double[]> yValues) {
				XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
				int length = titles.length;
				for (int i = 0; i < length; i++) {
					TimeSeries series = new TimeSeries(titles[i]);
					Date[] xV = xValues.get(i);
					double[] yV = yValues.get(i);
					int seriesLength = xV.length;
					for (int k = 0; k < seriesLength; k++) {
						series.add(xV[k], yV[k]);
					}
					dataset.addSeries(series);
				}
				return dataset;
			}

			/**
			 * Builds a category series using the provided values.
			 * 
			 * @param titles the series titles
			 * @param values the values
			 * @return the category series
			 */
			protected CategorySeries buildCategoryDataset(String title, double[] values) {
				CategorySeries series = new CategorySeries(title);
				int k = 0;
				for (double value : values) {
					series.add("Project " + ++k, value);
				}

				return series;
			}

			/**
			 * Builds a multiple category series using the provided values.
			 * 
			 * @param titles the series titles
			 * @param values the values
			 * @return the category series
			 */
			protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
					List<String[]> titles, List<double[]> values) {
				MultipleCategorySeries series = new MultipleCategorySeries(title);
				int k = 0;
				for (double[] value : values) {
					series.add(2007 + k + "", titles.get(k), value);
					k++;
				}
				return series;
			}

			/**
			 * Builds a category renderer to use the provided colors.
			 * 
			 * @param colors the colors
			 * @return the category renderer
			 */
			protected DefaultRenderer buildCategoryRenderer(int[] colors) {
				DefaultRenderer renderer = new DefaultRenderer();
				renderer.setLabelsTextSize(15);
				renderer.setLegendTextSize(15);
				renderer.setMargins(new int[] { 20, 30, 15, 0 });
				for (int color : colors) {
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					r.setColor(color);
					renderer.addSeriesRenderer(r);
				}
				return renderer;
			}

			/**
			 * Builds a bar multiple series dataset using the provided values.
			 * 
			 * @param titles the series titles
			 * @param values the values
			 * @return the XY multiple bar dataset
			 */
			protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
				XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
				int length = titles.length;
				for (int i = 0; i < length; i++) {
					CategorySeries series = new CategorySeries(titles[i]);
					double[] v = values.get(i);
					int seriesLength = v.length;
					for (int k = 0; k < seriesLength; k++) {
						series.add(v[k]);
					}
					dataset.addSeries(series.toXYSeries());
				}
				return dataset;
			}

			/**
			 * Builds a bar multiple series renderer to use the provided colors.
			 * 
			 * @param colors the series renderers colors
			 * @return the bar multiple series renderer
			 */
			protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
				XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
				renderer.setAxisTitleTextSize(16);
				renderer.setChartTitleTextSize(20);
				renderer.setLabelsTextSize(15);
				renderer.setLegendTextSize(15);
				int length = colors.length;
				for (int i = 0; i < length; i++) {
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					r.setColor(colors[i]);
					renderer.addSeriesRenderer(r);
				}
				return renderer;
			}
	
}
