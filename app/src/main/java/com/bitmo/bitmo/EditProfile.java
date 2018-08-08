package com.bitmo.bitmo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
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

public class EditProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText fName;
    EditText lName;
    EditText uName;
    FirebaseManager firebaseManager;
    DatabaseReference myRef;
    UserEntry me;
    SharedPreferences appData;
    SharedPreferences.Editor appEditor;
    float totalBTC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Edit Profile");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fName = findViewById(R.id.editText4);
        lName = findViewById(R.id.editText5);
        uName = findViewById(R.id.editText8);

        appData = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        appEditor = appData.edit();
        firebaseManager = FirebaseManager.getInstance();
        myRef = firebaseManager.getRef();
        myRef.child(appData.getString("pk", "@Frank")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserEntry.class);
                fName.setText(me.getFName());
                lName.setText(me.getLName());
                uName.setText(me.getUname());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseManager = FirebaseManager.setupInstance(getApplicationContext());
        firebaseManager = FirebaseManager.getInstance();
        myRef = firebaseManager.getRef();
        totalBTC = appData.getFloat("Total_BTC", -1);

        myRef.child(appData.getString("pk", "0")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserEntry.class);
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

    public void savePressed(View view) {
        String fNameStr = fName.getText().toString();
        String lNameStr = lName.getText().toString();
        String uNameStr = uName.getText().toString();
        firebaseManager.updateUserField("fname", fNameStr);
        firebaseManager.updateUserField("lname", lNameStr);
        firebaseManager.updateUserField("uname", uNameStr);
        firebaseManager.changeUser(appData.getString("pk", "0"), uNameStr);
        onBackPressed();
    }

    public void cancelPressed(View view) {
        onBackPressed();
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
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(EditProfile.this, Home.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_contacts) {
            Intent intent = new Intent(this, Contacts.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(EditProfile.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(EditProfile.this, Profile.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
