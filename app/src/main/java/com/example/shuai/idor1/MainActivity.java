package com.example.shuai.idor1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_ENABLE_BLE = 1;

    private LatLng location;
    private Marker sohoMarker;
    private Marker currLocation = null;
    private MapView mapView;
    MapboxMap mMap;

//    C0:86:6D:94:0C:D4;



    private Handler timerHandler  = new Handler();

    String MAC1 = "C0:86:6D:94:0C:D4";
    String MAC2 = "C5:80:03:5C:C2:AB";
    String MAC3 = "E5:79:F9:1C:53:2C";
    String MAC4 = "DC:AF:A3:63:37:0B";

    LatLng Tag1 = new LatLng(39.995187, 116.474437);//    C0:86:6D:94:0C:D4;     MB_17958
    LatLng Tag2 = new LatLng(39.995182, 116.474459);//    C5:80:03:5C:C2:AB;    MB_17891
    LatLng Tag3 = new LatLng(39.995151, 116.474422);//    E5:F9:F9:1C:53:2C;    MB_17956
    LatLng Tag4 = new LatLng(39.995159, 116.474406);//    DC:AF:A3:63:37:0B;    Fmxy-109746

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Mapbox access token only needs to be configured once in your app
//        MapboxAccountManager.start(this,AccessToken);

        // This contains the MapView in XML and needs to be called after the account manager
//        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMap = mapboxMap;

                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.maps_logo);
                Icon icon = iconFactory.fromDrawable(iconDrawable);

//                sohoMarker = mapboxMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(init_lat, init_Lng)));
                mapboxMap.setOnMarkerClickListener(myMarkerClickListener);
                mapboxMap.setOnMapLongClickListener(myMapLongClickListener);
                mapboxMap.setOnMapClickListener(myMapClickListener);


                BLE_Init();
                BLE.scanDevices(true);
                startRepeatingTask();

            }
        });
    }

    Runnable bleCheck = new Runnable() {
        @Override
        public void run() {
//            try {
                if (BLE.isResultsAvailable()){

//                    Log.d("ScanResults",Integer.toString(BLE.bleScanList.get(MAC1)));

//                    SC_Utils.toastMessage(MainActivity.this,BLE.bleScanList.toString());
                    int bleNo = 0;
//                    for (int i=0,len=BLE.bleScanList.size();i<len;i++) {
//                        if (BLE.bleScanList.containsKey(MAC1))
//                    }
                    if (BLE.bleScanList.containsKey(MAC1)) bleNo++;
                    if (BLE.bleScanList.containsKey(MAC2)) bleNo++;
                    if (BLE.bleScanList.containsKey(MAC3)) bleNo++;
                    if (BLE.bleScanList.containsKey(MAC4)) bleNo++;
                    Log.d("ScanResults",Integer.toString(bleNo));

                    double[][] dataTagLla = new double[3][bleNo];
                    if (BLE.bleScanList.containsKey(MAC1)){
                        dataTagLla[0][0] = Tag1.getLatitude();
                        Log.d("Debug00",Double.toString(Tag1.getLatitude()));
                        dataTagLla[1][0] = Tag1.getLongitude();
                        int rssi =  BLE.bleScanList.get(MAC1);
                        dataTagLla[2][0] = rssi;
                    }
                    if (BLE.bleScanList.containsKey(MAC2)) {
                        dataTagLla[0][1] = Tag2.getLatitude();
                        Log.d("Debug01",Double.toString(Tag2.getLatitude()));
                        dataTagLla[1][1] = Tag2.getLongitude();
                        int rssi =  BLE.bleScanList.get(MAC2);
                        dataTagLla[2][1] = rssi;
                    }
                    if (BLE.bleScanList.containsKey(MAC3)) {
                        Log.d("Debug03",Double.toString(Tag3.getLatitude()));
                        dataTagLla[0][2] = Tag3.getLatitude();
                        dataTagLla[1][2] = Tag3.getLongitude();
                        int rssi =  BLE.bleScanList.get(MAC3);
                        dataTagLla[2][2] = rssi;
                    }
                    if (BLE.bleScanList.containsKey(MAC4)) {
                        Log.d("Debug04",Double.toString(Tag4.getLatitude()));
                        dataTagLla[0][3] = Tag4.getLatitude();
                        dataTagLla[1][3] = Tag4.getLongitude();
                        int rssi =  BLE.bleScanList.get(MAC4);
                        dataTagLla[2][3] = rssi;
                    }
                    Log.d("Debuglla", Arrays.toString(dataTagLla[0]));
                    double[] point = onBleLocate(dataTagLla,0f);
                    location = new LatLng(point[0],point[1]);

                    Log.d("Debug","location = "+location.toString());
                    if (currLocation == null){
                        currLocation = mMap.addMarker(new MarkerOptions()
                                .position(location));
                    }else
                        currLocation.setPosition(location);
                }


                BLE.scanDevices(true);

/*            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception

//                timerHandler.postDelayed(bleCheck, 2000);
            }*/
            timerHandler.postDelayed(bleCheck, 2000);
        }
    };

    void startRepeatingTask() {
        bleCheck.run();
    }

    void stopRepeatingTask() {
        timerHandler.removeCallbacks(bleCheck);
    }

    private void BLE_Init(){
        if (!BLE.isSupported(this)) return;
        if (!BLE.isON(this)){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);
        }


    }

    /**
     * BLE 回调结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //region 监听
    private MapboxMap.OnMarkerClickListener myMarkerClickListener = new MapboxMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            /*if(sohoMarker == marker){
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(init_lat, init_Lng)) // Sets the new camera position
                        .zoom(20) // Sets the zoom
                        .bearing(180) // Rotate the camera
                        .tilt(30) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 3000);
                return true;
            }*/
            return false;
        }
    };

    private MapboxMap.OnMapLongClickListener myMapLongClickListener = new MapboxMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(@NonNull LatLng point) {
            if (currLocation == null){
                currLocation = mMap.addMarker(new MarkerOptions()
                        .position(point));
            }else
                currLocation.setPosition(point);
            SC_Utils.toastMessage(MainActivity.this,"Coordinates: ("+point.getLatitude() +", "+ point.getLongitude());
        }
    };

    private MapboxMap.OnMapClickListener myMapClickListener = new MapboxMap.OnMapClickListener() {
        @Override
        public void onMapClick(@NonNull LatLng point) {

            CameraPosition position = new CameraPosition.Builder()
                    .target(point) // Sets the new camera position
//                    .zoom(20) // Sets the zoom
//                    .bearing(180) // Rotate the camera
//                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 1000);


            if (BLE.isResultsAvailable()){
                SC_Utils.toastMessage(MainActivity.this,BLE.bleScanList.toString());
            }
            BLE.scanDevices(true);
        }
    };
    //endregion

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopRepeatingTask();
    }


    //region ye's functions
    public double[] onBleLocate(double[][] dataTagLla, float tagHeight) {
        double[][] dataTagLlaTrans = arrTrans(dataTagLla);
        double[][] dataTagTrans = new double[dataTagLlaTrans.length][dataTagLlaTrans[0].length];
        for (int i = 0; i < dataTagLlaTrans.length; i++) {
            dataTagTrans[i] = llaToFlat(dataTagLlaTrans[i], dataTagLlaTrans[0], Math.PI / 2);
            dataTagTrans[i][2] = dataTagLla[2][i];
        }
        double[][] dataTag = arrTrans(dataTagTrans);
        int n = dataTag[0].length;
        double[] mPos;
        double b = 2.5;
        double[] w = new double[n];
        for (int i = 0; i < n; i++) {
            w[i] = Math.pow(10, dataTag[2][i] / 10 / b);
        }
        double sumW = 0;
        for (double i : w) {
            sumW += i;
        }
        for (int i = 0; i < n; i++) {
            w[i] = w[i] / sumW;
        }
        double[] iPos = new double[3];
        iPos[0] = 0;
        iPos[1] = 0;
        iPos[2] = 0;
        for (int i = 0; i < n; i++) {
            iPos[0] += w[i] * dataTag[0][i];
            iPos[1] += w[i] * dataTag[1][i];
        }
        mPos = flatToLla(iPos, dataTagLlaTrans[0], Math.PI / 2);
        double[] iDis = new double[n];
        for (int i = 0; i < n; i++) {
            iDis[i] = Math.sqrt((iPos[0] - dataTag[0][i]) * (iPos[0] - dataTag[0][i]) + (iPos[1] - dataTag[1][i]) * (iPos[1] - dataTag[1][i]) + tagHeight * tagHeight);
        }
        double aEst = 0;
        for (int i = 0; i < n; i++) {
            aEst += (dataTag[2][i] + 10 * b * Math.log10(iDis[i])) / n;
        }
        double[] estSet = new double[3];
        estSet[0] = iPos[0];
        estSet[1] = iPos[1];
        estSet[2] = aEst;
        double[] disEst = new double[n];
        double[] rngEst = new double[n];
        for (int i = 0; i < n; i++) {
            disEst[i] = Math.sqrt((estSet[0] - dataTag[0][i]) * (estSet[0] - dataTag[0][i]) + (estSet[1] - dataTag[1][i]) * (estSet[1] - dataTag[1][i]) + tagHeight * tagHeight);
            rngEst[i] = Math.pow(10, (estSet[2] - dataTag[2][i]) / 10 / b);
        }
        double minErr = arrNorm(arrSub(arrDot(w, rngEst), arrDot(w, disEst)));
        double d_p = 10d;
        int NOI = 50;
        for (int i = 0; i < NOI; i++) {
            double[][] estSetDrv = new double[n][3];
            for (int j = 0; j < n; j++) {
                estSetDrv[j][0] = w[j] * (-estSet[0] + dataTag[0][j]) / disEst[j];
                estSetDrv[j][1] = w[j] * (-estSet[1] + dataTag[1][j]) / disEst[j];
                estSetDrv[j][2] = w[j] * rngEst[j] * Math.log(10) / 10 / b;
            }
            double[][] transedEstSetDrv = arrTrans(estSetDrv);
            float[] estSetDrvVec = new float[9];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (j == k) {
                        estSetDrvVec[j * 3 + k] = (float) ((1 + d_p) * arrMlp(transedEstSetDrv[j], transedEstSetDrv[k]));
                    } else {
                        estSetDrvVec[j * 3 + k] = (float) arrMlp(transedEstSetDrv[j], transedEstSetDrv[k]);
                    }
                }
            }
            Matrix estSetDrvMatrix = new Matrix();
            Matrix estSetDrvMatrixInv = new Matrix();
            estSetDrvMatrix.setValues(estSetDrvVec);
            estSetDrvMatrix.invert(estSetDrvMatrixInv);
            float[] estSetDrvVecInv = new float[9];
            estSetDrvMatrixInv.getValues(estSetDrvVecInv);
            double[][] estSetDrvMatInv = new double[3][3];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    estSetDrvMatInv[j][k] = estSetDrvVecInv[j * 3 + k];
                }
            }
            double[] delta = new double[3];
            double[] disErr = arrSub(rngEst, disEst);
            for (int j = 0; j < 3; j++) {
                double[] tempArr = new double[n];
                for (int k = 0; k < n; k++) {
                    tempArr[k] = arrMlp(estSetDrvMatInv[j], estSetDrv[k]);
                }
                delta[j] = -arrMlp(tempArr, arrDot(w, disErr));
            }
            double[] stepGrad = new double[3];
            for (int j = 0; j < 3; j++) {
                stepGrad[j] = arrMlp(transedEstSetDrv[j], arrDot(w, disErr));
            }
            double[] trustRgnRes = new double[n];
            for (int j = 0; j < 3; j++) {
                trustRgnRes[j] = arrMlp(estSetDrv[j], delta);
            }
            double trustRgn = -(arrMlp(stepGrad, delta) + 0.5 * arrMlp(trustRgnRes, trustRgnRes));
            double[] estSetTmp = arrAdd(estSet, delta);
            double[] disEstTmp = new double[n];
            double[] rngEstTmp = new double[n];
            for (int j = 0; j < n; j++) {
                disEstTmp[j] = Math.sqrt((estSetTmp[0] - dataTag[0][j]) * (estSetTmp[0] - dataTag[0][j]) + (estSetTmp[1] - dataTag[1][j]) * (estSetTmp[1] - dataTag[1][j]) + tagHeight * tagHeight);
                rngEstTmp[j] = Math.pow(10, (estSetTmp[2] - dataTag[2][j]) / 10 / b);
            }
            double[] disErrTmp = arrSub(rngEstTmp, disEstTmp);
            double[][] estSetDrvMat = new double[3][3];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    estSetDrvMat[j][k] = estSetDrvVec[j * 3 + k];
                }
            }
            double[] rho_tmp_1 = new double[3];
            for (int j = 0; j < 3; j++) {
                rho_tmp_1[j] = arrMlp(estSetDrvMat[j], delta);
            }
            double[] rho_tmp_2 = arrAdd(arrNumMlp(delta, d_p), rho_tmp_1);
            double rho = (Math.pow(minErr, 2) - arrMlp(arrDot(w, disErrTmp), arrDot(w, disErrTmp))) / arrMlp(arrNumMlp(delta, 2.d), rho_tmp_2);
            if (rho > 0.01) {
                d_p = Math.max(d_p / 4, 0.01d);
                minErr = arrNorm(arrDot(w, disErrTmp));
                estSet = estSetTmp;
                disEst = disEstTmp;
                rngEst = rngEstTmp;
                mPos[0] = estSet[0];
                mPos[1] = estSet[1];
            } else {
                d_p = Math.min(d_p * 5, 10d);
            }
            if (Math.abs(trustRgn) < 0.00001) {
                break;
            }
        }

        mPos = flatToLla(mPos, dataTagLlaTrans[0], Math.PI / 2);
        return mPos;
    }

    public double[] arrNumMlp(double[] arr, double x) {
        int n = arr.length;
        double[] numMlpedArr = new double[n];
        for (int i = 0; i < n; i++) {
            numMlpedArr[i] = x * arr[i];
        }
        return numMlpedArr;
    }

    public double arrMlp(double[] arr1, double[] arr2) {
        int n1 = arr1.length;
        int n2 = arr2.length;
        if (n1 == n2) {
            double sumSqrt = 0d;
            for (int i = 0; i < n1; i++) {
                sumSqrt += arr1[i] * arr2[i];
            }
            return sumSqrt;
        } else {
            return 0;
        }
    }

    public double[] arrAdd(double[] arr1, double[] arr2) {
        int n1 = arr1.length;
        int n2 = arr2.length;
        if (n1 == n2) {
            double[] addedArr = new double[n1];
            for (int i = 0; i < n1; i++) {
                addedArr[i] = arr1[i] + arr2[i];
            }
            return addedArr;
        } else {
            return null;
        }
    }

    public double[] arrSub(double[] arr1, double[] arr2) {
        int n1 = arr1.length;
        int n2 = arr2.length;
        if (n1 == n2) {
            double[] subedArr = new double[n1];
            for (int i = 0; i < n1; i++) {
                subedArr[i] = arr1[i] - arr2[i];
            }
            return subedArr;
        } else {
            return null;
        }
    }

    public double[] arrDot(double[] arr1, double[] arr2) {
        int n1 = arr1.length;
        int n2 = arr2.length;
        if (n1 == n2) {
            double[] dotedArr = new double[n1];
            for (int i = 0; i < n1; i++) {
                dotedArr[i] = arr1[i] * arr2[i];
            }
            return dotedArr;
        } else {
            return null;
        }
    }

    public double arrNorm(double[] arr) {
        double normArr = 0;
        for (double anArr : arr) {
            normArr += anArr * anArr;
        }
        return Math.sqrt(normArr);
    }

    public double[][] arrTrans(double[][] arr) {
        int m = arr.length;
        int n = arr[0].length;
        double[][] transedArr = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                transedArr[i][j] = arr[j][i];
            }
        }
        return transedArr;
    }

    public double[] llaToFlat(double[] lla, double[] llo, double psi0) {
        double[] dLlaLlo = arrSub(lla, llo);
        double f = 1d / 298.257223563;
        int R = 6378137;
        double Rn = R / Math.sqrt(1 - (2 * f - f * f) * Math.sin(Math.toRadians(llo[0])) * Math.sin(Math.toRadians(llo[0])));
        double Rm = Rn * (1 - (2 * f - f * f)) / (1 - (2 * f - f * f) * Math.sin(Math.toRadians(llo[0])) * Math.sin(Math.toRadians(llo[0])));
        double dN = dLlaLlo[0] / Math.toDegrees(Math.atan(1 / Rm));
        double dE = dLlaLlo[1] / Math.toDegrees(Math.atan(1 / Rn / Math.cos(Math.toRadians(llo[0]))));
        double[] xyz = new double[3];
        xyz[0] = Math.cos(psi0) * dN + Math.sin(psi0) * dE;
        xyz[1] = -Math.sin(psi0) * dN + Math.cos(psi0) * dE;
        xyz[2] = lla[2] - llo[2];
        return xyz;
    }

    public double[] flatToLla(double[] xyz, double[] llo, double psi0) {
        double[] dLlaLlo = new double[3];
        double dN = Math.cos(psi0) * xyz[0] - Math.sin(psi0) * xyz[1];
        double dE = Math.sin(psi0) * xyz[0] + Math.cos(psi0) * xyz[1];
        double f = 1d / 298.257223563;
        int R = 6378137;
        double Rn = R / Math.sqrt(1 - (2 * f - f * f) * Math.sin(Math.toRadians(llo[0])) * Math.sin(Math.toRadians(llo[0])));
        double Rm = Rn * (1 - (2 * f - f * f)) / (1 - (2 * f - f * f) * Math.sin(Math.toRadians(llo[0])) * Math.sin(Math.toRadians(llo[0])));
        dLlaLlo[0] = dN * Math.toDegrees(Math.atan(1 / Rm));
        dLlaLlo[1] = dE * Math.toDegrees(Math.atan(1 / Rn / Math.cos(Math.toRadians(llo[0]))));
        dLlaLlo[2] = xyz[2] - llo[2];
        return arrAdd(llo, dLlaLlo);
    }
    //endregion


}
