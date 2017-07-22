package com.mediaplayer;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;
import android.widget.ImageButton;
import android.database.Cursor;
public class MediaPlayerService extends Service{

	private final IBinder mBinder = new MediaPlayerBinder();
	private static MediaPlayer mp=new MediaPlayer();
	public static AudioPlayer ap;
	
	public static ImageButton play;
	public static boolean playing=true;
	public static String id="";
	public static String playing_path="";
	public static String playpause;//1=play 0=pause
	
	public static String repeat="all";
	public static String shuffle="off";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mp.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer mpi) {
				play.setImageResource(R.drawable.icon_play);
				playing=false;
				ap.next();
			}
			
		});
	}
	
	public class MediaPlayerBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	//methods for clients
	public void reset(String path)
	{
		mp.reset();
		try {
			mp.setDataSource(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setPath(String path)
	{
		
	}
	public void play()
	{
		
		mp.start();
	}
	public void pause()
	{
		mp.pause();
	}
	public int getDuration()
	{
		return mp.getDuration();
	}
	public int getCurrentPosition()
	{
		return mp.getCurrentPosition();
	}
	public boolean isPlaying()
	{
		return mp.isPlaying();
	}
	public void seek(int ms)
	{
		mp.seekTo(ms);
	}
}
