package com.acs.mainmenu;

import java.util.ArrayList;

import com.acs.mainmenu.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SignatureActivity extends Activity {
	SignatureView drawView;
	
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.signature);
	}
	*/
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature);
        // get the widgets
        FrameLayout Frame = (FrameLayout) findViewById(R.id.SigFrame);
        Button btnClear = (Button) findViewById(R.id.btnClear);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnDone = (Button) findViewById(R.id.btnDone);
        // set the colors if we must ... what is this the "HotDog Stand" ?  
        btnClear.getBackground().setColorFilter(0xffffff00, PorterDuff.Mode.MULTIPLY);  // yellow
        btnCancel.getBackground().setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY); // red
        btnDone.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY); // green
        TextView txtCust = (TextView) findViewById(R.id.CustName);
        TextView txtInvNo = (TextView) findViewById(R.id.InvNum);
        TextView txtInvAmt = (TextView) findViewById(R.id.InvAmt);
        
        Intent sender = getIntent();
        
        final String FileName    = sender.getExtras().getString("signature.FileName");
		String Title       = sender.getExtras().getString("signature.Title");
		String Customer    = sender.getExtras().getString("signature.Cust");
		String Amt         = sender.getExtras().getString("signature.Amt");
		String InvNo       = sender.getExtras().getString("signature.InvNo");
		
		txtCust.setText(Customer);
		txtInvNo.setText(InvNo);
		txtInvAmt.setText(Amt);
		
        //final ImageView img = (ImageView)findViewById(R.id.view1);
        drawView = new SignatureView(this);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(600, 600);
        Frame.addView(drawView, RESULT_OK, lp);
        // set the listeners
        btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawView.clearScreen();
			}
		});
        btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent retIntent = new Intent();
				setResult(RESULT_CANCELED, retIntent);
				finish();
			}
		});
        btnDone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawView.savePngFile(FileName);
                // Bitmap bitmap = BitmapFactory.decodeFile(fname);
                
                // img.setImageBitmap(bitmap);
    			Intent retIntent = new Intent();
    			setResult(RESULT_OK, retIntent);
    			finish();
			}

		});
        drawView.requestFocus();
        }
}