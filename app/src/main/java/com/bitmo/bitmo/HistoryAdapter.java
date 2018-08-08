package com.bitmo.bitmo;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class HistoryAdapter extends ArrayAdapter<PayEntry> {

    int resource;
    private SharedPreferences data;
    List<PayEntry> items;
    private Context ctx;
    SharedPreferences appData;
    public FirebaseManager firebaseManager;

    public HistoryAdapter(Context ctx, int res, List<PayEntry> items)
    {
        super(ctx, res, items);
        resource = res;
        this.ctx = ctx;
        data = ctx.getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        this.items = items;
        appData = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);
        firebaseManager = firebaseManager.getInstance();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LinearLayout itemView;
        final PayEntry it = getItem(position);
        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, itemView, true);
        } else {
            itemView = (LinearLayout) convertView;
        }

        TextView nameView = itemView.findViewById(R.id.fullname);
        TextView msgView = itemView.findViewById(R.id.msg);
        TextView amtView = itemView.findViewById(R.id.trans);
        final String name = it.getUname();
        final String amt = it.getAmt();
        final String msg = it.getMsg();
        final boolean fromSomeone = it.getIsFromSomeoneElse();
        try {
            if (fromSomeone) {
                amtView.setText("+ " + amt);
            } else {
                amtView.setText("- " + amt);
            }
        } catch (NullPointerException e) {
            amtView.setText("X");
        }
        try {
            nameView.setText(name);
        } catch (NullPointerException e) {
            nameView.setText("No Name");
        }
        try {
            msgView.setText(msg);
        } catch(NullPointerException e) {
            msgView.setText("No Message");
        }
        return itemView;

    }
}