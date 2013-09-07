package com.mypackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Home extends FarmbookActivity {

	private ImageButton button1,button2,button3,button4,button5,button6,button7,button8,button9;
	
	Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setUpViews();
	}

	private void setUpViews() {
		button1 = (ImageButton)findViewById(R.id.main_button1);
		button2 = (ImageButton)findViewById(R.id.main_button2);
		button3 = (ImageButton)findViewById(R.id.main_button3);
		button4 = (ImageButton)findViewById(R.id.main_button4);
		button5 = (ImageButton)findViewById(R.id.main_button5);
		button6 = (ImageButton)findViewById(R.id.main_button6);
		button7 = (ImageButton)findViewById(R.id.main_button7);
		button8 = (ImageButton)findViewById(R.id.main_button8);
		button9 = (ImageButton)findViewById(R.id.main_button9);	

		button1.setImageResource((getStuffApplication().getSex().equals("F") ? R.drawable.profile_female : R.drawable.profile_man));
		button1.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Profile.class);
				startActivity(intent);
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Friends.class);
				startActivity(intent);
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, FindFriend.class);
				startActivity(intent);
			}
		});
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, TakePhoto.class);
				startActivity(intent);
			}
		});
		button5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Wall.class);
				startActivity(intent);
			}
		});
		button6.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, CreatePost.class);
				intent.putExtra("type", POST);
				intent.putExtra("postid", "0");
				intent.putExtra("postphoneno", "0");
				startActivity(intent);
			}
		});
		button7.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, SoundRecord.class);
				startActivity(intent);
			}
		});
		button8.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog = new Dialog(Home.this);
				dialog.setContentView(R.layout.okdialog);
				dialog.setTitle("Dialog");
				
				TextView text = (TextView) dialog.findViewById(R.id.okdialog_tv);
				text.setText("This is a dialog!");

				Button button = (Button) dialog.findViewById(R.id.okdialog_ok);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Home.this, Wall.class);
						startActivity(intent);
					}
				});   
				dialog.show();
			}
		});
		button9.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Wall.class);
				startActivity(intent);
			}
		});
	}
}