package com.nariz.narizapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.nariz.narizapp.adapter.CardAdapterInfo;
import com.nariz.narizapp.model.CardInfo;
import com.nariz.narizapp.model.Global;
import com.nariz.narizapp.model.Peque;
import com.nariz.narizapp.model.PequesStore;
import com.nariz.narizapp.nariz.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ACsatillo on 14/09/2015.
 */
public class InfoPeque extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    private FloatingActionButton btnFab;
    private CollapsingToolbarLayout ctlLayout;
    private Bundle extras;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private ImageView imgToolbar;
    private PequesStore peqS = null;
    private Peque peque = null;
    private String ide = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_peque);
        extras = getIntent().getExtras();
        imgToolbar = (ImageView)findViewById(R.id.imgToolbar);

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbarPeque);
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

        //Floating Action Button
        btnFab = (FloatingActionButton)findViewById(R.id.btnFab);
        btnFab.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Esto es una prueba: " + extras.getString("id"), Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), AddPeques.class);
                intent.putExtra( "id", extras.getString("id") );
                startActivity(intent);
            }
        });

        peqS = new PequesStore(getApplicationContext(), "Peque", null, 1);
        List<CardInfo> ls = peqS.getPequeCards(extras.getString("id"));

        //CollapsingToolbarLayout
        ctlLayout = (CollapsingToolbarLayout) findViewById(R.id.ctlLayout);
        ctlLayout.setTitle(ls.get(0).getInfo());

        try {
            File file = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/" + peqS.getPeque(extras.getString("id")).getImagen() );
            Uri output = Uri.fromFile(file);
            imgToolbar.setImageBitmap( android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), output) );
        } catch(IOException io){
            io.printStackTrace();
        }

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclerInfo);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        if (ls != null) {
            adapter = new CardAdapterInfo(getApplicationContext(), ls);
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            ide = extras.getString("id");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(InfoPeque.this);
            alertDialog.setTitle("Confirmar Borrado");
            alertDialog.setMessage("Estas seguro de borrar?");
            alertDialog.setIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    peqS.borrarPeque( extras.getString("id") );
                    if(Global.online) new DeletePeque().execute();
                    onBackPressed();
                    Toast.makeText(getApplicationContext(), "Borrado", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class DeletePeque extends AsyncTask<String, String, String> {

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
                if( !ide.equals("") ){
                    param = "?ACC=" + URLEncoder.encode("DELP", "UTF-8");
                    param += "&ID=" + URLEncoder.encode(ide, "UTF-8");
                }

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
            return null;
        }
    }
}
