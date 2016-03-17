package com.nariz.narizapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.nariz.narizapp.nariz.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACaatillo on 04/09/2015.
 */
public class PequesStore extends SQLiteOpenHelper {

    private final String DB = "Peque";
    private final String[] d =  {"id", "nombre", "paterno", "materno", "tel", "cumple", "comentario", "imagen"};
    private String sqlCreate = "CREATE TABLE " + DB +
            " ("+d[0]+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            d[1]+" TEXT, "+d[2]+" TEXT, "+d[3]+" TEXT, "+d[4]+" TEXT, "+d[5]+" TEXT, "+d[6]+" TEXT, "+d[7]+" TEXT)";
    private SQLiteDatabase db = null;
    private Peque pque = null;

    public PequesStore(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    public void setPeque(Peque p) { pque = p; }

    @Override
    public void onCreate(SQLiteDatabase db) { db.execSQL(sqlCreate); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        if ( versionNueva > versionAnterior ) {
            db.execSQL( "DROP TABLE IF EXISTS " + DB );
            db.execSQL( sqlCreate );
        }
    }

    public boolean existeBD(){
        boolean ex = false;
        db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+d[0]+", "+d[1]+", "+d[2]+", "+d[3]+", "+d[5]+" FROM " + DB +
                " WHERE " + d[1] + " = '" + pque.getNombre() + "' " +
                " AND " + d[2] + " = '" + pque.getPaterno() + "' " +
                " AND " + d[3] + " = '" + pque.getMaterno() + "' " +
                " AND " + d[5] + " = '" + pque.getCumplea() + "'", null);
        if( cursor.moveToFirst() ) {
            ex = true;
            pque.setId( cursor.getString(0) );
        }
        cursor.close();
        db.close();
        return ex;
    }

    public void insertarPeque() {
        if( !existeBD() ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(d[1], pque.getNombre());
            contentValues.put(d[2], pque.getPaterno());
            contentValues.put(d[3], pque.getMaterno());
            contentValues.put(d[4], pque.getTelefono());
            contentValues.put(d[5], pque.getCumplea());
            contentValues.put(d[6], pque.getComentario());
            contentValues.put(d[7], pque.getImagen());

            db = getWritableDatabase();
            db.insert(DB, null, contentValues);
            db.close();
            //Log.e("Insertado", pque.toString());
        } else actualizaPeque(pque.getId());
    }

    public void insertaJSON(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = new JSONObject( jsonArray.getJSONObject(i).toString() );
                //Log.e("Json", obj.toString());
                pque = new Peque( new String(obj.getString("nombre").getBytes("ISO-8859-1"), "UTF-8"),
                        new String(obj.getString("paterno").getBytes("ISO-8859-1"), "UTF-8"),
                        new String(obj.getString("materno").getBytes("ISO-8859-1"), "UTF-8"),
                        obj.getString("tel"),
                        obj.getString("cumple"),
                        new String(obj.getString("comentario").getBytes("ISO-8859-1"), "UTF-8"),
                        obj.getString("imagen") );
                /*pque = new Peque( obj.getString("nombre"),
                        obj.getString("paterno"),
                        obj.getString("materno"),
                        obj.getString("tel"),
                        obj.getString("cumple"),
                        obj.getString("comentario"),
                        obj.getString("imagen") );*/
                //Log.e("Objeto Pequeñooo:::::::", pque.toString());
                insertarPeque();
            }
        }catch (JSONException je){
            Log.e("JSONException", je.getMessage());
        }catch (UnsupportedEncodingException uee){
            Log.e("Encode", uee.getMessage());
        }
    }

    public void borrarPeque(String id){
        db = getWritableDatabase();
        db.delete(DB, "id = " + id, null);
        db.close();
    }

    public List<CardInfo> getPequeCards(String id){
        List<CardInfo> lci = null;
        db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+d[0]+", "+d[1]+", "+d[2]+", "+d[3]+", "+d[3]+", "+d[4]+", "+d[5]+", "+d[6]+" FROM " + DB + " WHERE id = " + id, null);
        if( cursor.moveToFirst() ) {
            lci = new ArrayList<CardInfo>();
            do {
                lci.add(  new CardInfo("Nombre", cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3) , R.drawable.ic_face_black_24dp ));
                if( cursor.getString(5).length()>0 ) lci.add(  new CardInfo("Telefono", cursor.getString(5), R.drawable.ic_contact_phone_black_24dp ));
                if( cursor.getString(6).length()>0 ) lci.add(  new CardInfo("Cumpleaños", cursor.getString(6) , R.drawable.ic_redeem_black_24dp ));
                if( cursor.getString(7).length()>0 ) lci.add(  new CardInfo("Comentario", cursor.getString(7) , R.drawable.ic_comment_black_36dp ));
            } while ( cursor.moveToNext() );
            if(cursor != null && !cursor.isClosed()) cursor.close();
        }
        cursor.close();
        db.close();
        return lci;
    }

    public JSONArray getJSONPeques(){
        db = getWritableDatabase();
        String JSONPeque = "";
        JSONArray array = new JSONArray();
        Cursor cursor = db.rawQuery(" SELECT "+d[0]+", "+d[1]+", "+d[2]+", "+d[3]+", "+d[4]+", "+d[5]+", "+d[6]+", "+d[7]+" FROM " + DB, null);
        if( cursor.moveToFirst() ) {
            do {
                try {
                    JSONObject object = new JSONObject();
                    object.put(d[0], cursor.getString(0)); //ID
                    object.put(d[1], cursor.getString(1)); //Nombre
                    object.put(d[2], cursor.getString(2)); //Paterno
                    object.put(d[3], cursor.getString(3)); //Materno
                    object.put(d[4], cursor.getString(4)); //Tel
                    object.put(d[5], cursor.getString(5)); //Cumple
                    object.put(d[6], cursor.getString(6)); //Comentario
                    object.put(d[7], cursor.getString(7)); //Imagen

                    array.put(object);
                }catch (JSONException je){
                    je.printStackTrace();
                }
            } while ( cursor.moveToNext() );
            if(cursor != null && !cursor.isClosed()) cursor.close();
        }
        cursor.close();
        db.close();
        return array;
    }

    public Peque getPeque(String id){
        Peque peque = null;
        db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+d[0]+", "+d[1]+", "+d[2]+", "+d[3]+", "+d[4]+", "+d[5]+", "+d[6]+", "+d[7]+" FROM " + DB + " WHERE id = " + id, null);
        if( cursor.moveToFirst() ) {
            peque = new Peque(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7) );
            //Log.i("Peque SQL", peque.toString() );
            if(cursor != null && !cursor.isClosed()) cursor.close();
        }
        cursor.close();
        db.close();
        return peque;
    }

    public List<CardObject> getAllCards(){
        List<CardObject> lp = null;
        Peque p = null;

        db = getWritableDatabase();
        Cursor cursor = db.rawQuery(" SELECT "+d[0]+", "+d[1]+", "+d[2]+", "+d[3]+", "+d[4]+", "+d[5]+", "+d[6]+", "+d[7]+" FROM " + DB, null);
        if( cursor.moveToFirst() ) {
            lp = new ArrayList<CardObject>();
            do {
                p = new Peque();
                p.setId( cursor.getString(0) );
                p.setNombre(cursor.getString(1) + " " + cursor.getString(2));
                p.setPaterno(cursor.getString(2));
                p.setMaterno(cursor.getString(3));
                p.setTelefono(cursor.getString(4));
                p.setCumplea(cursor.getString(5));
                p.setComentario(cursor.getString(6));
                p.setImagen(cursor.getString(7));
                lp.add( p );
            } while ( cursor.moveToNext() );
            if(cursor != null && !cursor.isClosed()) cursor.close();
        }
        cursor.close();
        db.close();
        return lp;
    }

    public void actualizaPeque(String id){
        ContentValues contentValues = new ContentValues();
        //Log.e("Peque UPDT", pque.toString() );
        contentValues.put(d[1], pque.getNombre());
        contentValues.put( d[2], pque.getPaterno() );
        contentValues.put( d[3], pque.getMaterno() );
        contentValues.put( d[4], pque.getTelefono() );
        contentValues.put( d[5], pque.getCumplea() );
        contentValues.put( d[6], pque.getComentario() );
        contentValues.put( d[7], pque.getImagen() );

        db = getWritableDatabase();
        db.update(DB, contentValues, " id = " + id, null);
        db.close();
    }

}
