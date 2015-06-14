package com.example.coreblu.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import coreblu.SDK.Beacons.AnyBeacon;

public class ListAdapter extends ArrayAdapter<AnyBeacon> {

	Activity context;
	List<AnyBeacon> Items = new ArrayList<AnyBeacon> ();
	ViewHolder holder;
	int LayoutID;



	public ListAdapter(Activity context, List<AnyBeacon> Items,int LayoutID) {
		super(context, LayoutID,Items);

		this.context = context;
		this.Items = Items;
		this.LayoutID = LayoutID;
	}


	public void UpdateList(List<AnyBeacon> Items){
		this.Items = Items;
		notifyDataSetChanged();
	}
	



	@Override
	public int getCount() {
		return Items.size();
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

