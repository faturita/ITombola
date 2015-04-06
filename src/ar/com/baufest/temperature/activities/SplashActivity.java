package ar.com.baufest.temperature.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ar.com.baufest.temperature.R;

public class SplashActivity extends Activity {

	private Handler handler;
	private Thread thread;

	private static final int SPLASH_SCREEN_TIME_IN_MILLIS = 1000;
	
	private TextView resultado;
	private Button button;
	
	private TextView banner;
	
	private EditText apuesta;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		getActionBar().hide();
		handler = new Handler();
		
		apuesta = (EditText)findViewById(R.id.editText1);
		
		resultado = (TextView)findViewById(R.id.textView2);
		
		banner = (TextView)findViewById(R.id.textView5);
		
		banner.setText("");
		
		resultado.setText("--");
		
		button = (Button)findViewById(R.id.button1);
		
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//textView1.setText("completed!");

				new Thread(new Runnable(){
				    @Override
				    public void run() {
						String val="";
						try {
							connect("http://itombola.azurewebsites.net/WebMobile/rest/authenticate/bet");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Log.i(TAG(), val);
				    }
				}).start();
				
				

			}
			
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(SPLASH_SCREEN_TIME_IN_MILLIS);
					handler.post(new Runnable() {
						public void run() {
							//goToNextScreen();
						}
					});
				} catch (InterruptedException e) {

				}
			}
		};
		thread.start();
	}

	protected void goToNextScreen() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	public String TAG() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	
	
	public void connect(String url)
	{

	    HttpClient httpclient = new DefaultHttpClient();

	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url); 
	    
	    HttpPost httppost = new HttpPost(url);
	    httppost.setHeader("Accept", "application/json");

	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httppost);
	        // Examine the response status
	        Log.i("Fetching:",response.getStatusLine().toString());

	        // Get hold of the response entity
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        // to worry about connection release

	        if (entity != null) {

	            // A Simple JSON Response Read
	            InputStream instream = entity.getContent();
	            String result= convertStreamToString(instream);
	            // now you have the string representation of the HTML request
	            
	            JSONObject jObject = new JSONObject(result);
	            
	            final String winnernumber = jObject.getString("winner");
	            
	            
	            
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {

	    	            resultado.setText(winnernumber);
	    	            
	    	            Log.i(TAG(), winnernumber);
	    	            Log.i(TAG(), apuesta.getText().toString());
	    	            
	    	            if (apuesta.getText().toString().equals(winnernumber))
	    	            {
	    	            	banner.setText("GANASTE!!!");
	    	            } else {
	    	            	banner.setText("PERDISTE!!!");
	    	            }

	               }
	           });

	            
	            Log.e(TAG(), result);
	            instream.close();
	        }


	    } catch (Exception e) {
	    	Log.e(TAG(), "Error!");
	    	e.printStackTrace();
	    }
	}
	
    private String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	
	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}

}
