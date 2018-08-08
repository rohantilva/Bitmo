package com.bitmo.bitmo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class Pay extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences appData;
    SharedPreferences.Editor appEditor;
    EditText edittext;
    EditText edit_this;
    EditText address_edit;
    public static final String amtKey = "Total_BTC";
    FirebaseManager fbManager;
    DatabaseReference myRef;
    UserEntry me;
    float total_btc_amt = 0;
    //private String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
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

        fbManager = FirebaseManager.getInstance();


        edittext = (EditText) findViewById(R.id.editText9);
        address_edit = (EditText) findViewById(R.id.editText);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null ) {
            String pubkey = bundle.getString("pubkey");
            if (pubkey != null) {

                address_edit.setText(pubkey);
            }
        }


        edit_this = (EditText) findViewById(R.id.editText10);


        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Value = edittext.getText().toString();
                if (!Value.equals("")) {
                    if (Value.charAt(0) == '.') {
                        Value = "0" + Value;
                    }
                    int new_val = (int) (Float.parseFloat(Value) * Float.parseFloat(appData.getString("curr_price", "0.0")));
                    edit_this.setText(Integer.toString(new_val));
                } else {
                    edit_this.setText("");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                return;
            }
            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });


        myRef = fbManager.getRef();
        myRef.child(appData.getString("pk", "@Frank")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(UserEntry.class);
                total_btc_amt = Float.parseFloat(me.getTotalBTC());
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
        float total_USD = total_btc_amt * Float.parseFloat(appData.getString("curr_price", "0"));
        navAmt.setText(Float.toString(total_btc_amt) + " BTC | $" + total_USD);
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

    /* TODO Connect button to this function */
    public void pay(View view) {
        EditText addr = (EditText)findViewById(R.id.editText);
        final String address = addr.getText().toString();

        EditText amt = (EditText)findViewById(R.id.editText9);
        final String amtString = amt.getText().toString();
        if (amtString.equals("")) {
            Toast.makeText(getApplicationContext(), "Need to input BTC/USD amount", Toast.LENGTH_SHORT).show();
            return;
        }
        float amount = Float.parseFloat(amtString);

        EditText msg = (EditText)findViewById(R.id.editText11);
        final String message = msg.getText().toString();

        if (address.equals("")) {
            Toast.makeText(getApplicationContext(), "Need to input address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (total_btc_amt < amount) {
            Toast.makeText(getApplicationContext(), "Insufficient funds", Toast.LENGTH_SHORT).show();
            return;
        }
        /* Check if address exists */
        myRef.child(address).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntry user = dataSnapshot.getValue(UserEntry.class);
                if (user != null) {
                    fbManager.payUser(address, amtString, message);
                    onBackPressed();
                } else {
                    Toast.makeText(getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void request(View view) {
        EditText addr = (EditText)findViewById(R.id.editText);
        final String address = addr.getText().toString();

        EditText amt = (EditText)findViewById(R.id.editText9);
        final String amtString = amt.getText().toString();
        if (amtString.equals("")) {
            Toast.makeText(getApplicationContext(), "Need to input BTC/USD amount", Toast.LENGTH_SHORT).show();
            return;
        }
        Float amount = Float.parseFloat(amtString);

        EditText msg = (EditText)findViewById(R.id.editText11);
        final String message = msg.getText().toString();

        if (address.equals("")) {
            Toast.makeText(getApplicationContext(), "Need to input address", Toast.LENGTH_SHORT).show();
            return;
        }
        /* Check if address exists */
        myRef.child(address).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntry user = dataSnapshot.getValue(UserEntry.class);
                if (user != null) {
                    fbManager.addRequest(address, amtString, message);
                    onBackPressed();
                } else {
                    Toast.makeText(getApplicationContext(), "Address not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.pay, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(Pay.this, Home.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_contacts) {
            Intent intent = new Intent(this, Contacts.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Pay.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Pay.this, Profile.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
