package com.mediaplayer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class AudioBrowser extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiolistview);
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		
		Uri media_url = Media.EXTERNAL_CONTENT_URI;
		String[] projection = new String[] { Media._ID, Media.DISPLAY_NAME,
				Media.DATA, Media.TITLE, Media.ALBUM, Media.ARTIST };
		Cursor cr = managedQuery(media_url, projection, null, null,
				Media.DISPLAY_NAME + " ASC");
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.listview, cr, new String[] { Media.DISPLAY_NAME },
				new int[] { R.id.list_item });
		lv.setAdapter(adapter);

		

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent audio_intent = new Intent(AudioBrowser.this,
						AudioPlayer.class);
				SimpleCursorAdapter padapter = (SimpleCursorAdapter) parent
				.getAdapter();
				Cursor pcr = padapter.getCursor();
				audio_intent.putExtra("path",
						pcr.getString(pcr.getColumnIndex(Media.DATA)));
				audio_intent.putExtra("id",
						pcr.getString(pcr.getColumnIndex(Media._ID)));
				audio_intent.putExtra("title",
						pcr.getString(pcr.getColumnIndex(Media.TITLE)));
				audio_intent.putExtra("album",
						pcr.getString(pcr.getColumnIndex(Media.ALBUM)));
				startActivity(audio_intent);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.browsermenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.np:
			SharedPreferences settings = getSharedPreferences("Now_Playing", 0);
			Intent audio_intent = new Intent(AudioBrowser.this,
					AudioPlayer.class);
			audio_intent.putExtra("path", settings.getString("path", ""));
			audio_intent.putExtra("id", settings.getString("id", ""));
			audio_intent.putExtra("title", settings.getString("title", ""));
			audio_intent.putExtra("album", settings.getString("album", ""));
			startActivity(audio_intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
