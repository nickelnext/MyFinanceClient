package it.dev;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ToolDetailsActivity extends Activity 
{
	private MyFinanceDatabase db;

	private TextView toolReferenceTextView;
	private TextView tool_purchaseDate_TV;
	private TextView tool_purchasePrize_TV;
	private TextView tool_roundLot_TV;
	private TableLayout dynamic_detail_table;

	private String toolIsin;
	private String toolType;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tool_details);

		toolReferenceTextView = (TextView) findViewById(R.id.toolReferenceTextView);
		tool_purchaseDate_TV = (TextView) findViewById(R.id.tool_purchaseDate_TV);
		tool_purchasePrize_TV = (TextView) findViewById(R.id.tool_purchasePrize_TV);
		tool_roundLot_TV = (TextView) findViewById(R.id.tool_roundLot_TV);
		dynamic_detail_table = (TableLayout) findViewById(R.id.dynamic_detail_table);

		Intent intent = getIntent();
		String pkg = getPackageName();

		toolIsin = (String) intent.getStringExtra(pkg+".toolIsin");
		toolType = (String) intent.getStringExtra(pkg+".toolType");

		toolReferenceTextView.setText(toolIsin);
		tool_purchaseDate_TV.setText((String) intent.getStringExtra(pkg+".toolPurchaseDate"));
		tool_purchasePrize_TV.setText((String) intent.getStringExtra(pkg+".toolPurchasePrize"));
		tool_roundLot_TV.setText((String) intent.getStringExtra(pkg+".toolRoundLot"));

		db = new MyFinanceDatabase(this);

	}

	public void onResume()
	{
		super.onResume();
		updateView();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.tool_detail_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_forced_update:
			//do forced update...
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	//load data from database and create layout...
	private void updateView()
	{
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
			return;
		}

		startManagingCursor(toolDetails);

		if(toolDetails.getCount()==1)
		{
			toolDetails.moveToFirst();
			for (int i = 1; i < toolDetails.getColumnCount(); i++) 
			{
				LayoutInflater inflater = getLayoutInflater();

				TableRow newRow = (TableRow) inflater.inflate(R.layout.tool_details_row, dynamic_detail_table, false);

				TextView key = (TextView) newRow.findViewById(R.id.key_entry);
				TextView value = (TextView) newRow.findViewById(R.id.value_entry);

				key.setText(toolDetails.getColumnName(i));

				boolean found=false;
				try	{
					value.setText(toolDetails.getString(i));
				}
				catch(Exception e){
					try{
						value.setText("" +toolDetails.getInt(i));
					}
					catch(Exception e1)	{
						try{
						value.setText("" +toolDetails.getFloat(i));
						}
						catch(Exception e2){
							System.out.println("BUUUURN");
						}
					}
				}






			}
		}
















		db.close();
	}
}
