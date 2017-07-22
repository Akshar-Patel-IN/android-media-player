package com.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VideoBrowser extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiolistview);
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		
		Uri media_url = Media.EXTERNAL_CONTENT_URI;
		String[] projection = new String[] { Media._ID, Media.DISPLAY_NAME,
				Media.DATA, Media.TITLE};
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
				Intent video_intent = new Intent(VideoBrowser.this,
						VideoPlayer.class);
				SimpleCursorAdapter padapter = (SimpleCursorAdapter) parent
				.getAdapter();
				Cursor pcr = padapter.getCursor();
				video_intent.putExtra("path",
						pcr.getString(pcr.getColumnIndex(Media.DATA)));
				video_intent.putExtra("id",
						pcr.getString(pcr.getColumnIndex(Media._ID)));
				startActivity(video_intent);
			}

		});
	}

}
