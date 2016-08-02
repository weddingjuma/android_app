package com.acs.mainmenu;

import com.acs.mainmenu.AcsDBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.*;

public class AcsMainMenuActivity extends Activity {
    /** Called when the activity is first created. */
	
	public static final int GET_COUNT = 1;
	public static final int SENT_COUNT = 2;

	public AcsDBAdapter adb = new AcsDBAdapter(this);

    @Override
    public void onDestroy() {
   	 super.onDestroy();
   	 adb.close();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        adb.open();
        Button getDlvr = (Button) findViewById(R.id.getdeliver);
        Button getList = (Button) findViewById(R.id.listdeliver);
        Button sendInv = (Button) findViewById(R.id.sendinvoice);
        Button setUp = (Button) findViewById(R.id.setup);
        //Button exitProg = (Button) findViewById(R.id.exit);
        getDlvr.getBackground().setColorFilter(0xffffff33, PorterDuff.Mode.MULTIPLY);
        getList.getBackground().setColorFilter(0xff3333ff, PorterDuff.Mode.MULTIPLY);
        sendInv.getBackground().setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
        setUp.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
        //exitProg.getBackground().setColorFilter(0xff00ff33, PorterDuff.Mode.MULTIPLY);


        getDlvr.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDeliveries();
			}
		});

        getList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//android.util.Log.w("mayApp", "start");
				listInvoices();
			}
		});
        
        sendInv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendInvoices();
			}
		});
        
        setUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLogin();
			}
		});
        /*
        exitProg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		*/
    }
    
    private void getDeliveries() {
		if(adb.checkSignatures() != 0) {
			Toast.makeText(this, "Upload/Clear Existing Signatures", Toast.LENGTH_LONG).show();
		} else {
			Intent getDel = new Intent(this, com.acs.mainmenu.GetDeliveries.class);
			startActivityForResult(getDel, GET_COUNT);
		}
    }
    
    private void listInvoices() {
    	Intent listDel = new Intent(this,MultiList.class);
    	startActivity(listDel);
    }
    private void showStatus(String msg) {
		// TODO Auto-generated method stub
    	Toast.makeText(this, msg, Toast.LENGTH_LONG).show();	
	}

	private void sendInvoices() {
    	Intent sendDel = new Intent(this, SendDeliveries.class);
    	startActivityForResult(sendDel, SENT_COUNT);
    }
	private void getLogin() {
		Intent logIn = new Intent(this, LogIn.class);
		startActivity(logIn);
	}
    
    protected void onActivityResult(int requestcode, int resultcode, Intent Data)
    {
    	int cnt = 0;
    	String msg="";
    	switch(requestcode) {
    	case GET_COUNT:
    		if(resultcode == RESULT_OK) {
    			cnt = Data.getIntExtra("DeliveryCount", 0);
    			msg = "Invoices to Deliver: " + cnt;
    		} else 
    			msg = "Error Getting Invoices";
			break;
    		
    	case SENT_COUNT:
       		if(resultcode == RESULT_OK) {
    			cnt = Data.getIntExtra("SentCount", 0);
    			msg = "Invoices Sent: " + cnt;
    		} else
    			msg = "Error Sending Invoices";
			break;
    	default:
    		msg = "Unknown Intent";
    	}
    	if(msg != "")
    		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }    
}