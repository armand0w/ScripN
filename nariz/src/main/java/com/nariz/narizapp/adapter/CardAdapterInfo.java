package com.nariz.narizapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nariz.narizapp.model.CardInfo;
import com.nariz.narizapp.nariz.R;

import java.util.List;

/**
 * Created by ACsatillo on 15/09/2015.
 */
public class CardAdapterInfo extends RecyclerView.Adapter<CardAdapterInfo.CardInfoViewHolder> {

    private List<CardInfo> items;
    private Context mContext;

    public CardAdapterInfo(Context ctx, List<CardInfo> items) {
        this.items = items;
        this.mContext = ctx;
    }

    public class CardInfoViewHolder extends RecyclerView.ViewHolder {
        //public TextView id;
        public ImageView img;
        public TextView titulo;
        public TextView informacion;

        public CardInfoViewHolder(View v) {
            super(v);
            //id = (TextView) v.findViewById(R.id.);
            img = (ImageView) v.findViewById(R.id.imageViewCardInfo);
            titulo = (TextView) v.findViewById(R.id.titleCardInfo);
            informacion = (TextView) v.findViewById(R.id.infoCardInfo);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public CardInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_info, viewGroup, false);
        return new CardInfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardInfoViewHolder viewHolder, int i) {
        viewHolder.img.setImageResource( items.get(i).getImagen() );
        viewHolder.titulo.setText( items.get(i).getTitulo());
        viewHolder.informacion.setText( items.get(i).getInfo() );
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

