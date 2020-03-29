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

import android.util.Log;
import android.view.View;

import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.couchbase.lite.Document;
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
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import pl.droidsonroids.gif.GifImageView;

import static org.apache.commons.io.FileUtils.getFile;

//@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class SendActivity extends AppCompatActivity {
    Database database;
    private Handler handler = new Handler();
    DatabaseConfiguration config;
    Button  discover,  selectfile,save,receive,view,search,profile;
    ImageView imageView;
    Button send,listen;
    BluetoothAdapter mybluetoothAdapter;
    ArrayAdapter<String> arrayAdapter1;
    BluetoothDevice btArray[];
    EditText writemsg;
    ArrayList<String> stringArrayList = new ArrayList<>();
    ListView listView;
    TextView textView, msg_box;
    int requestcode;
    Intent btnenable;
    String name_file="";
    private static final int FILE_SELECT_CODE = 0;
    TextView message, status;
    SendReceive sendReceive;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private static final int PICKFILE_RESULT_CODE = 6;
    GifImageView anime;
    ProgressBar progressBar;

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


            setContentView(R.layout.send);


        discover = (Button) findViewById(R.id.discover);
        listen=(Button)findViewById(R.id.listen);
        send = (Button) findViewById(R.id.send);
       // anime=(GifImageView)findViewById(R.id.sendanime);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
       // progressBar.setProgress(60);





        status = (TextView) findViewById(R.id.status1);
        mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


        registerReceiver(myreceiver, intentFilter);
        // arrayAdapter1=new ArrayAdapter<String>(getApplicationContext(),and)
        //
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

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                sendReceive.start();


            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();

            }
        });



    }





public static final int Progress=9;





/*    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("LISTENING");

                    break;
                case STATE_CONNECTING:

                    status.setText("CONNECTING");
                  //  anime.setImageResource(R.drawable.connecting);
                    break;
                case STATE_CONNECTED:

                    status.setText("CONNECTED");
                  //  anime.setImageResource(R.drawable.file_share);
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("CONNECTION FAILED");
                    //anime.setImageResource(R.drawable.failed);
                    break;
                case STATE_MESSAGE_RECEIVED:
                    anime.setImageResource(R.drawable.dormant);
                    Toast.makeText(getApplicationContext(), "File succesfully sent", Toast.LENGTH_SHORT).show();
                    break;
                case  Progress:
                        Log.w("test", String.valueOf(msg.what));
                    progressBar.setProgress(msg.arg1/6000000);
                    break;

            }
            return true;
        }
    });


*/
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
                  //  handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                   status.setText("ERROR");


                }
                if (socket != null) {
                    //Message message = Message.obtain();
                    //message.what = STATE_CONNECTED;
                    //handler.sendMessage(message);
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



        public void run() {
            Log.w("run","in the thread run for formality");
            try {
                Query query = QueryBuilder.select(SelectResult.property("self"),
                        SelectResult.property("name"),
                        SelectResult.property("buisnessname"),
                        SelectResult.property("address"),
                        SelectResult.property("phone"),
                        SelectResult.property("email"),

                        SelectResult.expression(Meta.id))
                        .from(DataSource.database(database))
                        .where(Expression.property("self").equalTo(Expression.string("true")));
                ResultSet result = null;
                String id="";
                try {
                    result = query.execute();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                for (Result exp:result) {


                    id=exp.getString("id");

                }
                Document sender=database.getDocument(id);
                String number=sender.getString("phone");


                zipFileAtPath(getObbDir()+"/"+number,getObbDir()+"/compress.zip");
                String path=getObbDir()+"/compress.zip";
                Log.w("sender","zipping done");
                //path="/storage/2223-14F4/Download/testing.pdf";
                File file = new File(path);
                FileInputStream fis =new FileInputStream(file);
                BufferedInputStream bis=new BufferedInputStream(fis);
                byte[] contents;
                long fileLength = file.length();
                long current = 0;
                BufferedOutputStream bos =new BufferedOutputStream(outputStream);
                DataOutputStream dos=new DataOutputStream(bos);
                dos.writeUTF(sender.getString("name"));
                dos.writeUTF(sender.getString("buisnessname"));

                dos.writeUTF(number);
                dos.writeUTF(sender.getString("landline"));
                dos.writeUTF(sender.getString("email"));
                dos.writeUTF(sender.getString("address"));
                dos.writeUTF(sender.getString("longitude"));
                dos.writeUTF(sender.getString("latitude"));
             List<Object> location= sender.getArray("location").toList();

                dos.writeInt(location.size());
                for (Object loc:location) {
                    dos.writeUTF((String) loc);
                }
                List<Object> service= sender.getArray("service").toList();

                dos.writeInt(service.size());
                for (Object ser:service) {
                    dos.writeUTF((String) ser);
                }


                dos.writeLong(fileLength);
               dos.flush();
                while (current != fileLength) {
                    int size = 100000;
                    if (fileLength - current >= size)
                        current += size;
                    else {
                        size = (int) (fileLength - current);
                        current = fileLength;
                    }
                   // Message message = Message.obtain();
                    //message.what = Progress;
                    //message.arg1= (int) current;
                    //handler.sendMessage(message);
                   // progressBar.setProgress(60);
                    long finalCurrent = current;
                    handler.post(new Runnable() {
                        public void run() {
                            int progressStatus= (int) (finalCurrent*100 /fileLength);
                            Log.w("progress","test=="+progressStatus);
                            progressBar.setProgress(progressStatus);

                        }
                    });
                    contents = new byte[size];
                    //handler.removeMessages();
                    bis.read(contents, 0, size);
                     Log.w("sender ","sender is transferring"+current);
                    dos.write(contents);
                    dos.flush();

                }


                //dos.writeBytes("extraa timesadhghjagk");

                // dos.close();

                // Thread.sleep(2000);
                dos.flush();
                status.setText("COMPLETED");
                  Log.w("sender","sender  transfer is completed");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }






    @Override
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestcode)
        {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),"bluetooth is enabled",Toast.LENGTH_LONG).show();
            }else if(resultCode==RESULT_CANCELED){

                Toast.makeText(getApplicationContext(),"bluetoothenabling cancelled",Toast.LENGTH_LONG).show();

            }

        }


    }





    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                Log.w("debug","doing something");
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * /data/data/com.example.afinal/cache/test.zip
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }





}



