package dtc.research.sendAudio;

import android.app.Activity;
import android.os.Bundle;
import android.media.AudioRecord;
import java.io.IOException;
import android.media.MediaRecorder.AudioSource;
import java.net.Socket;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.media.AudioFormat;
import java.io.BufferedOutputStream;
import java.lang.Thread;
import android.widget.EditText;
import java.io.OutputStream;

public class AndroidSendAudioActivity extends Activity {
	//ipv4 is 136.152.39.68
	//also try to input text
	private AudioRecord theAudioRecord;
	public static Button killButton;
	public static Button sendButton;
	public static Button setIPButton;
	public Thread workThread;
	int blocksize = 8;
	public String targetIP;
	public static EditText et;
	public Socket theSocket = null;
	
	private OnClickListener forceQuitListener = new OnClickListener() {
		public void onClick(View notUsed) {
			try {
				if (theSocket != null) {
					theSocket.close();
				}
			} catch (IOException ioe) {
				//do nothing
			}
			System.out.println("exit");
			System.exit(0);
		}
	};
	
	private OnClickListener SendListener = new OnClickListener() {
		public void onClick(View notUsed) {
			workThread.start();
		}
	};
	
	private OnClickListener setIPListener = new OnClickListener() {
		public void onClick(View notUsed) {
			targetIP = et.getText().toString();
		}
	};
	
	public Runnable sendRunnable = new Runnable() {
		public void run() {
			sendSound();
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	killButton = (Button) findViewById(R.id.killButton);
    	killButton.setOnClickListener(forceQuitListener);
    	sendButton = (Button) findViewById(R.id.sendButton);
    	sendButton.setOnClickListener(SendListener);
    	setIPButton = (Button) findViewById(R.id.setIP);
    	setIPButton.setOnClickListener(setIPListener);
    	et = (EditText) findViewById(R.id.editInput);
    }
    
    public void sendSound() {
    	theAudioRecord = new AudioRecord(AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, 10000);
    	//Socket theSocket = null;
    	try {
    		theSocket = new Socket(targetIP, 4321);
    		OutputStream bufferedOut = theSocket.getOutputStream();
    		if (theAudioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
    			theAudioRecord.startRecording();
    		}
    		byte[] buf = new byte[blocksize];
    		while (true) {
    			theAudioRecord.read(buf, 0, blocksize);
    			bufferedOut.write(buf, 0, blocksize);
    			bufferedOut.flush();
    			/*Do I need some sleeping here?*/
    			
    		}
    		
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    		System.out.println("IOException");
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
    	workThread = new Thread(sendRunnable);
		
    }
    
}