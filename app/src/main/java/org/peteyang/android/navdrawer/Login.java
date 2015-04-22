package org.peteyang.android.navdrawer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Login extends Activity implements View.OnClickListener {

    public String user_name_ori,loggedin_username = "";
    public int loggedin_status;
    private EditText user, pass;
    private Button mSubmit, mRegister;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();


    // testing on Emulator:
    private static final String LOGIN_URL = "http://128.199.117.135/webservice/login.php";
    private static final String CHECK_LOGIN_URL = "http://128.199.117.135/webservice/checklogin.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public String deviceID = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TelephonyManager mngr2 = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String devid2 = null;
        if(mngr2.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            devid2 = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            //Log.d("IMEI! ", devid2);
        }else{
            devid2 = mngr2.getDeviceId();
            //Log.d("IMEI! ", devid2);
        }

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        if(deviceId == null){
            deviceId = devid2;
        }

        Log.d("IMEI! ", deviceId);
        deviceID = deviceId;






        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setup input fields
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);

        //setup buttons
        mSubmit = (Button) findViewById(R.id.login);
        //mRegister = (TextView) findViewById(R.id.register);

        //register listeners
        mSubmit.setOnClickListener(this);
        //mRegister.setOnClickListener(this);
        this.setTitle("Log in");

//        if(savedInstanceState == null){
//
//
//
//
//        }


        //Check Log in Status
//        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String devid = mngr.getDeviceId();
//




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                new AttemptLogin().execute();
                break;
            case R.id.register:
                Intent i = new Intent(this, Register.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }


    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }



        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag



            int success;
            String username = user.getText().toString();
            user_name_ori = username;
            String password = pass.getText().toString();
            String devID2 = deviceID;
            try {


                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("devid", devID2));


                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    //
                    Log.d("IMEI! ", deviceID);
                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor edit = sp.edit();

                    edit.putString("username", username);
                    edit.commit();

                    Intent i = new Intent(Login.this, MainActivity.class);
                    finish();
                    i.putExtra("username",username);
                    startActivity(i);
                    //Log.d("query!: ", json.getString("query"));
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }

        }



    }



}
