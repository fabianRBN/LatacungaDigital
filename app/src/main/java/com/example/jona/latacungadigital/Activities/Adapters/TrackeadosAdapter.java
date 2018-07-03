package com.example.jona.latacungadigital.Activities.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.TrackeadosModel;
import com.example.jona.latacungadigital.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackeadosAdapter extends RecyclerView.Adapter {

    private ArrayList<TrackeadosModel> listaAmigosAutorizados;

    public TrackeadosAdapter(ArrayList<TrackeadosModel> listaAmigos) {
        this.listaAmigosAutorizados = listaAmigos;
    }

    @NonNull
    @Override
    public TrackeadosAdapter.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackeados_item, parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrackeadosModel trackinModel = listaAmigosAutorizados.get(position);
        ((ViewHolderDatos) holder).asignarDatos(trackinModel);
    }

    @Override
    public int getItemCount() {
        return listaAmigosAutorizados.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvDistancia;
        ImageView ivFoto;

        public ViewHolderDatos(View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombreA);
            tvDistancia = (TextView) itemView.findViewById(R.id.tv_distancia);
            ivFoto = (ImageView) itemView.findViewById(R.id.iv_fotoA);
        }

        public void asignarDatos(TrackeadosModel trackeadosModel) {
            tvNombre.setText(trackeadosModel.getNombre());
            tvDistancia.setText(trackeadosModel.getDistancia());
            Picasso.get().load(trackeadosModel.getPathImagen()).transform(new CircleTransform()).into(ivFoto);


        }
    }
}