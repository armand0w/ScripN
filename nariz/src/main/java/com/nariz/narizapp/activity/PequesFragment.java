package com.nariz.narizapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nariz.narizapp.adapter.CardAdapter;
import com.nariz.narizapp.model.CardObject;
import com.nariz.narizapp.model.PequesStore;
import com.nariz.narizapp.nariz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACsatillo on 02/09/2015.
 */
public class PequesFragment extends Fragment {

    private FloatingActionButton fabButton;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private PequesStore pqs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_peques, container, false);

        List<CardObject> items = new ArrayList<CardObject>();

        fabButton = (FloatingActionButton)rootView.findViewById( R.id.fab );
        fabButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPeques.class);
                startActivity(intent);
            }
        });

        pqs = new PequesStore(getActivity().getApplicationContext(), "Peque", null, 1);
        items = pqs.getAllCards();

        // Obtener el Recycler
        recycler = (RecyclerView) rootView.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        if( items != null ) {
            adapter = new CardAdapter(getContext(), items);
            recycler.setAdapter(adapter);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
