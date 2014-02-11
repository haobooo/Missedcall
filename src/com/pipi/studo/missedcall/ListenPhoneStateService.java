package com.pipi.studo.missedcall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;


public class ListenPhoneStateService extends Service {
	private static final String TAG = "ListenPhoneStateService";
	private static final boolean DEBUG = true;
	private int mPreStatus = -1;
	private String mCallNumber = null;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent it) {
			if (!it.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	            int status = tm.getCallState();
	            
	            if (status == TelephonyManager.CALL_STATE_RINGING) {
	            	mCallNumber = it.getStringExtra("incoming_number");
	            }
	            
	            if ((status == TelephonyManager.CALL_STATE_IDLE) && (mPreStatus == TelephonyManager.CALL_STATE_RINGING)) {
	            	// Show missed call indicator.
	            	showMissedcallNotification(context, mCallNumber);
	            	
	            	this.abortBroadcast();
	            }
	            
	            mPreStatus = status;
			}
		}
		
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("popupIncall", "ListenPhoneStateService onCreate");
        
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        filter.setPriority(1000);
        
        registerReceiver(mReceiver, filter);
        
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.unregisterReceiver(mReceiver);
	}
	
	private void showMissedcallNotification(Context context, String number) {
		String title = number == null ? "" : number;
		
//		Notification noti = new Notification.Builder(context)
//        .setContentTitle("Missed call from " + title)
//        .setContentText("")
//        .setSmallIcon(R.drawable.ic_launcher)
//        .build();
		
		Notification noti = new Notification(R.drawable.ic_launcher, "Missed call from " + title, 0);
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		
		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
		contentView.setTextViewText(R.id.nofi_title, title);
		
		noti.contentView = contentView;
		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(100, noti);
	}
}