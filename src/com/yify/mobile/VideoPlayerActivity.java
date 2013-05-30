package com.yify.mobile;

import java.io.IOException;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import com.yify.manager.*;
import com.yify.object.ItemObject;

import android.content.Intent;
import android.media.*;
import android.net.Uri;
import android.os.Bundle;

public class VideoPlayerActivity extends ActionBarActivity implements
SurfaceHolder.Callback, VideoControllerView.MediaPlayerControl, MediaPlayer.OnPreparedListener{

	
	SurfaceView videoSurface;
	MediaPlayer player;
	VideoControllerView controller;
	ItemObject item;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
		videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
		SurfaceHolder videoHolder = videoSurface.getHolder();
		videoHolder.addCallback(this);
		
		Intent intent = getIntent();
		item = intent.getParcelableExtra("item");
		
		player = new MediaPlayer();
		controller = new VideoControllerView(this);
		
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(this, Uri.parse(item.getYoutubeURL()));
			player.setOnPreparedListener(this);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void start() {
		player.start();
		
	}

	@Override
	public void pause() {
		player.pause();
		
	}

	@Override
	public int getDuration() {
		return player.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		player.seekTo(pos);
		
	}

	@Override
	public boolean isPlaying() {
		return player.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public boolean isFullScreen() {
		
		return false;
	}

	@Override
	public void toggleFullScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		player.setDisplay(holder);
		player.prepareAsync();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		
		controller.setMediaPlayer(this);
		controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
		player.start();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		controller.show();
		return false;
	}

	

}
