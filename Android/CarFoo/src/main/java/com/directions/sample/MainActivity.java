package com.directions.sample;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.location.LocationListener;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    TextView something;
    Location lastloc;
    float distance_travelled;

    public void change(String abc) {
        something.setText(abc);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        LoginButton loginButton = (LoginButton)this.findViewById(R.id.login_button);
        something = (TextView) this.findViewById(R.id.text1);
        distance_travelled = 0;
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        callbackManager = CallbackManager.Factory.create();

        //

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener  = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastloc == null){
                    lastloc = location;return;}
                else{
                    distance_travelled += lastloc.distanceTo(location);
                    lastloc = location;
                    change(Float.toString(distance_travelled));
                }
                //TODO -- Barney says what should happen
                // when the framework invokes onLocationChanged
            }
            @Override
            public void onProviderDisabled(String z) {
                return;
            }
            @Override
            public void onProviderEnabled(String z) {
                return;
            }
            @Override
            public void onStatusChanged(String z, int a, Bundle c) {
                return;
            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)10000, (float)0, listener);


                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.e("token",loginResult.getAccessToken().toString());
            }

            @Override
            public void onCancel() {
                Log.e("callback1","cancelled");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("callback1","error");
                // App code
            }
        });
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    this.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("sa","dsds");

        } catch (NoSuchAlgorithmException e) {
            Log.e("sa","sdsds");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void sendMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, EstimateActivity.class);
        startActivity(intent);
    }

}
