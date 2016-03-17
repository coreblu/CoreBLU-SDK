package com.coreblu.sample;

import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import coreblu.SDK.Configuration.CharacteristicsResponce;
import coreblu.SDK.Configuration.CorebluiBeaconCharacteristic;
import coreblu.SDK.Configuration.CorebluiBeaconConfiguration;
import coreblu.SDK.Configuration.CorebluiBeaconConfiguration.FirmwareUpdateProgress;
import coreblu.SDK.Configuration.CorebluiBeaconConfiguration.TagInfo;
import coreblu.SDK.Configuration.OtaResult;

public class WriteConfiguration extends Activity implements CorebluiBeaconConfiguration.CallBack{

	private CorebluiBeaconConfiguration mCorebluiBeaconConfiguration;
	private TextView output;
	private Button write,buzzer,led;
	boolean alertReady = false;
	private boolean connected = false;
	private final String TAG = getClass().getSimpleName().toString();

	@Override
	public void onDestroy(){
		super.onDestroy();
		mCorebluiBeaconConfiguration.onDestroy();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_activity);
		output = (TextView)  findViewById(R.id.info);
		write = (Button) findViewById(R.id.write);
		buzzer = (Button) findViewById(R.id.buzzer);
		led = (Button) findViewById(R.id.led);

		BluetoothDevice bd = getIntent().getParcelableExtra("device");
		if(bd==null)
			finish();

		mCorebluiBeaconConfiguration = CorebluiBeaconConfiguration.getInstance(getApplicationContext(),bd,this);
		mCorebluiBeaconConfiguration.connect();

		write.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				write();
				//mCorebluiBeaconConfiguration.updateFirmware(WriteConfiguration.this);

			}
		});

		
		buzzer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(connected)
				{
					mCorebluiBeaconConfiguration.TurnOnBuzzer();
				}

				else
				{
					showToast("Not Connected");
				}
			}
		});

		led.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(connected)
				{
					mCorebluiBeaconConfiguration.TurnOnLED();
				}

				else
				{
					showToast("Not Connected");
				}

			}
		});
	}



	public void write()
	{
		if(!connected)
		{
			showToast("Not Connected");
			return;
		}
		showToast("writing...");

		int major = 9;
		int minor = 9;
		UUID mUUID = UUID.fromString("8c5e1368-1269-11e5-9493-1697f925ec79");
		float interval = Float.parseFloat("0.1");
		int rssi = -2;
		int conneatTimeout = 255;
		int txpower = 1;//for 4dbm see java doc
		CorebluiBeaconCharacteristic mCorebluiBeaconCharacteristic = new CorebluiBeaconCharacteristic(major, minor, rssi, mUUID, txpower, interval, conneatTimeout);
		mCorebluiBeaconConfiguration.setiBeaconCharacteristic(mCorebluiBeaconCharacteristic);
	}




	@Override
	public void onStateChanged(int state) {
		// TODO Auto-generated method stub
		if(state == CorebluiBeaconConfiguration.STATE_CONNECTED)
		{
			showToast("Connected");
			connected=true;

		}

		if(state == CorebluiBeaconConfiguration.STATE_DISCONNECTED)
		{
			showToast("Disconnected");
			connected=false;
		}

		if(state == CorebluiBeaconConfiguration.STATE_CONNECTING)
		{
			showToast("Connecting");
			connected=false;
		}
	}

	@Override
	public void onUuidReady(String Uuid) {
		// TODO Auto-generated method stub
		logi("Uuid="+Uuid);
		output.setText(output.getText().toString()+"\nUuid="+Uuid);
	}

	@Override
	public void onConnectTimeoutReady(int ConnectTimeout) {
		// TODO Auto-generated method stub
		logi("ConnectTimeout="+ConnectTimeout);
		output.setText(output.getText().toString()+"\nConnectTimeout="+ConnectTimeout);
	}

	@Override
	public void onTxPowerReady(int TxPower) {
		// TODO Auto-generated method stub
		logi("TxPower="+TxPower);
		output.setText(output.getText().toString()+"\nTxPower="+TxPower);
	}

	@Override
	public void onMajorMinorReady(int Major, int Minor) {
		// TODO Auto-generated method stub
		logi("Major="+Major+"Minor="+Minor);
		output.setText(output.getText().toString()+"\nMajor="+Major+"Minor="+Minor);
	}

	@Override
	public void onIntervalReady(float Interval) {
		// TODO Auto-generated method stub
		logi("Interval"+Interval);
		output.setText(output.getText().toString()+"\nInterval"+Interval);
	}


	@Override
	public void onRssiReady(int Rssi) {
		// TODO Auto-generated method stub
		logi("Calibirated Rssi"+Rssi);
		output.setText(output.getText().toString()+"\nCalibirated Rssi"+Rssi);
	}

	@Override
	public void onCharacteristicsWrite(
			CharacteristicsResponce paramCharacteristicsResponce) {
		// TODO Auto-generated method stub
		if(paramCharacteristicsResponce.getWriteResponce() == CharacteristicsResponce.WRITE_OK)
		{
			showToast("Write ok..");
			logi("Write ok..");
		}

		else
		{
			showToast("Write failed..");
			logi("Write failed..");
		}
	}

	@Override
	public void onFirmwareUpdateAvailable(String newVersion , String currentVersion) {
		// TODO Auto-generated method stub
		updateFirmware(newVersion,currentVersion );

	}

	@Override
	public void onTagInfoReady(TagInfo info) {
		// TODO Auto-generated method stub
		output.setText(output.getText().toString()+"\nTagInfo:"+info.getTagName()+" v"+info.getTagVersion());		
	}

	@Override
	public void onError(int ERROR) {
		// TODO Auto-generated method stub
		switch (ERROR) {
		case CorebluiBeaconConfiguration.ERROR_UNSUPPORTED_DEVICE:
			showToast("Error:Device not supported");
			break;

		case CorebluiBeaconConfiguration.ERROR_DEVICE_BUSY:
			showToast("Device busy please try again");
			break;

		case CorebluiBeaconConfiguration.ERROR_DEVICE_CONNECTION:
			showToast("Connection Error...");
			break;
		default:
			break;
		}

	}

	private void showToast(final String msg)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(WriteConfiguration.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void updateFirmware(String newVersion , String currentVersion){
		FragmentManager fm = getFragmentManager();
		UpdateDialog newFragment = new UpdateDialog(newVersion,currentVersion);
		newFragment.show(fm, "abc");
	}

	private void logi(String msg)
	{
		Log.i(TAG , msg);
	}

	public class UpdateDialog extends DialogFragment {

		String version;
		String cversion;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//setCancelable(false);
		}

		public UpdateDialog(final String version ,final String cversion){
			this.version = version;
			this.cversion = cversion;
		}




		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Button update,cancel;
			final ProgressBar progress;
			final TextView message;
			final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			setTitle("Update Tag Firmware");

			LayoutInflater inflater = getActivity().getLayoutInflater();

			View dView = inflater.inflate(R.layout.update_firmware,  null);
			update = (Button) dView.findViewById(R.id.update);
			cancel = (Button) dView.findViewById(R.id.cancel);
			message = (TextView) dView.findViewById(R.id.message);
			progress = (ProgressBar) dView.findViewById(R.id.update_progress);

			progress.setScaleY(2f);

			message.setText("Firmware version v"+version+" is available, current version is v"+cversion+" do you want to update?");
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});

			update.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(update.getText().toString().equals("dismiss"))
					{dismiss();return;}

					cancel.setVisibility(View.GONE);
					update.setText("Updating");
					update.setEnabled(false);
					mCorebluiBeaconConfiguration.updateFirmware(new FirmwareUpdateProgress() {

						@Override
						public void onUpdateStart(String deviceAddress) {
							// TODO Auto-generated method stub
							onUISetText(message, "Updating..");
							onUISetProfress(progress,message, "Updating..",0);
						}

						@Override
						public void onUpdateComplete(String deviceAddress) {
							// TODO Auto-generated method stub
							onUISetText(message, "Update Complete");
						}

						@Override
						public void onProgressChanged(String deviceAddress, int percent,
								float speed, float avgSpeed, int currentPart, int partsTotal) {
							// TODO Auto-generated method stub
							onUISetProfress(progress,message,"Updating..."+percent+"% completed",percent);
						}

						@Override
						public void onError(String deviceAddress, int errorType) {
							// TODO Auto-generated method stub
							String msg="";
							switch(errorType){
							case CorebluiBeaconConfiguration.UPDATE_ERROR_CONNECTION_ERROR:
								msg = "Connection error";
								break;
							case CorebluiBeaconConfiguration.UPDATE_ERROR_ENTER_UPDATE_MODE_FAILED:
								msg = "Failed to enter update mode";
								break;
							case CorebluiBeaconConfiguration.UPDATE_ERROR_FIRMWARE_UPTO_DATE:
								msg = "Firmware alredy upto date";
								break;
							case CorebluiBeaconConfiguration.UPDATE_ERROR_UPDATE_ABORTED:
								msg = "Update aborted";
								break;
							case CorebluiBeaconConfiguration.UPDATE_ERROR_RE_CONFIGURE_ERROR:
								msg = "Firmware updated but writing old values failed";
								break;
							default:
								msg="Unknown error";
								break;
							}

							onUISetText(message, "Update Failed :"+msg);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									update.setEnabled(true);
									update.setText("dismiss");
								}
							});
						}

						@Override
						public void onDeviceConnecting(String deviceAddress) {
							// TODO Auto-generated method stub
							onUISetText(message, "Connecting...");
						}

						@Override
						public void OnDownloadStart() {
							// TODO Auto-generated method stub
							//message.setText("Downloading...");
							onUISetText(message, "Downloading...");

						}

						@Override
						public void OnDownloadProgressUpdate(int percent) {
							// TODO Auto-generated method stub
							onUISetProfress(progress,message,"Downloading..."+percent+"% completed",percent);
						}

						@Override
						public void OnDownloadError(String err) {
							// TODO Auto-generated method stub
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									update.setEnabled(true);
									update.setText("dismiss");
								}
							});
						}

						@Override
						public void EnteringUpdateMode() {
							// TODO Auto-generated method stub
							onUISetText(message, "Entering Update Mode");

						}

						@Override
						public void OnReConfiguring(String status) {
							// TODO Auto-generated method stub
							onUISetText(message, status);
						}


						@Override
						public void onOperationComplete(OtaResult result) {
							// TODO Auto-generated method stub
							if(result.isOtaSuccessfull())
								onUISetText(message, "Update Completed Successfully");
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									//dismiss();
									update.setEnabled(true);
									update.setText("dismiss");
								}
							});
						}
					});

				}
			});


			builder.setView(dView);
			AlertDialog dialog =  builder.create();
			dialog.setTitle("Firmware Update");
			return dialog;
		}
	}

	private void onUISetProfress(final ProgressBar progress, final TextView message ,final String msg , final int percent){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				message.setText(msg);
				progress.setProgress(percent);
			}
		});

	}

	private void onUISetText(final TextView message ,final String msg ){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				message.setText(msg);
			}
		});

	}

}
