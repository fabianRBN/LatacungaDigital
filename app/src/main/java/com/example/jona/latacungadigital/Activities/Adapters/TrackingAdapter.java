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

public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolderDatos> {
    @NonNull
    Context context;
    ArrayList<TrackinModel> listaAmigos = new ArrayList<>();
    private static LayoutInflater inflater = null;

    public TrackingAdapter(@NonNull Context context, ArrayList<TrackinModel> listaAmigos) {
        this.context = context;
        this.listaAmigos = listaAmigos;
    }

    @Override
    public TrackingAdapter.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_item, null, false);
        System.out.println("VHL cREO");
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingAdapter.ViewHolderDatos holder, int position) {
        holder.asignarDatos(listaAmigos.get(position));
    }

    @Override
    public int getItemCount() {
        System.out.println("VHLlista: "+listaAmigos.size());
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
            System.out.println("VHLLlego: "+trackinModel.getNombre());

        }
    }
}
