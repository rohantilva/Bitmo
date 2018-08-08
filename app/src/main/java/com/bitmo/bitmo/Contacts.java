package com.bitmo.bitmo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FirebaseManager firebaseManager;
    private ContactDBAdapter contactDBAdapter;
    private ListView myList;
    protected ArrayList<ContactEntry> myItems;
    protected ContactEntryAdapter contactAdapter;
    private static Cursor curse;

    DatabaseReference myRef;
    UserEntry me;
    SharedPreferences appData;
    SharedPreferences.Editor appEditor;
    float totalBTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Contacts.this, AddContact.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        contactDBAdapter = contactDBAdapter.getInstance(getApplicationContext());
        contactDBAdapter.open();

        myItems = new ArrayList<ContactEntry>();
        myList = (ListView) findViewById(R.id.listViewContacts);
        contactAdapter = new ContactEntryAdapter(this, R.layout.contact_entry_layout, myItems);
        myList.setAdapter(contactAdapter);

        firebaseManager = FirebaseManager.getInstance();
        myRef = firebaseManager.getRef();
        //totalBTC = appData.getFloat("Total_BTC", -1);
        /*
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
        */
    }

    @Override
    public void onResume() {

        super.onResume();
        updateContacts();
    }

    public void updateContacts() {
        curse = contactDBAdapter.getAllItems();
        myItems.clear();
        if (curse.moveToFirst())
            do {
                ContactEntry result = new ContactEntry(curse.getString(1), curse.getString(2),
                        curse.getString(3), curse.getString(4));
                myItems.add(0, result);  // puts in reverse order
            } while (curse.moveToNext());

        contactAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(Contacts.this, Home.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_contacts) {
            onBackPressed();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Contacts.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Contacts.this, Profile.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
