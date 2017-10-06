package com.rype3.pocket_hrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rype3.pocket_hrm.realm.LocationDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DataObjectHolder> implements  ConnectivityReceiver.ConnectivityReceiverListener{
    private static String LOG_TAG = "Job List Adapter";
    private static MyClickListener ClickListener;
    private static List<LocationDetails> LocatioDetailList;
    private static Context context;
    LayoutInflater inflater;
    private static Utils utils;
    private static Activity activity;
    private static Constants constants;

    public HistoryAdapter(Activity activity ,Context context, List<LocationDetails> jdList){
        this.LocatioDetailList = new ArrayList<LocationDetails>();
        this.context = context;
        this.activity = activity;
        this.LocatioDetailList = jdList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView location ,time,status;
        Button btn_send;

        public DataObjectHolder(View itemView) {
            super(itemView);

            utils = new Utils(context);
            constants = new Constants(context);

            location = (TextView) itemView.findViewById(R.id.tv_location);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            status = (TextView) itemView.findViewById(R.id.tv_status);
            btn_send = (Button) itemView.findViewById(R.id.btn_send);

            Log.e(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.ClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.atendancerow, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryAdapter.DataObjectHolder holder, final int position) {

     //   holder.location.setText(LocatioDetailList.get(position).getMeta());
     //   holder.time.setText(LocatioDetailList.get(position).getMeta());

        holder.status.setText(LocatioDetailList.get(position).getCheckState().toUpperCase());

        if (LocatioDetailList.get(position).isState()){
            holder.btn_send.setText("SEND NOW");
        }else{
            holder.btn_send.setText("SEND AGAIN");
        }

        PassedDetails(LocatioDetailList.get(position).getMeta(),  holder.location, holder.time);

        holder.btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkConnection()) {
                    utils.setSharedPreference(context, String.valueOf(LocatioDetailList.get(position).getId()),Constants.TEMP_ID);
                    switch (LocatioDetailList.get(position).getCheckState()) {
                        case "in":
                            new ProcressAsyncTask(
                                    activity, //activity
                                    constants.urls(3),// url
                                    null,//email
                                    null,//pin
                                    null,//epf
                                    "POST",//HTTP_TYPE
                                    5,//type activity method
                                    "1.0",//version
                                    null,//token
                                    null,//deviceId
                                    utils.getSharedPreference(context, Constants.USER_ID),//uid
                                    PassedDetails(LocatioDetailList.get(position).getMeta(),  holder.location, holder.time),// checked at time
                                    null, // Check string ont use
                                    LocatioDetailList.get(position).getMeta()).execute();
                            break;

                        case "out":

                            new ProcressAsyncTask(
                                    activity, //activity
                                    constants.urls(4),// url
                                    null,//email
                                    null,//pin
                                    null,//epf
                                    "POST",//HTTP_TYPE
                                    5,//type activity method
                                    "1.0",//version
                                    null,//token
                                    null,//deviceId
                                    utils.getSharedPreference(context, Constants.USER_ID),//uid
                                    null, // Check string ont use
                                    PassedDetails(LocatioDetailList.get(position).getMeta(),  holder.location, holder.time),// checked at time
                                    LocatioDetailList.get(position).getMeta()).execute();

                            break;

                    }
                }
            }
        });
    }

    public void addItem(LocationDetails locationDetails, int index) {
        LocatioDetailList.add(index, locationDetails);
        notifyItemInserted(index);
    }


    public void deleteItem(int index) {
        LocatioDetailList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return LocatioDetailList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    private String PassedDetails(String meta, TextView location,TextView time) {

        //       Log.e("URL : " , "http://wmmmendis.rype3.net/io/api/v1/device/track");
//              new ProcressAsyncTask(
//                      "http://wmmmendis.rype3.net/io/api/v1/device/track",
//                      locationList.get(index).getLat(),
//                      locationList.get(index).getLon(),
//                      locationList.get(index).getDeviceId(),
//                      locationList.get(index).getTimeStamp(),
//                      locationList.get(index).getCheckStatus(),
//                      locationList.get(index).getLocation(),
//                      locationList.get(index).isInternetState()).
//                      execute();
        String url = "";
        String d_iso = "";

        try {
            JSONObject jsonObject = new JSONObject(meta);

            String did = jsonObject.getString("did");
            String d_location = jsonObject.getString("d_location");
            d_iso = jsonObject.getString("d_iso");
            String d_name = jsonObject.getString("d_name");
            String geo_location = jsonObject.getString("location");

            JSONObject locatio_json = new JSONObject(geo_location);

            String latitude = locatio_json.getString("lat");
            String longtitude = locatio_json.getString("long");

            String in = null;
            String out = null;

            location.setText(d_location);
            time.setText(d_iso);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return d_iso;
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        } else {
           Toast.makeText(context,"You don't have internet connection",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
