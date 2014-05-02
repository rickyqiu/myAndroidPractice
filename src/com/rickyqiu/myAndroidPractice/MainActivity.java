package com.rickyqiu.myAndroidPractice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.rickyqiu.myAndroidPractice.R;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.net.http.AndroidHttpClient;

import android.net.TrafficStats;

public class MainActivity extends Activity {
	
	private TextView tvHttpCode = null;
	private TextView tvHttpBody = null;
	private EditText etUrl = null;
	private Button btGo = null;
	private Handler handler = null;
	
	private String urlInput = "";
	private String strHttpCode = "";
	private String strHttpBody = "";
	
	private String TAG = "== life cycle ==";
	private Context mContext;
	
	//UMENG Appkey: 535f487856240b5395004324
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //MobclickAgent.setDebugMode(true);
        
        MobclickAgent.updateOnlineConfig(this);
        
        setContentView(R.layout.activity_main);
        
        Log.e(TAG, "enter onCreate");
        //switch to photo activity
        //Intent intent = new Intent(this, PhotoActivity.class);
        //startActivity(intent);

        //switch to phone state activity
        //Intent intent2 = new Intent(this, PhoneStateActivity.class);
        //startActivity(intent2);
        
        //get device info
        //String strDeviceInfo = getDeviceInfo(this);
        //Log.e("device info:", strDeviceInfo);
        
        //TrafficStats ts = new TrafficStats();
        //ts.getUidRxBytes(11);
        
        
        //when click button, start a new thread to get http data
        handler = new Handler();
        btGo = (Button)findViewById(R.id.goBtn);
        tvHttpCode = (TextView)findViewById(R.id.httpCode);
        tvHttpBody = (TextView)findViewById(R.id.httpContent);
        
        btGo.setOnClickListener(new submitOnClickListener());
        
        //tvHttpCode.setText("waiting...");
        //if not in new thread, will get Exception: android.os.NetworkOnMainThreadException
        //for more details: http://blog.csdn.net/wotoumingzxy/article/details/7797295
        //String url = "http://m.yixun.com";
        //visitWithApacheHttpClient(url);
        
    }
 
    //practice of activity's lifecycle
    protected void onStart() {  
        super.onStart();  
        Log.e(TAG, "onStart");  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        Log.e(TAG, "onResume"); 
        MobclickAgent.onResume(mContext);
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        Log.e(TAG, "onPause"); 
        MobclickAgent.onPause(mContext);
    }  
    @Override  
    protected void onStop() {  
        super.onStop();  
        Log.e(TAG, "onStop");  
    }  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        Log.e(TAG, "onDestroy");  
    }  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class submitOnClickListener implements OnClickListener {
    	@Override  
        public void onClick(View v) {
    		etUrl = (EditText)findViewById(R.id.urlInput);
    		urlInput = etUrl.getText().toString().trim();
    		
            new Thread() {  
                public void run() {    
                		getWebdata();   
                		Log.e("11", "got data");
                		handler.post(runnableUi);   
                    }                     
            }.start();                        
        }  
    	
    }
    
    // 构建Runnable对象，在runnable中更新界面  
    Runnable   runnableUi = new  Runnable(){  
        @Override  
        public void run() {  
        	
        	try {
	            //更新界面  
	        	Log.e("12", "before update");
	        	tvHttpCode.setText("HTTP return code: " + strHttpCode); 
	        	
	        	tvHttpBody.setMovementMethod(new ScrollingMovementMethod());
	        	tvHttpBody.setText(strHttpBody); 
	        	Log.e("13", "after update");
        	}
        	catch (Exception e) {
        		Log.e("Runnable", e.toString());
        	}
        }  
    };
    
    public void getWebdata() {
    	String url = urlInput;
        //Log.e("url", url);
        if (null == url || "" == url) {
        	url = "http://www.baidu.com";
        } 
        else if (!(url.toLowerCase().startsWith("http"))) {
        	url = "http://" + url;
        }
        
    	//String url = "http://www.baidu.com/";
    	String agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152";
    	
    	MyWebThread webThread = new MyWebThread(url);
    	webThread.start(); 
    	
    	//another way to start a new thread
    	/*new Thread() {
    		public void run() {
    			String strUrl = "http://m.yixun.com";
    			visitWithAndroidHttpClient(strUrl);	
    		}
    	}.start();
    	*/
        	
    }
    
    //thread to access network
    class MyWebThread extends Thread {	
    	String url = null;
    	//String agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152";
    	//String url = "http://m.yixun.com";
    	
    	public MyWebThread(String strURL) {
    		url = strURL;
    	}
    	
    	public void run() {
    		//visitWithApacheHttpClient(url);
    		visitWithAndroidHttpClient(url);
        		
    	} 	
    }
    
    public void visitWithApacheHttpClient(String url) {
    	
    	//for umeng tracking
		MobclickAgent.onEventBegin(this, "Apache_http_visit");
		
    	String data = null;    	
    	HttpGet get = new HttpGet(url);
    	
        BasicHttpParams httpParams = new BasicHttpParams();  

        HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);  
        HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);  
        HttpConnectionParams.setSocketBufferSize(httpParams, 102400);  

        HttpClientParams.setRedirecting(httpParams, true);  

        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";  
        HttpProtocolParams.setUserAgent(httpParams, userAgent);  

        HttpClient httpClient = new DefaultHttpClient(httpParams);  
    	
    	Log.e("visitWithApacheHttpClient", "prepare to start HTTP conn...");
    	
		try {
			HttpResponse response = httpClient.execute(get);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				data = EntityUtils.toString(response.getEntity());
				Log.e("visitWithApacheHttpClient", String.valueOf(response.getStatusLine().getStatusCode()));
			} 
			
		}
		catch (ClientProtocolException e) {  
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		    Log.e("visitWithApacheHttpClient", e.toString());
		} catch (IOException e) {  
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		    Log.e("visitWithApacheHttpClient", e.toString());
		} catch (Exception e) {    
			e.printStackTrace();  
			Log.e("visitWithApacheHttpClient", e.toString());
		}
		finally {
			//for umeng tracking
			MobclickAgent.onEventBegin(this, "Apache_http_visit");
		}
    }
    
	public void visitWithAndroidHttpClient(String url) {
		
		//for umeng tracking
		MobclickAgent.onEventBegin(this, "Android_http_visit");
		Log.e("visitWithApacheHttpClient", "start to track event...");
		
		Log.e("url2", url);
		String agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152";
		AndroidHttpClient client = AndroidHttpClient.newInstance(agent);
    	try {
    		HttpGet get = new HttpGet(url);
    		HttpResponse resp = client.execute(get);
    		
    		int retCode = resp.getStatusLine().getStatusCode();
    		
    		strHttpCode = String.valueOf(retCode);
    		
    		//Log.e("visitWithAndroidHttpClient", "after execute");
    		
    		if (retCode == HttpStatus.SC_OK) {
    			String respBody = EntityUtils.toString(resp.getEntity());
    			
    			strHttpBody = respBody;
    			Log.e("visitWithAndroidHttpClient", respBody);
    			//get all response HTTP headers 
    			Header[] headers = resp.getAllHeaders();
    			for (Header header:headers) {
    				Log.e(header.getName(), header.getValue());
    			}
    			
    			
    			//HttpEntity entity = resp.getEntity();
    			
    			//read to string line
    			/*InputStream in = entity.getContent();
    			if (null == in) {
    				Log.e("null", "response body is empty");
    			}
    			else {
    				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    				String line = "";
    				while (null != (line=reader.readLine())) {
    					Log.e("body", line);
    				}
    			}
    			*/
    			
    			//parse as JSON
    			//String retStr = EntityUtils.toString(entity);
    			//jsonParser(retStr);
    				
    		}
    		else {
    			Log.e("visitWithAndroidHttpClient", String.valueOf(retCode));
    		}
    		
    		client.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		Log.e("visitWithAndroidHttpClient", e.toString());
    	}
    	finally {
    		client.close();
    		//for umeng tracking
    		MobclickAgent.onEventEnd(this, "Android_http_visit");
    		Log.e("visitWithApacheHttpClient", "end to track event...");
    	}
		
	}
	
	public void jsonParser(String jsonStr) {
		Log.e("JSON", jsonStr);
		
		try {
			JSONObject json = new JSONObject(jsonStr);
			
			
			String str = json.getString("errno");
			Log.e("JSON", str);
			
			JSONArray jsonArray = json.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jo = (JSONObject)jsonArray.opt(i);
				Log.e("JSON", jo.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
    		Log.e("Network thread", e.toString());
		}
	}
	
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);
	  
	      String device_id = tm.getDeviceId();
	      
	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	          
	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }
	      
	      json.put("device_id", device_id);
	      
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
}
