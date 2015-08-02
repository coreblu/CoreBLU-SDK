package com.coreblu.sample;

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
import com.coreblu.sample.R;
import coreblu.SDK.Beacons.AnyBeacon;

public class ListAdapter extends ArrayAdapter<AnyBeacon> {

	private Activity context;
	private List<AnyBeacon> Items = new ArrayList<AnyBeacon> ();
	private ViewHolder holder;
	private int LayoutID;
	private Map<String, AnyBeacon>  bMap = new HashMap<String, AnyBeacon>();
	private final int LIST_REFRESH_TIME=1000;//time in ms 
	private long previousTime=0;
	//private int currentTime =0;

	public ListAdapter(Activity context, List<AnyBeacon> Items,int LayoutID) {
		super(context, LayoutID,Items);

		this.context = context;
		this.Items = Items;
		this.LayoutID = LayoutID;
	}


	@Override
	public int getCount(){
		return Items.size();
	}

	class ViewHolder{
		protected TextView text;
	}
	
	public void add(AnyBeacon mAnyBeacon)
	{
		bMap.put(mAnyBeacon.getMacAddress(), mAnyBeacon);
		long currentTime = System.currentTimeMillis();
		if(currentTime > previousTime + LIST_REFRESH_TIME ){
		this.Items =   new ArrayList<AnyBeacon>(bMap.values());
		notifyDataSetChanged();
		previousTime = currentTime;
		}
		
		
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
					+ "\nBeacon Type ="+Items.get(position).getType();

			holder.text.setText(txt);
		}
		catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		return convertView;
	}
}

