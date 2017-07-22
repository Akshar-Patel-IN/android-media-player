package com.mediaplayer;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class FileBrowser extends TabActivity {
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filebrowser);
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        Resources res = getResources(); 
        intent = new Intent().setClass(this, AudioBrowser.class);
        spec = tabHost.newTabSpec("audio").setIndicator("Audio",res.getDrawable(R.drawable.icon_tab_audio))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, VideoBrowser.class);
        spec = tabHost.newTabSpec("video").setIndicator("Video",res.getDrawable(R.drawable.icon_tab_video))
                      .setContent(intent);
        tabHost.addTab(spec);
    }
}