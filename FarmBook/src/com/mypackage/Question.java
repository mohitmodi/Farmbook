package com.mypackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Question extends FarmbookActivity {

	private final int FIELDS=6;
	
	private MediaPlayer mp_main;
	private ImageButton pause;
	
	private LayoutInflater inflater;
	private LinearLayout layout;
	
	private Vector<Bitmap> profilepic_bitmaps;
	private Vector<String> phonenos_bitmaps;
	
	private RelativeLayout post,comments[];
	
	private ImageView post_profilepic_imageview,post_image_imageview;
	private TextView post_phoneno_textview,post_text_textview,post_timestamp_textview;
	private ImageButton post_audio_imagebutton;
	private MediaPlayer post_mp;
	
	private ImageView[] profilepics,images;
	private TextView[] phonenos,texts,timestamps;
	private ImageButton[] audiobuttons;
	private MediaPlayer mp[];
	private Button newcomment;

	private String post_phoneno,post_image,post_audio,post_text,post_timestamp,post_queryid;
	private String commentlist[]=null,commentdetails[][]=null;
	private int no_comments=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall);

		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			post_phoneno = extras.getString("phoneno");
			post_image = extras.getString("image");
			post_audio = extras.getString("audio");
			post_text = extras.getString("text");
			post_timestamp = extras.getString("timestamp");
			post_queryid = extras.getString("queryid");
		}
		Log.i("Question","queryid = "+post_queryid);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("queryid", post_queryid));

		try {
			String response = CustomHttpClient.executeHttpPost("sln/comments.php", postParameters);	
			Log.i("Question","Server response = "+response);

			if(!response.equals(NONE)) {
				commentlist = response.split("\\/+");
				no_comments = commentlist.length;
			}
			Log.i("Question","Number of queries = "+no_comments);

		} catch (Exception e) {
			Log.e("Question",e.getMessage());
		}

		inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setUpViews();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}

	private void setUpViews() {
		
		mp_main = MediaPlayer.create(Question.this, R.raw.comment);
		mp_main.start();
		
		pause=(ImageButton)findViewById(R.id.wall_pause);
		pause.setImageResource(R.drawable.pause_button);
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
				else{
					mp_main.start();
					pause.setImageResource(R.drawable.pause_button);
				}
			}	
		});
		
		commentdetails = new String[no_comments][FIELDS];
		
		phonenos_bitmaps = new Vector<String>();
		profilepic_bitmaps = new Vector<Bitmap>();
		
		layout = (LinearLayout)findViewById(R.id.wall_linearlayout);

		post = (RelativeLayout)inflater.inflate(R.layout.comment, null);
		post.setBackgroundResource(R.drawable.border_blue);
		
		post_profilepic_imageview = (ImageView)post.findViewById(R.id.comment_profilepic);
		post_profilepic_imageview.setImageBitmap(CustomHttpClient.downloadImage(post_phoneno+"/profilepic.jpg"));
		
		post_phoneno_textview = (TextView)post.findViewById(R.id.comment_phoneno);
		post_phoneno_textview.setText(post_phoneno);

		post_text_textview = (TextView)post.findViewById(R.id.comment_text);
		post_text_textview.setText(post_text);

		post_timestamp_textview = (TextView)post.findViewById(R.id.comment_timestamp);
		post_timestamp_textview.setText(post_timestamp);

		post_image_imageview = (ImageView)post.findViewById(R.id.comment_image);
		if(!post_image.equals(""))
			post_image_imageview.setImageBitmap(CustomHttpClient.downloadImage(post_phoneno+"/"+post_image));

		post_audio_imagebutton = (ImageButton)post.findViewById(R.id.comment_audio);

		if(!post_audio.equals("")) {

			post_mp = playAudio(post_phoneno+"/"+post_audio);
			try {
				post_mp.prepare();
			} catch (IllegalStateException e) {
				Log.e("Question", e.getMessage());
			} catch (IOException e) {
				Log.e("Question", e.getMessage());
			}
			post_audio_imagebutton.setImageResource(R.drawable.play_audio);
			
			post_mp.setOnCompletionListener(new OnCompletionListener(){
				public void onCompletion(MediaPlayer arg0) {
					post_audio_imagebutton.setImageResource(R.drawable.play_audio);
				}
			});
			post_audio_imagebutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {					
					if(post_mp.isPlaying()) {
						post_mp.pause();
						post_audio_imagebutton.setImageResource(R.drawable.play_audio);
					}
					else{
						post_mp.start();
						post_audio_imagebutton.setImageResource(R.drawable.pause_button_large);
					}
				}	
			});
		}
		else
			post_audio_imagebutton.setVisibility(View.GONE);
		layout.addView(post);

		TextView tv = new TextView(this); 
		tv.setBackgroundColor(Color.TRANSPARENT);
		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 10));
		layout.addView(tv);

		comments = new RelativeLayout[no_comments];
		
		profilepics = new ImageView[no_comments];
		phonenos = new TextView[no_comments];
		texts = new TextView[no_comments];
		timestamps = new TextView[no_comments];
		images = new ImageView[no_comments];
		audiobuttons = new ImageButton[no_comments];
		
		mp = new MediaPlayer[no_comments];
		int loc=0;
		
		for(int i=0; i<no_comments; i++) {

			commentdetails[i] = commentlist[i].split("\\@");
			Log.i("Wall","Values = "+commentdetails[i][0]+"; "+commentdetails[i][1]+"; "+commentdetails[i][2]
					+"; "+commentdetails[i][3]+"; "+commentdetails[i][4]+"; "+commentdetails[i][5]+"; ");
			
			if(phonenos_bitmaps.indexOf(commentdetails[i][0]) == -1) {
				phonenos_bitmaps.add(commentdetails[i][0]);
				profilepic_bitmaps.add(CustomHttpClient.downloadImage(commentdetails[i][0]+"/profilepic.jpg"));
			}
			loc=phonenos_bitmaps.indexOf(commentdetails[i][0]);
			
			comments[i] = (RelativeLayout)inflater.inflate(R.layout.comment, null);
			comments[i].setBackgroundResource(R.drawable.border_green);
			
			profilepics[i] = (ImageView)comments[i].findViewById(R.id.comment_profilepic);
			profilepics[i].setImageBitmap(profilepic_bitmaps.get(loc));
			
			images[i] = (ImageView)comments[i].findViewById(R.id.comment_image);
			if(!commentdetails[i][1].equals(""))
				images[i].setImageBitmap(CustomHttpClient.downloadImage(commentdetails[i][0]+"/"+commentdetails[i][1]));

			phonenos[i] = (TextView)comments[i].findViewById(R.id.comment_phoneno);
			phonenos[i].setText(commentdetails[i][0]);

			texts[i] = (TextView)comments[i].findViewById(R.id.comment_text);
			texts[i].setText(commentdetails[i][3]);

			timestamps[i] = (TextView)comments[i].findViewById(R.id.comment_timestamp);
			timestamps[i].setText(commentdetails[i][4]);

			audiobuttons[i] = (ImageButton)comments[i].findViewById(R.id.comment_audio);
			audiobuttons[i].setId(i);

			if(!commentdetails[i][2].equals("")) {
				
				mp[i] = playAudio(commentdetails[i][0]+"/"+commentdetails[i][2]);
				try {
					mp[i].prepare();
				} catch (IllegalStateException e) {
					Log.e("Question", e.getMessage());
				} catch (IOException e) {
					Log.e("Question", e.getMessage());
				}
				audiobuttons[i].setImageResource(R.drawable.play_audio);
				
				final int index=i;
				mp[i].setOnCompletionListener(new OnCompletionListener(){
					public void onCompletion(MediaPlayer arg0) {
						audiobuttons[index].setImageResource(R.drawable.play_audio);
					}
				});
				audiobuttons[i].setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						int i=v.getId();
						
						if(mp[i].isPlaying()) {
							mp[i].pause();
							audiobuttons[i].setImageResource(R.drawable.play_audio);
						}
						else{
							mp[i].start();
							audiobuttons[i].setImageResource(R.drawable.pause_button_large);
						}
					}	
				});
			}
			else
				audiobuttons[i].setVisibility(View.GONE);
			
			layout.addView(comments[i]);

			TextView txv = new TextView(this); 
			txv.setBackgroundColor(Color.TRANSPARENT);
			txv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(txv);
		}

		newcomment = new Button(this);
		newcomment.setText("New Comment");
		newcomment.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		newcomment.setBackgroundResource(R.drawable.border_green);
		newcomment.setGravity(Gravity.CENTER_HORIZONTAL);
		newcomment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Question.this, CreatePost.class);
				intent.putExtra("type", COMMENT);
				intent.putExtra("postid", post_queryid);
				intent.putExtra("postphoneno", post_phoneno);
				startActivity(intent);
			}
		});
		layout.addView(newcomment);
	}
}
