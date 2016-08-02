package com.acs.mainmenu;

import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;

import com.acs.mainmenu.AcsDBAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.util.Date;


public class GetDeliveries extends Activity {
	

	private static final String ALLDISP = "/AllUndispatchedInvoicesXtended";
	private static final String DRVRDISP = "/GetDispatchesByDriverNameXtended";
	//private static final String WARNDISP = "/GetWarrenDeliveries";
	private static final String URL = "http://srv.epartconnection.com/DispatchWebService/Service.asmx";
	private static final int DIALOG_PROGRESS_ID = 0;
	ProgressDialog pd;

	TextView tv;
	int cnt = 0;
	int actionitems = 0;
	String savestore;
	String driver;
	String driverId;
	String multiSign;
	String errMsg;
	SharedPreferences prefs;
	AcsDBAdapter adb;
	
    @Override
    public void onDestroy() {
   	 super.onDestroy();
   	 adb.close();
   	 if(pd.isShowing())
   		 pd.dismiss();
    }

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		if(id == DIALOG_PROGRESS_ID)
			// circular is always indeterminate
			((ProgressDialog)dialog).setIndeterminate(true);
			
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if(id == DIALOG_PROGRESS_ID) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("In Progress");
			dialog.setTitle("DownLoading");
			dialog.setCancelable(false);
			dialog.setIndeterminate(true);
			return dialog;
		}
		return null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getdeliveries);

		adb = new AcsDBAdapter(this);
		prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
		savestore = prefs.getString("storeid", "demo");
		driver = prefs.getString("drivername", "gorak");
		driverId = prefs.getString("driverId", "true");
		multiSign = prefs.getString("multiSign", "true");
		//driver = driver.trim();
		driver = driver.replaceAll(" ", "+");

    	pd = (ProgressDialog)this.onCreateDialog(DIALOG_PROGRESS_ID);
    	pd.setOwnerActivity(this);
    	this.pd.show();

		new asyncTaskUpdater().execute();
	}
    
	private String convertIstoStr(InputStream is) throws IOException {
		if(is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		}
		return "";
	}
	
	private InputStream getInputStream(String acsurl) throws IOException
	{
		HttpURLConnection con = null;
		InputStream is = null;
		if(driverId.equals("true")){
			acsurl = acsurl + DRVRDISP + "?epartId=" + savestore + "&driver=" + driver;
		} else
			acsurl = acsurl + ALLDISP + "?EpartId=" + savestore;
		URL url = new URL(acsurl);
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			if(driverId.equals("true")) {
				con.addRequestProperty("epartId", savestore);
				con.addRequestProperty("driver", driver);
			} else
				con.addRequestProperty("EpartId", savestore);				
			con.connect();
			is = con.getInputStream();
		}catch (Exception e) {
			e.printStackTrace();
			errMsg = e.getMessage();
		}
		/*
		finally {
			if(is != null) {
				is.close();
			}
		}
		*/
		return is;
	}
	
	private void do_work(){
    	InputStream is = null;
    	try {
    		is = getInputStream(URL);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		Toast.makeText(GetDeliveries.this,"Unable to connect", Toast.LENGTH_LONG).show();
    		Intent Data = new Intent();
    		Data.putExtra("DeliveryCount" , cnt);
    		setResult(RESULT_CANCELED, Data);
    		finish();
    		String junk = e.getMessage();
    		e.printStackTrace();
    	}
    	String results = "";
    	try {
    		results = convertIstoStr(is);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		Toast.makeText(GetDeliveries.this,"Unable to convert input data", Toast.LENGTH_LONG).show();
    		Intent Data = new Intent();
    		Data.putExtra("DeliveryCount" , cnt);
    		setResult(RESULT_CANCELED, Data);
    		finish();
    		e.printStackTrace();
    	}
    	if (results != "") {
    		cleardb();
    		cnt = parseResults(results);
    	}
	}

	
	private int parseResults(String results){
		int lcnt = 0;
		int pcnt = 0;
		
		String pres[] = results.split("\n");
		for (String lc : pres)
		{
			if(pres[lcnt].startsWith("<")) { lcnt++; continue; }
			String pline[] = lc.split("\\|"); 
			addtoDb(pline);
			pcnt++;
			lcnt++;
		}
		return pcnt;
	}
	
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("") || str.trim().equals("");
	}
	
	private void addtoDb(String[] entry){
		String[] newentry = new String[AcsDBAdapter.RECORDSZ];
		for(int i=0; i < AcsDBAdapter.RECORDSZ; i ++)
			newentry[i] = null;
		int len = entry.length;
    	String startime = DateFormat.getDateTimeInstance().format(new Date());
    	newentry[AcsDBAdapter.STORENAME_COLUMN] = savestore;
		newentry[AcsDBAdapter.DRIVER_COLUMN] = driver;
		for (int i = 3, j=0; j < len; i++) 
		{
			if((i == AcsDBAdapter.SIGFILE_COLUMN) || (i== AcsDBAdapter.SIGDATE_COLUMN)) continue; // skip the signature and the signature date
			try {
				newentry[i] = entry[j++];				
			}
			catch (Exception e){
				String wtf = e.getMessage();
				e.printStackTrace();
			}
		}
		if (newentry[AcsDBAdapter.CUST_COLUMN].contains("&amp;"))
			newentry[AcsDBAdapter.CUST_COLUMN] = newentry[AcsDBAdapter.CUST_COLUMN].replaceAll("&amp;", "&"); // strip the crap if present

		if (!isNullOrEmpty(newentry[AcsDBAdapter.ACTION_COLUMN])) {
			newentry[AcsDBAdapter.TYPE_COLUMN] = "ACTION";
			if(isNullOrEmpty(newentry[AcsDBAdapter.INVOICE_COLUMN])) {
				// actionitems is an int so its Item1, Item2 etc .....
				newentry[AcsDBAdapter.INVOICE_COLUMN] = "Item" + ++actionitems;
			}
		}
		// this is the "printed time" but now it is the downloaded time or "start" time
		newentry[AcsDBAdapter.START_COLUMN] = startime;
		
		if(checkentry(newentry) < 0) {
			try 
			{
				adb.insertEntry(newentry);
			}
			catch(Exception e) {
				Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
				String msg = e.getMessage();
				e.printStackTrace();
			}			
		}
	}
	
	private int checkentry(String[] entry){
		int ndx = -1;
		String invno = entry[AcsDBAdapter.INVOICE_COLUMN];
		ndx = adb.getRowIndex(invno);
		return ndx;
	}
	
	private void cleardb(){
		int ndx = -1;
		Cursor row = adb.getAllEntries();
		if(row.moveToFirst()){
			do {
				String invno = row.getString(AcsDBAdapter.INVOICE_COLUMN);
				ndx = adb.getRowIndex(invno);
				adb.removeEntry(ndx);
			} while(row.moveToNext());
		}
		row.close();
	}

	public class asyncTaskUpdater extends AsyncTask <Void, Void, Void> 
	{
        @Override
        protected void onPostExecute(Void result) {
         // TODO Auto-generated method stub
    		Intent Data = new Intent();
    		Data.putExtra("DeliveryCount" , cnt);
    		setResult(RESULT_OK, Data);
    		pd.dismiss();
    		try {
        		finish();    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
       
        @Override
        protected void onPreExecute() {
         // TODO Auto-generated method stub
        	adb.open();
        }
       
        protected void onProgressUpdate(int[] values) {
         // TODO Auto-generated method stub
        }
       
        @Override
        protected Void doInBackground(Void... arg0) {
         // TODO Auto-generated method stub
        	do_work();
        	return null;
        }
    }	
}