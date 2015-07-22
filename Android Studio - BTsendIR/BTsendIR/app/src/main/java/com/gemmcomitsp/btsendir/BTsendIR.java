package com.gemmcomitsp.btsendir;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class BTsendIR extends ActionBarActivity {

    Button ProntoBtn, CloseBT, ProntoTest, HexBtn;
    EditText pBox;
    EditText HexBox;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_btsend_ir);

        //call the widgtes
        ProntoBtn = (Button)findViewById(R.id.btnSend);
        CloseBT = (Button)findViewById(R.id.btnDisc);
        pBox = (EditText)findViewById(R.id.pCode);
        ProntoTest = (Button)findViewById(R.id.ProntoTest);
        HexBtn = (Button)findViewById(R.id.btnHex);
        HexBox = (EditText)findViewById(R.id.hexCode);

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        ProntoBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendPronto();      //method to send IR
            }
        });

        HexBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendHex();      //method to send IR
            }
        });

        ProntoTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProntoTest();      //Send Sharp TV Pronto Power Code
            }
        });

        CloseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });

    }

        private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void sendPronto()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(pBox.getText().toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error Sending Code. Try again.");
            }
        }
    }

    private void sendHex()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(HexBox.getText().toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error Sending Code. Try again.");
            }
        }
    }

    private void ProntoTest()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("SEND 0000 0073 0000 000B 0040 0020 0020 0020 0020 0040 0040 0040 0020 0020 0040 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0CC8 /n".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error. Code Not sent");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_btsend_ir, menu);
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

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(BTsendIR.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Are you selecting the correct dongle?");
                finish();
            }
            else
            {
                msg("SendIR Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
