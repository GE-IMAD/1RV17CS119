// completed by Siddharth harish raikar on 3/30/2020 2:07 AM (Corona)
package com.example.afinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;

import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.model.Color;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.Where;

import org.apache.commons.io.FileUtils;


import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import pl.droidsonroids.gif.GifImageView;

import static org.apache.commons.io.FileUtils.getFile;


public class ReceiveActivity extends AppCompatActivity {
    Database database;
    DatabaseConfiguration config;
    Button  receive,view;
    BluetoothAdapter mybluetoothAdapter;
    ArrayAdapter<String> arrayAdapter1;
    ArrayList<String> strings;
    BluetoothDevice btArray[];
    ArrayList<String> stringArrayList = new ArrayList<>();
    int requestcode;
    Intent btnenable;
    private Handler handler = new Handler();
    String name_file="";
    private static final int FILE_SELECT_CODE = 0;
    TextView  status;
    SendReceive sendReceive;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private static final int PICKFILE_RESULT_CODE = 6;
    BubblePicker bubblePicker;
    ArrayList<PickerItem> listitems,refresh;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    EditText taskEditText;


    private String m_Text = "";

    TextView textFolder;
    EditText dataPath;
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;
    File root, fileroot, curFolder;
    private List<String> fileList = new ArrayList<String>();
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    Button buttonopenDailog, buttonUp;
    GifImageView anime;
    ProgressBar progressBar;
    Handler hadler;




    private static final String APP_NAME = "final";
    private static final UUID MY_UUID = UUID.fromString("63eb8101-6bd2-4f36-9fa2-de0630e15117");
    BroadcastReceiver myreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                arrayAdapter1.notifyDataSetChanged();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.receive_activity);
        bubblePicker = (BubblePicker) findViewById(R.id.pickerreceive);
        progressBar=(ProgressBar)findViewById(R.id.progressreceive);
        receive=(Button)findViewById(R.id.receive1);
        listitems=  new ArrayList<>();
        refresh=  new ArrayList<>();
        handler=new Handler();Log.w("bubble","check");
        status = (TextView) findViewById(R.id.statusreceive);
        mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myreceiver, intentFilter);
        Log.w("test","inside");
        Set<BluetoothDevice> bt = mybluetoothAdapter.getBondedDevices();
        strings=new ArrayList<>();
        int[] colors = new int[bt.size()];
        int index = 0;
        btArray = new BluetoothDevice[bt.size()];
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                strings.add(device.getName());
               // Log.w("realme",device.getName());
                colors[index]=android.graphics.Color.rgb(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256));
                index++;
            }


            for (int i=0;i<bt.size();i++){
              String var=strings.get(i)+"";
                PickerItem item = new PickerItem(var,colors[i], android.graphics.Color.WHITE,getDrawable(R.drawable.egg));
                refresh.add(item);
            }
            bubblePicker.setBubbleSize(10);
            bubblePicker.setItems(refresh);


        }



        CouchbaseLite.init(getApplicationContext());
        config = new DatabaseConfiguration();
        database = null;

        try {
            database = new Database("error-app", config);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        btnenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestcode = 1;

        File received=new File(getObbDir()+"/received");
        received.mkdir();



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



        eventlistners();

        if (!mybluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }


    }

    private void eventlistners() {




        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReceive.start();
            }
        });


        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {


                int index=strings.indexOf(pickerItem.getTitle());
                Log.w("debug","index="+index);
                ClientClass clientClass = new ClientClass(btArray[index]);
                clientClass.start();




            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                Toast.makeText(ReceiveActivity.this,pickerItem.getTitle()+"  Deselected", Toast.LENGTH_SHORT).show();
            }
        });


    }




    private class ServerClass extends Thread {

        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = mybluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    status.setText("CONNECTING");

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.w("test","connection failed");
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    status.setText("ERROR");



                }
                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    status.setText("CONNECTED");



                    sendReceive = new SendReceive(socket);


                    break;
                }
            }
        }
    }


    private class SendReceive extends Thread {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run(){


            Log.w("run","in the thread run for formality");

            try {

                long numberOfBytes=0,index=0;;

                boolean flag=true;
                FileOutputStream fos = fos = new FileOutputStream(getObbDir().getAbsolutePath()+"/received/test.zip");
                Log.w("myApp",  getCacheDir().getAbsolutePath()+"/received/test.zip");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                BufferedInputStream bis=new BufferedInputStream(inputStream);

                DataInputStream dis=new DataInputStream(bis);
                MutableDocument mutableDoc = new MutableDocument();
                mutableDoc.setString("name", dis.readUTF());
                mutableDoc.setString("buisnessname", dis.readUTF());
                String number=dis.readUTF();
                mutableDoc.setString("phone", number);
                mutableDoc.setString("self","false");
                mutableDoc.setString("landline",dis.readUTF());
                mutableDoc.setString("email",dis.readUTF());
                mutableDoc.setString("address",dis.readUTF());
                mutableDoc.setString("longitude",dis.readUTF());
                mutableDoc.setString("latitude",dis.readUTF());


                int len=dis.readInt();
                MutableArray array=new MutableArray();
                List<Object> location=new ArrayList<>();
                for(int i=0;i<len;i++){

                    location.add(dis.readUTF());

                }
                array.setData(location);
                mutableDoc.setArray("location",array);
                MutableArray array2=new MutableArray();
                List<Object> service=new ArrayList<>();
                len=dis.readInt();
                for(int i=0;i<len;i++){

                    service.add(dis.readUTF());

                }
                array2.setData(service);
                mutableDoc.setArray("service",array2);

                 database.save(mutableDoc);
                numberOfBytes= dis.readLong();

                flag=true;
                Log.w("myApp", "filesize"+numberOfBytes);

                while(flag)
                {
                    byte[] data=new byte[dis.available()];
                    int numbers=dis.read(data);
                    bos.write(data, 0, numbers);
                    index=index+numbers;
                    long finalCurrent = index;
                    long finalNumberOfBytes = numberOfBytes;
                    handler.post(new Runnable() {
                        public void run() {
                            int progressStatus= (int) (finalCurrent*100 / finalNumberOfBytes);
                            progressBar.setProgress(progressStatus);

                        }
                    });
                    Log.w("receiver", "tranfering"+index+"/"+numberOfBytes);
                    if(index==numberOfBytes) {
                        Log.w("receiver","transfor completed");
                        flag =false;
                        boolean test=unpackZip(getObbDir().getAbsolutePath()+"/received/","test.zip",number);
                        Log.w("receiver","zipping status"+test);
                        status.setText("COMPLETED");

                    }


                }


            }catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private class ClientClass extends Thread {

        private BluetoothSocket socket;
        private BluetoothDevice device;


        public ClientClass(BluetoothDevice device1) {

            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void run() {
            try {
                socket.connect();
                status.setText("CONNECTED");
                sendReceive = new SendReceive(socket);

            } catch (IOException e) {
                e.printStackTrace();
                Log.w("test","connection failed");
                status.setText("ERROR");

            }
        }

    }




    @Override
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestcode) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "bluetooth is enabled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(getApplicationContext(), "bluetoothenabling cancelled", Toast.LENGTH_LONG).show();

            }

        }


    }


    public boolean unpackZip(String path, String zipname,String dirpath)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;

            String dirname="";
            int i=0 ;
            while((ze = zis.getNextEntry()) != null)
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;

                String filename = ze.getName();
                Log.w("unzip",filename);

                File file=new File(path +filename);

                File dir=new File(path+dirpath+"/");
                dir.mkdir();

                FileOutputStream fout = new FileOutputStream(file);

                while((count = zis.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, count);
                    byte[] bytes = baos.toByteArray();
                    fout.write(bytes);
                    baos.reset();
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}




