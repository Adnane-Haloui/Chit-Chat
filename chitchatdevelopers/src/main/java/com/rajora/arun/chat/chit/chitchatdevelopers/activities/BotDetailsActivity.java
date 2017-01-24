package com.rajora.arun.chat.chit.chitchatdevelopers.activities;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.easing.ExpoEase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rajora.arun.chat.chit.chitchatdevelopers.R;
import com.rajora.arun.chat.chit.chitchatdevelopers.contentProviders.BotContentProvider;
import com.rajora.arun.chat.chit.chitchatdevelopers.database.BotContracts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotDetailsActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

	private final static int CURSOR_LOADER_ID=10;

	private int arr[]=new int[7];
	private String gid;
	String labels[]=new String[7];
	private long msgProcessed;

	Toolbar toolbar;
	LineChartView lineChartView;

	DatabaseReference mLogRef;
	DatabaseReference mStatsref;
	ValueEventListener mStatListener=new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot dataSnapshot) {
			if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
				msgProcessed=dataSnapshot.getValue(Integer.class);
				toolbar.setSubtitle(msgProcessed+" Message processed");
			}
		}
		@Override
		public void onCancelled(DatabaseError databaseError) {

		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bot_details);
		Bundle extras=getIntent().getExtras();
		for(int i=0;i<7;i++) {
			arr[i]=0;
			DateFormat dateFormat = new SimpleDateFormat("MM_dd");
			Date date = new Date();
			date .setTime(date.getTime()-(6-i)*24*60*60*1000);
			labels[i]=dateFormat.format(date);
		}
		if(extras!=null){
			gid=extras.getString("data");
		}
		else if(savedInstanceState!=null){
			gid=savedInstanceState.getString("data");
			arr=savedInstanceState.getIntArray("graph");
			msgProcessed=savedInstanceState.getLong("msgprocessed");
		}
		toolbar = (Toolbar) findViewById(R.id.details_toolbar);
		toolbar.setSubtitle("0 Message processed");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

		lineChartView=(LineChartView)findViewById(R.id.details_linechart);
		lineChartView.reset();
		refreshLineChart();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mLogRef= FirebaseDatabase.getInstance().getReference().child("botLog/"+gid);
		mStatsref=FirebaseDatabase.getInstance().getReference().child("botStat/"+gid);
		mLogRef.orderByKey().limitToLast(7).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot!=null && dataSnapshot.getChildrenCount()>0){
					for(DataSnapshot snapshotInstance:dataSnapshot.getChildren()){
						String key=snapshotInstance.getKey().substring(5);
						int val=snapshotInstance.getValue(Integer.class);
						for(int i=0;i<7;i++){
							if(labels[i].equals(key)){
								arr[i]=val;
							}
						}
					}
					refreshLineChart();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		mStatsref.addValueEventListener(mStatListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mStatsref.removeEventListener(mStatListener);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("data",gid);
		outState.putIntArray("graph",arr);
		outState.putLong("msgprocessed",msgProcessed);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this, BotContentProvider.CONTENT_URI,
				new String[]{BotContracts.COLUMN_BOT_NAME},
				BotContracts.COLUMN_GLOBAL_ID+" = ?",new String[]{gid},null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(cursor!=null && cursor.moveToFirst()){
			toolbar.setTitle(cursor.getString(cursor.getColumnIndex(BotContracts.COLUMN_BOT_NAME)));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	private void refreshLineChart() {
		lineChartView.reset();
		float min=Float.MAX_VALUE,max=Float.MIN_VALUE;
		for(int i=0;i<arr.length;i++)
		{
			min=min<arr[i]?min:arr[i];
			max=max>arr[i]?max:arr[i];
		}
		float values[]=new float[7];
		for (int i=0;i<7;i++){
			values[i]= arr[i];
		}
		LineSet lineset=new LineSet(labels,values);
		lineset.setFill(Color.rgb(145,205,100));
		lineset.setDotsStrokeColor(Color.RED);
		lineset.setColor(Color.GRAY);
		lineChartView.addData(lineset);
		int mmax=(int)max+10;
		int mmin=(int)(min-10<0?0:min-10);
		mmax+=10-(mmax-mmin)%10;
		lineChartView.setAxisBorderValues(mmin,mmax,10);
		buildChart(lineChartView,(mmax-mmin)/5,6);
		lineChartView.show(buildAnimation());
	}

	private void buildChart(ChartView chart,int r,int c){

		Paint mGridPaint =  new Paint();
		mGridPaint.setColor(Color.GRAY);
		mGridPaint.setStyle(Paint.Style.STROKE);
		mGridPaint.setAntiAlias(true);
		mGridPaint.setStrokeWidth(Tools.fromDpToPx(1));
		chart.setGrid(ChartView.GridType.FULL,r,c, mGridPaint);
	}
	private static Animation buildAnimation(){
		return new Animation(1000)
				.setEasing(new ExpoEase())
				.setStartPoint(0.5f,0.0f);
	}

}
