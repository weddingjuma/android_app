package com.acs.mainmenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.acs.mainmenu.AcsDBAdapter;
import com.acs.mainmenu.acsLocationListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.location.*;

public class GetSignature extends Activity {
	String cust;
	String invno; // what we display 
	String finvno; // what we use for filename and search 
	String invamt;
	String multiSign;
	String menu;
	String fileName;
	SharedPreferences prefs;
	String Path;
	String fname;
	ImageView img;
	AcsDBAdapter adb;
	double longitude = 0.0000;
	double latitude = 0.0000;

	
	private static final String ReservedChars = "|\\?&%*<\":>+[]/'";

	private static final int CAPTURE_REQUEST_CODE = 0;
	
	/**
	 * 
	 */
	private void startCaptureActivity() {
		
		Intent intent = new Intent(this, SignatureActivity.class);

		String keyFileName    = "signature.FileName";
		String keyTitle       = "signature.Title";
		String keyCustomer     = "signature.Cust";
		String keyAmt         = "signature.Amt";
		String keyInvNo       = "signature.InvNo";
				
		// just in case of Warren multisign issue because invo has several invoice no.s 
		if(multiSign.equals("true")) {
			String tmp [] = invno.split("\n");
			finvno= tmp[0];
		} else
			finvno = invno;
		fileName    = cust + finvno + ".png";    // set the file name (global write permissions required)
		fileName = parseFileName(fileName);
		fname = fileName;
        Path = Environment.getExternalStorageDirectory().toString();
        fileName = Path + "/" + fileName;
		String  title       = "eDelivery Signature";    // optional, default is set in AndroidManifest.xml

		intent.putExtra(keyFileName, fileName);
		intent.putExtra(keyTitle, title);
		intent.putExtra(keyCustomer, cust);
		intent.putExtra(keyAmt,  invamt);
		intent.putExtra(keyInvNo, invno);
		
		startActivityForResult(intent, CAPTURE_REQUEST_CODE);
	}

    @Override
    public void onConfigurationChanged(final Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
    }

	@Override 
	public void onDestroy(){
		adb.close();
		super.onDestroy();
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
    	cust = prefs.getString("cust", "cust");
    	invno = prefs.getString("invno", "1111");
    	invamt = prefs.getString("invamt", "11.11");
    	multiSign = prefs.getString("multiSign", "");
        
		adb = new AcsDBAdapter(this);
		adb.open();
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
    	try{
            startCaptureActivity();        	
        } catch (Exception e) {
        	String msg = e.getMessage().toString();
        	e.printStackTrace();
        }
}
    
    @Override
    protected void onActivityResult
        (
             int    requestCode,
             int    resultCode,
             Intent data
        ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            	done();
                // img.setImageBitmap(bitmap);
                
            } else {
            	cancel();
            }
        }
    }

    private void cancel() {
    	prefs = getSharedPreferences("cheapoIpc", MODE_PRIVATE);
    	Editor ed = prefs.edit();
    	ed.putInt("keypress", -1).commit();
    	finish();
    }
    private void done() {
    	if (isEmpty(fileName))
    		finish();
    	String signtime = DateFormat.getDateTimeInstance().format(new Date());
    	Cursor cur = null;
    	String custnum;
    	String[] res = null;
		long ndx = adb.getRowIndex(finvno);
		if(ndx > -1) {
			res = (String[])adb.getEntry(ndx);
			custnum = res[AcsDBAdapter.CUSTNUM_COLUMN];
	    	if (multiSign.equals("true")) {
	    		try {
	    			cur = adb.getCustNum(custnum);
	    		}
	    		catch(Exception e) {
	    			String msg = e.getMessage();
	    			e.printStackTrace();
	    		}
	    		if(cur.moveToFirst()) {
	    			do {
	    				int id = cur.getInt(AcsDBAdapter.ID_COLUMN);
	    				res = (String[])adb.getEntry(id);
	    				res[AcsDBAdapter.SIGFILE_COLUMN] = fileName;
	    				res[AcsDBAdapter.SIGDATE_COLUMN] = signtime;
	    				res[AcsDBAdapter.LAT_COLUMN] = Double.toString(latitude);
	    				res[AcsDBAdapter.LONG_COLUMN] = Double.toString(longitude);
	    				try{
	    					adb.updateEntry(id,res);    			
	    				} catch (Exception e) {
	    					e.printStackTrace();
	    				}	    				
	    			} while(cur.moveToNext());
	    		}
    			cur.close();
	    	} else {
				res[AcsDBAdapter.SIGFILE_COLUMN] = fileName;
				res[AcsDBAdapter.SIGDATE_COLUMN] = signtime;
				res[AcsDBAdapter.LAT_COLUMN] = Double.toString(latitude);
				res[AcsDBAdapter.LONG_COLUMN] = Double.toString(longitude);
				try{
					adb.updateEntry(ndx,res);    			
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
			
		}
    	prefs = getSharedPreferences("cheapoIpc", MODE_PRIVATE);
    	Editor ed = prefs.edit();
    	ed.putInt("keypress", 0).commit();
    	finish();
    }
        
    private static boolean isEmpty(String str)
    {
    	return str == null || str.length() == 0;
    }
    
    private String parseFileName(String fname) {
    	String result = fname;
    	for(int i = 0; i <  ReservedChars.length(); i++) {
    		char c = ReservedChars.charAt(i);
    		result = result.replace(c, ' ');
    	}
    	result = result.replaceAll(" ", "");    	
    	return result;
    }
}
