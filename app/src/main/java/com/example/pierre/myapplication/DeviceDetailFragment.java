package com.example.pierre.myapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by pierre on 28/03/16.
 */
public class DeviceDetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener{
    protected static  final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);

            }
        });
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new ClientAsyncTask(getActivity()).execute();



    /*                        Socket socket = null;
                            OutputStreamWriter osw;
                            String str = "Hello World";
                            try {
                                socket = new Socket("192.168.49.1", 8988);
                                osw =new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                                osw.write(str, 0, str.length());
                            } catch (IOException e) {
                                System.err.print(e);
                            } finally {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }*/


/*                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);*/
                    }
                });
        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(MainActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP" + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            Log.w("##############","#########");

            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    .execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
        Log.w("SHOW DETAILS", "DETAIL FRAGMENT");
    }


    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }


    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                ServerSocket s = new ServerSocket(8988);
                Socket soc = s.accept();

                // Un BufferedReader permet de lire par ligne.
                BufferedReader plec = new BufferedReader(
                        new InputStreamReader(soc.getInputStream())
                );

                // Un PrintWriter possède toutes les opérations print classiques.
                // En mode auto-flush, le tampon est vidé (flush) à l'appel de println.
                PrintWriter pred = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(soc.getOutputStream())),
                        true);

                while (true) {
                    String str = plec.readLine();          // lecture du message
                    if (str.equals("END")) break;
                    System.out.println("ECHO = " + str);   // trace locale
                    pred.println(str);                     // renvoi d'un écho
                }
                plec.close();
                pred.close();
                soc.close();
              /*  ServerSocket serverSocket = new ServerSocket(8988);
                Log.w(MainActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.w(MainActivity.TAG, "Server: connection done");
                Socket s = new Socket(InetAddress.getByName("stackoverflow.com"), 80);
                PrintWriter pw = new PrintWriter(s.getOutputStream());
                pw.print("GET / HTTP/1.1");
                pw.print("Host: stackoverflow.com");
                pw.print("");
                pw.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String t;
                while((t = br.readLine()) != null) System.out.println(t);
                br.close();*/
        /*        OutputStream mmOutStream = client.getOutputStream();
                String str = "FEKIR";
                mmOutStream.write(str.getBytes());*/


                /*BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                Log.w("--------------","############");
                line = " " + reader.read();
                Log.w("+++++", line);
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                    Log.w("------", line);
                }*/
                //Log.w("RESULT! ! ! ! ", sb.toString());


/*                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(MainActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
                return null;
            }*/

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "OL";
        }
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null && false) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
    }


    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class ClientAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        PrintWriter out;
        BufferedReader in;
        /**
         * @param context
         */
        public ClientAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket("192.168.49.1", 8988);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("SOCKET = " + socket);

            BufferedReader plec = null;
            try {
                plec = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            PrintWriter pred = null;
            try {
                pred = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pred.println("Lacazette Fekir Cornet Tolisso Umtiti Darder Ferri Kalulu");
            String str = "bonjour";
           /* for (int i = 0; i < 10; i++) {
                pred.println(str);          // envoi d'un message
                try {
                    str = plec.readLine();      // lecture de l'écho
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            System.out.println("END");     // message de terminaison
            pred.println("END") ;
            try {
                plec.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pred.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

           /* Socket socket = new Socket();
            String str = "Lacazette\n";
            try {
                socket.connect((new InetSocketAddress("192.168.49.1", 8988)), 5000);
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String message = "";
                int charsRead = 0;
                char[] buffer = new char[2048];

                while ((charsRead = in.read(buffer)) != -1) {
                    message += new String(buffer).substring(0, charsRead);
                }
                Log.w("{{{{{{{{'  ", message);
            } catch (IOException e) {
                return "Error receiving response:  " + e.getMessage();
            }*/
            return "OL";
        }
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
        }
    }



    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}
