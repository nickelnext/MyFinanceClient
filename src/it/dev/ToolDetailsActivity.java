package it.dev;

import it.util.ConnectionUtils;
import it.util.ResponseHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import Quotes.HistoryContainer;
import Quotes.QuotationContainer;
import Quotes.QuotationType;
import Quotes.Quotation_Bond;
import Quotes.Quotation_Fund;
import Quotes.Quotation_Share;
import Requests.Request;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

public class ToolDetailsActivity extends Activity 
{
	private MyFinanceDatabase db;
	private SupportDatabaseHelper supportDatabase = new SupportDatabaseHelper(this);

	private TextView tool_purchaseDate_label;
	private TextView tool_purchasePrize_label;
	private TextView tool_roundLot_label;
	
	private TextView toolReferenceTextView;
	private TextView tool_purchaseDate_TV;
	private TextView tool_purchasePrize_TV;
	private TextView tool_roundLot_TV;
	private TableLayout dynamic_detail_table;
	private Button plot_btn;

	private String toolIsin;
	private String toolType;
	
	private String preferredSite;
	private ArrayList<String> ignoredSites = new ArrayList<String>();
	
	private ArrayList<CheckBox> ignoredSitesCB = new ArrayList<CheckBox>();
	private ArrayList<TextView> ignoredSitesTV = new ArrayList<TextView>();
	
	private ArrayList<HistoricalData> toolHistoricalData = new ArrayList<HistoricalData>();

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_details);

		tool_purchaseDate_label = (TextView) findViewById(R.id.tool_purchaseDate_label);
		tool_purchasePrize_label = (TextView) findViewById(R.id.tool_purchasePrize_label);
		tool_roundLot_label = (TextView) findViewById(R.id.tool_roundLot_label);
		
		toolReferenceTextView = (TextView) findViewById(R.id.toolReferenceTextView);
		tool_purchaseDate_TV = (TextView) findViewById(R.id.tool_purchaseDate_TV);
		tool_purchasePrize_TV = (TextView) findViewById(R.id.tool_purchasePrize_TV);
		tool_roundLot_TV = (TextView) findViewById(R.id.tool_roundLot_TV);
		dynamic_detail_table = (TableLayout) findViewById(R.id.dynamic_detail_table);
		plot_btn = (Button) findViewById(R.id.plot_btn);

		Intent intent = getIntent();
		String pkg = getPackageName();

		toolIsin = (String) intent.getStringExtra(pkg+".toolIsin");
		toolType = (String) intent.getStringExtra(pkg+".toolType");

		toolReferenceTextView.setText(toolIsin);
		tool_purchaseDate_TV.setText((String) intent.getStringExtra(pkg+".toolPurchaseDate"));
		tool_purchasePrize_TV.setText((String) intent.getStringExtra(pkg+".toolPurchasePrize"));
		tool_roundLot_TV.setText((String) intent.getStringExtra(pkg+".toolRoundLot"));

		db = new MyFinanceDatabase(this);
		
		try 
        {
        	supportDatabase.createDataBase();
 
        } catch (IOException ioe) 
        {
        	throw new Error("Unable to create database");
        }
		
		initializeLabels();
		
		plot_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//se il tool considerato è un BOND o un FUND
				if(!toolType.equals("share"))
				{
					//faccio chiamata al DB per riempire l'ArrayList corrispondente...<< toolHistoricalData >>
					getHistoricalDataFromDatabase();
					showGraphDialog();
				}
				else
				{
					//faccio richiesta asincrona al Server per riempire l'ArrayList corrispondente...
					callHistoricalDataRequest();
				}
			}
		});
		
	}
	
	private void initializeLabels()
	{
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();
		
		tool_purchaseDate_label.setText(supportDatabase.getTextFromTable("Label_custom_add_new_tool_dialog", "date_TV", language)+": ");
		tool_purchasePrize_label.setText(supportDatabase.getTextFromTable("Label_custom_add_new_tool_dialog", "price_TV", language)+": ");
		tool_roundLot_label.setText(supportDatabase.getTextFromTable("Label_custom_add_new_tool_dialog", "lot_TV", language)+": ");
		
		supportDatabase.close();
	}

	public void onResume()
	{
		super.onResume();
		dynamic_detail_table.removeAllViews();
		updateView();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();
		
		getMenuInflater().inflate(R.menu.tool_detail_menu, menu);
		MenuItem forcedUpdate = menu.findItem(R.id.menu_forced_update);
		MenuItem advancedSettings = menu.findItem(R.id.menu_advanced_settings);
		MenuItem aboutPage = menu.findItem(R.id.menu_about_page);
		MenuItem helpPage = menu.findItem(R.id.menu_help_page);
		
		forcedUpdate.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_forced_update", language));
		advancedSettings.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_advanced_settings", language));
		aboutPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_about_page", language));
		helpPage.setTitle(supportDatabase.getTextFromTable("Label_MENU_MyFinanceActivity", "menu_help_page", language));
		
		supportDatabase.close();
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_forced_update:
			callForcedUpdate();
			break;
		case R.id.menu_advanced_settings:
			showAdvancedSettingsDialog();
			break;
		case R.id.menu_about_page:
			Intent i = new Intent(this, AboutActivity.class);
			startActivity(i);
			break;
		case R.id.menu_help_page:
			Intent i1 = new Intent(this, HelpActivity.class);
			startActivity(i1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//query the database in order to get the historical data of a particular Tool (ISIN)
	private void getHistoricalDataFromDatabase()
	{
		toolHistoricalData.clear();
		
		db.open();
		
		Cursor toolHD = db.getHistoricalDataOfTool(toolIsin);
		startManagingCursor(toolHD);
		if(toolHD.getCount()!=0)
		{
			//aggiungo all'Arraylist gli elementi che tiro su dal database...[data, valore]
			toolHD.moveToFirst();
			do {
				toolHistoricalData.add(new HistoricalData(toolHD.getString(2), toolHD.getFloat(3)));
			} while (toolHD.moveToNext());
		}
		
		db.close();
	}

	//load data from database and create layout...
	private void updateView()
	{
		db.open();
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();

		Cursor toolDetails;
		Cursor toolTranslate;

		if(toolType.equals("bond"))
		{
			toolDetails = db.getBondDetails(toolIsin);
			toolTranslate = supportDatabase.getBondTranslation(language);
		}
		else if(toolType.equals("fund"))
		{
			toolDetails = db.getFundDetails(toolIsin);
			toolTranslate = supportDatabase.getFundTranslation(language);
		}
		else if(toolType.equals("share"))
		{
			toolDetails = db.getShareDetails(toolIsin);
			toolTranslate = supportDatabase.getShareTranslation(language);
		}
		else
		{
			return;
		}

		startManagingCursor(toolDetails);
		startManagingCursor(toolTranslate);

		if(toolDetails.getCount()==1)
		{
			toolDetails.moveToFirst();
			toolTranslate.moveToFirst();
			int j = 2;
			for (int i = 1; i < toolDetails.getColumnCount(); i++) 
			{
				LayoutInflater inflater = getLayoutInflater();

				TableRow newRow = (TableRow) inflater.inflate(R.layout.tool_details_row, dynamic_detail_table, false);
				if(i%2==0);
				else
				{
					newRow.setBackgroundColor(Color.parseColor("#BDEEF9"));
				}

				TextView key = (TextView) newRow.findViewById(R.id.key_entry);
				TextView value = (TextView) newRow.findViewById(R.id.value_entry);

				key.setTextColor(Color.BLACK);
				key.setText(toolTranslate.getString(j));
				
				try	{
					value.setText(toolDetails.getString(i));
				}
				catch(Exception e){
					try{
						value.setText(""+toolDetails.getInt(i));
					}
					catch(Exception e1)	{
						try{
						value.setText(""+toolDetails.getFloat(i));
						}
						catch(Exception e2){
							System.out.println("error");
						}
					}
				}
				value.setTextColor(Color.BLACK);
				
				dynamic_detail_table.addView(newRow);
				j++;
			}
		}
		db.close();
		supportDatabase.close();
	}
	
	private void callHistoricalDataRequest()
	{
		//1. CALL ASYNCTASK TO GET DATA FROM SERVER....
		HistoricalDataRequestAsyncTask asyncTask1 = new HistoricalDataRequestAsyncTask(ToolDetailsActivity.this);
		asyncTask1.execute(toolIsin);
	}
	
	@SuppressWarnings("unchecked")
	private void callForcedUpdate()
	{
		
		ignoredSites.clear();
		db.open();
		QuotationType qType;
		Cursor details;
		
		if(toolType.equals("bond"))
		{
			qType = QuotationType.BOND;
			details = db.getBondDetails(toolIsin);
		}
		else if(toolType.equals("fund"))
		{
			qType = QuotationType.FUND;
			details = db.getFundDetails(toolIsin);
		}
		else if(toolType.equals("share"))
		{
			qType = QuotationType.SHARE;
			details = db.getShareDetails(toolIsin);
		}
		else
		{
			return;
		}
		
		startManagingCursor(details);
		if(details.getCount()==1)
		{
			details.moveToFirst();
			
			//add preferred site...
			String tmp = details.getString(details.getColumnIndex("sitoPreferito"));
			if(tmp.equals(""))
			{
				preferredSite = null;
			}
			else
			{
				preferredSite = details.getString(details.getColumnIndex("sitoPreferito"));;
			}
			
			if(preferredSite!=null)
			{
				if(preferredSite.equals(details.getString(details.getColumnIndex("sitoSorgente"))))
				{
					showMessage("Error", "You can't discard the preferred site for next search. Open the Advanced Settings Menu to resolve the problem.");
					return;
				}
			}
			
			//add ignored sites already saved in database...
			
			try 
			{
				if(details.getString(details.getColumnIndex("sitiIgnorati"))!=null)
				{
					String[] array = details.getString(details.getColumnIndex("sitiIgnorati")).split(" ");
					
					for (String string : array) 
					{
						if(!string.equals(""))
						{
							ignoredSites.add(string);
							System.out.println("sito ignorato: "+string);
						}
					}
				}
				
				
			} catch (StringIndexOutOfBoundsException e) 
			{
				e.printStackTrace();
			}
			
			
			//add source site...
			ignoredSites.add(details.getString(details.getColumnIndex("sitoSorgente")));
		}
		
		//1. create arrayList of Quotation Request....
		ArrayList<Request> array = new ArrayList<Request>();
		array.add(new Request(toolIsin, qType, preferredSite, ignoredSites));
		
		
		//2. CALL ASYNCTASK TO GET DATA FROM SERVER....
		ForcedRequestAsyncTask asyncTask1 = new ForcedRequestAsyncTask(ToolDetailsActivity.this);
		asyncTask1.execute(array);
		
		
		
		db.close();
	}
	
	//this method open the dialog for graph plotting...
	private void showGraphDialog()
	{
		
		
		final Dialog graphDialog = new Dialog(ToolDetailsActivity.this);
		graphDialog.setContentView(R.layout.custom_graph_dialog);
		graphDialog.setTitle("History Graph");
		graphDialog.setCancelable(false);
		
		Button close_graph_btn = (Button) graphDialog.findViewById(R.id.close_graph_btn);
		final LinearLayout graph_layout = (LinearLayout) graphDialog.findViewById(R.id.graph_layout);
		
		close_graph_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				graphDialog.dismiss();
			}
		});
		
		
		//qui ci va il codice per la visualizzazione del grafico:
		//		i dati dovrebbero essere già salvati nell'ArrayList opportuno...
		
		
		
		//////////////////////////////////////////////////////////////////////////////////
		String[] titles = new String[] {toolIsin};
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		
		//------------------retreaving data from ArrayList--------------------//
		Date[] dateTmp = new Date[toolHistoricalData.size()];
		double[] valueTmp = new double[toolHistoricalData.size()];
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		ArrayList<Double> listaPerOrdinareY = new ArrayList<Double>();
		
	    for (int i = 0; i < toolHistoricalData.size(); i++) 
		{
	    	try 
	    	{
	    		System.out.println(toolHistoricalData.get(i).getDate());
				dateTmp[i] = (Date)formatter.parse(toolHistoricalData.get(i).getDate());
			} 
	    	catch (Exception e) 
			{
				e.printStackTrace();
			}
//			valueTmp[i] = Double.valueOf(String.valueOf(toolHistoricalData.get(i).getValue()));
	    	valueTmp[i] = toolHistoricalData.get(i).getValue();
			listaPerOrdinareY.add(valueTmp[i]);
		}
	    
	    dates.add(dateTmp);
	    values.add(valueTmp);
        //------------------------------------------------------------------//
	    
	    int[] colors = new int[] {Color.BLUE};
	    PointStyle[] styles = new PointStyle[] {PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    
	    
	    Collections.sort(listaPerOrdinareY);
	    
	    setChartSettings(renderer, "History Diagram", "Date", "Price", dates.get(0)[0].getTime(),dates.get(0)[toolHistoricalData.size()-1].getTime(), 0, listaPerOrdinareY.get(listaPerOrdinareY.size()-1)+20, Color.GRAY, Color.LTGRAY);
	    renderer.setXLabels(5);
	    renderer.setYLabels(10);
	    int length = renderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) 
	    {
	        SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
	        seriesRenderer.setDisplayChartValues(true);
	    }
	    View mChartView = ChartFactory.getTimeChartView(ToolDetailsActivity.this, buildDateDataset(titles, dates, values),
	            renderer, "MM/dd/yyyy");
        
        
        
        
        
		LayoutParams params = new LayoutParams(400, 400);
		
		graph_layout.addView(mChartView, 0, params);
		
		graphDialog.show();
		
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
	
	//this method open the dialog for advanced settings...
	private void showAdvancedSettingsDialog()
	{
		supportDatabase.openDataBase();
		
		String language = supportDatabase.getUserSelectedLanguage();
		
		ignoredSitesCB.clear();
		ignoredSitesTV.clear();
		
		String prefSiteFromDB = null;
		final TextView tmp = new TextView(ToolDetailsActivity.this);
		
		final Dialog advancedOptionsDialog = new Dialog(ToolDetailsActivity.this);
		advancedOptionsDialog.setContentView(R.layout.custom_advanced_options_dialog);
		advancedOptionsDialog.setTitle(toolIsin);
		advancedOptionsDialog.setCancelable(true);
		
		final CheckBox prefSite_CB = (CheckBox) advancedOptionsDialog.findViewById(R.id.prefSite_CB);
		final TextView preferredSiteRef = (TextView) advancedOptionsDialog.findViewById(R.id.preferredSite_TV);
		final TextView ignoredSites_TV = (TextView) advancedOptionsDialog.findViewById(R.id.ignoredSites_TV);
		final TableLayout dynamic_ignoredSites_table = (TableLayout) advancedOptionsDialog.findViewById(R.id.dynamic_ignoredSites_table);
		
		Button canc_adv_sett_btn = (Button) advancedOptionsDialog.findViewById(R.id.canc_adv_sett_btn);
		Button save_adv_sett_btn = (Button) advancedOptionsDialog.findViewById(R.id.save_adv_sett_btn);
		
		preferredSiteRef.setText(supportDatabase.getTextFromTable("Label_custom_advanced_settings_dialog", "preferredSite_TV", language));
		ignoredSites_TV.setText(supportDatabase.getTextFromTable("Label_custom_advanced_settings_dialog", "ignoredSites_TV", language));
		
		canc_adv_sett_btn.setText(supportDatabase.getTextFromTable("Label_custom_advanced_settings_dialog", "canc_advanced_settings_btn", language));
		save_adv_sett_btn.setText(supportDatabase.getTextFromTable("Label_custom_advanced_settings_dialog", "save_advanced_settings_btn", language));
		
		prefSite_CB.setText(supportDatabase.getTextFromTable("Label_custom_advanced_settings_dialog", "preferredSite_CB", language));
		
		canc_adv_sett_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				advancedOptionsDialog.dismiss();
			}
		});
		
		save_adv_sett_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				db.open();
				
				//controlli:
				
				//1. deve esserci almeno una checkbox dei siti ignorati non checkata
				boolean allIgnoredSitesCBChecked = true;
				
				for (int i = 0; i < ignoredSitesCB.size(); i++) 
				{
					if(!ignoredSitesCB.get(i).isChecked())
					{
						allIgnoredSitesCBChecked = false;
						break;
					}
				}
				
				if(ignoredSitesCB.size()==0)
				{
					if(prefSite_CB.isChecked())
					{
						if(toolType.equals("bond"))
						{
							db.updateSelectedBondPreferredSite(toolIsin, tmp.getText().toString());
						}
						else if(toolType.equals("fund"))
						{
							db.updateSelectedFundPreferredSite(toolIsin, tmp.getText().toString());
						}
						else if(toolType.equals("share"))
						{
							db.updateSelectedSharePreferredSite(toolIsin, tmp.getText().toString());
						}
					}
					else
					{
						if(toolType.equals("bond"))
						{
							db.updateSelectedBondPreferredSite(toolIsin, "");
						}
						else if(toolType.equals("fund"))
						{
							db.updateSelectedFundPreferredSite(toolIsin, "");
						}
						else if(toolType.equals("share"))
						{
							db.updateSelectedSharePreferredSite(toolIsin, "");
						}
					}
					
					showMessage("Info", "Please be sure to have internet connection next time you will open the application.");
					
					advancedOptionsDialog.dismiss();
					
				}
				else
				{
					if(allIgnoredSitesCBChecked)
					{
						//error!
						showMessage("Error", "You can't ignore all sites for this tool.");
					}
					else
					{
						//2. se la checkbox del sito preferito è checkata, la corrispondente checkbox nei
						//   siti ignorati deve essere non checkata
						int index = 0;
						for (int i = 0; i < ignoredSitesCB.size(); i++) 
						{
							System.out.println("STRINGA CONFRONTO: "+ignoredSitesTV.get(i).getText().toString()+" - "+tmp.getText().toString());
							if(ignoredSitesTV.get(i).getText().toString().equals(tmp.getText().toString()))
							{
								index = i;
							}
						}
						
						System.out.println("INDEX = "+index);
						
						if(prefSite_CB.isChecked() && ignoredSitesCB.get(index).isChecked())
						{
							//error
							showMessage("Error", "You can't ignore the preferred site");
						}
						else
						{
							System.out.println("PROCEDO A SALVARE NEL DB...");
							//procedo a salvare nel DB...
							if(prefSite_CB.isChecked())
							{
								if(toolType.equals("bond"))
								{
									db.updateSelectedBondPreferredSite(toolIsin, tmp.getText().toString());
								}
								else if(toolType.equals("fund"))
								{
									db.updateSelectedFundPreferredSite(toolIsin, tmp.getText().toString());
								}
								else if(toolType.equals("share"))
								{
									db.updateSelectedSharePreferredSite(toolIsin, tmp.getText().toString());
								}
							}
							else
							{
								if(toolType.equals("bond"))
								{
									db.updateSelectedBondPreferredSite(toolIsin, "");
								}
								else if(toolType.equals("fund"))
								{
									db.updateSelectedFundPreferredSite(toolIsin, "");
								}
								else if(toolType.equals("share"))
								{
									db.updateSelectedSharePreferredSite(toolIsin, "");
								}
							}
							
							String stringTmp = "";
							
							for (int i = 0; i < ignoredSitesCB.size(); i++) 
							{
								if(ignoredSitesCB.get(i).isChecked())
								{
									stringTmp = stringTmp + ignoredSitesTV.get(i).getText().toString()+" ";
								}
							}
							
							if(toolType.equals("bond"))
							{
								db.updateSelectedBondIgnoredSites(toolIsin, stringTmp);
							}
							else if(toolType.equals("fund"))
							{
								db.updateSelectedFundIgnoredSites(toolIsin, stringTmp);
							}
							else if(toolType.equals("share"))
							{
								db.updateSelectedShareIgnoredSites(toolIsin, stringTmp);
							}
							
							advancedOptionsDialog.dismiss();
						}
						
					}
				}
				
				db.close();
				
			}
		});
		
		db.open();
		
		Cursor toolDetails;
		if(toolType.equals("bond"))
		{
			toolDetails = db.getBondDetails(toolIsin);
		}
		else if(toolType.equals("fund"))
		{
			toolDetails = db.getFundDetails(toolIsin);
		}
		else if(toolType.equals("share"))
		{
			toolDetails = db.getShareDetails(toolIsin);
		}
		else
		{
			toolDetails = null;
		}
		startManagingCursor(toolDetails);
		
		
		//add preferred site...
		if(toolDetails!=null)
		{
			if(toolDetails.getCount()==1)
			{
				toolDetails.moveToFirst();
				
				String label = supportDatabase.getTextFromTable("Label_custom_advanced_settings_dialog", "preferredSite_CB", language);
				prefSiteFromDB = toolDetails.getString(toolDetails.getColumnIndex("sitoSorgente"));
				tmp.setText(prefSiteFromDB);
				
				label = label.replaceAll("__NAME__", prefSiteFromDB);
				
				
				prefSite_CB.setText(label);
				
				if(prefSiteFromDB.equals(toolDetails.getString(toolDetails.getColumnIndex("sitoPreferito"))))
				{
					prefSite_CB.setChecked(true);
				}
				
			}
		}
		
		ArrayList<String> array = new ArrayList<String>();
		
		//add rows for ignored sites...
		//1. all sites that find this type of tools...
		Cursor sites = db.getSitesForType(toolType.toUpperCase());
		startManagingCursor(sites);
		
		if(sites.getCount()!=0)
		{
			sites.moveToFirst();
			do {
				array.add(sites.getString(sites.getColumnIndex("sito")));
			} while (sites.moveToNext());
		}
		
		
		//2. all sites already ignored must be checked...
		ArrayList<String> ignoredSitesFromDB = new ArrayList<String>();
		
		if(toolDetails!=null)
		{
			if(toolDetails.getCount()==1)
			{
				toolDetails.moveToFirst();
				String[] arraytmp = toolDetails.getString(toolDetails.getColumnIndex("sitiIgnorati")).split(" ");
				for (String string : arraytmp) 
				{
					if(!string.equals(""))
					{
						ignoredSitesFromDB.add(string);
					}
				}
			}
		}
		
		for (int i = 0; i < array.size(); i++)
		{
			LayoutInflater inflater = getLayoutInflater();
			
			TableRow newRow = (TableRow) inflater.inflate(R.layout.advanced_options_row, dynamic_ignoredSites_table, false);
			
			CheckBox ignored_cb = (CheckBox) newRow.findViewById(R.id.ignoredSite_CB);
			TextView ignored_tv = (TextView) newRow.findViewById(R.id.ignoredSite_TV);
			
			if(ignoredSitesFromDB.contains(array.get(i)))
			{
				ignored_cb.setChecked(true);
			}
			ignored_tv.setText(array.get(i));
			
			ignoredSitesCB.add(ignored_cb);
			ignoredSitesTV.add(ignored_tv);
			
			dynamic_ignoredSites_table.addView(newRow);
		}
		
		advancedOptionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				
				dynamic_detail_table.removeAllViews();
				
				updateView();
			}
		});
		supportDatabase.close();
		db.close();
		advancedOptionsDialog.show();
	}
	
	private void showMessage(String type, String message)
	{
		AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
    	alert_builder.setTitle(type);
    	alert_builder.setMessage(message);
    	alert_builder.setCancelable(false);
    	alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
    	AlertDialog message_empty = alert_builder.create();
    	message_empty.show();
	}
	
	private boolean isinRequestedIsReturned(String isinRequested, QuotationContainer container)
	{
		boolean result = false;
		
		for(Quotation_Bond qb : container.getBondList())
		{
			if(qb.getISIN().equals(isinRequested))
			{
				result = true;
			}
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			if(qf.getISIN().equals(isinRequested))
			{
				result = true;
			}
		}
		for(Quotation_Share qs : container.getShareList())
		{
			if(qs.getISIN().equals(isinRequested))
			{
				result = true;
			}
		}
		
		return result;
		
	}
	
	private ArrayList<String> searchIsinNotRequested(String toolIsin, QuotationContainer container)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		ArrayList<String> support = new ArrayList<String>();
		for(Quotation_Bond qb : container.getBondList())
		{
			support.add(qb.getISIN());
		}
		for(Quotation_Fund qf : container.getFundList())
		{
			support.add(qf.getISIN());
		}
		for(Quotation_Share qs : container.getShareList())
		{
			support.add(qs.getISIN());
		}
		
		for (int i = 0; i < support.size(); i++) 
		{
			if(!support.get(i).equals(toolIsin))
			{
				result.add(support.get(i));
			}
		}
		
		return result;
	}
	
	private String getTodaysDate() 
	{
	    final Calendar c = Calendar.getInstance();
	    return(new StringBuilder()
	            .append(c.get(Calendar.MONTH) + 1).append("/")
	            .append(c.get(Calendar.DAY_OF_MONTH)).append("/")
	            .append(c.get(Calendar.YEAR)).append(" ")
	            .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
	            .append(c.get(Calendar.MINUTE)).append(":")
	            .append(c.get(Calendar.SECOND)).append(" ")).toString();
	}
	
	private class HistoricalDataRequestAsyncTask extends AsyncTask<String, Void, HistoryContainer>
	{
		private ProgressDialog dialog;
		private Context context;
		
		public HistoricalDataRequestAsyncTask(Context ctx)
		{
			this.context = ctx;
		}
		
		@Override
		protected HistoryContainer doInBackground(String... params) 
		{
			try {
				HistoryContainer historyCont = new HistoryContainer();
//				Gson converter = new Gson();
//				String jsonReq = converter.toJson(params[0]);
//				System.out.println(""+jsonReq);
				String jsonResponse = ConnectionUtils.searchHistory(params[0]);
				if(jsonResponse != null)
				{
					historyCont = ResponseHandler.decodeHistoryData(jsonResponse);
					return historyCont;
				}
				else
				{
					return null;
				}
			} catch (Exception e) {
				System.out.println("connection ERROR");
			}
			return null;
		}
		
		@Override
		protected void onPreExecute()
		{
			//load progress dialog....
			dialog = new ProgressDialog(this.context);
			dialog.setMessage("Loading, contacting Server for Historical Data.");
			dialog.show();
		}
		
		@Override
		protected void onPostExecute(HistoryContainer container)
		{
			//dismiss progress dialog....
			if(dialog.isShowing())
			{
				dialog.dismiss();
			}
			
			if(container!=null)
			{
				if(container.getHistoryList()!=null)
				{
					//svuoto l'arraylist...per accogliere i nuovi dati...
					toolHistoricalData.clear();
					
//					for (int i = 0; i < container.getHistoryList().size(); i++) 
//					{
//						//aggiungo all'arraylist tutti gli elementi del container...
//						toolHistoricalData.add(container.getHistoryList().get(i));
//					}
					toolHistoricalData = container.getHistoryList();
					showGraphDialog();
					System.out.println("wtffffffffaaaaaaaa");
				}
				else
				{
					showMessage("ERROR", "There were errors during connection");
				}
			}	
		}
	}
	
	private class ForcedRequestAsyncTask extends AsyncTask<ArrayList<Request>, Void, QuotationContainer>
	{
		private ProgressDialog dialog;
		private Context context;
		
		public ForcedRequestAsyncTask(Context ctx)
		{
			this.context = ctx;
		}
		
		@Override
		protected QuotationContainer doInBackground(ArrayList<Request>... params) 
		{
			try {
				QuotationContainer quotCont = new QuotationContainer();
				Gson converter = new Gson();
				String jsonReq = converter.toJson(params[0]);
				System.out.println(""+jsonReq);
				String jsonResponse = ConnectionUtils.postData(jsonReq);
				if(jsonResponse != null)
				{
					quotCont = ResponseHandler.decodeQuotations(jsonResponse);
					return quotCont;
				}
				else
				{
					return null;
				}
			} catch (Exception e) {
				System.out.println("connection ERROR");
			}
			return null;
		}
		
		@Override
		protected void onPreExecute()
		{
			//load progress dialog....
			dialog = new ProgressDialog(this.context);
			dialog.setMessage("Loading, forced update");
			dialog.show();
		}
		
		@Override
		protected void onPostExecute(QuotationContainer container)
		{
			db.open();
			
			//dismiss progress dialog....
			if(dialog.isShowing())
			{
				dialog.dismiss();
			}
			
			
			if(container!=null)
			{
				int totalQuotationReturned = container.getBondList().size() + container.getFundList().size() + container.getShareList().size();
				
				System.out.println("total returned: "+totalQuotationReturned);
				
				if(isinRequestedIsReturned(toolIsin, container))
				{
					if(totalQuotationReturned > 1)
					{
						//ne ho ricevuti di più rispetto a quello richiesto....
						ArrayList<String> listaIsinNotRequested = searchIsinNotRequested(toolIsin, container);
						
						for (int i = 0; i < listaIsinNotRequested.size(); i++) 
						{
							for(Quotation_Bond qb : container.getBondList())
							{
								if(qb.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getBondList().remove(qb);
								}
							}
							for(Quotation_Fund qf : container.getFundList())
							{
								if(qf.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getFundList().remove(qf);
								}
							}
							for(Quotation_Share qs : container.getShareList())
							{
								if(qs.getISIN().equals(listaIsinNotRequested.get(i)))
								{
									container.getShareList().remove(qs);
								}
							}
						}
					}
					
					
					String ignoredSitesString = null;
					for (int i = 0; i < ignoredSites.size(); i++) 
					{
						if(i==0)
						{
							ignoredSitesString = ignoredSites.get(i);
						}
						else
						{
							ignoredSitesString = ignoredSitesString+" "+ignoredSites.get(i);
						}
					}
					System.out.println("siti ignorati:"+ignoredSitesString);
					
					//procedo all'update dei dati del titolo...
					for(Quotation_Bond qb : container.getBondList())
					{
						try {
							db.updateSelectedBondByQuotationObject(qb, getTodaysDate());
							db.updateSelectedBondIgnoredSites(qb.getISIN(), ignoredSitesString);
						} catch (Exception e) {
							System.out.println("Database update error");
						}
					}
					
					//4. for all FUND returned...
					for(Quotation_Fund qf : container.getFundList())
					{
						//4.1 control if fund already exist in database --> UPDATE
						//UPDATE
						try {
							db.updateSelectedFundByQuotationObject(qf, getTodaysDate());
							db.updateSelectedFundIgnoredSites(qf.getISIN(), ignoredSitesString);
						} catch (Exception e) {
							System.out.println("Database update error");
						}
					}
					
					//5. for all SHARE returned...
					for(Quotation_Share qs : container.getShareList())
					{
						//5.1 control if share already exist in database --> UPDATE
						//UPDATE
						try {
							db.updateSelectedShareByQuotationObject(qs, getTodaysDate());
							db.updateSelectedShareIgnoredSites(qs.getISIN(), ignoredSitesString);
						} catch (Exception e) {
							System.out.println("Database update error");
						}
					}
				}
				else
				{
					showMessage("Info", "The "+toolIsin+" tool is not found from other sites");
				}
			}
			else
			{
				//connection error!
				showMessage("Error", "There were errors during connection with server. Please try again.");
			}
			
			dynamic_detail_table.removeAllViews();
			
			updateView();
			
			db.close();
		}
	}
}

