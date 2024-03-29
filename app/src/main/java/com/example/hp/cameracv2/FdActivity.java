package com.example.hp.cameracv2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static org.opencv.core.CvType.CV_8UC4;

//Pehle extends Activity tha.

public class FdActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private DetectionBasedTracker  mNativeDetector;
    private GetHeartRate           mGetHeartRate;
    //private Display                mDisplay;

    private int                    mDetectorType       = NATIVE_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    //private CameraBridgeViewBase   mOpenCvCameraView;
    public JavaCameraView mOpenCvCameraView;

    private int cameraId = JavaCameraView.CAMERA_ID_FRONT;

    public  Mat[]   FrameArray;

    private TextView textView;

    private int k = 0;

    private double mFrequency,fpsum;
    public static double ave;

    private long mprevFrameTime;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                        mGetHeartRate = new GetHeartRate();

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.enableFpsMeter();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
        FrameArray = new Mat[200];
        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.face_detect_surface_view);

        Toast.makeText(getApplicationContext(),"Please hold your phone in landscape orientation",Toast.LENGTH_LONG).show();

        Button switch_camera = (Button)findViewById(R.id.switch_camera);



        //CameraBridgeViewBase substituted with JavaCameraView

        switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                onCameraViewStopped();
                if(cameraId==JavaCameraView.CAMERA_ID_FRONT) {
                    mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_BACK);
                    cameraId=JavaCameraView.CAMERA_ID_BACK;
                    onResume();
                }
                else if(cameraId==JavaCameraView.CAMERA_ID_BACK) {
                    mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_FRONT);
                    cameraId=JavaCameraView.CAMERA_ID_FRONT;
                    onResume();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.fd_activity_surface_view);


        mOpenCvCameraView.setVisibility(JavaCameraView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_FRONT);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if(mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    /*
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }
    */

    public void onCameraViewStarted(int width,int height){
        mGray = new Mat(height,width,CvType.CV_8UC4);
        mRgba = new Mat(height,width,CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        for(int i=0;i<k-1;i++)
            FrameArray[i].release();
        k=0;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if(cameraId==JavaCameraView.CAMERA_ID_FRONT) {
            Core.flip(mRgba, mRgba, 1);
            Core.flip(mGray, mGray, 1);
        }

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGEm
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);   //.tl denotes top left and .br denotes bottom right to get the rectangle


        if(facesArray.length==1 && k<200){
            if(0 <= facesArray[0].x && 0 <= facesArray[0].width && facesArray[0].x + facesArray[0].width <= mRgba.cols() && 0 <= facesArray[0].y && 0 <= facesArray[0].height && facesArray[0].y + facesArray[0].height <= mRgba.rows()) {
                FrameArray[k++] = mRgba.submat(facesArray[0]);
                Log.e(TAG, "K=" + k);
            }
        }
        if(k==200) {
            k++;
//            mGetHeartRate.getValues(FrameArray,25.0);
//            Run runner = new Run();
//            runner.execute();
            long start=System.currentTimeMillis();
            ave = mGetHeartRate.xyz(FrameArray,25.0);
            long end=System.currentTimeMillis();
            Log.i("Total time","Total time="+(end-start));
//            Log.i("HEART RATE","HR = "+ave);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(getApplicationContext(),Integer.toString((int)ave),Toast.LENGTH_SHORT).show();
                   // textView.setText("HR = "+Integer.toString((int)ave)+" bpm");
//                     Stuff that updates the UI
                    startActivity(new Intent(FdActivity.this,HeartRateDisplay.class));

                }
            });
        }
        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }




    public class Run extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
//            publishProgress("Calculating..."); // Calls onProgressUpdate()
            try {
//                int time = Integer.parseInt(params[0])*1000;

//                Thread.sleep(time);
                ave=mGetHeartRate.xyz(FrameArray,25.0);
                resp=Double.toString(ave);
                Log.i("hrt",resp);


            }
            catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
//            publishProgress("Calculating..."+resp);
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),Integer.toString((int)ave),Toast.LENGTH_SHORT).show();
                 //   textView.setText("HR = "+Integer.toString((int)ave)+" bpm");
//                     Stuff that updates the UI



                    progressDialog.dismiss();
                }
            });

//        finalResult.setText(result);
        }


        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show(FdActivity.this,
                            "ProgressDialog","iygib");
//                    textView.setText("HR = "+(ave)+" bpm");
                    // Stuff that updates the UI

                }
            });

//                "Wait for "+time.getText().toString()+ " seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
//        finalResult.setText(text[0]);
        }
    }
}
