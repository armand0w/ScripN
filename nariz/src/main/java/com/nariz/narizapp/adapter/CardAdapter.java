package com.nariz.narizapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nariz.narizapp.activity.InfoPeque;
import com.nariz.narizapp.model.CardObject;
import com.nariz.narizapp.nariz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ACsatillo on 07/09/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements View.OnClickListener  {

    private List<CardObject> items;
    private static Context mContext;

    public CardAdapter(Context ctx, List<CardObject> items) {
        this.items = items;
        this.mContext = ctx;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public ImageView imagen;
        public TextView nombre;
        public TextView comentario;

        public CardViewHolder(View v) {
            super(v);
            id = (TextView) v.findViewById(R.id.idCard);
            imagen = (ImageView) v.findViewById(R.id.imagenCard);
            nombre = (TextView) v.findViewById(R.id.nombreCard);
            comentario = (TextView) v.findViewById(R.id.comentario);

            v.setOnClickListener(CardAdapter.this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, InfoPeque.class);
        TextView txt = (TextView)v.findViewById(R.id.idCard);
        //Log.i(items.get(0).getClass().getSimpleName(), txt.getText().toString());
        intent.putExtra( "id", txt.getText().toString() );
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() { return items.size(); }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_item, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder viewHolder, int i) {
        if( items.get(i).getImagen().length()>0 ) { //texto imagen en DB
            File t = new File(mContext.getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/" + items.get(i).getImagen());
            if (t.exists()) { //Si existe thumbnail
                viewHolder.imagen.setImageURI(Uri.fromFile(new File(mContext.getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/" + items.get(i).getImagen())));
            } else {
                t = new File(mContext.getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/" + items.get(i).getImagen());
                if (t.exists()) viewHolder.imagen.setImageBitmap(getThumbnail(items.get(i).getImagen()));
                else viewHolder.imagen.setImageResource(R.drawable.suzumiya); //Existe en DB pero no la imagen
            }
        } else viewHolder.imagen.setImageResource(R.drawable.angel);

        viewHolder.id.setText( items.get(i).getId() );
        viewHolder.nombre.setText( items.get(i).getNombre() );
        viewHolder.comentario.setText( String.valueOf(items.get(i).getComentario()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static Bitmap getThumbnail(String f){
        File thumbnailFile = new File(mContext.getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/thumbnail/" + f );
        File file = new File(mContext.getExternalFilesDir(null).getAbsolutePath() + "/FotoPeques/" + f); // the image file
        Bitmap thumbnail = null;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true; // obtain the size of the image, without loading it in memory
        BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);

        // find the best scaling factor for the desired dimensions
        int desiredWidth = 300;
        int desiredHeight = 200;
        float widthScale = (float) bitmapOptions.outWidth / desiredWidth;
        float heightScale = (float) bitmapOptions.outHeight / desiredHeight;
        float scale = Math.min(widthScale, heightScale);


        int sampleSize = 1;
        while (sampleSize < scale) { sampleSize *= 2; }
        bitmapOptions.inSampleSize = sampleSize; // this value must be a power of 2,
        // this is why you can not have an image scaled as you would like to have
        bitmapOptions.inJustDecodeBounds = false; // now we want to load the image


        // Let's load just the part of the image necessary for creating the thumbnail, not the whole image
        thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);

        // Save the thumbnail
        try {
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
        }catch(IOException io){
            io.printStackTrace();
        }
        return thumbnail;
    }

}