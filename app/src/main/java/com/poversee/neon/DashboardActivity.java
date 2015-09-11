package com.poversee.neon;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.nats.Connection;
import org.nats.MsgHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Properties;
import java.io.IOException;

public class DashboardActivity extends AppCompatActivity {
    ListView listView;
    CustomDeviceAdapter adapter;
    public DashboardActivity dashboardActivity = null;
    public ArrayList<ListDevice> data = new ArrayList<ListDevice>();
    public final static String EXTRA_MESSAGE = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // receive value from pi
        getMsgFromPi();

        dashboardActivity = this;
        generateListContent();

        Resources res= getResources();
        listView = (ListView) findViewById(R.id.deviceListView);

        //Create custom adapter
        adapter = new CustomDeviceAdapter(dashboardActivity, data, res);
        listView.setAdapter(adapter);
    }

    private void generateListContent(){
        //for (int i = 0; i < 11; i++) {

        final ListDevice listDevice1 = new ListDevice();
        listDevice1.setDeviceName("Lampu Kamar");
        listDevice1.setDeviceDescription("Lampu Kamar");
        listDevice1.setImageUrl("right_round_26");
        data.add(listDevice1);

        final ListDevice listDevice2 = new ListDevice();
        listDevice2.setDeviceName("Lampu Depan");
        listDevice2.setDeviceDescription("Lampu Depan");
        listDevice2.setImageUrl("right_round_26");
        data.add(listDevice2);

        final ListDevice listDevice3 = new ListDevice();
        listDevice3.setDeviceName("Kipas Kamar");
        listDevice3.setDeviceDescription("Kipas Kamar");
        listDevice3.setImageUrl("right_round_26");
        data.add(listDevice3);

        final ListDevice listDevice4 = new ListDevice();
        listDevice4.setDeviceName("AC Kamar");
        listDevice4.setDeviceDescription("AC Kamar");
        listDevice4.setImageUrl("right_round_26");
        data.add(listDevice4);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        Intent intent=new Intent(this,DetailInfoActivity.class);
        intent.putExtra(EXTRA_MESSAGE, String.valueOf(mPosition));
        startActivity(intent);


        //ListDevice tempValues = ( ListDevice ) data.get(mPosition);

        // SHOW ALERT
        //Toast.makeText( dashboardActivity, ""+tempValues.getDeviceName() + "Image:"+tempValues.getDeviceDescription()+"Url:"+tempValues.getImageUrl(),Toast.LENGTH_LONG).show();
        //sendMessageToServer("testing");
    }

    // Thread for sending nats message
    public void getMsgFromPi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties opts = new Properties();
                opts.put("uri", "nats://nats.trihatmaja.web.id:4222");
                try{
                    Connection conn = Connection.connect(opts);
                    conn.subscribe("trihatmaja.current", new MsgHandler() {
                        @Override
                        public void execute(String msg) {
                            final float amp = Float.parseFloat(msg);
                            final int voltage = 220;
                            final double kwh = amp * voltage * 60 * 0.001;
                            final double price = (0.016 * kwh);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SetElectricParameter(voltage, amp, kwh, price);
                                }
                            });
                        }
                    });
                }
                catch (IOException ioException){
                    Toast.makeText( dashboardActivity,"io exception",Toast.LENGTH_LONG).show();
                }
                catch (InterruptedException interuptedException){
                    Toast.makeText( dashboardActivity, "interuptedException",Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    private void SetElectricParameter(int voltage, float current, double kwh, double price){
        DecimalFormat newFormat = new DecimalFormat("#.##");
        double prices =  Double.valueOf(newFormat.format(price));
        double kwhs = Double.valueOf(newFormat.format(kwh));
        final TextView voltageText= (TextView) findViewById(R.id.voltageText);
        voltageText.setText(String.valueOf(voltage)+" V");
        final TextView currentText= (TextView) findViewById(R.id.currentText);
        currentText.setText(String.format("%.2f", current)+" A");
        final TextView kwhText= (TextView) findViewById(R.id.kwhText);
        kwhText.setText(String.valueOf(kwhs));
        final TextView priceText= (TextView) findViewById(R.id.priceText);
        priceText.setText(String.valueOf(prices)+" Rp");
    }
}
