package com.aakashapp.modguru;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aakashapp.modguru.src.Parser;

public class ResultActivity extends Activity {

	ArrayList<String> results;
	RelativeLayout graphView;
	int totalQuestions, totalResults;
	int [][] resultSummaryData;
	int[] correctQuesCount, incorrectQuesCount, unattemptedQuesCount;
	Spinner spinner;

	int noOfAttempts;
	TextView textViewParticipantData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		initialize();
		try {
			loadResultData();
		} catch (Exception e) {
			Toast.makeText(ResultActivity.this, "No Results Found!!", Toast.LENGTH_SHORT).show();
			finish();
		}
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
					showPieChart();
				if(arg2==4)
					showTabularForm();
				if(arg2==5)
					showParticipantResult();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				/*
				 * Do Nothing
				 */
			}
		});
	}

	protected void showParticipantResult() {
		noOfAttempts = 0;

		final EditText editText = new EditText(ResultActivity.this);
		editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		editText.setId(32145);
		editText.setHint("Enter Participant Name/ID");

		Button button = new Button(ResultActivity.this);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		button.setId(32154);
		button.setText("Show Result");
		button.setTextColor(Color.WHITE);
		button.setBackgroundResource(R.drawable.button);

		RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParam.addRule(RelativeLayout.BELOW, editText.getId());

		ScrollView scrollView = new ScrollView(ResultActivity.this);
		textViewParticipantData = new TextView(ResultActivity.this);
		textViewParticipantData.setPadding(10, 0, 10, 0);
		textViewParticipantData.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		scrollView.addView(textViewParticipantData);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, button.getId());

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String uid = editText.getText().toString();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showParticipantResultData(uid);
					}
				});
			}
		});

		graphView.removeAllViews();
		graphView.addView(editText);
		graphView.addView(button, layoutParam);
		graphView.addView(scrollView, layoutParams);
	}

	private void showParticipantResultData(String uid) {
		textViewParticipantData.setText("");
		int count = 0;
		String resultFolder = getIntent().getStringExtra("file");
		File path=new File(Environment.getDataDirectory()+"/data/"+Main.PACKAGE_NAME+"/res/"+resultFolder);
		for(File f: path.listFiles()) {
			if(f.getAbsolutePath().endsWith("_" + uid))
				try {
					Parser p = new Parser(new FileInputStream(f));
					ArrayList<String> extractResult = p.extractResult();
					String resultSummary = extractResult.get(0);
					int length = resultSummary.length();
					int attemptedQuestions = 0, noca = 0;
					for(int i=0;i<length;i++) {
						if(resultSummary.charAt(i)=='1') {
							attemptedQuestions++;
							noca++;
						}
						if(resultSummary.charAt(i)=='2')
							attemptedQuestions++;
					}

					textViewParticipantData.append("\nAttempt #"+(++count));
					textViewParticipantData.append("\nTotal No. of Questions\t: "+length);
					textViewParticipantData.append("\nAttempted Questions\t: "+(attemptedQuestions));
					textViewParticipantData.append("\nCorrect Answers\t\t\t: "+noca);
					textViewParticipantData.append("\nIncorrect Answers\t\t: "+(attemptedQuestions - noca));
					textViewParticipantData.append("\nAggregate\t\t\t\t\t\t: "+(float)(noca*100/totalQuestions)+"%\n\n");

					for(int i=0;i<length;i++) {
						textViewParticipantData.append("Answer #" + (i+1) + ": ");
						for(int j=0;j<4;j++)
							if(extractResult.get(i+1).contains(String.valueOf(j)))
								textViewParticipantData.append("("+(char)(65+j)+") ");
						textViewParticipantData.append("\n");
					}
				} catch (Exception e) {
					Log.e("IO", "RESULT",e);
					Toast.makeText(ResultActivity.this, "One or More Results failed to Load!!", Toast.LENGTH_SHORT).show();
				}
		}
		if(count==0)
			textViewParticipantData.setText("\nNo Such Participant attempted the Quiz.");
	}

	protected void showPieChart() {
		SeekBar seekBar = new SeekBar(ResultActivity.this);
		seekBar.setMax(totalQuestions-1);
		seekBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		seekBar.setId(54321);

		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.BELOW, seekBar.getId());

				graphView.removeAllViews();
				graphView.addView(seekBar);
				graphView.addView(getPieChartView(progress), layoutParams);
			}

		});

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, seekBar.getId());

		graphView.removeAllViews();
		graphView.addView(seekBar);
		graphView.addView(getPieChartView(0), layoutParams);
	}

	protected GraphicalView getPieChartView(int ques) {
		String[] code = new String[] { "Correct", "Incorrect", "Unattempted" };    	
		double[] distribution = { (double) (correctQuesCount[ques]*100)/totalResults , (double) (incorrectQuesCount[ques]*100)/totalResults, (double) (unattemptedQuesCount[ques]*100)/totalResults} ;
		int[] colors = { Color.BLUE, Color.RED, Color.LTGRAY };

		CategorySeries distributionSeries = new CategorySeries("Student Performance");
		for(int i=0 ;i < distribution.length;i++){
			distributionSeries.add(String.format("%.02f",(float)distribution[i])+"% "+code[i], (float)distribution[i]);
		}   

		DefaultRenderer defaultRenderer  = new DefaultRenderer();    	
		for(int i = 0 ;i<distribution.length;i++){    		
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();    	
			seriesRenderer.setColor(colors[i]);
			seriesRenderer.setDisplayChartValues(true);

			defaultRenderer.setPanEnabled(false);
			defaultRenderer.setApplyBackgroundColor(true);
			defaultRenderer.setBackgroundColor(Color.WHITE);
			defaultRenderer.addSeriesRenderer(seriesRenderer);
			defaultRenderer.setZoomEnabled(false);
			defaultRenderer.setMargins(new int[] {20, 20, 20, 20});
			defaultRenderer.setLabelsColor(Color.BLACK);
		}

		defaultRenderer.setChartTitle("Question No. "+(ques+1));
		defaultRenderer.setChartTitleTextSize(20);

		return ChartFactory.getPieChartView(ResultActivity.this, distributionSeries, defaultRenderer);
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
		textViewResultData.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		textViewResultData.setText("\nNo. of Participants: " +totalResults);

		int totalCorrectAttempts = 0;
		for (int i=0; i<totalQuestions;i++)
			totalCorrectAttempts+=correctQuesCount[i];
		textViewResultData.append("\nAverage Score: "+ String.format("%.02f",(float)((totalCorrectAttempts*100)/(totalQuestions*totalResults)))+"%");

		int max = 0, min = totalQuestions;
		for (int i=0 ; i<totalResults; i++) {
			int temp = 0;
			for (int j=0; j<totalQuestions;j++)
				if(resultSummaryData[i][j]==1)
					temp++;
			if(max<temp)
				max=temp;
			if(min>temp)
				min=temp;
		}
		textViewResultData.append("\nHishest Marks Obtained: " + String.format("%.02f",(float)(max*100)/totalQuestions)+"%");
		textViewResultData.append("\nLowest Marks Obtained: " + String.format("%.02f",(float)(min*100)/totalQuestions)+"%");

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.BELOW, textViewTitle.getId());

		graphView.removeAllViews();
		graphView.addView(textViewTitle);
		graphView.addView(textViewResultData, layoutParams);
	}

	protected void showLineGraph() {
		XYSeries correctSeries = new XYSeries("Correct");

		for (int i=0 ; i<totalQuestions; i++)
			correctSeries.add(i, correctQuesCount[i]);

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
		multiRenderer.setChartTitle("Result Summary");
		multiRenderer.setYTitle("No. of Correct Answers");
		multiRenderer.setXTitle("Question Number");   	 
		for(int i=0; i<totalQuestions;i++){
			multiRenderer.addXTextLabel(i, String.valueOf(i+1));    		
		}  
		multiRenderer.addSeriesRenderer(correctRenderer);

		GraphicalView lineChartView = ChartFactory.getLineChartView(ResultActivity.this, dataset, multiRenderer);
		graphView.removeAllViews();
		graphView.addView(lineChartView);
	}

	protected void showStackedBarGraph(){
		XYSeries correctSeries = new XYSeries("Correct");
		XYSeries incorrectSeries = new XYSeries("Incorrect");

		for (int i=0 ; i<totalQuestions; i++) {
			correctSeries.add(i, correctQuesCount[i]);
			incorrectSeries.add(i, incorrectQuesCount[i]+correctQuesCount[i]);
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
		multiRenderer.setChartTitle("Result Summary");
		multiRenderer.setYTitle("No. of Correct Answers");
		multiRenderer.setXTitle("Question Number");  	    	
		for(int i=0; i<totalQuestions;i++){
			multiRenderer.addXTextLabel(i, String.valueOf(i+1));    		
		}  
		multiRenderer.addSeriesRenderer(correctRenderer);
		multiRenderer.addSeriesRenderer(incorrectRenderer);

		GraphicalView stackedBarChartView = ChartFactory.getBarChartView(ResultActivity.this, dataset, multiRenderer, Type.STACKED);
		graphView.removeAllViews();
		graphView.addView(stackedBarChartView);
	}

	protected void showBarGraph() {
		XYSeries correctSeries = new XYSeries("Correct");
		XYSeries incorrectSeries = new XYSeries("Incorrect");
		XYSeries unattemptedSeries = new XYSeries("Unattempted");

		for (int i=0 ; i<totalQuestions; i++) {
			correctSeries.add(i, correctQuesCount[i]);
			incorrectSeries.add(i, incorrectQuesCount[i]);
			unattemptedSeries.add(i, unattemptedQuesCount[i]);
		}
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(correctSeries);
		dataset.addSeries(incorrectSeries);
		dataset.addSeries(unattemptedSeries);

		XYSeriesRenderer correctRenderer = new XYSeriesRenderer();
		correctRenderer.setColor(Color.BLUE);

		XYSeriesRenderer incorrectRenderer = new XYSeriesRenderer();
		incorrectRenderer.setColor(Color.RED);

		XYSeriesRenderer unattemptedRenderer = new XYSeriesRenderer();
		unattemptedRenderer.setColor(Color.LTGRAY);

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
		multiRenderer.setChartTitle("Result Summary");
		multiRenderer.setYTitle("No. of Correct Answers");
		multiRenderer.setXTitle("Question Number");  	
		for(int i=0; i<totalQuestions;i++){
			multiRenderer.addXTextLabel(i, String.valueOf(i+1));    		
		}  
		multiRenderer.addSeriesRenderer(correctRenderer);
		multiRenderer.addSeriesRenderer(incorrectRenderer);
		multiRenderer.addSeriesRenderer(unattemptedRenderer);

		GraphicalView barChartView = ChartFactory.getBarChartView(ResultActivity.this, dataset, multiRenderer, Type.DEFAULT);
		graphView.removeAllViews();
		graphView.addView(barChartView);
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
		File path=new File(Environment.getDataDirectory()+"/data/"+Main.PACKAGE_NAME+"/res/"+resultFolder);
		if(path.isDirectory()) {
			for(File f: path.listFiles()) {
				try {
					Parser p = new Parser(new FileInputStream(f));
					results.add(p.extractResult().get(0));
				} catch (Exception e) {
					Log.e("IO", "RESULT",e);
					Toast.makeText(ResultActivity.this, "One or More Results failed to Load!!", Toast.LENGTH_SHORT).show();
				}
			}
			totalResults = results.size();
			totalQuestions = results.get(0).length();
			resultSummaryData = new int[totalResults][totalQuestions];
			correctQuesCount = new int[totalQuestions];
			incorrectQuesCount = new int[totalQuestions];
			unattemptedQuesCount = new int[totalQuestions];
			for(int i=0;i<totalResults;i++) 
				for(int j=0;j<totalQuestions;j++) {
					resultSummaryData[i][j] = Integer.parseInt(String.valueOf(results.get(i).charAt(j)));
				}
			for (int i=0 ; i<totalQuestions; i++) {
				correctQuesCount[i]=0;
				incorrectQuesCount[i]=0;
				unattemptedQuesCount[i]=0;
				for (int j=0; j<totalResults;j++)
					switch (resultSummaryData[j][i]) {
					case 0:
						unattemptedQuesCount[i]++;
						break;
					case 1:
						correctQuesCount[i]++;
						break;
					default:
						incorrectQuesCount[i]++;
						break;
					}
			}

			File parentFile = path.getParentFile();
			String[] list = parentFile.list();
			for (final String s:list) 
				if(s.startsWith(path.getName()+"-")) {
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setTitle("Previous Result Data Available");
					alert.setMessage("Do you want to see the previous data?");

					alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Intent i = new Intent(ResultActivity.this,ResultActivity.class);
							i.putExtra("file", s);
							startActivity(i);
						}
					});

					alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							/*
							 * Do Nothing
							 */
						}
					});
					alert.show();
					break;
				}
		}
		else {
			boolean found = false;
			File parentFile = path.getParentFile();
			String[] list = parentFile.list();
			for (String s:list) 
				if(s.startsWith(path.getName()+"-")) {
					Intent i = new Intent(ResultActivity.this,ResultActivity.class);
					i.putExtra("file", s);
					startActivity(i);
					found = true;
					break;
				}
			if(!found)
				Toast.makeText(ResultActivity.this, "No Results Found!!", Toast.LENGTH_SHORT).show();
			ResultActivity.this.finish();
		}
	}
}
