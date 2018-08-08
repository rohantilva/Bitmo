package com.bitmo.bitmo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class RequestEntryAdapter extends ArrayAdapter<RequestEntry> {

    int resource;
    private SharedPreferences data;
    List<RequestEntry> items;
    private Context ctx;
    SharedPreferences appData;
    public FirebaseManager firebaseManager;

    public RequestEntryAdapter(Context ctx, int res, List<RequestEntry> items)
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
        final RequestEntry req = getItem(position);

        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, itemView, true);
        } else {
            itemView = (LinearLayout) convertView;
        }

        boolean reminded = req.getRemind();
        TextView nameView = itemView.findViewById(R.id.name);
        TextView unameView = itemView.findViewById(R.id.uname);
        TextView infoView = itemView.findViewById(R.id.info);
        final Button but1 = itemView.findViewById(R.id.button7);
        final Button but2 = itemView.findViewById(R.id.button8);

        final String uname = req.getUname();
        final String amt = req.getAmt();
        final String msg = req.getMsg();

        nameView.setText(uname);
        unameView.setText(msg);
        infoView.setText(amt + " BTC");

        if (reminded) {
            nameView.setTextColor(Color.parseColor("#FF0000"));
        }

        if (!req.getIsFromSomeoneElse()) {
            but2.setText("Remind");
            but2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseManager.remind(req);
                }
            });
            but1.setText("Cancel");
            but1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove our and their database entry
                    firebaseManager.cancelRequest(req.getPk(), req.getTimeStamp());

                }
            });
        } else if (req.getIsFromSomeoneElse()){
            but2.setText("Pay");
            but2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float amtFloat = Float.parseFloat(req.getAmt());
                    float totalBTC = appData.getFloat("Total_BTC", 0.0f);
                    if (amtFloat > totalBTC) {
                        Toast.makeText(ctx, "Insufficient funds", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseManager.payUser(req.getPk(), req.getAmt(), req.getMsg());
                        firebaseManager.fulfillRequest(req.getPk(), req.getTimeStamp());
                        float x = appData.getFloat("Total_BTC", 0.0f);
                        View temp = (View) parent.getParent().getParent();
                        TextView tv = temp.findViewById(R.id.textView2);
                        tv.setText(Float.toString(x));
                        TextView damt = temp.findViewById(R.id.textView3);
                        String curr_price = appData.getString("curr_price", "0.0");
                        float price = Float.parseFloat(curr_price);
                        damt.setText(Float.toString(price * x));
                    }

                }
            });
            but1.setText("Deny");
            but1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove our and their database entry
                    firebaseManager.cancelRequest(req.getPk(), req.getTimeStamp());

                }
            });
        }
        return itemView;
    }
}
