package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aakashapp.modguru.src.Parser;

public class ResultActivity extends Activity {

	ArrayList<String> results;
	RelativeLayout graphView;
	int totalQuestions, totalResults;
	int [][] resultSummaryData;
	Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		initialize();
		loadResultData();
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2==0)
					showBarGraph();
				if (arg2==1)
					showStackedBarGraph();
				if(arg2==2)
					showLineGraph();
				if(arg2==3)
					showTabularForm();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initialize() {
		results = new ArrayList<String>();
		graphView = (RelativeLayout) findViewById(R.id.graphView);
		totalQuestions = 0;
		totalResults = 0;

		spinner = (Spinner) findViewById(R.id.spinnerSelect);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.selectGraphView, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
		spinner.setAdapter(adapter);
	}

	private void loadResultData() {
		String resultFolder = getIntent().getStringExtra("file");
		File path=new File(Environment.getExternalStorageDirectory()+"/Android/data/"+Main.PACKAGE_NAME+"/"+resultFolder);
		if(path.isDirectory()) {
			for(File f: path.listFiles()) {
				try {
					Parser p = new Parser(new FileInputStream(f));
					results.add(p.extractResult());
				} catch (FileNotFoundException e) {
					Log.e("IO", "RESULT",e);
				}
			}
			totalResults = results.size();
			totalQuestions = results.get(0).length();
			resultSummaryData = new int[totalResults][totalQuestions];
			for(int i=0;i<totalResults;i++)
				for(int j=0;j<totalQuestions;j++)
				{
					resultSummaryData[i][j] = Integer.parseInt(String.valueOf(results.get(i).charAt(j)));
				}
			//showBarGraph();
		}
		else {
			Toast.makeText(ResultActivity.this, "No Results Found!!", Toast.LENGTH_SHORT).show();
			ResultActivity.this.finish();
		}
	}

	protected void showTabularForm() {
		TextView textViewTitle = new TextView(ResultActivity.this);
		textViewTitle.setText("Result Summary");
		textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
		textViewTitle.setGravity(Gravity.CENTER_HORIZONTAL);
		textViewTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		textViewTitle.setPadding(20, 20, 20, 20);
		textViewTitle.setId(12345);

		TextView textViewResultData = new TextView(ResultActivity.this);
		textViewResultData.setPadding(20, 0, 20, 0);
		textViewResultData.setText("\nNo. of Participants: " +totalResults);
		int totalCorrectAttempts = 0;
		for (int i=0 ; i<totalResults; i++)
			for (int j=0; j<totalQuestions;j++)
				totalCorrectAttempts+=resultSummaryData[i][j];
		textViewResultData.append("\nAverage Score: "+ (float)((totalCorrectAttempts*100)/(totalQuestions*totalResults))+"%");
		textViewResultData.append("\nHishest Marks Obtained: ");
		textViewResultData.append("\nLowest Marks Obtained: ");
		textViewResultData.append("\nQuestionwise: ");
		textViewResultData.append("\nresultwise: ");

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, textViewTitle.getId());

		graphView.removeAllViews();
		graphView.addView(textViewTitle);
		graphView.addView(textViewResultData, layoutParams);
	}

	protected void showLineGraph() {
		XYSeries correctSeries = new XYSeries("Correct");

		int []x = new int[totalQuestions]; 
		for (int i=0 ; i<totalQuestions; i++) {
			x[i]=0;
			for (int j=0; j<totalResults;j++)
				x[i] = x[i] + resultSummaryData[j][i];
			correctSeries.add(i, x[i]);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(correctSeries);

		XYSeriesRenderer correctRenderer = new XYSeriesRenderer();
		correctRenderer.setColor(Color.rgb(0, 0, 255));

		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setMarginsColor(Color.rgb(255, 255, 255));
		multiRenderer.setAxesColor(Color.rgb(0, 0, 0));
		multiRenderer.setLabelsColor(Color.rgb(0, 0, 0));
		multiRenderer.setXLabelsColor(Color.rgb(0, 0, 0));
		multiRenderer.setYLabelsColor(0,Color.rgb(0, 0, 0));
		multiRenderer.setShowGridX(true);
		multiRenderer.setShowGridY(true);
		multiRenderer.setXLabels(0);
		multiRenderer.setBarSpacing(1);
		multiRenderer.setYAxisMin(0);
		multiRenderer.setYAxisMax(totalResults);
		multiRenderer.setScale(1);
		multiRenderer.setChartTitle("Result Summary");
		multiRenderer.setYTitle("No. of Correct Answers");
		multiRenderer.setXTitle("Question Number");
		multiRenderer.setZoomButtonsVisible(false);    	 
		for(int i=0; i<totalQuestions;i++){
			multiRenderer.addXTextLabel(i, String.valueOf(i+1));    		
		}  
		multiRenderer.addSeriesRenderer(correctRenderer);

		GraphicalView lineChartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiRenderer);
		graphView.removeAllViews();
		graphView.addView(lineChartView);
	}

	protected void showStackedBarGraph(){
		XYSeries correctSeries = new XYSeries("Correct");
		XYSeries incorrectSeries = new XYSeries("Incorrect");

		int []x = new int[totalQuestions]; 
		int []y = new int[totalQuestions];
		for (int i=0 ; i<totalQuestions; i++) {
			x[i]=0;
			for (int j=0; j<totalResults;j++)
				x[i] = x[i] + resultSummaryData[j][i];
			y[i] = totalResults - x[i];
			correctSeries.add(i, x[i]);
			incorrectSeries.add(i, totalResults);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(incorrectSeries);
		dataset.addSeries(correctSeries);

		XYSeriesRenderer correctRenderer = new XYSeriesRenderer();
		correctRenderer.setColor(Color.rgb(0, 0, 255));

		XYSeriesRenderer incorrectRenderer = new XYSeriesRenderer();
		correctRenderer.setColor(Color.rgb(255, 0, 0));

		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setMarginsColor(Color.rgb(255, 255, 255));
		multiRenderer.setAxesColor(Color.rgb(0, 0, 0));
		multiRenderer.setLabelsColor(Color.rgb(0, 0, 0));
		multiRenderer.setXLabelsColor(Color.rgb(0, 0, 0));
		multiRenderer.setYLabelsColor(0,Color.rgb(0, 0, 0));
		multiRenderer.setShowGridX(true);
		multiRenderer.setShowGridY(true);
		multiRenderer.setXLabels(0);
		multiRenderer.setBarSpacing(1);
		multiRenderer.setYAxisMin(0);
		multiRenderer.setYAxisMax(totalResults);
		multiRenderer.setScale(1);
		multiRenderer.setChartTitle("Result Summary");
		multiRenderer.setYTitle("No. of Correct Answers");
		multiRenderer.setXTitle("Question Number");
		multiRenderer.setZoomButtonsVisible(false);    	    	
		for(int i=0; i<totalQuestions;i++){
			multiRenderer.addXTextLabel(i, String.valueOf(i+1));    		
		}  
		multiRenderer.addSeriesRenderer(correctRenderer);
		multiRenderer.addSeriesRenderer(incorrectRenderer);

		GraphicalView stackedBarChartView = ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, Type.STACKED);
		graphView.removeAllViews();
		graphView.addView(stackedBarChartView);
	}

	protected void showBarGraph() {
		XYSeries correctSeries = new XYSeries("Correct");

		int []x = new int[totalQuestions]; 
		for (int i=0 ; i<totalQuestions; i++) {
			x[i]=0;
			for (int j=0; j<totalResults;j++)
				x[i] = x[i] + resultSummaryData[j][i];
			correctSeries.add(i, x[i]);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(correctSeries);

		XYSeriesRenderer correctRenderer = new XYSeriesRenderer();
		correctRenderer.setColor(Color.rgb(0, 0, 255));

		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setMarginsColor(Color.rgb(255, 255, 255));
		multiRenderer.setAxesColor(Color.rgb(0, 0, 0));
		multiRenderer.setLabelsColor(Color.rgb(0, 0, 0));
		multiRenderer.setXLabelsColor(Color.rgb(0, 0, 0));
		multiRenderer.setYLabelsColor(0,Color.rgb(0, 0, 0));
		multiRenderer.setShowGridX(true);
		multiRenderer.setShowGridY(true);
		multiRenderer.setXLabels(0);
		multiRenderer.setBarSpacing(1);
		multiRenderer.setFitLegend(true);
		multiRenderer.setYAxisMin(0);
		multiRenderer.setYAxisMax(totalResults);
		multiRenderer.setScale(1);
		multiRenderer.setChartTitle("Result Summary");
		multiRenderer.setYTitle("No. of Correct Answers");
		multiRenderer.setXTitle("Question Number");
		multiRenderer.setZoomButtonsVisible(false);    	    	
		for(int i=0; i<totalQuestions;i++){
			multiRenderer.addXTextLabel(i, String.valueOf(i+1));    		
		}  
		multiRenderer.addSeriesRenderer(correctRenderer);

		GraphicalView barChartView = ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, Type.STACKED);
		graphView.removeAllViews();
		graphView.addView(barChartView);
	}	

}
