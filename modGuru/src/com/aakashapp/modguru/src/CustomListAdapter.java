package com.aakashapp.modguru.src;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.aakashapp.modguru.CreateQuizActivity;
import com.aakashapp.modguru.R;

public class CustomListAdapter extends SimpleAdapter {

	private Context parent;
	private List<? extends Map<String, ?>> data;
	private String[] from;
	private int[] to;

	public CustomListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.data=data;
		this.from=from;
		this.to=to;
		parent=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos;
		View view=convertView;
		if(view==null) {
			LayoutInflater inflater = LayoutInflater.from(this.parent);
			view = inflater.inflate(R.layout.ques_list_layout, parent, false);
			Map<String, ?> dataSet=data.get(position);
			for(int i=0;i<from.length;i++) {
				Object data=dataSet.get(from[i]);
				String text=data==null?"":data.toString();
				((TextView)view.findViewById(to[i])).setText(text);
			}
		}
			pos = position;
			view.findViewById(R.id.buttonDeleteQuestion).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((CreateQuizActivity)CustomListAdapter.this.parent).quizData.deleteQuestion(pos);
					((CreateQuizActivity)CustomListAdapter.this.parent).refreshQuestionList();
				}
			});
		return view;
	}
	
	@Override

	public int getViewTypeCount() {                 
	    return getCount();
	}

	@Override
	public int getItemViewType(int position) {
	    return position;
	}
}
