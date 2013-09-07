package com.mypackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Wall extends FarmbookActivity {

	private final int FIELDS=6;
	private MediaPlayer mp_main;
	private ImageButton pause;
	
	private LayoutInflater inflater;
	private LinearLayout layout;
	private RelativeLayout questions[]=null;
	
	private Vector<Bitmap> profilepic_bitmaps;
	private Vector<String> phonenos_bitmaps;
 	
	private ImageView[] profilepics,images;
	private TextView[] phonenos,texts,timestamps;
	private ImageButton[] audiobuttons,openbuttons;
	private MediaPlayer mp[];
	private AlertDialog noqueries;

	private String querylist[]=null,querydetails[][]=null;
	private int no_queries=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			String response = CustomHttpClient.executeHttpPost("sln/wall.php", postParameters);	
			Log.i("Wall","Server response = "+response);

			if(!response.equals(NONE)) {
				querylist = response.split("\\/+");
				no_queries = querylist.length;
			}
			Log.i("Wall","Number of queries = "+no_queries);

		} catch (Exception e) {
			Log.e("Wall",e.getMessage());
		}

		if(no_queries==0) {

			noqueries = new AlertDialog.Builder(this)
			.setTitle("No Queries")
			.setMessage("There are no posts to be displayed")
			.setPositiveButton("OK", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create();
			noqueries.show();
		}
		else {	
			inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			setUpViews();
		}
	}

	private void setUpViews() {
		mp_main = MediaPlayer.create(Wall.this, R.raw.wall_audio);
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
		
		querydetails = new String[no_queries][FIELDS];
		
		phonenos_bitmaps = new Vector<String>();
		profilepic_bitmaps = new Vector<Bitmap>();
		
		layout = (LinearLayout)findViewById(R.id.wall_linearlayout);
		questions = new RelativeLayout[no_queries];
		
		profilepics = new ImageView[no_queries];
		phonenos = new TextView[no_queries];
		texts = new TextView[no_queries];
		timestamps = new TextView[no_queries];
		images = new ImageView[no_queries];
		audiobuttons = new ImageButton[no_queries];
		openbuttons = new ImageButton[no_queries];
		
		mp = new MediaPlayer[no_queries];
		int loc=0;
		
		for(int i=0; i<no_queries; i++) {

			querydetails[i] = querylist[i].split("\\@");
			Log.i("Wall","Values = "+querydetails[i][0]+"; "+querydetails[i][1]+"; "+querydetails[i][2]
					+"; "+querydetails[i][3]+"; "+querydetails[i][4]+"; "+querydetails[i][5]+"; ");
			
			if(phonenos_bitmaps.indexOf(querydetails[i][0]) == -1) {
				phonenos_bitmaps.add(querydetails[i][0]);
				profilepic_bitmaps.add(CustomHttpClient.downloadImage(querydetails[i][0]+"/profilepic.jpg"));
			}
			loc=phonenos_bitmaps.indexOf(querydetails[i][0]);
			
			questions[i] = (RelativeLayout)inflater.inflate(R.layout.post, null);
			questions[i].setBackgroundResource(R.drawable.border_blue);
			
			profilepics[i] = (ImageView)questions[i].findViewById(R.id.post_profilepic);
			profilepics[i].setImageBitmap(profilepic_bitmaps.get(loc));
			
			images[i] = (ImageView)questions[i].findViewById(R.id.post_image);
			if(!querydetails[i][1].equals(""))
				images[i].setImageBitmap(CustomHttpClient.downloadImage(querydetails[i][0]+"/"+querydetails[i][1]));

			phonenos[i] = (TextView)questions[i].findViewById(R.id.post_phoneno);
			phonenos[i].setText(querydetails[i][0]);

			texts[i] = (TextView)questions[i].findViewById(R.id.post_text);
			texts[i].setText(querydetails[i][3]);

			timestamps[i] = (TextView)questions[i].findViewById(R.id.post_timestamp);
			timestamps[i].setText(querydetails[i][4]);

			audiobuttons[i] = (ImageButton)questions[i].findViewById(R.id.post_audio);
			audiobuttons[i].setId(i);

			if(!querydetails[i][2].equals("")) {
				
				mp[i] = playAudio(querydetails[i][0]+"/"+querydetails[i][2]);
				try {
					mp[i].prepare();
				} catch (IllegalStateException e) {
					Log.e("Wall", e.getMessage());
				} catch (IOException e) {
					Log.e("Wall", e.getMessage());
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

			openbuttons[i] = (ImageButton)questions[i].findViewById(R.id.post_expand);
			openbuttons[i].setId(i);

			openbuttons[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i=v.getId();	

					Intent intent = new Intent(Wall.this, Question.class);
					intent.putExtra("phoneno", querydetails[i][0]);
					intent.putExtra("image", querydetails[i][1]);
					intent.putExtra("audio", querydetails[i][2]);
					intent.putExtra("text", querydetails[i][3]);
					intent.putExtra("timestamp", querydetails[i][4]);
					intent.putExtra("queryid", querydetails[i][5]);
					startActivity(intent);
				}
			});
			layout.addView(questions[i]);

			TextView tv = new TextView(this); 
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(tv);
		}
	}
}
