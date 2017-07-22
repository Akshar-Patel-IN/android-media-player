package com.mediaplayer;

import com.mediaplayer.MediaPlayerService.MediaPlayerBinder;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AudioPlayer extends Activity {
	MediaPlayerService mps;

	String path;
	String id;
	String tlt;
	String alb;

	TextView title, album;
	ImageView album_art;
	SeekBar sb;
	TextView current_position, duration;

	ImageButton play,next,previous,shuffle,repeat,list;
	Handler playHandler = new Handler();
	Handler seekHandler = new Handler();

	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager mNotificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.audioplayer);

		Bundle bd = new Bundle();
		Intent intent = getIntent();
		bd = intent.getExtras();
		path = bd.getString("path");
		id = bd.getString("id");
		tlt = bd.getString("title");
		alb = bd.getString("album");

		SharedPreferences settings = getSharedPreferences("Now_Playing", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("path", path);
		editor.putString("id", id);
		editor.putString("title", tlt);
		editor.putString("album", alb);

		editor.commit();

		setTitle(tlt);
		setAlbum(alb);
		setAlbumArt(id);

		current_position = (TextView) findViewById(R.id.current_position);
		duration = (TextView) findViewById(R.id.duration);

		play = (ImageButton) findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MediaPlayerService.playing) {
					play.setImageResource(R.drawable.icon_play);
					mps.pause();
					MediaPlayerService.playing = false;
					unnotif();
				} else {
					play.setImageResource(R.drawable.icon_pause);
					mps.play();
					MediaPlayerService.playing = true;
					notif(tlt,alb);
				}
				MediaPlayerService.id = id;
			}
		});
		
		next = (ImageButton) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				next();
			}
		});
		
		previous = (ImageButton) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				previous();
			}
		});
		
		list = (ImageButton) findViewById(R.id.list);
		list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent audio_intent = new Intent(AudioPlayer.this,FileBrowser.class);
				startActivity(audio_intent);
			}
		});
		
		repeat = (ImageButton) findViewById(R.id.repeat);
		repeat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MediaPlayerService.repeat.equals("all")) {
					repeat.setImageResource(R.drawable.icon_repeatone);
					MediaPlayerService.repeat="one";
				}
				else if (MediaPlayerService.repeat.equals("one")) {
					repeat.setImageResource(R.drawable.icon_repeatoff);
					MediaPlayerService.repeat="off";
				}
				else {
					repeat.setImageResource(R.drawable.icon_repeatall);
					MediaPlayerService.repeat="all";
				}
			}
		});
		
		shuffle = (ImageButton) findViewById(R.id.shuffle);
		shuffle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MediaPlayerService.shuffle.equals("off")) {
					shuffle.setImageResource(R.drawable.icon_shuffle);
					MediaPlayerService.shuffle="on";
				}
				else {
					shuffle.setImageResource(R.drawable.icon_shuffleoff);
					MediaPlayerService.shuffle="off";
				}
			}
		});
		
		sb = (SeekBar) findViewById(R.id.seekBar1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser == true) {
					seekBar.setProgress(progress);
					mps.seek(progress);
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		});

		playHandler.removeCallbacks(PlayTask);
		playHandler.postDelayed(PlayTask, 500);

		seekHandler.removeCallbacks(SeekTask);
		seekHandler.postDelayed(SeekTask, 1000);
	}

	public void notif(String tlt,String alb) {
		mNotificationManager = (NotificationManager) getSystemService(ns);
		CharSequence tickerText = "Media Player Started";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.notification,
				tickerText, 0);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		Context context = getApplicationContext();
		CharSequence contentTitle = tlt;
		CharSequence contentText = alb;
		Intent notificationIntent = new Intent(this, AudioPlayer.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SharedPreferences settings = getSharedPreferences("Now_Playing", 0);

		notificationIntent.putExtra("path", settings.getString("path", ""));
		notificationIntent.putExtra("id", settings.getString("id", ""));
		notificationIntent.putExtra("title", settings.getString("title", ""));
		notificationIntent.putExtra("album", settings.getString("album", ""));

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(1, notification);

	}

	public void unnotif() {
		mNotificationManager.cancel(1);
	}

	public void setTitle(String t) {
		title = (TextView) findViewById(R.id.title);
		title.setText(t);
	}

	public void setAlbum(String t) {
		album = (TextView) findViewById(R.id.album);
		album.setText(t);
	}

	public void setAlbumArt(String id) {
		album_art = (ImageView) findViewById(R.id.album_art);
		Uri album_uri = Uri.parse("content://media/external/audio/albumart");
		album_uri = ContentUris.withAppendedId(album_uri, Long.parseLong(id));
		Cursor cur = managedQuery(album_uri, null, null, null, null);
		String a_art = null;
		if (cur.moveToFirst()) {
			a_art = cur.getString(1);
		}

		if (cur.getCount() < 1) {
			album_art.setImageResource(R.drawable.unknown_albumart);
		} else {
			Drawable d = Drawable.createFromPath(a_art);
			album_art.setImageDrawable(d);

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent1 = new Intent(this, MediaPlayerService.class);
		bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService(mConnection);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MediaPlayerBinder binder = (MediaPlayerBinder) service;
			mps = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};
	private Runnable PlayTask = new Runnable() {
		@Override
		public void run() {
			notif(tlt,alb);
			Bundle bd = new Bundle();
			Intent intent = getIntent();
			bd = intent.getExtras();
			final String str = bd.getString("path");
			if (str.equals(MediaPlayerService.playing_path)) {
				if (!MediaPlayerService.playing) {
					play.setImageResource(R.drawable.icon_play);
				}
			} else {
				mps.reset(str);
				mps.play();
			}
			MediaPlayerService.playing = true;
			MediaPlayerService.playing_path = str;
			MediaPlayerService.play = play;
			MediaPlayerService.ap = AudioPlayer.this;
			MediaPlayerService.id = id;
		}
	};
	private Runnable SeekTask = new Runnable() {
		@Override
		public void run() {
			sb.setMax(mps.getDuration());
			long millis = mps.getDuration();
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			duration.setText(String.format("%d:%02d", minutes, seconds));

			sb.setProgress(mps.getCurrentPosition());
			millis = mps.getCurrentPosition();
			seconds = (int) (millis / 1000);
			minutes = seconds / 60;
			seconds = seconds % 60;
			current_position
					.setText(String.format("%d:%02d", minutes, seconds));

			seekHandler.postDelayed(this, 100);
		}
	};

	public void showToast(String str) {
		Context context = getApplicationContext();
		CharSequence text = str;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	public void previous()
	{
		int song_id=Integer.parseInt(id);
		song_id=song_id-1;
		id=Integer.toString(song_id);
		Uri song = Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI,id);
		String tl,al,pth;
		tl=al=pth=null;
		Cursor crs = managedQuery(song,null,null,null,null);
		if(crs.moveToFirst())
		{
			tl=crs.getString(crs.getColumnIndex(Media.TITLE));
			al=crs.getString(crs.getColumnIndex(Media.ALBUM));
			pth=crs.getString(crs.getColumnIndex(Media.DATA));
		}
		
		setTitle(tl);
		setAlbum(al);
		setAlbumArt(id);
		play.setImageResource(R.drawable.icon_pause);
		
		mps.reset(pth);
		mps.play();
		MediaPlayerService.playing = true;
		MediaPlayerService.playing_path = pth;
		MediaPlayerService.play = play;
		MediaPlayerService.ap = AudioPlayer.this;
		MediaPlayerService.id = id;
		
		SharedPreferences settings = getSharedPreferences("Now_Playing", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("path", pth);
		editor.putString("id", id);
		editor.putString("title", tl);
		editor.putString("album", al);

		editor.commit();
		notif(tl,al);
	}
	public void next()
	{
		
		int song_id=Integer.parseInt(MediaPlayerService.id);
		song_id=1+song_id;
		id=Integer.toString(song_id);
		Uri song = Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI,id);
		String tl,al,pth;
		tl=al=pth=null;
		Cursor crs = managedQuery(song,null,null,null,null);
		if(crs.moveToFirst())
		{
			tl=crs.getString(crs.getColumnIndex(Media.TITLE));
			al=crs.getString(crs.getColumnIndex(Media.ALBUM));
			pth=crs.getString(crs.getColumnIndex(Media.DATA));
		}
		
		setTitle(tl);
		setAlbum(al);
		setAlbumArt(id);
		play.setImageResource(R.drawable.icon_pause);
		
		mps.reset(pth);
		mps.play();
		MediaPlayerService.playing = true;
		MediaPlayerService.playing_path = pth;
		MediaPlayerService.play = play;
		MediaPlayerService.ap = AudioPlayer.this;
		MediaPlayerService.id = id;
		
		SharedPreferences settings = getSharedPreferences("Now_Playing", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("path", pth);
		editor.putString("id", id);
		editor.putString("title", tl);
		editor.putString("album", al);

		editor.commit();
		notif(tl,al);
	}
}
