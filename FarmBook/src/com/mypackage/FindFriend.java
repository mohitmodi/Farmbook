package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class FindFriend extends FarmbookActivity {

	private EditText phonenumber;
	private Button ok,cancel;
	private ImageButton pause;
	private AlertDialog nofriend;
	MediaPlayer mp_main,mp_nofriend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findfriend);

		pause=(ImageButton)findViewById(R.id.findfriend_pause);
		mp_main = MediaPlayer.create(FindFriend.this, R.raw.find_friend);
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

		phonenumber=(EditText)findViewById(R.id.findfriend_number);
		ok=(Button)findViewById(R.id.findfriend_ok);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showProfile();
			}	
		});
		cancel=(Button)findViewById(R.id.findfriend_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}	
		});
	}
	private void showProfile() {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
		postParameters.add(new BasicNameValuePair("friendno", phonenumber.getText().toString()));

		try {
			String response = CustomHttpClient.executeHttpPost("sln/findfriend.php", postParameters);

			if(response.equals(NONE)) {
				mp_nofriend = MediaPlayer.create(FindFriend.this, R.raw.wrong_friend_number);
				mp_nofriend.start();

				nofriend = new AlertDialog.Builder(this)
				.setTitle("Friend Not Found")
				.setMessage("This person was not found or is already your friend! Please re-enter the phone number")
				.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						nofriend.cancel();
					}
				})
				.create();
				nofriend.show();
			}
			else {
				String frienddetails[] = response.split("\\@+");
				String friend_firstname = frienddetails[0];
				String friend_lastname = frienddetails[1];
				String friend_location = frienddetails[2];

				Intent intent = new Intent(FindFriend.this, FriendProfile.class);

				intent.putExtra("friend_phoneno", phonenumber.getText().toString());
				intent.putExtra("friend_firstname", friend_firstname);
				intent.putExtra("friend_lastname", friend_lastname); 
				intent.putExtra("friend_location", friend_location);
				startActivity(intent);
			}	

		} catch (Exception e) {
			Log.e("FindFriend",e.toString());
		}
	}
}
