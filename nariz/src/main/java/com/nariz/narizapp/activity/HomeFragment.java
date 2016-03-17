package com.nariz.narizapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.nariz.narizapp.model.Global;
import com.nariz.narizapp.model.PequesStore;
import com.nariz.narizapp.nariz.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by ACsatillo on 02/09/2015.
 */
public class HomeFragment extends Fragment {

    private Button btnSync = null;
    private ToggleButton btnOnline = null;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        btnOnline = (ToggleButton)rootView.findViewById(R.id.btnOnline);
        btnOnline.setChecked(Global.online);
        btnOnline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Global.online = btnOnline.isChecked();
            }
        });
        btnSync = (Button)rootView.findViewById(R.id.btnSync);
        btnSync.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if( isConected() ) new SyncTables().execute();
                else Toast.makeText(getContext(), "No estas conectado a una red WIFI o 3G", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    /*public String getWifiName() {
        String ssid = "";
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if( WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED ) {
            ssid = wifiInfo.getSSID();
        }
        return ssid;
    }*/

    public boolean isConected(){
        //boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;

        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        //return connected;
        return (wifiConnected || mobileConnected);
    }

    //@Override
    //public void onAttach(Activity activity) { super.onAttach(activity); }

    @Override
    public void onDetach() { super.onDetach(); }

    class SyncTables extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), "Sincronizando", "Espere...", true);
        }

        protected String doInBackground(String... args) {
            /********************  MANDAR A SERVIDOR  **********************************/
            PequesStore ps = new PequesStore(getContext(), "Peque", null, 1);
            JSONObject JSONLocal = new JSONObject();
            JSONArray JSONpq = null;
            try {
                JSONpq = ps.getJSONPeques();
                JSONLocal.put("peque", JSONpq);
                //Log.i("JSONLocal", JSONLocal.toString());
            }catch (JSONException je){
                je.printStackTrace();
            }


            if( JSONpq.length() > 0 ) {
                String url = Global.url_serv + "wsproxy.php?ACC=SYNC";
                URL url_connect;
                HttpURLConnection connect;
                /////////////////////
                try {
                    url_connect = new URL(url);
                    //Log.e("Enviar", URLEncoder.encode(JSONLocal.toString(), "UTF-8"));
                    String param = "json=" + URLEncoder.encode(JSONLocal.toString(), "UTF-8");

                    connect = (HttpURLConnection) url_connect.openConnection();
                    connect.setDoOutput(true);
                    connect.setRequestMethod("POST");

                    /********************  MANDA JSON  **********************************/
                    connect.setFixedLengthStreamingMode(param.getBytes().length);
                    connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    PrintWriter out = new PrintWriter(connect.getOutputStream());
                    out.print(param);
                    out.close();

                    String response = "";
                    Scanner inStream = new Scanner(connect.getInputStream());

                    while (inStream.hasNextLine()) response += (inStream.nextLine());

                    Log.e("Response", response);
                    connect.disconnect();
                } catch (MalformedURLException mfu) {
                    Log.e("MalformedURLException", mfu.getMessage());
                } catch (UnsupportedEncodingException ue) {
                    Log.e("EncodingException", ue.getMessage());
                } catch (IOException ioe) {
                    Log.e("IOException", ioe.getMessage());
                }
            }else {
                Log.e("Json", "No hay nada en local");
            }
            /********************  ENVIAR IMAGENES  **********************************/
            File lFiles[];
            File t = new File(getContext().getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/");
            lFiles = t.listFiles();
            String response = "";

            for(int i=0; i<lFiles.length; i++) {
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 3; //3MB
                int serverResponseCode = 0;

                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(lFiles[i]);
                    URL url = new URL(Global.url_serv + "wsproxy.php?ACC=IMGUP&dir=peques" );

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", lFiles[i].getName());

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + lFiles[i].getName() + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : [" + serverResponseMessage + "]: " + serverResponseCode );

                    if(serverResponseCode == 200){
                        Log.e("Exito Again!!", "se termino de subir imagen");
                    }

                    Scanner inStream = new Scanner(conn.getInputStream());
                    response = "";

                    //process the stream and store it in StringBuilder
                    while(inStream.hasNextLine()) response += (inStream.nextLine());

                    conn.disconnect();
                    Log.e("JSONResponse", response);

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", "Exception : " + e.getMessage(), e);
                }
                Log.e("Archivos", lFiles[i].getAbsolutePath());
            }


            /*********************  *****************  **********************************/
            /*********************  BAJAR DE SERVIDOR  **********************************/
            /*********************  *****************  **********************************/

            response = "";
            URL url_newPewque;
            try {
                url_newPewque = new URL(Global.url_serv + "wsproxy.php?ACC=ALLP" );

                HttpURLConnection urlConn = (HttpURLConnection)url_newPewque.openConnection();
                urlConn.setDoOutput(true);
                urlConn.setRequestMethod("GET");

                /********************  RECIBE JSON  **********************************/
                Scanner inStream = new Scanner(urlConn.getInputStream());
                while( inStream.hasNextLine() ) response += (inStream.nextLine());

                urlConn.disconnect();
                Log.e("Congenia", response);
            }catch( MalformedURLException mue){
                Log.e("URLError", mue.getCause().toString() );
            }catch(IOException ioe){
                //Log.e("IOError", ioe.getCause().toString() );
                ioe.printStackTrace();
            }

            try{
                JSONObject json = new JSONObject(response);

                if( json.getInt("success") == 1 ) {
                    //Log.e("Response Congenia", response);
                    ps.insertaJSON(json.getJSONArray("peque")); //Insertar JSONArray "peques"

                    JSONArray jArray = new JSONArray(json.getJSONArray("peque").toString());
                    String img = "";
                    String url_dir = Global.url_serv + "upload/peques/";
                    String path = getContext().getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/";
                    URL url_img = null;
                    /********************  RECIBE IMAGENES  **********************************/
                    for (int i = 0; i < jArray.length(); i++) {
                        img = jArray.getJSONObject(i).getString("imagen");
                        Bitmap bmImg = null;
                        InputStream is = null;
                        HttpURLConnection conn = null;
                        File fLocal = new File(path + img);
                        if (img.length() > 0 && !fLocal.exists()) {
                            try {
                                url_img = new URL(url_dir + img);
                            } catch (MalformedURLException mfue) {
                                mfue.printStackTrace();
                            }

                            try {
                                if (url_img != null) {
                                    conn = (HttpURLConnection) url_img.openConnection();

                                    conn.setDoInput(true);
                                    conn.connect();
                                    is = conn.getInputStream();
                                    bmImg = BitmapFactory.decodeStream(is);
                                    conn.disconnect();
                                    is.close();
                                }
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }

                            try {
                                if (conn != null && conn.getResponseMessage().equals("OK")) {
                                    conn.disconnect();

                                    FileOutputStream out = new FileOutputStream(fLocal);
                                    if (bmImg != null) {
                                        bmImg.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                        out.flush();
                                        out.close();
                                        bmImg.recycle();
                                        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), fLocal.getAbsolutePath(), fLocal.getName(), fLocal.getName());
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            /************************************/

            //Log.e("Terminado", "Termino el sync!!!!!!!!!");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }
}
