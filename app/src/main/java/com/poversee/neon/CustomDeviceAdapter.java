package com.poversee.neon;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by effendi on 9/10/2015.
 * http://androidexample.com/How_To_Create_A_Custom_Listview_-_Android_Example/index.php?view=article_discription&aid=67&aaid=92
 */
public class CustomDeviceAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList arrayList;
    private static LayoutInflater inflater=null;
    public Resources resources;
    ListDevice tempValues=null;

    public CustomDeviceAdapter(Activity act, ArrayList arrList, Resources resLocal){
        activity = act;
        arrayList = arrList;
        resources= resLocal;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        if(arrayList.size()<=0)
            return 1;
        return arrayList.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    /*== Create a holder class to contain inflated xml file elements ==*/
    public static class ViewHolder{
        public TextView deviceName;
        public TextView deviceDescription;
        public ImageButton image;
    }

    /* Depends upon data size called for each row, Create each ListView row */

    public View getView(int position, View convertView, ViewGroup parent){
        View vi= convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.device_item, null);

            holder = new ViewHolder();
            holder.deviceDescription = (TextView) vi.findViewById(R.id.list_item_desc);
            holder .deviceName = (TextView) vi.findViewById(R.id.list_item_name);
            holder.image = (ImageButton) vi.findViewById(R.id.list_item_btn);

            // Set holder with the LayoutInflater
            vi.setTag(holder);
        }
        else {
            holder=(ViewHolder) vi.getTag();
        }

        if(arrayList.size()<=0){
            holder.deviceName.setText("No Data");
        }
        else{
            // Get each model object from arrayList
            tempValues=null;
            tempValues=(ListDevice) arrayList.get(position);

            // Set Model values in holder elements
            holder.deviceName.setText(tempValues.getDeviceName());
            holder.deviceDescription.setText(tempValues.getDeviceDescription());
            holder.image.setImageResource(
                    resources.getIdentifier("com.poversee.neon:drawable/" + tempValues.getImageUrl(), null, null));

            // Set item click listen for layoutInflater for each row

            vi.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            DashboardActivity sct = (DashboardActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
