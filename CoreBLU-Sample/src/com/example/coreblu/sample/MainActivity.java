package com.example.coreblu.sample;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import coreblu.SDK.Beacons.AnyBeacon;
import coreblu.SDK.Beacons.Beacon;
import coreblu.SDK.Beacons.iBeacon;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager.AnyBeaconListener;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager.iBeaconListener;

public class MainActivity extends Activity {

	private Button startIbeaconScan;
	private Button startAllBeaconScan;
	boolean scanningAll=false,scanningIb=false;
	private CorebluDeviceManager mCorebluDeviceManager;
	private ListAdapter anyBeaconAdapter;
	private ListAdapterIbeacon mListAdapterIbeacon;
	private ListView listv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCorebluDeviceManager = new CorebluDeviceManager(MainActivity.this);
		mListAdapterIbeacon = new ListAdapterIbeacon(this , new ArrayList<iBeacon>() , R.layout.beacon_list_layout);
		anyBeaconAdapter = new ListAdapter(this , new ArrayList<AnyBeacon>() , R.layout.beacon_list_layout);


		listv = (ListView)findViewById(R.id.beacon_listview);


		listv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				// TODO Auto-generated method stub
				if(listv.getAdapter() instanceof ListAdapterIbeacon)
				{
					iBeacon ib = mListAdapterIbeacon.getobj(pos);
					if(ib.getType().equals(Beacon.BEACON_TYPE_COREBLU_IBEACON))
					{
						if(!ib.isConnectable())
						{
							showToast("Beacon is not connectable");
							return true;
						}
						stopScanIBeacons();
						ShowConfigDialog(ib);
					}
					else
					{
						showToast("Only coreblu ibeacons are configurable..");
					}
				}

				else
				{
					showToast("Only coreblu ibeacons are configurable..");
				}
				return true;
			}
		}); 


		startAllBeaconScan = (Button) findViewById(R.id.sallbeacon);
		startAllBeaconScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Another Scan is running
				if(scanningIb)
				{
					showToast("Please stop ibeacon scan first");
					return;
				}

				//Scan is already started
				if(scanningAll)
				{
					((Button) v).setText("Start All Beacon Scan");
					stopScanAllBeacons();
				}

				else
				{
					//Starting Scan
					((Button) v).setText("Stop All Beacon Scan");
					startScanAllBeacons();
					listv.setAdapter(anyBeaconAdapter);
				}
			}
		});


		startIbeaconScan = (Button) findViewById(R.id.sibeacon);
		startIbeaconScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Another Scan is running
				if(scanningAll)
				{
					showToast("Please stop all beacon scan first");
					return;
				}

				//Scan is already started
				if(scanningIb)
				{
					((Button) v).setText("Start IBeacon Scan");
					stopScanIBeacons();
				}
				else
				{
					//Starting Scan
					((Button) v).setText("Stop IBeacon Scan");
					startScanIBeacons();
					listv.setAdapter(mListAdapterIbeacon);
				}
			}
		});
	}


	private void stopScanAllBeacons()
	{
		mCorebluDeviceManager.stopAllBeaconScan();
		startAllBeaconScan.setText("Start All Beacon Scan");
		scanningAll=false;
	}

	private void startScanAllBeacons()
	{

		scanningAll=true;
		mCorebluDeviceManager.startAllBeaconScan(new AnyBeaconListener() {

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				arg0.printStackTrace();

			}

			@Override
			public void onAnyBeaconFound(AnyBeacon arg0) {
				// TODO Auto-generated method stub
				Log.i("Beacon Found",arg0.getMacAddress());
				anyBeaconAdapter.add(arg0);
				//UpdateInfo(arg0.getMacAddress());

			}
		});
	}


	private void stopScanIBeacons()
	{
		mCorebluDeviceManager.stopiBeaconScan();
		scanningIb=false;
		startIbeaconScan.setText("Start iBeacon Scan");
	}

	private void startScanIBeacons()
	{

		scanningIb=true;
		mCorebluDeviceManager.startiBeaconScan(new iBeaconListener() {

			@Override
			public void oniBeaconFound(iBeacon arg0) {
				// TODO Auto-generated method stub
				Log.i("IBeacon Found",arg0.getMacAddress());
				//UpdateInfo(arg0.getMacAddress());
				mListAdapterIbeacon.add(arg0);
			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void showToast(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(scanningIb)
		{
			mCorebluDeviceManager.stopiBeaconScan();
		}

		if(scanningAll)
		{
			mCorebluDeviceManager.stopAllBeaconScan();
		}

	}


	private void ShowConfigDialog(final iBeacon ib) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this);
		builder.setTitle("Configuration");
		builder.setMessage("Do you want to configure Device:"+ib.getMacAddress());
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		builder.setPositiveButton("Configure",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				//writConfiguration(ib);
				Intent i = new Intent(MainActivity.this , WriteConfiguration.class);
				i.putExtra("device", ib.getDevice());
				startActivity(i);
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
