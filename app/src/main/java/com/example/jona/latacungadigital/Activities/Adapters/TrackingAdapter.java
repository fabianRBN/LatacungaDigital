package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.TrackinModel;
import com.example.jona.latacungadigital.R;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class TrackingAdapter extends RecyclerView.Adapter {

   private ArrayList<TrackinModel> listaAmigos;

    public TrackingAdapter( ArrayList<TrackinModel> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }

    @NonNull
    @Override
    public TrackingAdapter.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_item, parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrackinModel trackinModel = listaAmigos.get(position);
        ((ViewHolderDatos) holder).asignarDatos(trackinModel);
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvEmail;
        ImageView ivFoto;
        Switch swEstado;

        public ViewHolderDatos(View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
            ivFoto = (ImageView) itemView.findViewById(R.id.iv_foto);
            swEstado = (Switch) itemView.findViewById(R.id.switch1);
        }

        public void asignarDatos(TrackinModel trackinModel) {
            tvNombre.setText(trackinModel.getNombre());
            tvEmail.setText(trackinModel.getEmail());
            Picasso.get().load(trackinModel.getPathImagen()).transform(new CircleTransform()).into(ivFoto);
            if (trackinModel.isAutorizacion())
                swEstado.setChecked(true);

        }
    }
}
