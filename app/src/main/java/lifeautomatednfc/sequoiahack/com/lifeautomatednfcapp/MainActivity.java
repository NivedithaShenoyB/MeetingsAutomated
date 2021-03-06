package lifeautomatednfc.sequoiahack.com.lifeautomatednfcapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private AudioManager myAudioManager;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    Button _createFileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView = (TextView) findViewById(R.id.text_id);
        setContentView(R.layout.activity_main);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled. Please enable NFC", Toast.LENGTH_LONG).show();
        } else {
            handleIntent(getIntent());
            /*myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            connectToWifi();*/


        }

        _createFileBtn = (Button) findViewById(R.id.btn_createfile);

        _createFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateFileActivity.class);
                startActivity(intent);

            }
        });

    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            connectToWifi();

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
                sendEmail();
                //sendSMSMessage("hey");


            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            sendSMSMessage("hey");
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }



    }

    public void connectToWifi() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        // wc.SSID = "\"YUREKA\"";
        //wc.preSharedKey = "\"shenoy1234@\"";
        wc.SSID = "\"Photon Max Wi-Fi_0031\"";
        wc.preSharedKey = "\"12345678\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void sendSMSMessage(String message) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", new String("9448311034"));
        smsIntent.putExtra("sms_body", "Test ");

        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onResume() {
        super.onResume();

    }


    public void sendEmail() {
        Intent emailIntent = new Intent();
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");// Package Name, Class Name
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nivedithabshenoy@gmail.com"});  /// TODO: Receivers, will take this from csv later.
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Meeting starting now");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please join at meeting room 25. Check the Group Google Docs for the Minutes of the meeting!");
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }


}
