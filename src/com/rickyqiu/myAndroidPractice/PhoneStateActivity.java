package com.rickyqiu.myAndroidPractice;

import java.util.List;

import com.rickyqiu.myAndroidPractice.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneStateActivity extends Activity {

	private String TAG = "PhoneStateActivity";
	private TelephonyManager tel;
	private MyPhoneStateListener MyListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonestate);
		
		Log.e(TAG, "init phone state listener...");
		
		MyListener = new MyPhoneStateListener();
		tel = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phonestate, menu);
		return true;
	}

	@Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);  
    }  
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        tel.listen(MyListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);  
    }  
    
	private class MyPhoneStateListener extends PhoneStateListener {
		/*得到信号的强度由每个tiome供应商,有更新*/  
        TextView myText = (TextView)findViewById(R.id.textViewCdma);  
        TextView myText1=(TextView)findViewById(R.id.textViewGsm);  
        TextView myText2=(TextView)findViewById(R.id.textViewCDMA2);  
        TextView myText3=(TextView)findViewById(R.id.textViewGSM2);  
        
        @Override  
        public void onSignalStrengthsChanged(SignalStrength signalStrength){  
  
	        super.onSignalStrengthsChanged(signalStrength);//调用超类的该方法，在网络信号变化时得到回答信号  
	  
	        Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();//cinr：Carrier to Interference plus Noise Ratio（载波与干扰和噪声比）  
	        myText.setText("CDMA RSSI = "+ String.valueOf(signalStrength.getCdmaDbm()));  
	        myText1.setText("GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()));  
	        //CellSignalStrengthWcdma
	        //CellSignalStrengthGsm
        }  
        
        /*
        public void onCDMASignalStrengthsChanged(CellSignalStrengthWcdma signalStrengthCDMA){  
        	  
	        super.(signalStrengthCDMA);//调用超类的该方法，在网络信号变化时得到回答信号  
	  
	        Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();//cinr：Carrier to Interference plus Noise Ratio（载波与干扰和噪声比）  
	        myText.setText("CDMA RSSI = "+ String.valueOf(signalStrength.getCdmaDbm()));  
	        myText1.setText("GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()));  
        }  
        
        public void onGSMSignalStrengthsChanged(CellSignalStrengthGsm signalStrengthgms){  
      	  
	        super.onSignalStrengthsChanged(signalStrength);//调用超类的该方法，在网络信号变化时得到回答信号  
	  
	        Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();//cinr：Carrier to Interference plus Noise Ratio（载波与干扰和噪声比）  
	        myText.setText("CDMA RSSI = "+ String.valueOf(signalStrength.getCdmaDbm()));  
	        myText1.setText("GSM Cinr = "+ String.valueOf(signalStrength.getGsmSignalStrength()));  
	        //CellSignalStrengthWcdma
	        //CellSignalStrengthGsm
        }  
        */
	}
	
}
