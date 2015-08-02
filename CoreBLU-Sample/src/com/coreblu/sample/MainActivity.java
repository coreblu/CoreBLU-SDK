package com.coreblu.sample;

import java.util.ArrayList;
import java.util.Collection;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.coreblu.sample.R;
import coreblu.SDK.Beacons.AnyBeacon;
import coreblu.SDK.Beacons.Beacon;
import coreblu.SDK.Beacons.iBeacon;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager.AnyBeaconListener;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager.IBeaconsInRegionListner;
import coreblu.SDK.CorebluDevice.CorebluDeviceManager.iBeaconListener;
import coreblu.SDK.CorebluDevice.Region;

public class MainActivity extends Activity {

	private Button startIbeaconScan,startAllBeaconScan,startScanningInRegion;
	private boolean scanningAllBeacons=false,scanningIbeacons=false,scanningInRegion = false;
	private CorebluDeviceManager mCorebluDeviceManager;
	private ListAdapter anyBeaconAdapter;
	private ListAdapterIbeacon mListAdapterIbeacon;
	private ListView listv;
	private final String TAG = getClass().getSimpleName();

	private final String stopIbeaconScan = "Please stop ibeacon scan first";
	private final String stopAllBeaconScan = "Please stop all beacon scan first";
	private final String stopInRegionBeaconScan = "Please stop in region beacon scan first";
	private final int REQUEST_ENABLE_BT = 1;

	/**
	 * Checks whether the Bluetooth adapter is enabled.
	 */
	private boolean isBleEnabled() {
		final BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		final BluetoothAdapter ba = bm.getAdapter();
		return ba != null && ba.isEnabled();
	}

	/**
	 * Checks whether the device supports Bluetooth Low Energy communication
	 * 
	 * @return <code>true</code> if BLE is supported, <code>false</code> otherwise
	 */
	private boolean ensureBleExists() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Unsupported Device..", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/**
	 * Tries to start Bluetooth adapter.
	 */
	private void enableBle() {
		final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	}


	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode != RESULT_OK) {
				finish();
			} 
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (ensureBleExists()  && !isBleEnabled())
		{
			enableBle();
		}

		mCorebluDeviceManager =CorebluDeviceManager.getInstance(getApplicationContext());
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
						stopScan();
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

		startScanningInRegion = (Button) findViewById(R.id.sinregion);
		startScanningInRegion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Another Scan is running
				if(scanningIbeacons)
				{
					showToast(stopIbeaconScan);
					return;
				}
				//Another Scan is running
				else if(scanningAllBeacons)
				{
					showToast(stopAllBeaconScan);
					return;
				}

				//Scan is already started
				if(scanningInRegion)
				{
					stopScan();
				}

				else
				{
					//Starting Scan

					listv.setAdapter(mListAdapterIbeacon);
					startScanIBeaconsInRegion();

				}
			}
		});

		startAllBeaconScan = (Button) findViewById(R.id.sallbeacon);
		startAllBeaconScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Another Scan is running
				if(scanningIbeacons)
				{
					showToast(stopIbeaconScan);
					return;
				}
				//Another Scan is running
				if(scanningInRegion)
				{
					showToast(stopInRegionBeaconScan);
					return;
				}

				//Scan is already started
				if(scanningAllBeacons)
				{
					//((Button) v).setText("Start All Beacon Scan");
					stopScan();

				}
				else
				{
					//Starting Scan

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
				if(scanningAllBeacons)
				{
					showToast("Please stop all beacon scan first");
					return;
				}
				//Another Scan is running
				if(scanningInRegion)
				{
					showToast(stopInRegionBeaconScan);
					return;
				}

				//Scan is already started
				if(scanningIbeacons)
				{

					stopScan();
				}
				else
				{
					//Starting Scan

					startScanIBeacons();
					listv.setAdapter(mListAdapterIbeacon);
				}
			}
		});
	}


	private void stopScan()
	{
		mCorebluDeviceManager.stopScan();

		if(scanningAllBeacons)
		{
			scanningAllBeacons=false;
			startAllBeaconScan.setText("Start All Beacon Scan");

		}

		if(scanningIbeacons)
		{
			scanningIbeacons=false;
			startIbeaconScan.setText("Start IBeacon Scan");
		}


		if(scanningInRegion)
		{
			scanningInRegion = false;
			startScanningInRegion.setText("Start beacon in region Beacon Scan");
		}
	}

	private void startScanAllBeacons()
	{

		scanningAllBeacons=true;
		startAllBeaconScan.setText("Stop All Beacon Scan");
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


			}
		});
	}


	private void startScanIBeacons()
	{

		final boolean coreBluTags = false;
		scanningIbeacons=true;
		startIbeaconScan.setText("Stop IBeacon Scan");
		mListAdapterIbeacon.clear();

		mCorebluDeviceManager.startiBeaconScan(new iBeaconListener() {

			@Override
			public void oniBeaconFound(iBeacon arg0) {
				// TODO Auto-generated method stub
				Log.i("IBeacon Found",arg0.getMacAddress());
				if(arg0.getType().equals(Beacon.BEACON_TYPE_COREBLU_IBEACON))
				{
					Log.i(TAG,"BatteryVoltage="+arg0.getbatteryVoltage());
				}
				if(coreBluTags)
				{
					if(arg0.getType().equals(Beacon.BEACON_TYPE_COREBLU_IBEACON))

						mListAdapterIbeacon.add(arg0);
				}
				else{
					mListAdapterIbeacon.add(arg0);
				}
			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void startScanIBeaconsInRegion()
	{
		scanningInRegion=true;
		startScanningInRegion.setText("Stop beacon in region Scan");
		mListAdapterIbeacon.clear();
		mCorebluDeviceManager.startiBeaconScan(new IBeaconsInRegionListner() {

			@Override
			public void IBeaconsInRegion(Collection<iBeacon> beacons , Region region) {
				// TODO Auto-generated method stub
				Log.i("Beacons Found" ,"Beacon Count:"+beacons.size()+" Region:"+region.getName());

				mListAdapterIbeacon.add(beacons);
			}
		} , new Region("bee", "8c5e1368-1269-11e5-9493-1697f925ec7b",null, null));
	}

	private void showToast(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCorebluDeviceManager.stopScan();
	}




	private void ShowConfigDialog(final iBeacon ib) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

				Intent i = new Intent(MainActivity.this , WriteConfiguration.class);
				i.putExtra("device", ib.getDevice());
				startActivity(i);
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
