# CoreBLU-SDK

Averos
www.averos.com

SDK documentation
http://www.coreblu.com/documentation/sdk/config-app/

Provides asynchoronous scanning functionality for all beacon.
Provides asynchoronous scanning  functionality for ibeacon.
Provides confuguration for CoreBLU beacons.
Turn on Led / Buzzer of CoreBLU beacon.

Scanning:

start scanning all beacons

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
				
				

			}
		});
	
Stop scanning all beacons

      mCorebluDeviceManager.stopAllBeaconScan();


Start scanning ibeacons

      mCorebluDeviceManager.startiBeaconScan(new iBeaconListener() {

			@Override
			public void oniBeaconFound(iBeacon arg0) {
				// TODO Auto-generated method stub
				Log.i("IBeacon Found",arg0.getMacAddress());
				
				
			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub

			}
		});

Stop scanning ibeacons
      mCorebluDeviceManager.stopiBeaconScan();

Configuration:

public class WriteConfiguration extends Activity implements CorebluiBeaconConfiguration.CallBack{

	CorebluiBeaconConfiguration mCorebluiBeaconConfiguration;
	CorebluiBeaconCharacteristic mCorebluiBeaconCharacteristic;
	boolean connected = false;




	private TextView output;
	private Button write,buzzer,led;

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

		mCorebluiBeaconConfiguration = new CorebluiBeaconConfiguration(this,bd,this);
		mCorebluiBeaconConfiguration.connect();

		write.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				write();

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

				else{
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

				else{
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

		int major = 2;
		int minor = 2;
		UUID mUUID = UUID.fromString("8c5e1368-1269-11e5-9493-1697f925ec7b");
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
			mCorebluiBeaconConfiguration.connect();
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
	public void onTagInfoReady(String[] TagInfo) {
		// TODO Auto-generated method stub
		logi("TagInfo"+TagInfo[0]+" "+TagInfo[1]);
		output.setText(output.getText().toString()+"\nTagInfo"+TagInfo[0]+" "+TagInfo[1]);

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

	private void logi(String msg)
	{
		Log.i(getClass().getSimpleName().toString() , msg);
	}





      
      CorebluiBeaconConfiguration mCorebluiBeaconConfiguration= 
      new CorebluiBeaconConfiguration(Activity,iBeacon#getDevice,mCallBack);
      boolean connected = false ;
      
      CorebluiBeaconConfiguration.connect();
      
      CorebluiBeaconConfiguration.CallBack mCallBack = new CorebluiBeaconConfiguration.CallBack(){

      		@Override
      		public void onStateChanged(int paramInt) {
      			// TODO Auto-generated method stub
      			if(state == CorebluiBeaconConfiguration.STATE_CONNECTED)
          		{
          			showToast("Connected");
          			connected=true;
          
          		}
      			
      			
      		}

      		@Override
      		public void onUuidReady(String paramString) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onConnectTimeoutReady(int paramInt) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onTxPowerReady(int paramInt) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onMajorMinorReady(int paramInt1, int paramInt2) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onIntervalReady(float paramFloat) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onTagInfoReady(String[] paramArrayOfString) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onRssiReady(int paramInt) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onCharacteristicsWrite(
      				CharacteristicsResponce paramCharacteristicsResponce) {
      			// TODO Auto-generated method stub
      			
      		}

      		@Override
      		public void onError(int paramInt) {
      			// TODO Auto-generated method stub
      			
      		}
      		};
      		
      
      //Method to write Configuration
      	public void write()
      	{
      		if(!connected)
      		{
      			showToast("Not Connected");
      			return;
      		}
      		showToast("writing...");
      
      		int major = 2;
      		int minor = 2;
      		UUID mUUID = UUID.fromString("8c5e1368-1269-11e5-9493-1697f925ec7b");
      		float interval = Float.parseFloat("0.1");
      		int rssi = -2;
      		int conneatTimeout = 255;
      		int txpower = 1;//for 4dbm see java doc
      		CorebluiBeaconCharacteristic mCorebluiBeaconCharacteristic = new CorebluiBeaconCharacteristic(major, minor, rssi,            mUUID, txpower, interval, conneatTimeout);
      		mCorebluiBeaconConfiguration.setiBeaconCharacteristic(mCorebluiBeaconCharacteristic);
      
      
      	}
            
            


