package com.example.coreblu.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import coreblu.SDK.Beacons.iBeacon;

public class ListAdapterIbeacon extends ArrayAdapter<iBeacon> {

	Activity context;
	List<iBeacon> Items;
	ViewHolder holder;
	int LayoutID;
	private Map<String, iBeacon>  it = new HashMap<String, iBeacon>();



	public ListAdapterIbeacon(Activity context, List<iBeacon> Items,int LayoutID) {
		super(context, LayoutID,Items);

		this.context = context;
		this.Items = Items;
		this.LayoutID = LayoutID;
	}


	public void UpdateList(List<iBeacon> Items){
		this.Items = Items;
		notifyDataSetChanged();
	}
	
	public void add(iBeacon miBeacon)
	{
		it.put(miBeacon.getMacAddress(), miBeacon);
		this.Items =   new ArrayList<iBeacon>(it.values());
		notifyDataSetChanged();
	}
	



	@Override
	public int getCount() {
		return Items.size();
	}

	
	public iBeacon getobj(int pos) {
		 ArrayList<iBeacon> ib = new ArrayList<iBeacon>(it.values());
		return ib.get(pos);
	}
	
	
	class ViewHolder{
		protected TextView text;
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
			holder.text.setText("MAC = "+Items.get(position).getMacAddress()+""
					+ "\nRssi ="+Items.get(position).getRssi()
					+ "\nBeacon Type ="+Items.get(position).getType());
			
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}


		return convertView;
	}

}


