package com.acs.mainmenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogIn extends Activity {
	private EditText username;
	private EditText password;
	private Button login;
	private TextView loginLockedTV;
	private TextView attemptsLeftTV;
	private TextView numberOfRemainingLoginAttemptsTV;
	int numberOfRemainingLoginAttempts = 3;
	private String user = "admin";
	private String userpw = "admin";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setupVariables();
	}


	public void authenticateLogin(View view) {

		try {
			SharedPreferences prefs = getSharedPreferences("acsetup", MODE_PRIVATE);
			user = prefs.getString("administrator", "admin");
			userpw = prefs.getString("password", "admin");		
		} catch (Exception e) {
		}	

		if (username.getText().toString().equals(user) && 
				password.getText().toString().equals(userpw)) {
				setUpProgram();
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Invalid Credentials!", 
				Toast.LENGTH_SHORT).show();
				numberOfRemainingLoginAttempts--;
				attemptsLeftTV.setVisibility(View.VISIBLE);
				numberOfRemainingLoginAttemptsTV.setVisibility(View.VISIBLE);
				numberOfRemainingLoginAttemptsTV.setText(Integer.toString(numberOfRemainingLoginAttempts));
				
				if (numberOfRemainingLoginAttempts == 0) {
					login.setEnabled(false);
					loginLockedTV.setVisibility(View.VISIBLE);
					loginLockedTV.setBackgroundColor(Color.RED);
					loginLockedTV.setText("LOGIN LOCKED!!!");
				}
			}
		}

	
	private void setUpProgram() {
	    Intent setup = new Intent(this, SetUp.class);
	    startActivity(setup);		
	}
	private void setupVariables() {
		username = (EditText) findViewById(R.id.usernameET);
		password = (EditText) findViewById(R.id.passwordET);
		login = (Button) findViewById(R.id.loginBtn);
		loginLockedTV = (TextView) findViewById(R.id.loginLockedTV);
		attemptsLeftTV = (TextView) findViewById(R.id.attemptsLeftTV);
		numberOfRemainingLoginAttemptsTV = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
		numberOfRemainingLoginAttemptsTV.setText(Integer.toString(numberOfRemainingLoginAttempts));
	}
		
}
