package com.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

     // ===========================================================
     // Fields
     // ===========================================================

     private final int SPLASH_DISPLAY_LENGHT = 2000;

     // ===========================================================
     // "Constructors"
     // ===========================================================

     /** Called when the activity is first created. */
     @Override
     public void onCreate(Bundle icicle) {
          super.onCreate(icicle);
          setContentView(R.layout.splash);

          /* New Handler to start the Menu-Activity
           * and close this Splash-Screen after some seconds.*/
          new Handler().postDelayed(new Runnable(){
               @Override
               public void run() {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(Splash.this,FileBrowser.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
               }
          }, SPLASH_DISPLAY_LENGHT);
     }
}
