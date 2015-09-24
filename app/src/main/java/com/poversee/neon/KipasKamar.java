package com.poversee.neon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.nats.Connection;
import org.nats.MsgHandler;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;

public class KipasKamar extends AppCompatActivity {

    public KipasKamar kipasKamar = null;
    private Switch mySwitch;
    private SeekBar seekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        String message=intent.getStringExtra(DashboardActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_kipas_kamar);
        kipasKamar = this;

        // get message from pi
        getMsgFromPi();
        mySwitch = (Switch) findViewById(R.id.switch1);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Properties opts = new Properties();
                            opts.put("uri", "nats://nats.trihatmaja.web.id:4222");
                            try {
                                Connection conn = Connection.connect(opts);
                                conn.publish("trihatmaja.ctrl.req", "{\"point_number\":1,\"dimm_value\":\"1\"}");
                                conn.close();
                            } catch (IOException ioException) {
                                Toast.makeText(kipasKamar, "io exception", Toast.LENGTH_LONG).show();
                            } catch (InterruptedException interuptedException) {
                                Toast.makeText(kipasKamar, "interuptedException", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Properties opts = new Properties();
                            opts.put("uri", "nats://nats.trihatmaja.web.id:4222");
                            try {
                                Connection conn = Connection.connect(opts);
                                conn.publish("trihatmaja.ctrl.req", "{\"point_number\":1,\"dimm_value\":\"0\"}");
                                conn.close();
                            } catch (IOException ioException) {
                                Toast.makeText(kipasKamar, "io exception", Toast.LENGTH_LONG).show();
                            } catch (InterruptedException interuptedException) {
                                Toast.makeText(kipasKamar, "interuptedException", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
                }

            }
        });

        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.incrementProgressBy(1);
        seekbar.setProgress(0);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float hall = (float) progress/10;
                final float finalProgress = hall;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Properties opts = new Properties();
                        opts.put("uri", "nats://nats.trihatmaja.web.id:4222");
                        try {
                            Connection conn = Connection.connect(opts);
                            conn.publish("trihatmaja.ctrl.req", "{\"point_number\":3,\"dimm_value\":\""+ finalProgress +"\"}");
                            conn.close();
                        } catch (IOException ioException) {
                            Toast.makeText(kipasKamar, "io exception", Toast.LENGTH_LONG).show();
                        } catch (InterruptedException interuptedException) {
                            Toast.makeText(kipasKamar, "interuptedException", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_info, menu);
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
                    Toast.makeText( kipasKamar,"io exception",Toast.LENGTH_LONG).show();
                }
                catch (InterruptedException interuptedException){
                    Toast.makeText( kipasKamar, "interuptedException",Toast.LENGTH_LONG).show();
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
