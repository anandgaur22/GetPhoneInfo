package com.anand.getphoneinfo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity {

    TextView textView,simData,phonData;
    @RequiresApi(api = Build.VERSION_CODES.M)

    TextView textView2;
    View view1,view2;
    Button getWifiInfo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.phoneNumber);
        simData = findViewById(R.id.simData);
        phonData = findViewById(R.id.phonData);

        textView2 = findViewById(R.id.text);

        getWifiInfo = findViewById(R.id.button);
        view1 = findViewById(R.id.view);
        view2 = findViewById(R.id.view1);


        getPhoneNumbers();
        SimsName();


        getWifiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                // Level of a Scan Result
                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult scanResult : wifiList) {
                    int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                    System.out.println("Level is  " + level + " out of 5");
                }

                // Level of current connection
                int rssi = wifiManager.getConnectionInfo().getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 5);
                double percentage = WifiManager.calculateSignalLevel(rssi, 50) * 2;

                int ip = wifiInfo.getIpAddress();
                int linkSpeed = wifiInfo.getLinkSpeed();

                int networkID = wifiInfo.getNetworkId();
                String ssid = wifiInfo.getSSID();
                boolean hssid = wifiInfo.getHiddenSSID();

                String macAddress = wifiInfo.getMacAddress();
                String IpAddress = Formatter.formatIpAddress(ip);

                String info ="Ip Address :"+IpAddress+
                        "\n"+"MAC Address :"+macAddress+
                        "\n"+"Network ID :"+networkID+
                        "\n"+"Link Speed :"+linkSpeed+
                        "\n"+"SSID :"+ssid+
                        "\n"+"Hidden SSID :"+hssid+
                        "\n"+"Wifi Strength :"+percentage+"%"+
                        "\n"+"Wifi Level :"+level+"out of 5";
                textView2.setText(info);
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getPhoneNumbers(){
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager tMgr = (TelephonyManager)
                    this.getSystemService(Context.TELEPHONY_SERVICE);
            String MyPhoneNumber = "";
            try
            {
                MyPhoneNumber =tMgr.getLine1Number();
                SimsName();
                textView.setText("Known Number:"+MyPhoneNumber);
            }
            catch(NullPointerException ex)
            {
            }

            if(MyPhoneNumber.equals("")){
                MyPhoneNumber = tMgr.getSubscriberId();
                textView.setText("Not Available"+MyPhoneNumber);
            }

        } else {
            requestPermission();
        }
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
            textView.setText("Request Permission:");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SimsName() {
        if (Build.VERSION.SDK_INT > 22) {
            //for dual sim mobile
            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(this);
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                return;
            }
            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                //if there are two sims in dual sim mobile
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    return;
                }
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
                SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(1);

                final String sim1 = simInfo.getDisplayName().toString();
                final String sim2 = simInfo1.getDisplayName().toString();
                simData.setText("Sim 1:"+sim1+""+"\nSim 2:"+sim2);

            } else {
                //if there is 1 sim in dual sim mobile
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
                final String sim1 = simInfo.getDisplayName().toString();
                TelephonyManager tManager = (TelephonyManager) getBaseContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String userPhone = tManager.getLine1Number();
                String IMIE = tManager.getImei();
                String SimCountry = tManager.getSimCountryIso();
                String NetworkOperator = tManager.getNetworkOperator();
                String NetworkOperatorName = tManager.getNetworkOperatorName();
                String SimSerialNumber = tManager.getSimSerialNumber();
                String SimOperator = tManager.getSimOperator();
                String SimState = String.valueOf(tManager.getSimState());
                String PhoneType = String.valueOf(tManager.getPhoneType());
                //Toast.makeText(this, userPhone, Toast.LENGTH_SHORT).show();
                textView.setText("My mobile no: "+userPhone);
                simData.setText("sim 1 company name : "+sim1);

                String info ="IMIE :"+IMIE+
                        "\n"+"Sim Country :"+SimCountry+
                        "\n"+"Network Operator :"+NetworkOperator+
                        "\n"+"Network Operator Name :"+NetworkOperatorName+
                        "\n"+"Sim Serial Number :"+SimSerialNumber+
                        "\n"+"Sim Operator :"+SimOperator+
                        "\n"+"Sim State :"+SimState +
                        "\n"+"PhoneType :"+PhoneType;
                phonData.setText(info);

            }
        }else{
            //below android version 22
            TelephonyManager tManager = (TelephonyManager) getBaseContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String sim1 = tManager.getNetworkOperatorName();
            Toast.makeText(this, "version 2"+sim1, Toast.LENGTH_SHORT).show();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager)  this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED  &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                                PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                textView.setText(mPhoneNumber);
                break;
        }
    }


}
