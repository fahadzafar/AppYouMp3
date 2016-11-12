package com.youtube.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.youtube.activity.R;
import com.youtube.util.Helper;

public class QueueAdapter extends ArrayAdapter<List<ParseObject> > {
	private Context context;
	private  List<ParseObject> values;

	public QueueAdapter(Context context, List<ParseObject> values) {
		super(context, R.layout.queue_list_item);
		this.context = context;
		this.values = values;
	}

	@Override
	public int getCount() {
	    return values.size();
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater
				.inflate(R.layout.queue_list_item, parent, false);
		TextView qItemTitle = (TextView) rowView.findViewById(R.id.queue_item_title);
		TextView qItemDuration = (TextView) rowView.findViewById(R.id.queue_item_duration);
		TextView qItemExecPosition = (TextView) rowView.findViewById(R.id.queue_item_position);
	 
		qItemTitle.setText(values.get(position).getString("title"));
		
		float duration = Float.parseFloat(values.get(position).getString("duration"));
		qItemDuration.setText(Helper.ConvertToDisplayTime(duration));
		qItemExecPosition.setText( "Execution No:" + values.get(position).get("execPosition").toString() );

		return rowView;
	}
}