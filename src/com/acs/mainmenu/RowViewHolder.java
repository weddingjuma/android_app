package com.acs.mainmenu;

import android.widget.CheckBox;
import android.widget.TextView;
	
public class RowViewHolder {
		private CheckBox checkBox;
		private TextView textView;
		public void RowVeiwHolder () {}
		public RowViewHolder(TextView textview, CheckBox checkbox) {
			this.checkBox = checkbox;
			this.textView = textview;
		}
		public CheckBox getCheckBox() {
			return checkBox;
		}
		public void setCheckBox(CheckBox checkbox) {
			this.checkBox = checkbox;
		}
		public TextView getTextView () {
			return textView;
		}
		public void setTextView(TextView textview) {
			this.textView = textview;
		}
	}
