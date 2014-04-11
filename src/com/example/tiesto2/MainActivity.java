package com.example.tiesto2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	static final String URLPATH = "http://192.168.1.8/arduino/";
	private String urlPath = URLPATH;
    
    private	Button slower ;
	private	Button faster ;
	private	Button reverse ;
	private	Button forward ;
	private Button stop;
	private Button left;
	private Button right;
	private EditText cmdText;
	private Button setUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    
	    
	    cmdText = (EditText) this.findViewById(R.id.editText1);
	    cmdText.setText(urlPath);	    
	    
		forward = (Button) this.findViewById(R.id.forward);
		forward.setOnClickListener(new ButtonForwardClickListener() );				

		reverse = (Button) this.findViewById(R.id.reverse);
		reverse.setOnClickListener(new ButtonReverseClickListener() );		
		
		faster = (Button) this.findViewById(R.id.faster);
		faster.setOnClickListener(new ButtonFasterClickListener() );	
		slower = (Button) this.findViewById(R.id.slower);
		slower.setOnClickListener(new ButtonSlowerClickListener() );	
		stop = (Button) this.findViewById(R.id.stop);
		stop.setOnClickListener(new ButtonStopClickListener() );
		
		left = (Button) this.findViewById(R.id.left);
		left.setOnClickListener(new ButtonLeftClickListener() );
		right = (Button) this.findViewById(R.id.right);
		right.setOnClickListener(new ButtonRightClickListener() );
				
		//the IP address may diff from place to place
		//use cmdText field for IP address dynamically update
		//or reset it back the the original
		setUrl = (Button) this.findViewById(R.id.setUrl);
		setUrl.setOnClickListener(new ButtonSetUrlClickListener() );
		setUrl.setText("Set URL");
		
	}
	
	private final class ButtonSetUrlClickListener implements View.OnClickListener{
		public void onClick(View v){
			String text = setUrl.getText().toString();
			if(text == "Set URL"){
				urlPath = cmdText.getText().toString();
				System.out.println("url="+urlPath);
				setUrl.setText("Reset URL");
			}else{
				urlPath = URLPATH;
				System.out.println("url="+urlPath);
				setUrl.setText("Set URL");
				cmdText.setText(URLPATH);
			}
		}
	}
	
	private final class ButtonLeftClickListener implements View.OnClickListener{
		public void onClick(View v){
			String  path = new String(urlPath + "analog/9/1");
			sendCmd(path);
		}
	}
	private final class ButtonRightClickListener implements View.OnClickListener{
		public void onClick(View v){
			String  path = new String(urlPath + "analog/10/1");
			sendCmd(path);
		}
	}
	
	private final class ButtonFasterClickListener implements View.OnClickListener{
		public void onClick(View v){
			String  path = new String(urlPath + "analog/7/1");
			sendCmd(path);
		}
	}
	
	private final class ButtonSlowerClickListener implements View.OnClickListener{
		public void onClick(View v){
			String  path = new String(urlPath + "analog/8/1");
			sendCmd(path);
		}
	}
	
	private final class ButtonStopClickListener implements View.OnClickListener{
		public void onClick(View v){
			String path = new String(urlPath + "digital/7/1");
			sendCmd(path);
		}
	}
	
	private final class ButtonForwardClickListener implements View.OnClickListener{
		public void onClick(View v){
			sendCmd(urlPath + "digital/13/1");			
		}
	}

	private final class ButtonReverseClickListener implements View.OnClickListener{
		public void onClick(View v){
			sendCmd(urlPath + "digital/13/0");		
		}
	}
	
	/*  sync http call
	private void sendCmd(final String path){
		try {
			sendGETRequest(path);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection error");
		}
		cmdText.setText(path); 
	}
	private static boolean sendGETRequest (String path) throws Exception{
		//for tmp test
		path = new String("https://www.google.com/");
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(10000);
		conn.setRequestMethod("GET");
		System.out.println("return code = "+ conn.getResponseCode() +" path: " +path );
		if(conn.getResponseCode() == 200){
			conn.disconnect();   
			return true;
		}
		System.out.println("Return code error");
		conn.disconnect();  
		return false;
	}
	*/
	
	// async http call
	private void sendCmd(final String path){
		new MyAsyncTask().execute(path);
	}
			
	private class MyAsyncTask extends AsyncTask<String, Void, String> {
	    protected String doInBackground(String... path) {
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL("https://www.google.com/").openConnection();
				//HttpURLConnection conn = (HttpURLConnection) new URL(path[0]).openConnection();
				//in case of failure in connection, the max waiting time is 1 second
				conn.setConnectTimeout(10000);   
				conn.setRequestMethod("GET");
				System.out.println("return code = "+ conn.getResponseCode() +" path: " +path[0] );
				if(conn.getResponseCode() == 200){
					conn.disconnect();  
					return path[0];
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			System.out.println("Return code error");
			return "Return code error" ;
	    }
	    
	    protected void onPostExecute(String result) {
	    	cmdText.setText(result); 
	    	System.out.println("Cmd status: " + result );
	    }
	}
	
	
	/* async socket call
	 private class ConnToCarTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String...url) {
            String response = "";
               try {
                       myClient = new SocketClient(host, port);
                       connectionFlag = true;
                       response = "connect to car successfully";
               } catch (IOException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                       connectionFlag = false;
                       response = "Error connect to car";
               }
               return response;
        }
        protected void onPostExecute(String result) {
               String disInfo ;
               if (connectionFlag) {
               		disInfo = "Disconnect from car";
               } else {
                    disInfo = "Connect to car";
               }
                
               cmdText.setText(result + disInfo); 
        }
    }
	 */
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
