package com.acs.mainmenu;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.acs.mainmenu.AcsDBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;


public class ShowList extends Activity {
	Cursor row;
	AcsDBAdapter adb;
	final ArrayList<String> dl = new ArrayList<String>();
	ArrayAdapter<String> da;
	String fileName;
	ListView lv;
	TextView tv;
	
	private static final int CAPTURE_REQUEST_CODE = 0;
	
	@Override 
	public void onDestroy(){
		adb.close();
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showlist);
		lv = (ListView)findViewById(R.id.view1);
		tv = (TextView)findViewById(R.id.TextView1);
		
		lv.setClickable(true);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				Object o = lv.getItemAtPosition(pos);
				String results = o.toString();
				if (checksignature(results) < 0)
					return;
				getsignature(results);
				av.getChildAt(pos).setBackgroundColor(Color.BLUE);
			}
		}); 
		
		adb = new AcsDBAdapter(this);
		adb.open();
		da = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dl);
		buildList();
		lv.setAdapter(da);
		
		
		Button btnEnd = (Button) findViewById(R.id.btnExit);
        btnEnd.getBackground().setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);		
		btnEnd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	protected void onActivityResult(int request, int result, Intent data){
		/*
		da.clear();
		buildList();
		lv.setAdapter(da);
		*/
	}
	private void buildList(){
		row = adb.getAllEntries();
		String Cust;
		String Inv;
		String Amt;
		String Type;
		String fname;
		if(row.moveToFirst()) {
			do{
				fname = row.getString(AcsDBAdapter.SIGFILE_COLUMN);
				Cust = row.getString(AcsDBAdapter.CUST_COLUMN);
				Inv = row.getString(AcsDBAdapter.INVOICE_COLUMN);
				Amt = row.getString(AcsDBAdapter.AMT_COLUMN);
				Type = row.getString(AcsDBAdapter.TYPE_COLUMN);
				if (fname != null)
					dl.add(Cust + ": " + Inv + ": " + "***SIGNED***");
				else
					dl.add(Cust + ": " + Inv + ": " + Amt + ":" + Type);
			} while(row.moveToNext());
		}
		row.close();
		lv.setAdapter(da);
	}
	
	private int checksignature(String res){
		String[] parms = res.split(":");
		String invno = parms[1];
		invno = invno.replaceAll(":", "");
		long ndx = adb.getRowIndex(invno);
    	if(ndx > -1) {
        	String[] rsp = (String[])adb.getEntry(ndx);
        	String sig = rsp[AcsDBAdapter.SIGFILE_COLUMN];
        	if((sig != null) && (sig.length() > 0))
			{
				showStatus("Already Signed");
				return -1;
			}
    	}
    	return 0;
	}

	private void getsignature(String res){
		String[] parms = res.split(":");
		String cust = parms[0];
		String invno = parms[1];
		cust = cust.replaceAll(":", "");
		invno = invno.replaceAll(":", "");
		int requestcode = 1;

		SharedPreferences prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
		Editor med = prefs.edit();

		med.putString("cust", cust);
		med.putString("invno", invno);
		med.commit();

		Intent getsig = new Intent(this, GetSignature.class);
		try {
			startActivityForResult(getsig, requestcode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showStatus(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();		
	}

}
