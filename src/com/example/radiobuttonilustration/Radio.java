package com.example.radiobuttonilustration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Radio extends MapActivity {
	private RadioButton radioSexButton;
	private RadioButton radioModeButton;
	private Button btnDisplay;
	final RadioButton[] rb = new RadioButton[5];
	// private static final int DISCOVERY_REQUEST = 1;
	private boolean deviceFoundFlag;
	public BluetoothAdapter bluetoothAdapter;
	boolean bluetoothflag;
	Vector<String> firstDiscovery = new Vector<String>();
	Vector<String> secondDiscovery = new Vector<String>();
	Vector<String> thirdDiscovery = new Vector<String>();
	int slidingWindowCounter = 0;
	int count = 0;
	boolean presence, flag = false;
	int innerCount = 0;
	int firstPresenceFlag = 0;
	int connectionDisconnectionFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		Vector<String> pairedList = new Vector<String>();
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("");
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		if (adapter == null) {
			tv.append("\nBluetooth NOT supported. Aborting.");
			return;
		}

		adapter.startDiscovery();
		// Listing paired devices
		// tv.setText("");
		// tv.append("Devices Paired:\n");
		Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter()
				.getBondedDevices();

		for (BluetoothDevice device : devices) {
			pairedList.add(device.getName());
		}
		/*
		 * Enumeration<String> pairedPrintList = pairedList.elements(); while
		 * (pairedPrintList.hasMoreElements()) {
		 * tv.append(pairedPrintList.nextElement() + "\n"); }
		 */
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioSex);
		rg.setOrientation(RadioGroup.VERTICAL);// or RadioGroup.VERTICAL
		for (int i = 0; i < pairedList.size(); i++) {
			rb[i] = new RadioButton(this);
			rg.addView(rb[i]);
			// rb[i].setText(i+"");
			rb[i].setText(pairedList.get(i));
		}
		addListenerOnButton(rb, rg, tv);
	}

	public void addListenerOnButton(final RadioButton[] rb,
			final RadioGroup rg, final TextView tv) {

		// radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
		btnDisplay = (Button) findViewById(R.id.btnDisplay);
		final RadioGroup rg1 = (RadioGroup) findViewById(R.id.radioSex1);
		btnDisplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{

				// get selected radio button from radioGroup
				//int yes = 0;
				int selectedId = rg.getCheckedRadioButtonId();
				int selectedMode = rg1.getCheckedRadioButtonId();
				// find the radiobutton by returned id
				int id = rg.getCheckedRadioButtonId();
				if (id == -1) {
					Toast.makeText(Radio.this, "No device selected",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					radioSexButton = (RadioButton) findViewById(selectedId);

					radioModeButton = (RadioButton) findViewById(selectedMode);
					btnDisplay.setVisibility(View.INVISIBLE);
					/*
					 * Toast.makeText(Radio.this, radioSexButton.getText(),
					 * Toast.LENGTH_SHORT).show();
					 */
					tv.setText("");
					String a = (String) radioSexButton.getText();
					String mode = (String) radioModeButton.getText();
					// tv.setText("Device selected:\n"+a+"\n");
					// tv.append("Mode Selected:\n"+mode+"\n");
					count = 0;
					innerCount = 0;
					slidingWindowCounter = 0;
					flag = false;
					// presence=false;
					firstDiscovery.clear();
					secondDiscovery.clear();
					thirdDiscovery.clear();
					if (mode.equals("Energy Efficient mode")) {
						Toast.makeText(
								Radio.this,
								"Selected Energy Efficient Mode"
										+ "Device Selected is " + a,
								Toast.LENGTH_SHORT).show();
						periodicDeviceDiscoveryForEnergyEfficientMode(tv, a);
					} else {
						Toast.makeText(Radio.this,
								"Selected Normal Mode" + "Device Selected is " + a,
								Toast.LENGTH_SHORT).show();
						periodicDeviceDiscoveryForNormalMode(tv, a);
					}

				}
				
				// periodicDeviceDiscovery(tv,a);
			}

		});

	}

	private void periodicDeviceDiscoveryForNormalMode(final TextView tv,
			final String deviceName) {
		tv.append("\nDiscovered Devices\n");
		final double[] latitudes = new double[3];
		final double[] longitudes = new double[3];
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null)
			return;
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String bluetooth = new String("00:26:C6:4A:99:EA");
					BluetoothDevice remote = bluetoothAdapter
							.getRemoteDevice(bluetooth);

					if (slidingWindowCounter % 3 == 0) {
						tv.append("6---\n");
						firstDiscovery.add(device.getName());
						
						
					}

					else if (slidingWindowCounter % 3 == 1) {
						tv.append("8---\n");
						secondDiscovery.add(device.getName());
						

					}

					else if (slidingWindowCounter % 3 == 2) 
					{
						tv.append("10---\n");
						thirdDiscovery.add(device.getName());
						
						
					}
					if (device.equals(remote)) 
					{
						deviceFoundFlag = true;
						// tv.append(device.getName() + "-inside"+ /*
						// device.getAddress()+ */"\n");
						// bluetoothAdapter.cancelDiscovery();
					}
				} 
				else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {

					if (!deviceFoundFlag) 
					{
						// tv.append("\nentered false\n");
						count++;
						// tv.append("Iteration "+count+"\n");
						if (slidingWindowCounter % 3 == 0) 
						{
							tv.append("7---\n");
							if (firstDiscovery.contains(deviceName)) 
							{
								// tv.append("contains" + "\n");
							}
							Enumeration<String> firstDiscoveryPrintList = firstDiscovery.elements();
							while (firstDiscoveryPrintList.hasMoreElements()) 
							{
								tv.append(firstDiscoveryPrintList.nextElement()+ "\n");
							}
							if(firstDiscovery.contains(deviceName))
							{
								Location location1 = getLocationDetails();
								latitudes[0] = location1.getLatitude();
								longitudes[0] = location1.getLongitude();
								tv.append(latitudes[0]+","+longitudes[0]+"\n");
							}
							else
							{
								latitudes[0] = 0.0;
								longitudes[0] = 0.0;
								//tv.append(latitudes[0]+","+longitudes[0]+"\n");
							}

						} 
						else if (slidingWindowCounter % 3 == 1) 
						{
							tv.append("9---\n");
							if (secondDiscovery.contains(deviceName)) 
							{
								// tv.append("contains" + "\n");
							}
							Enumeration<String> secondDiscoveryPrintList = secondDiscovery.elements();
							while (secondDiscoveryPrintList.hasMoreElements()) 
							{
								tv.append(secondDiscoveryPrintList.nextElement() + "\n");
							}
							if(secondDiscovery.contains(deviceName))
							{
								Location location2 = getLocationDetails();
								latitudes[1] = location2.getLatitude();
								longitudes[1] = location2.getLongitude();
								tv.append(latitudes[1]+","+longitudes[1]+"\n");
							}
							else
							{
								latitudes[1] = 0.0;
								longitudes[1] = 0.0;
								//tv.append(latitudes[1]+","+longitudes[1]+"\n");
							}
						} 
						else if (slidingWindowCounter % 3 == 2) 
						{
							tv.append("11---\n");
							if (thirdDiscovery.contains(deviceName)) 
							{
								// tv.append("contains" + "\n");
							}
							Enumeration<String> thirdDiscoveryPrintList = thirdDiscovery.elements();
							while (thirdDiscoveryPrintList.hasMoreElements()) 
							{
								tv.append(thirdDiscoveryPrintList.nextElement()+ "\n");
							}
							if(thirdDiscovery.contains(deviceName))
							{
								Location location3 = getLocationDetails();
								latitudes[2] = location3.getLatitude();
								longitudes[2] = location3.getLongitude();
								tv.append(latitudes[2]+","+longitudes[2]+"\n");
							}
							else
							{
								latitudes[2] = 0.0;
								longitudes[2] = 0.0;
								//tv.append(latitudes[2]+","+longitudes[2]+"\n");
							}
						}
						slidingWindowCounter++;
						if (count >= 3) 
						{
							tv.append("-----\n");
							for (int i = 0; i < latitudes.length; i++) 
							{
								tv.append(latitudes[i] + "," + longitudes[i]+ "\n");
							}
							tv.append("-----\n");
							innerCount++;
							// tv.append("12---\n");
							firstPresenceFlag++;
							presence = computeDifferences(firstDiscovery,secondDiscovery, thirdDiscovery, tv,deviceName);
							// tv.append(presence + "\n");
							double lat = 0, lng = 0;
							if (presence == true) 
							{
								lat = computeLatitude(firstDiscovery,secondDiscovery, thirdDiscovery, tv,deviceName, latitudes,firstPresenceFlag);
								lng = computeLongitude(firstDiscovery,secondDiscovery, thirdDiscovery, tv,deviceName, longitudes,firstPresenceFlag);
								// tv.append("Lat="+lat+","+"\n");
							}
							if (firstPresenceFlag == 3)
								firstPresenceFlag = 0;
							if (presence == true && flag == false) 
							{
								tv.append("Established for the first time\n");
								connectionDisconnectionFlag = 0;
								try 
								{
									updateWithFirstFoundLocation(tv,connectionDisconnectionFlag, lat,lng);
									// updateWithNewLocation(tv,connectionDisconnectionFlag);
								} 
								catch (JSONException e) 
								{
									e.printStackTrace();
								}
								flag = true;
							} 
							else if (presence == true && flag == true) 
							{
								tv.append("Already connected\n");
							} 
							else if (presence == false && flag == true) 
							{
								tv.append("Connection disconnected\n");
								connectionDisconnectionFlag = 1;
								try 
								{
									updateWithNewLocation(tv,connectionDisconnectionFlag);
								} 
								catch (JSONException e) 
								{
									e.printStackTrace();
								}
								flag = false;
							} 
							else if (presence == false && flag == true) 
							{
								// tv.append("Cannot find in the vicinity. Please select a different device\n");
							}
							// count=0;
							if (innerCount == 1) 
							{
								firstDiscovery.clear();
								// tv.append("first discovery cleared\n");
							} 
							else if (innerCount == 2) 
							{
								secondDiscovery.clear();
								// tv.append("second discovery cleared\n");
							} 
							else if (innerCount == 3) 
							{
								thirdDiscovery.clear();
								// tv.append("third discovery cleared\n");
							}
							if (innerCount == 3) 
							{
								innerCount = 0;
								// tv.append("\ninner count cleared\n");
							}
						}
						bluetoothAdapter.startDiscovery();
						bluetoothflag = false;
					}
				}
			}

		};
		// String aDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
		// startActivityForResult(new Intent(aDiscoverable), DISCOVERY_REQUEST);
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		bluetoothAdapter.startDiscovery();
	}

	private void updateWithFirstFoundLocation(TextView tv, int flag,
			double lat, double lng) throws JSONException {
		// String latLongString = "Lat:" + lat + "\nLong:" + lng + "\n";
		// tv.append(latLongString);
		tv.append("Lat:" + lat + "\nLong:" + lng + "\n");
		String json = getJson(lat, lng);
		// tv.append(json + "\n");
		if (json.equals("[]"))
			tv.append("Not a valid parking location\n");
		else {
			if (flag == 0) {
				parseJson(json, tv, lat, lng);
				tv.append("The Car is deparked\n");
				Toast.makeText(Radio.this, "The Car is Deparked",
						Toast.LENGTH_SHORT).show();
			}

			else if (flag == 1) {
				parseJson(json, tv, lat, lng);
				tv.append("The car is parked\n");
				Toast.makeText(Radio.this, "The Car is parked",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	private double computeLongitude(Vector<String> firstDiscovery,
			Vector<String> secondDiscovery, Vector<String> thirdDiscovery,
			TextView tv, String deviceName, double[] longitudes,
			int firstPresenceFlag) {
		double lng = 0.0;
		if (firstPresenceFlag == 1) 
		{
			if (firstDiscovery.contains(deviceName)) 
			{
				lng = longitudes[0];
			} 
			else if (secondDiscovery.contains(deviceName)) 
			{
				lng = longitudes[1];
			} 
			else if (thirdDiscovery.contains(deviceName)) 
			{
				lng = longitudes[2];
			}
		} 
		else if (firstPresenceFlag == 2) 
		{
			if (secondDiscovery.contains(deviceName)) 
			{
				lng = longitudes[1];
			} 
			else if (thirdDiscovery.contains(deviceName)) 
			{
				lng = longitudes[2];
			} 
			else if (firstDiscovery.contains(deviceName)) 
			{
				lng = longitudes[0];
			}
		} 
		else 
		{
			if (thirdDiscovery.contains(deviceName)) 
			{
				lng = longitudes[2];
			} 
			else if (firstDiscovery.contains(deviceName)) 
			{
				lng = longitudes[0];
			} 
			else if (secondDiscovery.contains(deviceName)) 
			{
				lng = longitudes[1];
			}
		}
		return lng;
	}

	private double computeLatitude(Vector<String> firstDiscovery,
			Vector<String> secondDiscovery, Vector<String> thirdDiscovery,
			TextView tv, String deviceName, double[] latitudes,
			int firstPresenceFlag) 
	{
		double lat = 0.0;
		if (firstPresenceFlag == 1) 
		{
			if (firstDiscovery.contains(deviceName)) 
			{
				tv.append("11---\n");
				lat = latitudes[0];
			} 
			else if (secondDiscovery.contains(deviceName)) 
			{
				tv.append("12---\n");
				lat = latitudes[1];
			} 
			else if (thirdDiscovery.contains(deviceName)) 
			{
				tv.append("13---\n");
				lat = latitudes[2];
			}
		} 
		else if (firstPresenceFlag == 2) 
		{
			if (secondDiscovery.contains(deviceName)) 
			{
				tv.append("21---\n");
				lat = latitudes[1];
			} 
			else if (thirdDiscovery.contains(deviceName)) 
			{
				tv.append("22---\n");
				lat = latitudes[2];
			} 
			else if (firstDiscovery.contains(deviceName)) 
			{
				tv.append("23---\n");
				lat = latitudes[0];
			}
		} 
		else 
		{
			if (thirdDiscovery.contains(deviceName)) 
			{
				tv.append("31---\n");
				lat = latitudes[2];
			} 
			else if (firstDiscovery.contains(deviceName)) 
			{
				tv.append("32---\n");
				lat = latitudes[0];
			} 
			else if (secondDiscovery.contains(deviceName)) 
			{
				tv.append("33---\n");
				lat = latitudes[1];
			}
		}
		return lat;
	}

	private Location getLocationDetails() {

		MapView view = (MapView) findViewById(R.id.themap);
		view.setBuiltInZoomControls(true);

		final MapController control = view.getController();

		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// Location location =
		// manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LocationListener listener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onLocationChanged(Location location) {

				control.setCenter(new GeoPoint((int) location.getLatitude(),
						(int) location.getLongitude()));

			}
		};
		/*
		 * manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
		 * 0, listener); Location location = manager
		 * .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		 */
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				listener);
		Location location4 = manager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// double lat = location4.getLatitude();
		// double lng = location4.getLongitude();
		return location4;
	}

	private void periodicDeviceDiscoveryForEnergyEfficientMode(
			final TextView tv, final String deviceName) {
		tv.append("\nDiscovered Devices\n");
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null)
			return;
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String bluetooth = new String("00:26:C6:4A:99:EA");
					BluetoothDevice remote = bluetoothAdapter
							.getRemoteDevice(bluetooth);

					if (slidingWindowCounter % 3 == 0) {
						tv.append("6---\n");
						firstDiscovery.add(device.getName());
					}

					else if (slidingWindowCounter % 3 == 1) {
						tv.append("8---\n");
						secondDiscovery.add(device.getName());
					}

					else if (slidingWindowCounter % 3 == 2) {
						tv.append("10---\n");
						thirdDiscovery.add(device.getName());
					}
					// tv.append(device.getName() + "-"+ /* device.getAddress()+
					// */"\n");

					if (device.equals(remote)) {
						deviceFoundFlag = true;
						// tv.append(device.getName() + "-inside"+ /*
						// device.getAddress()+ */"\n");
						// bluetoothAdapter.cancelDiscovery();
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					if (!deviceFoundFlag) {
						// tv.append("\nentered false\n");
						count++;
						// tv.append("Iteration "+count+"\n");
						if (slidingWindowCounter % 3 == 0) {
							tv.append("7---\n");
							if (firstDiscovery.contains(deviceName)) {
							}// tv.append("contains" + "\n");
							Enumeration<String> firstDiscoveryPrintList = firstDiscovery
									.elements();
							while (firstDiscoveryPrintList.hasMoreElements()) {
								tv.append(firstDiscoveryPrintList.nextElement()
										+ "\n");
							}

						} else if (slidingWindowCounter % 3 == 1) {
							tv.append("9---\n");
							if (secondDiscovery.contains(deviceName)) {
							}// tv.append("contains" + "\n");
							Enumeration<String> secondDiscoveryPrintList = secondDiscovery
									.elements();
							while (secondDiscoveryPrintList.hasMoreElements()) {
								tv.append(secondDiscoveryPrintList
										.nextElement() + "\n");
							}
						} else if (slidingWindowCounter % 3 == 2) {
							tv.append("11---\n");
							if (thirdDiscovery.contains(deviceName)) {
							}// tv.append("contains" + "\n");
							Enumeration<String> thirdDiscoveryPrintList = thirdDiscovery
									.elements();
							while (thirdDiscoveryPrintList.hasMoreElements()) {
								tv.append(thirdDiscoveryPrintList.nextElement()
										+ "\n");
							}
						}
						slidingWindowCounter++;
						if (count >= 3) {
							innerCount++;
							// tv.append("12---\n");
							presence = computeDifferences(firstDiscovery,
									secondDiscovery, thirdDiscovery, tv,
									deviceName);
							// tv.append(presence + "\n");

							if (presence == true && flag == false) {
								tv.append("Established for the first time\n");
								connectionDisconnectionFlag = 0;
								try {
									updateWithNewLocation(tv,
											connectionDisconnectionFlag);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								flag = true;
							} else if (presence == true && flag == true) {
								tv.append("Already connected\n");
							} else if (presence == false && flag == true) {
								tv.append("Connection disconnected\n");
								connectionDisconnectionFlag = 1;
								try {
									updateWithNewLocation(tv,
											connectionDisconnectionFlag);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								flag = false;
							} else if (presence == false && flag == true) {
								// tv.append("Cannot find in the vicinity. Please select a different device\n");
							}
							// count=0;
							if (innerCount == 1) {
								firstDiscovery.clear();
								// tv.append("first discovery cleared\n");
							} else if (innerCount == 2) {
								secondDiscovery.clear();
								// tv.append("second discovery cleared\n");
							} else if (innerCount == 3) {
								thirdDiscovery.clear();
								// tv.append("third discovery cleared\n");
							}

							if (innerCount == 3) {
								innerCount = 0;
								// tv.append("\ninner count cleared\n");
							}

						}
						bluetoothAdapter.startDiscovery();
						bluetoothflag = false;
					}
				}
			}

		};
		// String aDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
		// startActivityForResult(new Intent(aDiscoverable), DISCOVERY_REQUEST);
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		bluetoothAdapter.startDiscovery();
	}

	private void updateWithNewLocation(TextView tv, int flag)
			throws JSONException {
		// tv.append("entered location update function\n");
		MapView view = (MapView) findViewById(R.id.themap);
		view.setBuiltInZoomControls(true);

		final MapController control = view.getController();

		LocationManager manager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// Location location =
		// manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LocationListener listener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onLocationChanged(Location location) {

				control.setCenter(new GeoPoint((int) location.getLatitude(),
						(int) location.getLongitude()));

			}
		};
		/*
		 * manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
		 * 0, listener); Location location = manager
		 * .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		 */
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				listener);
		Location location = manager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		if (location != null) {
			// String latLongString = "Lat:" + lat + "\nLong:" + lng + "\n";
			// tv.append(latLongString);
			String json = getJson(lat, lng);
			// tv.append(json + "\n");
			if (json.equals("[]"))
				tv.append("Not a valid parking location\n");
			else {
				if (flag == 0) {
					parseJson(json, tv, lat, lng);
					tv.append("The Car is deparked\n");
					Toast.makeText(Radio.this, "The Car is Deparked",
							Toast.LENGTH_SHORT).show();
				}

				else if (flag == 1) {
					parseJson(json, tv, lat, lng);
					tv.append("The car is parked\n");
					Toast.makeText(Radio.this, "The Car is parked",
							Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	private void parseJson(String json, TextView tv, double lat, double lng)
			throws JSONException {
		JSONArray jObject = new JSONArray(json);
		Vector<String> nameVector = new Vector<String>();
		Vector<String> addressVector = new Vector<String>();
		Vector<String> cityVector = new Vector<String>();
		Vector<String> stateVector = new Vector<String>();
		Vector<String> parkingVector = new Vector<String>();
		Vector<String> kioskVector = new Vector<String>();
		Vector<String> longitudeVector = new Vector<String>();
		Vector<String> latitudeVector = new Vector<String>();
		Vector<Double> longitudeDoubleVector = new Vector<Double>();
		Vector<Double> latitudeDoubleVector = new Vector<Double>();
		for (int i = 0; i < jObject.length(); i++) {
			JSONObject menuObject = jObject.getJSONObject(i);

			String name = menuObject.getString("Name");
			nameVector.add(name);
			String address = menuObject.getString("Address");
			addressVector.add(address);
			String city = menuObject.getString("City");
			cityVector.add(city);
			String state = menuObject.getString("State");
			stateVector.add(state);
			String parkingSpaces = menuObject.getString("ParkingSpaces");
			parkingVector.add(parkingSpaces);
			String kioskId = menuObject.getString("KioskId");
			kioskVector.add(kioskId);
			String longitude = menuObject.getString("Longitude");
			longitudeVector.add(longitude);
			String latitude = menuObject.getString("Latitude");
			latitudeVector.add(latitude);
		}
		/*
		 * printVectors(nameVector,tv); printVectors(addressVector,tv);
		 * printVectors(cityVector,tv); printVectors(stateVector,tv);
		 * printVectors(parkingVector,tv); printVectors(kioskVector,tv);
		 */
		// printVectors(longitudeVector,tv);
		// printVectors(latitudeVector,tv);

		for (String s : latitudeVector)
			latitudeDoubleVector.add(Double.parseDouble(s));
		for (String s : longitudeVector)
			longitudeDoubleVector.add(Double.parseDouble(s));
		/*
		 * printDoubleVectors(latitudeDoubleVector,tv);
		 * printDoubleVectors(longitudeDoubleVector,tv);
		 */
		int index = calculateDistances(latitudeDoubleVector,
				longitudeDoubleVector, lat, lng, tv);
		tv.append("The meter id is " + nameVector.get(index)
				+ " and the address is " + addressVector.get(index) + "\n");
	}

	private int calculateDistances(Vector<Double> latitudeDoubleVector,
			Vector<Double> longitudeDoubleVector, double lat, double lng,
			TextView tv) {
		int i, j;
		Vector<Double> distances = new Vector<Double>();
		for (i = 0, j = 0; i < latitudeDoubleVector.size()
				&& j < longitudeDoubleVector.size(); i++, j++) {
			// tv.append(latitudeDoubleVector.get(i)+","+longitudeDoubleVector.get(j)+"\n");
			// tv.append(lat+","+lng+"\n");
			double lat1 = latitudeDoubleVector.get(i);
			double lng1 = longitudeDoubleVector.get(j);

			double distance = distFrom(lat, lng, lat1, lng1);
			distances.add(distance);
			// tv.append("distance="+distance+"\n");
		}

		// printDoubleVectors(distances, tv);
		Object obj = Collections.min(distances);
		// tv.append("Minimum Distance="+obj.toString()+"\n");
		/* tv.append("index of minimum Distance="+distances.indexOf(obj)+"\n"); */
		return distances.indexOf(obj);
	}

	private double distFrom(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 0.8684;
		return (dist);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	/*
	 * private void printDoubleVectors(Vector<Double> vector, TextView tv) {
	 * Enumeration<Double> printList = vector.elements(); while
	 * (printList.hasMoreElements()) { tv.append(printList.nextElement() + ",");
	 * } tv.append("\n-------\n"); }
	 * 
	 * 
	 * private void printVectors(Vector<String> vector,TextView tv) {
	 * Enumeration<String> printList = vector.elements(); while
	 * (printList.hasMoreElements()) { tv.append(printList.nextElement() + ",");
	 * } tv.append("\n-------\n"); }
	 */

	private String getJson(double lat, double lng) {
		/*
		 * lat = 41.8579314; lng = -87.6573525
		 */;// remove these 2 lines for thorough testing
		/*
		 * lng=-87.6574626; lat=41.8693973;
		 */
		/*lat = 41.868804;
		lng = -87.656308;*/
		String json = "";
		String latLongString = lng + "," + lat;
		String address = "http://admin.chicagometers.com/Kiosk/RadiusArray/0?LngLats="
				+ latLongString + "&Miles=0.1";

		try {
			URL url = null;
			url = new URL(address);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()), 8192);

			String line = "";
			while ((line = br.readLine()) != null) {
				json = json + line;
			}

		} catch (Exception e) {
			// tv.append("No Json");
		}
		return json;
	}

	private boolean computeDifferences(Vector<String> firstDiscovery,
			Vector<String> secondDiscovery, Vector<String> thirdDiscovery,
			TextView tv, String deviceName) {
		int count = 0;
		if (firstDiscovery.contains(deviceName))
			count++;
		if (secondDiscovery.contains(deviceName))
			count++;
		if (thirdDiscovery.contains(deviceName))
			count++;
		// tv.append("presence count=" + count + "\n");
		if (count >= 2)
			return true;
		else
			return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_radio, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
