package c346.rp.edu.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    Button btnSendMsg;
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnSendMsg = findViewById(R.id.buttonSendMsg);

        checkPermission();

        br = new MessageReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etTo.getText().toString();
                String text = etContent.getText().toString();

                SmsManager smsManager = SmsManager.getDefault();

                if (address.contains(",")) {
                    String[] addresses = address.split(",");
                    String address1 = addresses[0];
                    String address2 = addresses[1];

                    smsManager.sendTextMessage(address1,null,text,null,null);
                    smsManager.sendTextMessage(address2,null,text,null,null);
                }
                else {
                    smsManager.sendTextMessage(address,null,text,null,null);
                }
                etContent.setText("");
                Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_LONG).show();
            }
        });
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = etTo.getText().toString();
                String text = etContent.getText().toString();

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);

                if (address.contains(",")) {
                    String[] addresses = address.split(",");
                    address = addresses[0];
                }

                sendIntent.setData(Uri.parse("sms:" + address));
                sendIntent.putExtra("sms_body", text);

                startActivity(sendIntent);
            }
        });
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED && permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
