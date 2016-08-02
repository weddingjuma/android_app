package com.acs.mainmenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.acs.mainmenu.AcsDBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class SetUp extends Activity {
	
	Cursor dbrow;
	AcsDBAdapter adb;

	private static final String URL = "http://srv.epartconnection.com/DispatchWebService/Service.asmx";
	private static final String VERIFYID = "/verifyEpartId";
	private static final String VERIFYDRIVE = "/verifyDriver";
	private static CharSequence storeId ="";
	private static CharSequence driverName = "";
	private static CharSequence Admin ="";
	private static CharSequence adminPw = "";
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		adb = new AcsDBAdapter(this);

		SharedPreferences prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
		final Editor med = prefs.edit();

		final String driverId;
		final String multiSign;
		final String reSign;
		final String clearSend;
		final String administrator;
		final String adminpw;
		String msg = "";
		
		final EditText txtStore = (EditText) findViewById(R.id.txtStore);
		final EditText txtDriver = (EditText) findViewById(R.id.txtDriver);
		final EditText txtAdmin = (EditText) findViewById(R.id.txtAdmin);
		final EditText txtAdminPw = (EditText) findViewById(R.id.txtPassword);
		final CheckBox chkId = (CheckBox) findViewById(R.id.checkId);
		final CheckBox chkSig = (CheckBox) findViewById(R.id.checkSign);
		final CheckBox chkReSign = (CheckBox) findViewById(R.id.reSign);
		final CheckBox chkMainMenu = (CheckBox) findViewById(R.id.mainMenu);
		final CheckBox chkClearSend = (CheckBox) findViewById(R.id.clearSend);
		final Context context = this;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = cm.getActiveNetworkInfo();
        if(mWifi != null && mWifi.isAvailable() && mWifi.isConnected())
        	msg = "Network Connected";
        else
        	msg = "No Network Connection Available";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

		chkId.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox) v).isChecked()) {
					chkId.setChecked(false);
					return true;
				} else {
					chkId.setChecked(false);
					return false;
				}				
			}
		});
		
		chkSig.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox) v).isChecked()) {
					chkSig.setChecked(false);
					return true;
				} else {
					chkSig.setChecked(false);
					return false;
				}				
			}
		});
		
		chkReSign.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox) v).isChecked()) {
					chkReSign.setChecked(false);
					return true;
				} else {
					chkReSign.setChecked(false);
					return false;
				}				
			}
		});
		
		chkMainMenu.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox) v).isChecked()) {
					chkMainMenu.setChecked(false);
					return true;
				} else {
					chkMainMenu.setChecked(false);
					return false;
				}				
				
			}
		});

		chkClearSend.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(((CheckBox) v).isChecked()) {
					chkClearSend.setChecked(false);
					return true;
				} else {
					chkClearSend.setChecked(false);
					return false;
				}				
			}
		});

		final String savestore = prefs.getString("storeid", "");
		final String savedriver = prefs.getString("drivername","");
		final String saveadmin = prefs.getString("administrator","");
		final String savepw = prefs.getString("password","");
		final String saveId = prefs.getString("driverId", "");
		final String saveMulti = prefs.getString("multiSign", "");
		final String saveReSign = prefs.getString("reSign", "");
		final String saveMainMenu = prefs.getString("mainMenu", "");
		final String saveClearSend = prefs.getString("clearSend", "");

		if(!savestore.equals(""))
			txtStore.setText(savestore);
		if(!savedriver.equals(""))
			txtDriver.setText(savedriver);
		if(!saveadmin.equals(""))
			txtAdmin.setText(saveadmin);
		if(!savepw.equals(""))
			txtAdminPw.setText(savepw);
		if(saveId.equals("true"))
			chkId.setChecked(true);
		if(saveMulti.equals("true"))
			chkSig.setChecked(true);
		if(saveReSign.equals("true"))
			chkReSign.setChecked(true);
		if(saveMainMenu.equals("true"))
			chkMainMenu.setChecked(true);
		if(saveClearSend.equals("true"))
			chkClearSend.setChecked(true);
		storeId = txtStore.getText();
		driverName = txtDriver.getText();
		Admin = txtAdmin.getText();
		adminPw = txtAdminPw.getText();

		Button btnSave = (Button) findViewById(R.id.save_data);		
        btnSave.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				storeId = txtStore.getText();
				driverName = txtDriver.getText();
				Admin = txtAdmin.getText();
				adminPw = txtAdminPw.getText();
				med.putString("storeid", storeId.toString());
				med.putString("drivername", driverName.toString());
				if(!Admin.equals(""))
					med.putString("administrator", Admin.toString());
				if(!adminPw.equals(""))
					med.putString("password", adminPw.toString());
				if(chkId.isChecked()) 
					med.putString("driverId", "true");
				else
					med.putString("driverId", "false");
				if(chkSig.isChecked()) 
					med.putString("multiSign", "true");
				else
					med.putString("multiSign", "false");
				if(chkReSign.isChecked()) 
					med.putString("reSign", "true");
				else
					med.putString("reSign", "false");
				if(chkMainMenu.isChecked()) 
					med.putString("mainMenu", "true");
				else
					med.putString("mainMenu", "false");
				if(chkClearSend.isChecked()) 
					med.putString("clearSend", "true");
				else
					med.putString("clearSend", "false");
				med.commit();
				finish();
			}
		});

		Button btnTest = (Button) findViewById(R.id.test_connect);
        btnTest.getBackground().setColorFilter(0xffffff33, PorterDuff.Mode.MULTIPLY);

        btnTest.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeId = txtStore.getText();
				String testId = storeId.toString();
				testStoreId(testId);
			}			
		});
		
		Button btnDriver = (Button) findViewById(R.id.test_driver);
        btnDriver.getBackground().setColorFilter(0xffffff33, PorterDuff.Mode.MULTIPLY);

        btnDriver.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeId = txtStore.getText();
				driverName = txtDriver.getText();
				String testId = storeId.toString();
				String testdriver = driverName.toString();
				testDriver(testId, testdriver);
			}			
		});

        Button btnClear = (Button) findViewById(R.id.clear_data);
//        btnClear.getBackground().setColorFilter(0xffffff33, PorterDuff.Mode.MULTIPLY);
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adbuild = new AlertDialog.Builder(context);
				adbuild.setTitle("All deliveries will be lost!");
				adbuild
					.setInverseBackgroundForced(true)
					.setMessage("Are You Sure?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							adb.open();
							dbrow = adb.getAllEntries();
							if(dbrow.moveToFirst()) {
								do{
									String invno = dbrow.getString(AcsDBAdapter.INVOICE_COLUMN);
									long ndx = adb.getRowIndex(invno);
									adb.removeEntry(ndx);
								} while(dbrow.moveToNext());
							}
							dbrow.close();
							dbCleared("Records Cleared");
							adb.close();							
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
				AlertDialog alertdialog = adbuild.create();
				adbuild.show();
			}
		});
	}

	private void testStoreId(String Id) {
		HttpURLConnection con = null;
		InputStream is = null;
		String acsurl = URL + VERIFYID + "?storeId=" + Id; 
		URL url = null;
		try {
			url = new URL(acsurl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.addRequestProperty("storeId", Id);
			con.connect();
			is = con.getInputStream();
		}catch (Exception e) {
			String errMsg = e.getMessage();
			e.printStackTrace();
		}
		if(is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			showResults(sb.toString(), Id);
		}
	}
	
	private void testDriver(String Id, String Driver) {
		HttpURLConnection con = null;
		InputStream is = null;
		String acsurl = URL + VERIFYDRIVE + "?storeid=" + Id + "&driver=" + Driver; 
		URL url = null;
		try {
			url = new URL(acsurl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.addRequestProperty("storeid", Id);
			con.addRequestProperty("driver", Driver);
			con.connect();
			is = con.getInputStream();
		}catch (Exception e) {
			String errMsg = e.getMessage();
			e.printStackTrace();
		}
		if(is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			showResults(sb.toString(), Driver);
		}
	}
	
	private void showResults(String msg, String Id) {
		String results;
		if(msg.indexOf("Verified") != -1)
			results = new String("Verified: " + Id);
		else
			results = new String(Id +  " NOT FOUND ");
		Toast.makeText(this, results, Toast.LENGTH_LONG).show();
	}
	private void dbCleared(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	private void displayStrings() {
		Toast.makeText(this, storeId, Toast.LENGTH_SHORT).show();
		Toast.makeText(this, driverName, Toast.LENGTH_SHORT).show();
//		Toast.makeText(this, driverId, Toast.LENGTH_SHORT).show();
	}
}


