package com.example.hybridimage;

import java.util.concurrent.Semaphore;

import org.apache.http.util.EntityUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity{
	
	Mat myImage_m;
	Size ksize;
	LinearLayout myLayout;
	BitmapDrawable conv;
	MyGraphics graphics;
	Semaphore semaphore;
	Button buttonAdd, buttonSub ;
	TextView displaySigma0;
	FrameLayout myFrame;
	boolean ok = false;
	Double sigma = 0.0;
	Double sigmaTemp = 0.0;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		myLayout = new LinearLayout(this);
		graphics = new MyGraphics(this);
		myFrame = new FrameLayout(this);
		

		displaySigma0 = new TextView(this);
		
		buttonAdd = new Button(this);
		buttonAdd.setText("sigma +");
		buttonAdd.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ok = true;
				
				sigmaTemp = sigmaTemp + 1;
				displaySigma0.setText("Sigma: " + sigmaTemp.toString());
				graphics.invalidate();
			}
		});
		
		
		buttonSub = new Button(this);
		buttonSub.setText("sigma -");
		buttonSub.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ok = true;
				
				// if sigmaX and sigmaY are both zero 
				// then sigma will be calculated from kernel size
				// this is why we do not let sigma be 0
				if(sigmaTemp > 1){
					sigmaTemp = sigmaTemp - 1;
					displaySigma0.setText("Sigma: " + sigmaTemp.toString());
				}

				graphics.invalidate();

			}
		});
		
	
		LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		myLayout.setLayoutParams(layout);
		LayoutParams layoutInner = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		myFrame.addView(graphics);
		myLayout.addView(buttonAdd, layoutInner);
		myLayout.addView(buttonSub, layoutInner);
		myLayout.addView(displaySigma0, layoutInner);
		myFrame.addView(myLayout, layout);
		setContentView(myFrame);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		
		return true;
	}
		
	private BaseLoaderCallback myLoaderCallback = new BaseLoaderCallback(this){
		
		@Override
		public void onManagerConnected(int status){
			if (status == LoaderCallbackInterface.SUCCESS){
				
				Log.i(getCallingPackage(), "OPENCV LOADED!!");
				
			}
			else{
				
				super.onManagerConnected(status);
			}
			
		}
	
		
	};
	
	
	@Override
	public void onResume()
	{
	   super.onResume();
	   OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, myLoaderCallback);
	}

	
	

	@TargetApi(16)
	public class MyGraphics extends View{
		
		Bitmap kathrine, temp;
		
		public MyGraphics(Context context) {
			// TODO Auto-generated constructor stub
			this(context, null);
		}
		
		@SuppressWarnings("deprecation")
		public MyGraphics(Context context, AttributeSet attrs){
			super(context, attrs);
			
			kathrine = BitmapFactory.decodeResource(getResources(), R.drawable.kathrine0);
			if(android.os.Build.VERSION.SDK_INT >= 16){
				setBackground(new BitmapDrawable(getResources(), kathrine));
			}else{
				setBackgroundDrawable(new BitmapDrawable(getResources(), kathrine));
			}
			
			

		}
		
		// ondraw is called when the View is initalized
		// and when invalidate is called
		protected void onDraw(Canvas canvas){
			
			
				if(ok == true){
		
					myImage_m = new Mat(kathrine.getWidth(), kathrine.getHeight(),CvType.CV_8UC1);
					Utils.bitmapToMat(kathrine, myImage_m);
					
					//ksize must be odd
					ksize = new Size();
					ksize.width = ksize.height = 45.0;
					sigma = sigmaTemp;
					Imgproc.GaussianBlur(myImage_m, myImage_m, ksize , sigma, sigma);
					temp = kathrine.copy(kathrine.getConfig(), true);
					Utils.matToBitmap(myImage_m, temp);
					canvas.drawBitmap(temp, null, new Rect(0,0, canvas.getWidth(), canvas.getHeight()), null);
				}
			
		}
	}
	
		

	
}
