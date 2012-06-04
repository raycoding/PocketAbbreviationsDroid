package com.pocketabbr.droid;


import com.droid.abbr.abbrdata;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AbbreviationResults extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        TextView result=(TextView)findViewById(R.id.searchText);
        result.setText(PocketAbbreviationsDroidActivity.dataList.length + " results found.");
        ListView results=(ListView)findViewById(R.id.resultsList);
        results.setAdapter(new ResultsAdapter(PocketAbbreviationsDroidActivity.dataList));
	}
	
	private class ResultsAdapter extends BaseAdapter
    {
		abbrdata[] dataList;
		public ResultsAdapter(abbrdata[] dataList) {
			this.dataList=dataList;
		}

		@Override
		public int getCount() {
			
			return dataList.length;
		}

		@Override
		public Object getItem(int arg0) {
			
			return null;
		}

		@Override
		public long getItemId(int arg0) {
		
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.resultrow, null);
            }
           
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText("Definition: "+dataList[position].getDefinition());                            }
                    if(bt != null){
                          bt.setText("Category: "+dataList[position].getCategory());
                    }
            
            return v;
		}
    	
    }
}
