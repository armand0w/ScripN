package com.nariz.narizapp.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.nariz.narizapp.model.Global;
import com.nariz.narizapp.nariz.R;

import java.io.File;
import java.io.IOException;

public class MainNarizActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainNarizActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nariz);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        if( isConected() ) Global.online = true;

        try {
            /*File dir = new File(Environment.getExternalStorageDirectory() + "/NarizApp/");
            if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);
            dir = new File(Environment.getExternalStorageDirectory() + "/NarizApp/", ".nomedia");
            if (!dir.exists()) if (!dir.createNewFile()) Log.e("File noMedia", "No se creó noMedia" + dir);
            dir = new File(Environment.getExternalStorageDirectory() + "/NarizApp/FotoPeques/");
            if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);
            dir = new File(Environment.getExternalStorageDirectory() + "/NarizApp/FotoPeques/thumbnail/");
            if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);
            dir = new File( Environment.getExternalStorageDirectory() +"/NarizApp/FotoBooks/");
            if( !dir.mkdir() ) Log.e("File", "No se creó la carpeta " + dir);
            dir = new File( Environment.getExternalStorageDirectory() +"/NarizApp/FotoColabs/");
            if( !dir.mkdir() ) Log.e("File", "No se creó la carpeta " + dir);*/

            if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
                File dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/");
                if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);
                dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/");
                if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);

                dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoBooks/");
                if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);
                dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoBooks/thumbnail/");
                if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);

                dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoColabs/");
                if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);
                dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/FotoColabs/thumbnail/");
                if (!dir.exists()) if (!dir.mkdir()) Log.e("File", "No se creó la carpeta " + dir);

                //Log.i("Directorio", getExternalFilesDir(null).getAbsolutePath());
            } else Log.e("Directorio", "No existe Memoria externa!!!");
        }catch (NullPointerException ioe){
            //ioe.printStackTrace();
            Log.e(this.getLocalClassName()+ ".onCreate = NullPointerException", ioe.getMessage());
        }

        displayView(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_nariz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.action_search ){
            Toast.makeText(getApplicationContext(), "Search in " + getTitle().toString(), Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch( position ) {
            case 0:
                fragment = new HomeFragment();
                setTitle( getString(R.string.app_name) );
                break;
            case 1:
                fragment = new PequesFragment();
                setTitle( getString(R.string.title_peques) );
                break;
            case 2:
                fragment = new BooksFragment();
                setTitle( getString(R.string.title_books) );
                break;
            case 3:
                fragment = new ColabFragment();
                setTitle( getString(R.string.title_friends) );
                break;
            default:
                Toast.makeText(getApplicationContext(), "Fragment Error!", Toast.LENGTH_SHORT).show();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
        }
    }

    public boolean isConected(){
        //boolean connected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;

        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;

        //return connected;
        return (wifiConnected || mobileConnected);
    }

}
