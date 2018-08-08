package com.bitmo.bitmo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences.Editor appEditor;

    public static final String amtKey = "Total_BTC";
    FirebaseManager firebaseManager;
    DatabaseReference myRef;
    UserEntry me;
    SharedPreferences appData;
    TextView pub;
    TextView priv;
    float totalBTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        appData = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        appEditor = appData.edit();


        pub = (TextView) findViewById(R.id.editText6);
        priv = (TextView) findViewById(R.id.editText7);

        firebaseManager = FirebaseManager.getInstance();
        myRef = firebaseManager.getRef();
        totalBTC = appData.getFloat("Total_BTC", -1);
        myRef.child(appData.getString("pk", "0")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserEntry.class);
                System.out.println(me.getPK());
                pub.setText(me.getPK());
                priv.setText(me.getSK());
                updateNavPrice();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void restore(View view) {
        TextView newPKText = findViewById(R.id.editText2);
        TextView newSKText = findViewById(R.id.editText3);
        final String newPK = newPKText.getText().toString();
        final String newSK = newSKText.getText().toString();

        if (newPK.equals("") || newSK.equals("")) {
            Toast.makeText(Settings.this, "Please enter valid information", Toast.LENGTH_SHORT).show();
            return;
        }

        myRef.child(newPK).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserEntry.class);
                if (me == null || !me.getSK().equals(newSK)) {
                    Toast.makeText(getApplicationContext(), "Invalid keypair", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseManager.changeUser(me.getPK(), me.getSK());
                appEditor.putString("pk", newPK);
                appEditor.putString("uname", me.getUname());
                appEditor.putFloat("Total_BTC", Float.parseFloat(me.getTotalBTC()));
                appEditor.commit();
                pub.setText(me.getPK());
                priv.setText(me.getSK());
                Toast.makeText(getApplicationContext(), "Account restored", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void addSat(View view) {
        float valueToAdd = 500;
        myRef.child(appData.getString("pk", "0")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserEntry.class);
                float currBTCBalance = Float.parseFloat(me.getTotalBTC());
                firebaseManager.setBTC(currBTCBalance + 5.0f);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(Settings.this, Home.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_contacts) {
            Intent intent = new Intent(this, Contacts.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            onBackPressed();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Settings.this, Profile.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
