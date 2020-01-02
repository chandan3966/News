package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LocationManager locationManager;
    private List<NewsItems> list;
    private String URL_DATA = "";
    private int count =0;
    private ProgressDialog progressDialog;
    private static final String TAG = "MainActivity";
    private AdView mAdView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String Address;
    private static boolean decide = false;

    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        declaraions();

        int delay = 0; // delay for 0 sec.
        int period = 1000; // repeat every 10 sec.
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                //Call function
                if (!URL_DATA.equals("")){
                    loadRecycleViewData();
                    timer.cancel();
                }
                else
                    loadRecycleViewData();
            }
        }, delay, period);
        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
        .setDeveloperModeEnabled(true)
        .build());

        HashMap<String,Object> defaults = new HashMap<>();
        defaults.put("ads",true);
        mFirebaseRemoteConfig.setDefaults(defaults);

        final Task<Void> fetch = mFirebaseRemoteConfig.fetch(0);
        fetch.addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseRemoteConfig.activateFetched();
                if (mFirebaseRemoteConfig.getBoolean("ads"))
                    mAdView.setVisibility(View.VISIBLE);
                else
                    mAdView.setVisibility(View.GONE);
            }
        });

    }

    private void declaraions(){
        notification = Notification.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        recyclerView = findViewById(R.id.recycle);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching news");
        progressDialog.setMessage("Please wait while we fetch your news");
        progressDialog.setCancelable(true);
        progressDialog.show();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getCountryCode(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Address = address.toLowerCase();
            if (URL_DATA.equals(""))
                URL_DATA = "https://newsapi.org/v2/top-headlines?country="+Address+"&apiKey=5a92e7d12a834156a3521b603a95a3af";


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void loadRecycleViewData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("articles");
//                            Toast.makeText(getApplicationContext(),array.length()+"",Toast.LENGTH_SHORT).s
                            for (int i=0;i<array.length();i++){
                                JSONObject o = array.getJSONObject(i);
                                NewsItems item = new NewsItems(
                                    o.getString("title"),
                                    o.getString("author"),
                                    o.getString("description"),
                                    o.getString("url"),
                                    o.getString("urlToImage"),
                                    o.getString("publishedAt"),
                                    o.getString("content")
                                );
                                list.add(item);
                            }
                            progressDialog.dismiss();
                            adapter = new NewsAdapter(list,getApplicationContext());
//                            adapter.setHasStableIds(true);
                            recyclerView.setAdapter(adapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
