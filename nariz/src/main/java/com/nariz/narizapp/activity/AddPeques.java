package com.nariz.narizapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.nariz.narizapp.adapter.CardAdapter;
import com.nariz.narizapp.model.Global;
import com.nariz.narizapp.model.Peque;
import com.nariz.narizapp.model.PequesStore;
import com.nariz.narizapp.nariz.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by ACastillo on 02/09/2015.
 */
public class AddPeques extends AppCompatActivity {

    private EditText nom = null;
    private EditText mat = null;
    private EditText pat = null;
    private EditText tel = null;
    private EditText date = null;
    private EditText coment = null;
    private PequesStore dbPeque = null;
    private ImageButton camara = null;
    private ImageView img = null;

    private Bundle extras = null;
    private Peque editP = null;

    private Uri output = null;
    private Date d = new Date();
    private Timestamp ts = new Timestamp( d.getTime() );
    private String imgaux = "";
    private String simg = "";
    private File dir = null;

    private static final String TAG_SUCCESS = "success";
    private String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_peque);
        dbPeque = new PequesStore(getApplicationContext(), "Peque", null, 1);
        extras = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        nom = (EditText)findViewById(R.id.editTextNom);
        mat = (EditText)findViewById(R.id.editTextMat);
        pat = (EditText)findViewById(R.id.editTextPat);
        tel = (EditText)findViewById(R.id.editTextCel);
        date = (EditText)findViewById(R.id.editTextCumple);
        coment = (EditText)findViewById(R.id.editTextComent);
        img = (ImageView)findViewById(R.id.imageViewPequeProfile);

        if( extras != null && !extras.getString("id").equals("") ){
            setTitle(R.string.title_upd_peque);
            id = extras.getString("id");
            editP = dbPeque.getPeque( extras.getString("id") );
            nom.setText( editP.getNombre() );
            pat.setText( editP.getPaterno() );
            mat.setText( editP.getMaterno() );
            tel.setText( editP.getTelefono() );
            date.setText( editP.getCumplea() );
            coment.setText( editP.getComentario() );
            img.setImageURI(
                    Uri.fromFile(
                            new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/" + editP.getImagen() )
                    )
            );
            imgaux = editP.getImagen();
        } else setTitle(R.string.title_new_peque);

        camara = (ImageButton)findViewById(R.id.imageButtonCamPeque);
        camara.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                simg = ts.toString().replace(" ", "")+".jpg";
                dir = new File( getExternalFilesDir(null).getAbsolutePath() +"/FotoPeques/" + simg);
                Log.e("IMG", dir.getAbsolutePath());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                output = Uri.fromFile(dir);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(intent, 1);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver cr = this.getContentResolver();
        Bitmap bit = null;
        try {
            bit = android.provider.MediaStore.Images.Media.getBitmap(cr, output);
            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface( dir.getAbsolutePath() );
                int orientation = exif.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bit = Bitmap.createBitmap(bit , 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        img.setImageBitmap(bit);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String nomImg = "";

        switch( id ){
            case R.id.action_save:
                if( nom.getText().toString().length()>2 && pat.getText().toString().length()>2 ) {
                    if( simg.length()>0 && dir.exists() ){
                        nomImg = simg;
                    } else nomImg = imgaux;
                    Peque peque = new Peque(nom.getText().toString(), pat.getText().toString(), mat.getText().toString(), tel.getText().toString(),
                            date.getText().toString(), coment.getText().toString(), nomImg );
                    dbPeque.setPeque(peque);
                    if( editP == null ) {
                        dbPeque.insertarPeque();
                        Toast.makeText(getApplicationContext(), "Guardado: " + nom.getText().toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        dbPeque.actualizaPeque(extras.getString("id"));
                        Toast.makeText(getApplicationContext(), "Actualzado: " + nom.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                    if(Global.online) new CreateNewPeque().execute();
                    onBackPressed();
                } else {
                    Toast.makeText(getApplicationContext(), "No se guarda sin nombre", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class CreateNewPeque extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            String response= "";
            URL url_newPewque;
            try {
                String param = "";
                if( id.equals("") ){
                    param = "?ACC=" + URLEncoder.encode("INSP", "UTF-8");
                } else {
                    param = "?ACC=" + URLEncoder.encode("UPDP", "UTF-8");
                    param += "&ID=" + URLEncoder.encode(id, "UTF-8");
                }
                param += "&nom=" + URLEncoder.encode(nom.getText().toString(), "UTF-8")+
                "&pat=" + URLEncoder.encode(pat.getText().toString(), "UTF-8")+
                "&mat=" + URLEncoder.encode(mat.getText().toString(), "UTF-8")+
                "&tel=" + URLEncoder.encode(tel.getText().toString(), "UTF-8")+
                "&cumple=" + URLEncoder.encode(date.getText().toString(), "UTF-8")+
                "&coment=" + URLEncoder.encode(coment.getText().toString(), "UTF-8")+
                "&img=" + URLEncoder.encode(ts.toString().replace(" ", "") + ".jpg", "UTF-8");

                url_newPewque = new URL(Global.url_serv + "wsproxy.php" + param );

                HttpURLConnection urlConn = (HttpURLConnection)url_newPewque.openConnection();
                urlConn.setDoOutput(true);
                urlConn.setRequestMethod("GET");
                urlConn.setFixedLengthStreamingMode(param.getBytes().length);
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //send the GET out
                PrintWriter out = new PrintWriter(urlConn.getOutputStream());
                out.print(param);
                out.close();

                //start listening to the stream
                Scanner inStream = new Scanner(urlConn.getInputStream());

                //process the stream and store it in StringBuilder
                while(inStream.hasNextLine()) response += (inStream.nextLine());

                urlConn.disconnect();
                Log.e("JSONResponse", response);

            }catch( MalformedURLException mue){
                Log.e("URLError", mue.getCause().toString() );
            }catch(IOException ioe){
                Log.e("IOError", ioe.getCause().toString() );
            }

            try {
                JSONObject json = new JSONObject( response );
                Log.d("Create JSON Response", json.toString());
                int success = json.getInt(TAG_SUCCESS);
                if( success == 1 ) {
                    Log.i("Exitoooo!!", json.getString("message"));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if( dir != null ){
                CardAdapter.getThumbnail(dir.getName());
                File t = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/" + dir.getName() );

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
                    FileInputStream fileInputStream = new FileInputStream(t);
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
                    conn.setRequestProperty("uploaded_file", t.getName());

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + t.getName() + "\"" + lineEnd);

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

                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode );

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
            }

            return null;
        }
    }
}