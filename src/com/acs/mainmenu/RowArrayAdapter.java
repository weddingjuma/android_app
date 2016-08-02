package com.acs.mainmenu;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class RowArrayAdapter extends ArrayAdapter<Row> {

		private LayoutInflater inflater;
		public RowArrayAdapter(Context context, int textViewResourceId, List<Row> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Row row = (Row) this.getItem(position);
			
			// child views in each rows
			CheckBox checkbox;
			TextView textview;
			
			// create a new row view
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.simplerow, null);
				
				textview = (TextView) convertView.findViewById(R.id.rowTextView);
				checkbox = (CheckBox) convertView.findViewById(R.id.rowCheckBox);
				checkbox.setEnabled(true);
				checkbox.setClickable(false);
				
				// Optimization: Tag the rows with its child views, so we dont have to 
				// call findViewById() later when we reuse the row.
				convertView.setTag(new RowViewHolder(textview, checkbox));
				
				// if CheckBox is toggled update the row it is tagged with
				checkbox.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						CheckBox cb = (CheckBox) v;
						Row row = (Row) cb.getTag();
						row.setChecked(cb.isChecked());
						if (!row.isChecked()) {
							row.setChecked(cb.isChecked());
						}
						else 
							return;
					}
				});
			}
			// reuse
			else {
				// because we use a ViewHolder we avoid having to call findViewById()
				RowViewHolder viewHolder = (RowViewHolder) convertView.getTag();
				checkbox = viewHolder.getCheckBox();
				textview = viewHolder.getTextView();
			}
			// Tag the checkbox with the row it is displaying so we can access the row onClick
			checkbox.setTag(row);
			
			// display the row data
			checkbox.setChecked(row.isChecked());
			checkbox.setClickable(false);
			textview.setText( row.getTxtStr());
			
			return convertView;
		}
	}
