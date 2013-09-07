package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendProfile extends FarmbookActivity {
	
	private String friend_phoneno,friend_firstname,friend_lastname,friend_location;
	private Button add,cancel;
	private ImageView friend_profilepic_imageview;
	private TextView friend_phoneno_textview,friend_username_textview,friend_location_textview;
	private MediaPlayer mp_main;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendprofile);
		
		mp_main = MediaPlayer.create(FriendProfile.this, R.raw.connect_friend);
		mp_main.start();
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			friend_phoneno = extras.getString("friend_phoneno");
			friend_firstname = extras.getString("friend_firstname");
			friend_lastname = extras.getString("friend_lastname");
			friend_location = extras.getString("friend_location");
		}
		
		friend_profilepic_imageview = (ImageView)findViewById(R.id.friendprofile_profilepic);
		friend_profilepic_imageview.setImageBitmap(CustomHttpClient.downloadImage(friend_phoneno+"/profilepic.jpg"));
		friend_username_textview = (TextView)findViewById(R.id.friendprofile_name);
		friend_username_textview.setText(friend_firstname+" "+friend_lastname);
		friend_location_textview = (TextView)findViewById(R.id.friendprofile_location);
		friend_location_textview.setText(friend_location);
		friend_phoneno_textview = (TextView)findViewById(R.id.friendprofile_phoneno);
		friend_phoneno_textview.setText(friend_phoneno);
		
		add = (Button)findViewById(R.id.friendprofile_add);
        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                add();
                finish();
            }
        });
        cancel = (Button)findViewById(R.id.friendprofile_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }	
        });
	}


	private void add() {
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
			postParameters.add(new BasicNameValuePair("friends", friend_phoneno));
			
			CustomHttpClient.executeHttpPost("sln/addfriends.php", postParameters);	
			Toast.makeText(getApplicationContext(), "Friend added", Toast.LENGTH_LONG).show();				
    	} catch (Exception e) {
    		Log.e("FriendProfile","Error while adding friend : "+e.toString());
    	}
	}	
}
