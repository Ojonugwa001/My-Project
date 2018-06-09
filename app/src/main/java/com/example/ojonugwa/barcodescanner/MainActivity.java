package com.example.ojonugwa.barcodescanner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    AlertDialog alert;
    String qrInfo;
    final int RequestCameraPermissionID = 1001;
    SparseArray<Barcode> qrcodes;

    String mMatricNo;
    String mName;
    String mDepartment;
    String mLevel;
    String mSession;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }


    /** Sample JSON response for a USGS query */
    private static final String SAMPLE_JSON_RESPONSE = "{\"Student\":[{ \"mMatricNo\":\"13MS1023\",\"surname\":\"Alikali\", \"mName\":\"Ojonugwa Justice\",    \"mDepartment\":\"Mathematical Sciences(computer option)\",\"mLevel\":\"400\",\"mSession\":\"2016/2017\",\"amount\":\"N46,500\",\"description\":\"RETURNING INDIGENE 400 AND 500 LEVEL SCHOOL FEE\",\"payment date\":\"2018-02-14\" },{ \"mMatricNo\":\"14MS1043\",\"surname\":\"Iyanda\", \"mName\":\"Moses Iko-Ojo\",    \"mDepartment\":\"Mathematical Sciences\",\"mLevel\":\"200\",\"mSession\":\"2016/2017\",\"amount\":\"N46,500\",\"description\":\"RETURNING: INDIGENE 200,300 AND 400 LEVEL SCHOOL FEE\",\"payment date\":\"2017-10-18\" }]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraPreview = findViewById(R.id.cameraView);
        txtResult = findViewById(R.id.txtResult);

        View scannerLayout = findViewById(R.id.scannerLayout);
        View scannerBar = findViewById(R.id.scannerBar);
        Scanner scanner = new Scanner();
        scanner.scannerAnimator(scannerLayout, scannerBar);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();

        // Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request Permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//                //  Auto focus
            }

            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                qrcodes = detections.getDetectedItems();

                if (qrcodes.size() != 0){
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                        //Create vibrate
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                        qrInfo = qrcodes.valueAt(0).displayValue;

                        barcodeDetector.release();

                        ///// Recent ////
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertBuilder.setIcon(R.drawable.ic_error_outline_black_24dp);
                        alertBuilder.setTitle("Receipt Details");
                        alertBuilder.setMessage(qrInfo);

                        ImageView showImage = new ImageView(MainActivity.this);
                        showImage.setImageResource(R.drawable.ic_error_outline_black_24dp);
                        alertBuilder.setView(showImage);

                        alertBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
//                                    dialogInterface.cancel();
                            }
                        });

                        alert = alertBuilder.create();
                        alert.show();

                        }



                    });
                }

            }
        });
        


    }

    private void showDiag() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        final TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(qrInfo);

        final ImageView image = dialog.findViewById(R.id.indicator);
        Log.d("JSONData", mMatricNo + " " + mName + " " + mDepartment + " " + mLevel +
                " " + mSession);


        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrinfoParser()){
                    final Dialog dialog = new Dialog(getApplicationContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog);
                    image.setBackgroundColor(getResources().getColor(R.color.Success));
                    text.setText("Verified");


                } else {
                    final Dialog dialog = new Dialog(getApplicationContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog);

                    text.setText("Unpaid");
                }
            }
        });

        dialog.show();

    }
    public boolean qrinfoParser(){

        String qrMatricNo = null;
        String qrName = null;
        String qrDepartment = null;
        String qrLevel = null;
        String qrSession = null;
        try {
            String[] parts = qrInfo.split(Pattern.quote(" || "));
            qrMatricNo = parts[0];
            qrName = parts[1];
            qrDepartment = parts[2];
            qrLevel = parts[3];
            qrSession = parts[4];

            JSON(qrMatricNo, qrName, qrDepartment, qrLevel, qrSession);
        } catch (Exception e) {
            Log.d("qrinfoParser:", " No qrInfo found ");
        }


        if (qrMatricNo.equalsIgnoreCase(mMatricNo) && qrName.equalsIgnoreCase(mName) &&
                qrDepartment.equalsIgnoreCase(mDepartment) && qrLevel.equalsIgnoreCase(mLevel) &&
                qrSession.equalsIgnoreCase(mSession)){
            return true;
        }else  { return false; }

    }

    public void JSON( String matno, String name, String department, String level, String session) {
        String matricNo;
        String name1;
        String department1;
        String level1;
        String session1;

        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Students objects with the corresponding data.

            JSONObject jsonobj = new JSONObject(SAMPLE_JSON_RESPONSE);
            JSONArray studentArray = jsonobj.getJSONArray("student");

            for (int i=0; i < studentArray.length(); i++){
                JSONObject currentStudent = studentArray.getJSONObject(i);

                // Extract the value for the keys "matricNo, name, department, level, session"
                matricNo = currentStudent.getString("matricNo");
                name1= currentStudent.getString("surname") +
                        "" + currentStudent.getString("name");
                department1 = currentStudent.getString("department");
                level1 = currentStudent.getString("level");
                session1 = currentStudent.getString("session");

                if (matricNo.equalsIgnoreCase(matno) &&
                        name1.equalsIgnoreCase(name) &&
                        department1.equalsIgnoreCase(department) &&
                        level1.equalsIgnoreCase(level) &&
                        session1.equalsIgnoreCase(session) ){
                    mMatricNo = matricNo;
                    mName = name1;
                    mDepartment = department1;
                    mLevel = level1;
                    mSession = session1;
                }

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }


    }

}

