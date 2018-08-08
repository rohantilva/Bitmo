package com.bitmo.bitmo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alexowen on 5/3/18.
 */

public class ContactEntryAdapter extends ArrayAdapter<ContactEntry> {

    int resource;
    //private SharedPreferences data;
    List<ContactEntry> items;
    private Context ctx;

    public String packagePrefix = "com.bitmo.bitmo.ContactBundlePrefix";

    public ContactEntryAdapter(Context ctx, int res, List<ContactEntry> items) {
        super(ctx, res, items);
        resource = res;
        this.ctx = ctx;
        //data = ctx.getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        this.items = items;
        //appData = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final LinearLayout itemView;
        final ContactEntry contactEntry = getItem(position);
        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, itemView, true);
        } else {
            itemView = (LinearLayout) convertView;
        }
        TextView fullNameView = (TextView) itemView.findViewById(R.id.contact_display);
        if (contactEntry.getLName() == null) {
            //we'll just display the first name
            if (contactEntry.getFName() != null) {
                fullNameView.setText(contactEntry.getFName());
            }
            fullNameView.setText(contactEntry.getPK());
        } else {
            if (contactEntry.getFName() != null) {
                fullNameView.setText(contactEntry.getFName() + " " + contactEntry.getLName());
            } else {
                fullNameView.setText(contactEntry.getLName());
            }
        }
        itemView.setClickable(true);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pubkey = contactEntry.getPK();
                Intent intent = new Intent(ctx, Pay.class);
                //Bundle bundle = intent.getExtras();
                intent.putExtra("pubkey", pubkey);
                ctx.startActivity(intent);
            }
        });

        return itemView;
    }
}
