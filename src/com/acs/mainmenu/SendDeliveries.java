package com.acs.mainmenu;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import com.acs.mainmenu.AcsDBAdapter;
import com.acs.mainmenu.GetDeliveries.asyncTaskUpdater;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;

public class SendDeliveries extends Activity {

	Cursor row;
	AcsDBAdapter adb;
	List<NameValuePair> nvp;
	//private static final String ACTIONURL = "http://192.168.2.46/DispatchWebService/Service.asmx/UpdateActionItem";
	//private static final String IMAGEURL = "http://192.168.2.46/DispatchWebService/Service.asmx/saveSignedImageXtended";
	private static final String ACTIONURL = "http://srv.epartconnection.com/DispatchWebService/Service.asmx/UpdateActionItem";
	private static final String IMAGEURL = "http://srv.epartconnection.com/DispatchWebService/Service.asmx/saveSignedImageXtended";
	//private static final String IMAGEURL = "http://10.0.2.2:52602/DispatcherService/Service.asmx/saveSignedImageXtended";
	int cnt = 0;
	int actionflag = 0;
	SharedPreferences prefs;
	String multiSign;
	String clearSend;
	private static final int DIALOG_PROGRESS_ID = 0;
	ProgressDialog pd;
	Hashtable<String,String> sigPairs = new Hashtable<String,String>();

	@Override 
	public void onDestroy(){
		super.onDestroy();
		adb.close();
		if(pd != null && pd.isShowing()) {
			pd.dismiss();
		}
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
			dialog.setTitle("UpLoading");
			dialog.setCancelable(false);
			dialog.setIndeterminate(true);
			return dialog;
		}
		return null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendeliveries);

		final TextView txtDummy  = (TextView) findViewById(R.id.textView1);
    	prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
    	multiSign = prefs.getString("multiSign", "");
    	clearSend = prefs.getString("clearSend", "");
    	adb = new AcsDBAdapter(this);
    	pd = (ProgressDialog)this.onCreateDialog(DIALOG_PROGRESS_ID);
    	pd.setOwnerActivity(this);
    	this.pd.show();

		new asyncTaskUpdater().execute();    
	}
	 
	private void do_work() {
		row = adb.getAllEntries();
		String Store;
		String Inv;
		String fname;
		String Loc;
		String sigData=null;
		String sDate;
		String bDate;
		String lat;
		String lng;
		String driver;
		String action;
		String dispatch;
		String custnum;
		
		InputStream Is = null;
		List<NameValuePair> mvp = new ArrayList<NameValuePair>(10);
		List<NameValuePair> nvp = new ArrayList<NameValuePair>(6);
		if(row.moveToFirst()) {
			do{
				fname = row.getString(AcsDBAdapter.SIGFILE_COLUMN);
				action = row.getString(AcsDBAdapter.ACTION_COLUMN);
				custnum = row.getString(AcsDBAdapter.CUSTNUM_COLUMN);
				if (!isNullOrEmpty(fname)) {
					sigData = prepData(fname, custnum);
					dumpdata(sigData);
				}
				else
					if(isNullOrEmpty(action)) {						
						// option to "clear" (remove) records w/o signature on send
						if(clearSend.equals("true")) {
							Inv = row.getString(AcsDBAdapter.INVOICE_COLUMN);
							delRecord(Inv);
						}
						continue;
					}
				Store = row.getString(AcsDBAdapter.STORENAME_COLUMN);
				Inv = row.getString(AcsDBAdapter.INVOICE_COLUMN);
				Loc = row.getString(AcsDBAdapter.STORENO_COLUMN);
				bDate = row.getString(AcsDBAdapter.START_COLUMN);
				sDate = row.getString(AcsDBAdapter.SIGDATE_COLUMN);
				lat = row.getString(AcsDBAdapter.LAT_COLUMN);
				lng = row.getString(AcsDBAdapter.LONG_COLUMN);
				driver = row.getString(AcsDBAdapter.DRIVER_COLUMN);
				if(driver == null) driver ="";
				dispatch = row.getString(AcsDBAdapter.DISPATCH_COLUMN);
				if(dispatch == null) dispatch = "";
				if(!isNullOrEmpty(sigData)) {
					mvp.add(new BasicNameValuePair("storeId", Store));
					mvp.add(new BasicNameValuePair("invoiceNumber", Inv));
					mvp.add(new BasicNameValuePair("Location", Loc));
					mvp.add(new BasicNameValuePair("signDate", sDate));
					mvp.add(new BasicNameValuePair("startDate", bDate));
					mvp.add(new BasicNameValuePair("base64ImageData", sigData));
					mvp.add(new BasicNameValuePair("latitude", lat));
					mvp.add(new BasicNameValuePair("longitude", lng));
					mvp.add(new BasicNameValuePair("driver", driver));
					mvp.add(new BasicNameValuePair("dispatch", dispatch));
				} else {
					actionflag = 1;
					nvp.add(new BasicNameValuePair("location", Loc));
					nvp.add(new BasicNameValuePair("eventdate", sDate));
					nvp.add(new BasicNameValuePair("latitude", lat));
					nvp.add(new BasicNameValuePair("longitude", lng));
					nvp.add(new BasicNameValuePair("action", action));
					nvp.add(new BasicNameValuePair("dispatch", dispatch));
				}
				Is = postData((actionflag == 1) ? nvp : mvp);
				if(Is != null){
					postResults(Is);
					if(delRecord(Inv) > 0) {
						try{
							new File(fname).delete();							
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					cnt++;
				}
				mvp.clear();
				nvp.clear();				
			} while(row.moveToNext());
			row.close();
		}
		Intent Data = new Intent();
		Data.putExtra("SentCount" , cnt);
		setResult(RESULT_OK, Data);
	}
	
	private void dumpdata(String data)
	{
		File root = android.os.Environment.getExternalStorageDirectory();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(root.getAbsolutePath() + "/signature.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.print(data);
		pw.flush();
		pw.close();
	}

	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("") || str.trim().equals("");
	}

	private void showStatus() {
		String msg = "Signatures sent succesfuly " + cnt;
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();		
	}

	private InputStream postData(List<NameValuePair> nvp){
		HttpClient htpc = new DefaultHttpClient();
		String URL = actionflag == 1 ? ACTIONURL : IMAGEURL;
		actionflag = 0;
		HttpPost htpp = new HttpPost(URL);
		htpp.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		htpp.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		InputStream ins = null;
		
		try {
			htpp.setEntity(new UrlEncodedFormEntity(nvp));
			HttpResponse rsp = htpc.execute(htpp);
			ins = rsp.getEntity().getContent();
		} catch (Exception e) {
			String msg = e.getMessage();
			Toast.makeText(SendDeliveries.this,"Unable to connect", Toast.LENGTH_LONG).show();
			Intent Data = new Intent();
			Data.putExtra("SentCount" , cnt);
			setResult(RESULT_CANCELED, Data);
			//finish();			
			e.printStackTrace();
		}
		
		return ins;			
	}
	
	private String postResults(InputStream ins){
		if(ins != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			try {
				while ((line = br.readLine())!= null){
					sb.append(line).append("\n");
				}
				String msg = sb.toString();
			}catch (IOException e){
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ins.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return sb.toString();			
		}
		return "";
	}
	
	private String prepData(String fname, String custnum){
		Bitmap bitmap = null;
        String encoded = null;
        if(multiSign.equals("true")) {
        	if(!sigPairs.isEmpty()) {
        		encoded = (String) sigPairs.get(custnum);
        		if(!isNullOrEmpty(encoded))
        			return encoded;
        	}
        }
        try {
        	bitmap = BitmapFactory.decodeFile(fname);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        if(bitmap == null) {
        	return "";
        }
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);			
		byte [] ba = baos.toByteArray();
        encoded = Base64.encodeToString(ba, 0); // default encoder flag URL "safe" is 8
        try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(encoded == null) {
        	return "";
        }
        if(multiSign.equals("true")) {
        	String tmp = sigPairs.get(custnum);
        	if(isNullOrEmpty(tmp))
        		sigPairs.put(custnum, encoded);
        }
        return encoded;
	}
	
	private int delRecord(String invno) {
		int result = 0;
    	long ndx = adb.getRowIndex(invno);
    	if(ndx > -1) {
        	String[] res = (String[])adb.getEntry(ndx);
    		try{
    			if(adb.removeEntry(ndx)) result = 1;    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}

		return result;
	}

	public class asyncTaskUpdater extends AsyncTask <Void, Void, Void>
	{
        @Override
        protected void onPostExecute(Void result) {
         // TODO Auto-generated method stub
    		Intent Data = new Intent();
    		Data.putExtra("SentCount" , cnt);
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
