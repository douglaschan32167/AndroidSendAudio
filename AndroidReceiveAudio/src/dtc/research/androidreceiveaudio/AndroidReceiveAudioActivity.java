package dtc.research.androidreceiveaudio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioFormat;
import java.lang.Runnable;

//can I play the first few seconds at a faster rate so that it almost catches up? 
public class AndroidReceiveAudioActivity extends Activity {
	public static Button receiveButton;
	public static Button killButton;
	int blocksize = 16;
	public Thread receiveThread;
	public ServerSocket theSocket;
	public Socket inputSocket;
	private OnClickListener killListener = new OnClickListener() {
		public void onClick(View notUsed) {
			try {
				theSocket.close();
				inputSocket.close();
			} catch (Exception e) {
				// do nothing
			}
			System.out.println("stop receiving");
			System.exit(0);
		}
	};
	
	private OnClickListener receiveListener = new OnClickListener() {
		public void onClick(View notUsed) {
			receiveThread.start();
		}
	};
	
	Runnable receiveRunnable = new Runnable() {
		public void run() {
			receiveSound();
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        killButton = (Button) findViewById(R.id.killButton);
        killButton.setOnClickListener(killListener);
        receiveButton = (Button) findViewById(R.id.receiveButton);
        receiveButton.setOnClickListener(receiveListener);
    }
    
    public void receiveSound() {
    	try {
    		theSocket = new ServerSocket(4321);
    		inputSocket = theSocket.accept();
    		byte[] buffer = new byte[blocksize];
    		InputStream bufInput = inputSocket.getInputStream();
    		AudioTrack theAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_IN_STEREO,
    				AudioFormat.ENCODING_PCM_16BIT, 100000, AudioTrack.MODE_STREAM);
    		while (true) {
    			bufInput.read(buffer, 0, blocksize);
    			theAudioTrack.write(buffer, 0, blocksize);
    			if(theAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
    				theAudioTrack.play();
    			}
    			
    		}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    		System.out.println("you can't create a serversocket at 4321");
    		System.exit(1);
    	}
    	
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    @Override
    public void onPause() {
    	super.onPause();
    }
    @Override
    public void onResume() {
    	super.onResume();
    }
    @Override
    public void onStart() {
    	super.onStart();
    	receiveThread = new Thread(receiveRunnable);
    }
}