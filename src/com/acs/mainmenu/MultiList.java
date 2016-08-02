package com.acs.mainmenu;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.acs.mainmenu.AcsDBAdapter;
import com.acs.mainmenu.acsLocationListener;


import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;
import android.location.*;


public class MultiList extends Activity {
	Cursor dbrow;
	AcsDBAdapter adb;
	String fileName;
	SharedPreferences prefs;
	String multiSign;
	String reSign;
	String mainMenu;
	private ListView mainListView;
	private Row[] rows;
	private ArrayAdapter<Row> listAdapter;
	double longitude = 0.0000;
	double latitude = 0.0000;

	
	public static final int CAPTURE_REQUEST_CODE = 0;
	
	@Override 
	public void onDestroy(){
		adb.close();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		int position = 0;
		boolean flag = false;
		if (! listAdapter.isEmpty()) {
			do {
				try {
					Row row = listAdapter.getItem(position);
					if(showcheckmark(row) < 0)
						row.setChecked(true);
					position++;
				} catch (Exception e) {
					flag = true;
				}
			} while (!flag);
			listAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	public MultiList() {
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.simplerow);
		
	    CheckBox topchkbox = (CheckBox) findViewById(R.id.rowCheckBox);
		topchkbox.setVisibility(View.INVISIBLE); // set the top level checkbox invisible so only the list checkboxes show 	    	
		
		adb = new AcsDBAdapter(this);
		adb.open();
		prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
		multiSign = prefs.getString("multiSign", "false");
		reSign = prefs.getString("reSign", "false");
		mainMenu = prefs.getString("mainMenu", "false");
		
		try {
			LocationManager mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			LocationListener mLocListen = new acsLocationListener();
			mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListen);
			List<String> proc = (List<String>) mLocMgr.getProviders(true);
			Location loc = mLocMgr.getLastKnownLocation("gps");			
		}
		catch (Exception e){
			String msg = e.getMessage();
			e.printStackTrace();
		}

		mainListView = (ListView) findViewById(R.id.mainListView);
		mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				Row row = listAdapter.getItem( position );
				RowViewHolder viewHolder = (RowViewHolder) item.getTag();
				if(checkcompleted(row, true) < 0) {
					viewHolder.getCheckBox().setChecked(row.isChecked());					
					if(reSign.equals("false"))
						return;
				}
				String results = row.getTxtStr();
				String[] parms = results.split("\\|");
				String task = parms[0];
				task = task.replaceAll("\\|", "");
				// parameter 0 is either the customer OR actionitem
				if(task.equals("ACTION")) 
					updateActionCompleted(row);
				else
					getsignature(results);
				row.setChecked(true);
				viewHolder.getCheckBox().setChecked(row.isChecked());
				//finish();
			}
		});

		
		View v = View.inflate(this, R.layout.footer, null);
		mainListView.addFooterView(v);

		Button btnEnd = (Button) findViewById(R.id.rowBtn);
        btnEnd.getBackground().setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);		
		btnEnd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	    // TODO Auto-generated method stub
		rows = (Row[]) getLastNonConfigurationInstance() ;
		ArrayList<Row> rowList = new ArrayList<Row>();
		if ( rows == null) {
			rowList = buildList();
			if(rowList == null) {
				//showStatus("Error Building List");
				finish();
			}
			
		}

		listAdapter = new RowArrayAdapter(this, R.id.rowTextView, rowList);
		try {
			mainListView.setAdapter(listAdapter);			
		}
		catch (Exception e){
			String msg = e.getMessage();
			e.printStackTrace();
		}
	}
	
	protected void onActivityResult(int request, int result, Intent data){
    	// prefs = getSharedPreferences("cheapoIpc", MODE_PRIVATE);
    	// int kp = prefs.getInt("keypress", 0);
    	// cancel sets this to -1 so it stays on the list screen ....  
    	// if((kp == 0) && (mainMenu.equals("true")))
		// change for warren always return to main menu
    	if(mainMenu.equals("true"))
    		finish();
	}

	private ArrayList<Row> buildList(){
		dbrow = adb.getAllEntries();
		String Cust;
		String Inv;
		String Amt;
		String Type;
		String Addr;
		String sigdate;
		String fname;
		String Action;
		ArrayList<Row> al = new ArrayList<Row>();
		if(dbrow.moveToFirst()) {
			do{
				fname = dbrow.getString(AcsDBAdapter.SIGFILE_COLUMN);
				sigdate = dbrow.getString(AcsDBAdapter.SIGDATE_COLUMN);
				Cust = dbrow.getString(AcsDBAdapter.CUST_COLUMN);
				Inv = dbrow.getString(AcsDBAdapter.INVOICE_COLUMN);
				Amt = dbrow.getString(AcsDBAdapter.AMT_COLUMN);
				Type = dbrow.getString(AcsDBAdapter.TYPE_COLUMN);
				Addr = dbrow.getString(AcsDBAdapter.ADDRESS_COLUMN);
				Action = dbrow.getString(AcsDBAdapter.ACTION_COLUMN);
				//Cust = Cust.replaceAll(":","");
				if(!isNullOrEmpty(Action)) {
					if(!isNullOrEmpty(sigdate))
						al.add(new Row(Type + "| " + Inv + "|\n" + Action, true));
					else
						al.add(new Row(Type + "| " + Inv + "|\n" + Action));	
					continue;
				}
				if (!isNullOrEmpty(fname)) 
					al.add(new Row(Cust + "| " + Inv + "|\n" + Amt + "| " + Type + "|" + Addr, true));
				else
					al.add(new Row(Cust + "| " + Inv + "|\n" + Amt + "| " + Type + "|" + Addr));						
			} while(dbrow.moveToNext());
		} else {
			showStatus("No Records");
			try {
			dbrow.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		try {
			rows = new Row[al.size()];
			al.toArray(rows);		
		}
		catch (Exception e) {
			String msg = e.getMessage();
			e.printStackTrace();
		}
		dbrow.close();
		return al;
	}


	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("") || str.trim().equals("");
	}
	
	private int showcheckmark(Row row) {
		String res = row.getTxtStr();
		String[] parms = res.split("\\|");
		String invno = parms[1];
		invno = invno.replaceAll("\\|", "");
		long ndx = adb.getRowIndex(invno);
    	if(ndx > -1) {
        	String[] rsp = (String[])adb.getEntry(ndx);
        	String sdate = rsp[AcsDBAdapter.SIGDATE_COLUMN];
        	if(!isNullOrEmpty(sdate))
        		return -1;
    	}
    	return 0;		
	}

	private int checkcompleted(Row row, boolean display){
		String res = row.getTxtStr();
		String[] parms = res.split("\\|");
		String invno = parms[1];
		invno = invno.replaceAll("\\|", "");
		long ndx = adb.getRowIndex(invno);
    	if(ndx > -1) {
        	String[] rsp = (String[])adb.getEntry(ndx);
        	String sdate = rsp[AcsDBAdapter.SIGDATE_COLUMN];
        	if(!isNullOrEmpty(sdate))
			{
        		if(display) {
            		if(reSign.equals("true"))
            			return -1;
        			displaycompleted(row);
        		}
        		return -1;
			}
    	}
    	return 0;
	}

	private void displaycompleted(Row row)
	{
		row.setChecked(true);
		showStatus("Completed");
	}
	
	private void updateActionCompleted(Row row) {
		int ndx = -1;
		String res = row.getTxtStr();
		String[] parms = res.split("\\|");
    	String signtime = DateFormat.getDateTimeInstance().format(new Date());
		String invno = parms[1];
		invno = invno.replaceAll("\\|", "");
		ndx = adb.getRowIndex(invno);
    	if(ndx > -1) {
        	String[] rsp = (String[])adb.getEntry(ndx);
        	rsp[AcsDBAdapter.SIGDATE_COLUMN] = signtime;
        	rsp[AcsDBAdapter.LAT_COLUMN] = Double.toString(latitude);
        	rsp[AcsDBAdapter.LONG_COLUMN] = Double.toString(longitude);
        	try {
        		adb.updateEntry(ndx, rsp);
        	} catch (Exception e) {
    			String msg = e.getMessage();
    			e.printStackTrace();        		
        	}
    	}
	}

	private void getsignature(String res){
		String[] parms = res.split("\\|");
		String cust = parms[0];
		cust = cust.replaceAll("\\|", "");
		String invno = parms[1];
		invno = invno.replaceAll("\\|", "");
		String invamt = parms[2];
		invamt = invamt.replaceAll("\n", "");
		Cursor cur = null;
		String[] cres = null;
		String tqty = "";
		int total = 0;
		double amt = 0.0;
		int requestcode = 1;
    	if (multiSign.equals("true")) {
    		try {
    			cur = adb.getCustByName(cust);
    		}
    		catch(Exception e) {
    			String msg = e.getMessage();
    			e.printStackTrace();
    		}
    		if(cur.moveToFirst()) {
    			invno = "";
    			invamt = "";
    			do {
    				int id = cur.getInt(AcsDBAdapter.ID_COLUMN);
    				cres = (String[])adb.getEntry(id);
    				if(multiSign.equals("true")) {
    					invno += cres[AcsDBAdapter.INVOICE_COLUMN] + "\n";
    				} else {
    					invno += cres[AcsDBAdapter.INVOICE_COLUMN];
    				}
    				amt = Double.parseDouble(cres[AcsDBAdapter.AMT_COLUMN]);
    				if(multiSign.equals("true")) 
    					invamt += Double.toString(amt) + "\n";
    				else 
    					invamt = Double.toString(amt);
    			} while(cur.moveToNext());
    		}
			cur.close();
    	}

		SharedPreferences prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
		Editor med = prefs.edit();

		med.putString("cust", cust);
		med.putString("invno", invno);
		med.putString("invamt",invamt);
		med.commit();

		Intent getsig = new Intent(this, GetSignature.class);
		try {
			startActivityForResult(getsig, requestcode);
		} catch (Exception e) {
			String msg = e.getMessage();
			e.printStackTrace();
		}
	}

	private void showStatus(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();		
	}
	
	public Object getRowConfigurationInstance () {
		return rows;
	}
}
