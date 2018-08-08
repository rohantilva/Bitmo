package com.bitmo.bitmo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.UUID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public FirebaseManager firebaseManager;
    DatabaseReference myRef;
    UserEntry me;
    public ArrayList<RequestEntry> requests;
    public RequestEntryAdapter reqAdapt;
    ListView myList;


    SharedPreferences appData;
    SharedPreferences.Editor appEditor;

    //for bitcoin price
    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView txt;
    static String curr_price;
    public static final String amtKey = "Total_BTC";
    public float totalBTC;
    public boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstStart = true;
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseManager = FirebaseManager.setupInstance(getApplicationContext());
        appData = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        appEditor = appData.edit();

        txt = (TextView) findViewById(R.id.textView4);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BTC Price Loading");
        progressDialog.setMessage("Wait...");

        totalBTC = appData.getFloat(amtKey, -1);
        if (totalBTC == -1) {
            makeNewAccount();
        }

        requests = new ArrayList<RequestEntry>();
        myList = (ListView) findViewById(R.id.listView);
        reqAdapt = new RequestEntryAdapter(getApplicationContext(), R.layout.item_layout, requests);
        myList.setAdapter(reqAdapt);
        reqAdapt.notifyDataSetChanged();


        firebaseManager = FirebaseManager.getInstance();
        myRef = firebaseManager.getRef();
        myRef.child(appData.getString("pk", "0")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if (firstStart) {
                    me = dataSnapshot.getValue(UserEntry.class);
                    float currBTCBalance = Float.parseFloat(me.getTotalBTC());
                    appEditor.putFloat(amtKey, currBTCBalance);
                    appEditor.commit();
                    updateAmount();
                    updateNavPrice();
                //    firstStart = false;
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child(appData.getString("pk", "0")).child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestEntry req = snapshot.getValue(RequestEntry.class);
                    //requests.add(0, req);
                    requests.add(0, req);
                }
                reqAdapt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.child(appData.getString("pk", "0")).child("pay_queue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PayEntry p = snapshot.getValue(PayEntry.class);
                    totalBTC += Float.parseFloat(p.getAmt());
                    firebaseManager.addHistory(p);
                }
                appEditor.putFloat(amtKey, totalBTC);
                appEditor.commit();
                firebaseManager.setBTC(totalBTC);
                updateAmount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        load();
    }


    private void updateNavPrice() {
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        View head = navView.getHeaderView(0);
        TextView navAmt = head.findViewById(R.id.textView);
        TextView firstName = head.findViewById(R.id.headerName);
        firstName.setText(me.getFName() + " " + me.getLName());
        float total_USD = totalBTC * Float.parseFloat(appData.getString("curr_price", "0"));
        navAmt.setText(Float.toString(totalBTC) + " BTC | $" + total_USD);
    }

    public void updateAmount() {
        TextView amountView = (TextView)findViewById(R.id.textView2);
        totalBTC = appData.getFloat(amtKey, -1);

        if (totalBTC > 0) {
            // post this number
            amountView.setText(Float.toString(totalBTC));
        }  else if (totalBTC == 0){
            //prompt them to add more
            amountView.setText(Float.toString(totalBTC));
            Toast.makeText(getApplicationContext(), "Please add some BTC!", Toast.LENGTH_SHORT).show();
        } else {
            amountView.setText("0");
            totalBTC = 0;
            appEditor.putFloat(amtKey, totalBTC);
            appEditor.commit();
            Toast.makeText(getApplicationContext(), "Welcome! Please add some BTC!", Toast.LENGTH_SHORT).show();
        }
        TextView dlrView = (TextView)findViewById(R.id.textView3);
        TextView use = (TextView) findViewById(R.id.textView4);
        curr_price = appData.getString("curr_price", "0.0");
        float total_dlr = totalBTC*(float)(Float.parseFloat(curr_price));
        dlrView.setText(Float.toString(total_dlr));

    }

    public void makeNewAccount() {
        String pk = UUID.randomUUID().toString();
        pk = pk.replace("-", "");
        String sk = UUID.randomUUID().toString();
        sk = sk.replace("-", "");
        UserEntry newUser = new UserEntry(pk, "First", "Last", "0", pk, sk);
        firebaseManager.addUser(newUser);
        appEditor.putString("uname", pk);
        appEditor.putString("pk", pk);
        appEditor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAmount();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pay) {
            //launch a new activity
            Intent intent = new Intent(this, Pay.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_load) {
            load();
        }

        return super.onOptionsItemSelected(item);
    }

    private void load() {
        Request request = new Request.Builder()
                .url(BPI_ENDPOINT)
                .build();

        progressDialog.show();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Home.this, "Error during BPI loading : "
                        + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        //System.out.println("in here");
                        parseBpiResponse(body);
                    }
                });
            }
        });

    }

    private void parseBpiResponse(String body) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("at $");
            JSONObject jsonObject = new JSONObject(body);

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            String rate_val1 = usdObject.getString("rate");
            builder.append(usdObject.getString("rate").substring(0, usdObject.getString("rate").indexOf("."))).append("/BTC");
            txt.setText(builder.toString());
            String rate_val = rate_val1.replaceAll(",", "");
            curr_price = rate_val;
            appEditor.putString("curr_price", rate_val);
            appEditor.commit();
            updateAmount();
        } catch (Exception e) {

        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            onBackPressed();
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_contacts) {
            Intent intent = new Intent(this, Contacts.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Home.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Home.this, Profile.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
