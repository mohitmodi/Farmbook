package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Profile extends FarmbookActivity {

	private TextView phoneno_textview,username_textview,location_textview,posts_textview,comments_textview;
	private ImageView profilepic_imageview,friends[];
	private LinearLayout layout;
	private String[] details,friendlist;
	private int no_friends;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		profilepic_imageview = (ImageView)findViewById(R.id.profile_profilepic);
		profilepic_imageview.setImageBitmap(getStuffApplication().getProfilepic());
		username_textview = (TextView)findViewById(R.id.profile_name);
		username_textview.setText(getStuffApplication().getFirstname()+" "+getStuffApplication().getLastname());
		location_textview = (TextView)findViewById(R.id.profile_location);
		location_textview.setText(getStuffApplication().getLocation());
		phoneno_textview = (TextView)findViewById(R.id.profile_phoneno);
		phoneno_textview.setText(getStuffApplication().getPhonenumber());
		
		posts_textview = (TextView)findViewById(R.id.profile_posts);
		comments_textview = (TextView)findViewById(R.id.profile_comments);
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			String response = CustomHttpClient.executeHttpPost("sln/profile.php", postParameters);	
			Log.i("Profile","Server response = "+response);

			details = response.split("\\@");
			friendlist = details[0].split("\\/");
			no_friends = friendlist.length;
			Log.i("Wall","Number of friends = "+no_friends);
			
			setUpViews();

		} catch (Exception e) {
			Log.e("Wall",e.getMessage());
		}
	}
	
	public void setUpViews() {
	
		layout = (LinearLayout)findViewById(R.id.profile_friendlist);
		
		friends = new ImageView[no_friends];
		
		for(int i=0; i<no_friends; i++) {
			friends[i] = new ImageView(this);
			friends[i].setLayoutParams(new LayoutParams(96, 96));
			//friends[i].setImageBitmap(getStuffApplication().getProfilepic());
			friends[i].setImageBitmap(CustomHttpClient.downloadImage(friendlist[i]+"/profilepic.jpg"));
			
			layout.addView(friends[i]);
		}
		posts_textview.setText(details[1]);
		comments_textview.setText(details[2]);
		
	}

}
