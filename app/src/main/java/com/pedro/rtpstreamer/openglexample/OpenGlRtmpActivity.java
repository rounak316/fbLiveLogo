package com.pedro.rtpstreamer.openglexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pedro.encoder.utils.gl.GifStreamObject;
import com.pedro.encoder.utils.gl.ImageStreamObject;
import com.pedro.encoder.utils.gl.TextStreamObject;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.readyToStreaam;
import com.pedro.rtpstreamer.R;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.view.OpenGlView;
import java.io.IOException;
import net.ossrs.rtmp.ConnectCheckerRtmp;

/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.Camera1Base}
 * {@link com.pedro.rtplibrary.rtmp.RtmpCamera1}
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OpenGlRtmpActivity extends AppCompatActivity
    implements ConnectCheckerRtmp, View.OnClickListener {

  private RtmpCamera2 rtmpCamera1;
  private Button button;
  private EditText etUrl;

  class Size{
    float width= 0 ;
    float height= 0;
    public Size(float width , float height){
      this.width = width;
      this.height = height;
    }

    float getWidth(){
      return this.width;
    }
    float getHeight(){
      return this.height;
    }

    public void reize(  float screenWidth, float screenHeight  ){
      float ratio = width / height;

      float _height = 20 * screenHeight/100;
      float _width  = ratio * _height;

      this.width = _width;
      this.height = _height;
    }
  }

  float w = 0;
  float h = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_open_gl);
    OpenGlView openGlView = findViewById(R.id.surfaceView);
    openGlView.addSurfaceReadyHandler(new readyToStreaam() {
      @Override
      public void iAmRead() {
        Log.d("READY_SURFACE" , "I am ready");
        onClick((null));
        setImageToStream(w , h);
      }

      @Override
      public void sizeChanged(int width, int height) {
        rtmpCamera1.setPositionStreamObject( 2, 98);
        w = width;
        h = height;

        Log.d("READY_SURFACE SIZE" ,  "Message");

      }
    });

    button = findViewById(R.id.b_start_stop);
    button.setOnClickListener(this);
    etUrl = findViewById(R.id.et_rtp_url);
    etUrl.setHint(R.string.hint_rtmp);
    rtmpCamera1 = new RtmpCamera2(openGlView, this);
//    onClick((openGlView));


  }

  @Override
  protected void onStart() {
    super.onStart();



  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.gl_menu, menu);
    return true;
  }

  void calculateSize(float width, float height , float screenWidth , float screenHeight){

//    Height Percentage 20 PERCENT


  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (rtmpCamera1.isStreaming()) {
      switch (item.getItemId()) {
        case R.id.text:
          setTextToStream();
          return true;
        case R.id.image:
          setImageToStream(w,h);
          return true;
        case R.id.gif:
          setGifToStream();
          return true;
        case R.id.clear:
          rtmpCamera1.clearStreamObject();
          return true;
        default:
          return false;
      }
    } else {
      return false;
    }
  }



  private void setTextToStream() {
    try {
      TextStreamObject textStreamObject = new TextStreamObject();
      textStreamObject.load("Hello world", 22, Color.RED);
      rtmpCamera1.setTextStreamObject(textStreamObject);
    } catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void setImageToStream( float screenWidth , float screenHeight) {
    try {

      Bitmap bitmap =   BitmapFactory.decodeResource(getResources(), R.drawable.failaan);

      Size size = new Size( bitmap.getWidth() , bitmap.getHeight() );
      size.reize( screenWidth , screenHeight  );

      ImageStreamObject imageStreamObject = new ImageStreamObject();
      imageStreamObject.load(bitmap );
      imageStreamObject.resize( (int)( size.getWidth()) , (int)(size.getHeight()));
      rtmpCamera1.setImageStreamObject(imageStreamObject);
    } catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void setGifToStream() {
    try {
      GifStreamObject gifStreamObject = new GifStreamObject();
      gifStreamObject.load(getResources().openRawResource(R.raw.banana));
      rtmpCamera1.setGifStreamObject(gifStreamObject);
    } catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onConnectionSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onConnectionFailedRtmp(final String reason) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
            .show();
        rtmpCamera1.stopStream();
        rtmpCamera1.stopPreview();
        button.setText(R.string.start_button);
      }
    });
  }

  @Override
  public void onDisconnectRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthErrorRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Auth success", Toast.LENGTH_SHORT).show();



      }
    });
  }

  @Override
  public void onClick(View view) {
    String URL = "rtmp://live-api.facebook.com:80/rtmp/10214679536852379?ds=1&a=ATi2Ylt6a6X8AGPx";
    if (!rtmpCamera1.isStreaming()) {
      if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
        button.setText(R.string.stop_button);
        rtmpCamera1.startStream(URL);
      } else {
        Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT)
            .show();
      }
    } else {
      button.setText(R.string.start_button);
      rtmpCamera1.stopStream();
      rtmpCamera1.stopPreview();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (rtmpCamera1.isStreaming()) {
      rtmpCamera1.stopStream();
      rtmpCamera1.stopPreview();
    }
  }
}
