package org.peteyang.android.navdrawer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Welcome extends ActionBarActivity {

    public String loggedin_username = "";
    public String deviceID = null;
    private static final String CHECK_LOGIN_URL = "http://128.199.117.135/webservice/checklogin.php";

    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new AttemptWelcome().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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

    class AttemptWelcome extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(Login.this);
//            pDialog.setMessage("Attempting login...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }



        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag


//            int success;
//            String username = user.getText().toString();
//            user_name_ori = username;
//            String password = pass.getText().toString();
//            String devID2 = deviceID;

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


            try {


                List<NameValuePair> check_params = new ArrayList<NameValuePair>();
                check_params.add(new BasicNameValuePair("devid", deviceId));
                Log.d("check login status of: ", deviceId);


                    JSONObject check_login_json = jsonParser.makeHttpRequest(CHECK_LOGIN_URL, "POST", check_params);
                    Log.d("Result", check_login_json.toString());
                    loggedin_username = check_login_json.getString("loggedin_username");
                    Log.d("loggedin_username: ", loggedin_username);


                if (!loggedin_username.equals("null")) {


                    Intent i = new Intent(Welcome.this, MainActivity.class);
                    finish();
                    i.putExtra("username", loggedin_username);

                    startActivity(i);



                } else {

                    Intent i = new Intent(Welcome.this, Login.class);
                    finish();
                    //i.putExtra("username", loggedin_username);
                    startActivity(i);

                }

                return null;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {


            // dismiss the dialog once product deleted
//            pDialog.dismiss();
//            if (file_url != null) {
//                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
//            }

        }



    }
}
