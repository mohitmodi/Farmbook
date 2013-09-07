package com.mypackage;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mypackage.FarmbookActivity;
import com.utilitiespackage.CustomHttpClient;

public class Friends extends FarmbookActivity {

	private LinearLayout layout;
	private Button friends[]=null,addfriend=null;
	private String friendlist[]=null,frienddetails[][]=null;
	private int no_friends=0,no_added=0;
	private boolean friendsel[];

	private AlertDialog nofriends,confirmadd;
	private MediaPlayer mp_main,mp_confirm,mp_nofriends;
	private ImageButton pause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
		
		try {
			String response = CustomHttpClient.executeHttpPost("sln/viewfriends.php", postParameters);	
			
			if(!response.equals(NONE)) {
				friendlist = response.split("\\/+");
				no_friends = friendlist.length;
			}
			Log.i("Friends","Number of friends = "+no_friends);
				
    	} catch (Exception e) {
    		Log.e("Friends",e.toString());
    	}
		
		if(no_friends==0) {
			mp_nofriends = MediaPlayer.create(Friends.this, R.raw.no_suggestion);
	        mp_nofriends.start();
			
			nofriends = new AlertDialog.Builder(this)
			.setTitle("No Potential Friends")
			.setMessage("You have added all the people in your locality as friends")
			.setPositiveButton("OK", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create();
			nofriends.show();
		}
		else {	
			setUpViews();
		}
	}
	
	private void setUpViews() {
		pause=(ImageButton)findViewById(R.id.friends_pause);
		mp_main = MediaPlayer.create(Friends.this, R.raw.add_friend);
		pause.setImageResource(R.drawable.pause_button);
        mp_main.start();
        mp_main.setOnCompletionListener(new OnCompletionListener(){
            public void onCompletion(MediaPlayer arg0) {
            	pause.setImageResource(R.drawable.play_button);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	if(mp_main.isPlaying()) {
            		mp_main.pause();
            		pause.setImageResource(R.drawable.play_button);
            	}
            	else {
            		mp_main.start();
            		pause.setImageResource(R.drawable.pause_button);
            	}
            }	
        });
        
		layout = (LinearLayout)findViewById(R.id.friends_linearlayout);
		friends = new Button[no_friends];
		friendsel = new boolean[no_friends];
		frienddetails = new String[no_friends][3];
		
		final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for(int i=0; i<no_friends; i++) {
			friendsel[i] = false;
			frienddetails[i] = friendlist[i].split("\\@+");
			
			friends[i] = (Button)inflater.inflate(R.layout.friendbutton, null);
			friends[i].setId(i);
			friends[i].setBackgroundResource(R.drawable.border_gray);
			friends[i].setText(frienddetails[i][1]+" "+frienddetails[i][2]+"\n"+frienddetails[i][0]);
			friends[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i=v.getId();
					
					friendsel[i] = !friendsel[i];
					
					if(friendsel[i]) {
						friends[i].setBackgroundResource(R.drawable.border_cyan);
						no_added++;
						Log.i("Friends","i = "+i+" no_added = "+no_added);
					}
					else {
						friends[i].setBackgroundResource(R.drawable.border_gray);
						no_added--;
						Log.i("Friends","i = "+i+" no_added = "+no_added);
					}
				}
			});
			
			layout.addView(friends[i]);
			
			TextView tv = new TextView(this); 
	        tv.setBackgroundColor(Color.TRANSPARENT);
	        tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
	        layout.addView(tv);
		}
		
		addfriend = new Button(this);
		addfriend.setText("Add Friends");
		addfriend.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addfriend.setBackgroundResource(R.drawable.border_green);
		addfriend.setGravity(Gravity.CENTER_HORIZONTAL);
		addfriend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirm();
			}
		});
		layout.addView(addfriend);
	}
	
	private void confirm() {
		mp_confirm = MediaPlayer.create(Friends.this, R.raw.confirmation);
        mp_confirm.start();
		
		confirmadd = new AlertDialog.Builder(this)
		.setTitle("Confirm Add Friends")
		.setMessage("You have selected "+no_added+" friends. Continue?")
		.setPositiveButton("OK", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(no_added>0)
					add();
				finish();
			}
		})
		.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				confirmadd.cancel();
			}
		})
		.create();
		confirmadd.show();
	}

	private void add() {
		String addedfriends="";
		
		for(int i=0; i<no_friends; i++)
			if(friendsel[i])
				addedfriends += frienddetails[i][0]+"/";
		
		addedfriends = addedfriends.substring(0, addedfriends.length()-1);
		Log.i("Friends","Friends added = "+addedfriends);
		
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
			postParameters.add(new BasicNameValuePair("friends", addedfriends));
			
			CustomHttpClient.executeHttpPost("sln/addfriends.php", postParameters);	
			Toast.makeText(getApplicationContext(), no_added+" friends added", Toast.LENGTH_LONG).show();				
    	} catch (Exception e) {
    		Log.e("Friends","Error while adding friends : "+e.toString());
    	}
	}
}

