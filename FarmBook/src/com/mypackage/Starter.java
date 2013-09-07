package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Starter extends FarmbookActivity {
	private static final int REGISTER_CODE = 1;
	private String phonenumber;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
    }
	
	@Override
	protected void onRestart() {
		super.onRestart();
		start();
	}

	private void start() {
		if(!numberExists()) {
        	Intent intent = new Intent(Starter.this, Register.class);
        	startActivityForResult(intent, REGISTER_CODE);
        }
        else {
        	Intent intent = new Intent(Starter.this, Home.class);
        	startActivity(intent);
        }
	}

	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("Starter", "Reqcode = "+requestCode+" ResultCode = "+resultCode+" reqd = "+Activity.RESULT_OK);
		switch(requestCode) {
			case REGISTER_CODE:
				if(resultCode == Activity.RESULT_OK) {
					Log.i("Starter", "Reqcode = "+requestCode+" ResultCode = "+resultCode+" reqd = "+Activity.RESULT_OK);
					Intent intent = new Intent(Starter.this, Home.class);
		        	startActivity(intent);
				}
		}
	}
	
	private boolean numberExists() {
		phonenumber=getStuffApplication().getPhonenumber();
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", phonenumber));
		
    	try {
    		Log.i("Starter","Running check");
    		String response = CustomHttpClient.executeHttpPost("sln/check.php", postParameters);
    		Log.i("Starter","Check response = "+response);
    		
    		if(response.equals(NONE)) {
    	    	return false;
    	    }
    	    String details[] = response.split("\\@+");
        	getStuffApplication().setFirstname(details[0]);
        	getStuffApplication().setLastname(details[1]);
        	getStuffApplication().setLocation(details[2]);
        	getStuffApplication().setSex(details[3]);
    	} catch (Exception e) {
    		Log.e("MyProjectActivity",e.toString());
    	}
		
    	return true;
	}
}
