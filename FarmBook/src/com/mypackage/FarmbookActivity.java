package com.mypackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.utilitiespackage.CustomHttpClient;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

public class FarmbookActivity extends Activity {
	public static final String NONE = "none";
	public static final String POST = "post";
	public static final String COMMENT = "comment";

	public FarmbookActivity() {
		super();
	}

	protected FarmbookApplication getStuffApplication() {
		return (FarmbookApplication)getApplication();
	}

	protected MediaPlayer playAudio(String mediaUrl) {
		try {
			URLConnection cn = new URL("http://"+CustomHttpClient.thisIp+"/sln/audio/"+mediaUrl).openConnection();
			InputStream is = cn.getInputStream();

			// create file to store audio
			File mediaFile = new File(this.getCacheDir(),"mediafile");
			FileOutputStream fos = new FileOutputStream(mediaFile);   
			byte buf[] = new byte[16 * 1024];
			Log.i("playAudio", mediaUrl+" downloaded");

			// write to file until complete
			do {
				int numread = is.read(buf);   
				if (numread <= 0)  
					break;
				fos.write(buf, 0, numread);
			} while (true);
			fos.flush();
			fos.close();
			Log.i("playAudio", mediaUrl+" saved");

			MediaPlayer mp = new MediaPlayer();
			FileInputStream fis = new FileInputStream(mediaFile);

			mp.setDataSource(fis.getFD());
			return mp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
