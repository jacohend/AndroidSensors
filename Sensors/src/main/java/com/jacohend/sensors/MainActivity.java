package com.jacohend.sensors;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    SensorManager sense;
    List<Sensor> sensor;
    ListView sensors;
    List<String> list_sense;
    CustomListAdapter CustomAdapter;
    Boolean lock = false;
    LocationManager lm;
    LocListener loclist;
    Location lastLocation = null;
    Boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensors = (ListView) findViewById(R.id.cardListView);
        sensors.setVerticalScrollBarEnabled(false);
        sense = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sense.getSensorList(Sensor.TYPE_ALL);
        list_sense = new ArrayList<String>();
        for (Sensor s : sensor){
            list_sense.add("<b>" + s.getName() + "</b>");
        }
        list_sense.add("<b>GPS Telemetry Loading...</b>");  //add a space for GPS
        CustomAdapter = new CustomListAdapter(this, list_sense);
        sensors.setAdapter(CustomAdapter);
        for (Sensor s : sensor){
            sense.registerListener(sensorlistener, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        String bestProvider = lm.getBestProvider(criteria, false);
        loclist = new LocListener();
        lm.requestLocationUpdates(bestProvider, 5000, 0, loclist);
    }


    SensorEventListener sensorlistener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values.length == 0){
                return;
            }
            if ((sensorEvent.sensor != null) && !lock){
                lock = true;
                String str = "";
                for (int i = 0; i < sensorEvent.values.length; i++){
                    str += "\n<br>" + sensorEvent.values[i];
                }
                updateView(sensor.indexOf(sensorEvent.sensor), sensorEvent.sensor.getName(), str);
                lock = false;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    private void updateView(int index, String name, String data){
        try{
            list_sense.set(index, "<b>" + name + "</b>" + data);
            CustomAdapter.notifyDataSetChanged();
        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    class CustomListAdapter extends BaseAdapter {

        Context mContext;
        List<String> mList;

        public CustomListAdapter (Context context, List<String> list) {
            mList = list;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // This method is called to draw each row of the list
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // here you inflate the layout you want for the row
            final View view = View.inflate(mContext, R.layout.list_item, null);
            // you bind the layout with the content of your list
            // for each element of your list of notes, the adapter will create a row and affect the right title
            final TextView noteTitle= (TextView)view.findViewById(R.id.info);
            noteTitle.setText(Html.fromHtml(mList.get(position)));

            return view;
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        for (Sensor s : sensor){
            sense.unregisterListener(sensorlistener, s);
        }
        lm.removeUpdates(loclist);
        finish();
    }

    //update GPS data
    private class LocListener implements LocationListener {
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                String location = "<b>GPS Telemetry</b>\n<br>";
                location += "Latitude: " + String.valueOf(loc.getLatitude()) + "\n<br>";
                location += "Longitude: " + String.valueOf(loc.getLongitude()) + "\n<br>";
                location += "Speed: " + String.valueOf(loc.getSpeed()) + "\n<br>";
                location += "Bearing: " + String.valueOf(loc.getBearing());
                lastLocation = loc;
                list_sense.set(list_sense.size() - 1, location);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // TODO Auto-generated method stub
        }
    }


}


