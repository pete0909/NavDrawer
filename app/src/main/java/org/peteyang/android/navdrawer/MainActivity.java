package org.peteyang.android.navdrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {


    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    public String main_username;

    public int success_val = 0;
    public String TAG_MESSAGE = "message";
    public String deviceID = null;


    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Chats","Add Friends","Friend Lists","About GapChat","Log Out"};
    int ICONS[] = {R.drawable.ic_question_answer_grey600_36dp,R.drawable.ic_group_add_grey600_36dp,R.drawable.ic_person_grey600_36dp,R.drawable.ic_error_grey600_36dp,R.drawable.ic_error_grey600_36dp};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "Akash Bangad";
    String EMAIL = "akash.bangad@android4devs.com";
    int PROFILE = R.drawable.avatar;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle




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


        Intent k = getIntent();
        main_username = k.getStringExtra("username");
        NAME = main_username;
        EMAIL = main_username+"@gmail.com";


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);




        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

                String des = null;
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());



                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();

                    int position = recyclerView.getChildPosition(child);

                    if(position == 1){
                        des = "Here are your chats!";
                        LinearLayout scr1 = (LinearLayout)findViewById(R.id.screen1);
                        scr1.setVisibility(View.VISIBLE);

                        LinearLayout scr2 = (LinearLayout)findViewById(R.id.screen2);
                        scr2.setVisibility(View.GONE);
                    }else if(position == 2){
                        des = "Let's meet more friends!";
                        LinearLayout scr2 = (LinearLayout)findViewById(R.id.screen2);
                        scr2.setVisibility(View.VISIBLE);

                        LinearLayout scr1 = (LinearLayout)findViewById(R.id.screen1);
                        scr1.setVisibility(View.GONE);
                    }else if(position == 3){
                        des = "List of your friends!";
                    }else if(position == 4){
                        des = "GapChat Prerelease V1.0!";
                    }else if(position == 5){
                        des = "Logging Out!";
                        Toast.makeText(MainActivity.this,des,Toast.LENGTH_SHORT).show();
                        new AttemptLogout().execute();
                    }

                    Toast.makeText(MainActivity.this,des,Toast.LENGTH_SHORT).show();

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });


        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

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


    class AttemptLogout extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting logout...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }



        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String devID2 = deviceID;
            String username_out = main_username;

            try {


                // Building Parameters
                List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                params2.add(new BasicNameValuePair("username", username_out));
                params2.add(new BasicNameValuePair("devid", devID2));
                Log.d("logout!", username_out);
                Log.d("devid", devID2);
                Log.d("request logout!", "starting");
                String LOGOUT_URL = "http://128.199.117.135/webservice/logout.php";
                // getting product details by making HTTP request
                JSONObject logout_json = jsonParser.makeHttpRequest(LOGOUT_URL, "POST",
                        params2);

                // check your log for json response
                Log.d("Logout attempt", logout_json.toString());

                // json success tag
                success_val = logout_json.getInt("success");
                Log.d("success", Integer.toString(success_val));

                if (success_val == 1) {
                    Log.d("Log Out Successful!", logout_json.getString("reply"));
                    //
                    Log.d("IMEI! ", deviceID);
                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor edit = sp.edit();

                    edit.putString("username", username_out);
                    edit.commit();

                    Intent j = new Intent(MainActivity.this, Login.class);
                    finish();
                    //i.putExtra("username",username);
                    startActivity(j);
                    //Log.d("query!: ", json.getString("query"));
                    return logout_json.getString("reply");
                } else {
                    Log.d("Log Out Failure!", logout_json.getString("reply"));
                    return logout_json.getString("reply");
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
                //Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }



    }


}