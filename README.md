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

      CorebluiBeaconConfiguration mCorebluiBeaconConfiguration= 
      new CorebluiBeaconConfiguration(Activity,iBeacon#getDevice,mCallBack);
      boolean connected = false ;
      
      mCorebluiBeaconConfiguration.connect();
      
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
      		
      
When connected configuration can be written as

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
            
            


