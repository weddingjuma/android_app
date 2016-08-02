package com.acs.mainmenu;

public class Row {
		private String txtStr = "";
		private boolean checked = false;
		public Row() {}
		
		public Row(String txtStr) {
			this.txtStr = txtStr;
		}
		public Row (String txtstr, boolean checked) {
			this.txtStr = txtstr;
			this.checked = checked;
		}
		public String getTxtStr() {
			return txtStr;
		}
		public void setTxtStr(String txtstr) {
			this.txtStr = txtstr;
		}
		public boolean isChecked() {
			return checked;
		}
		public void setChecked(boolean checked) {
			this.checked = checked;
		}
		public String toString() {
			return txtStr;
		}
		public void toggleChecked() {
			checked = !checked;
		}
}
