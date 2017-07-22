package com.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayer extends Activity{
	 private String path = "";
	 private VideoView mVideoView;
	 private MediaController mc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoplayer);
		Bundle bd = new Bundle();
		Intent intent = getIntent();
		bd = intent.getExtras();
		path = bd.getString("path");
		mVideoView = (VideoView) findViewById(R.id.videoView1);
		mc=(MediaController) findViewById(R.id.mediaController1);
		mVideoView.setVideoPath(path);
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.start();
	}
	 
}
