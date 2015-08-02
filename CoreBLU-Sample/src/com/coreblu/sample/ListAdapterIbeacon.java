package com.coreblu.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.coreblu.sample.R;
import coreblu.SDK.Beacons.Beacon;
import coreblu.SDK.Beacons.iBeacon;

public class ListAdapterIbeacon extends ArrayAdapter<iBeacon> {

	private Activity context;
	private List<iBeacon> Items;
	private ViewHolder holder;
	private int LayoutID;
	private Map<String, iBeacon>  it = new HashMap<String, iBeacon>();
	private final int LIST_REFRESH_TIME=1000;//time in ms 
	private long previousTime=0;

	class ViewHolder{
		protected TextView text;
	}


	public ListAdapterIbeacon(Activity context, List<iBeacon> Items,int LayoutID) {
		super(context, LayoutID,Items);
		this.context = context;
		this.Items = Items;
		this.LayoutID = LayoutID;
	}

	public void add(iBeacon miBeacon)
	{
		it.put(miBeacon.getMacAddress(), miBeacon);
		long currentTime = System.currentTimeMillis();
		if(currentTime > previousTime + LIST_REFRESH_TIME ){
		this.Items =   new ArrayList<iBeacon>(it.values());
		notifyDataSetChanged();
		previousTime = currentTime;
		}
	}

	public void add(Collection<iBeacon> beacons)
	{
		//this.Items =   new ArrayList<iBeacon>(beacons);
		for(iBeacon ib : beacons)
		{
			it.put(ib.getMacAddress(), ib);
		}
		long currentTime = System.currentTimeMillis();
		if(currentTime > previousTime + LIST_REFRESH_TIME ){
		this.Items =   new ArrayList<iBeacon>(it.values());
		notifyDataSetChanged();
		previousTime = currentTime;
		}
		
	}
	
	public void clear()
	{
		it.clear();
		Items.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return Items.size();
	}

	public iBeacon getobj(int pos) {
		return new ArrayList<iBeacon>(it.values()).get(pos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(LayoutID, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(viewHolder);
		} else {
		}

		holder = (ViewHolder) convertView.getTag();

		try{
			String txt = "MAC = "+Items.get(position).getMacAddress()+""
					+ "\nRssi ="+Items.get(position).getRssi()
					+ "\nBeacon Type ="+Items.get(position).getType()
					+ "\nUUID ="+Items.get(position).getUUID()
					+ "\nMajor="+Items.get(position).getMajor()+"Minor="+Items.get(position).getMinor();

			if(Items.get(position).getType().equals(Beacon.BEACON_TYPE_COREBLU_IBEACON))
				txt = txt+ "\nBattery Voltage ="+Items.get(position).getbatteryVoltage();

			holder.text.setText(txt);
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		return convertView;
	}
}


