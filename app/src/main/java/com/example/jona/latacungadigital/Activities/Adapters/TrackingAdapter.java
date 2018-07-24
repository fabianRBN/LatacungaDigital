package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Fragments.TrackinFragment;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.TrackinModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class TrackingAdapter extends RecyclerView.Adapter {

   private ArrayList<TrackinModel> listaAmigos;
   FirebaseDatabase mDatabase;
   private FirebaseAuth userFirebase;

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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final TrackinModel trackinModel = listaAmigos.get(position);
        ((ViewHolderDatos) holder).asignarDatos(trackinModel);
        if (trackinModel.isAutorizacion()){
            ((ViewHolderDatos) holder).swAutorizar.setChecked(true);
        }
        ((ViewHolderDatos) holder).swAutorizar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDatabase = FirebaseDatabase.getInstance();
                userFirebase = FirebaseAuth.getInstance();
                String uid = userFirebase.getCurrentUser().getUid();
                if (isChecked){
                    mDatabase.getReference("meAutorizaron").child(listaAmigos.get(position).getKey()).child(uid).child("autorizacion").setValue(true);
                    mDatabase.getReference("autorizados").child(uid).child(listaAmigos.get(position).getKey()).child("autorizacion").setValue(true);
                } else {
                    mDatabase.getReference("meAutorizaron").child(listaAmigos.get(position).getKey()).child(uid).child("autorizacion").setValue(false);
                    mDatabase.getReference("autorizados").child(uid).child(listaAmigos.get(position).getKey()).child("autorizacion").setValue(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvEmail;
        ImageView ivFoto;
        Switch swAutorizar;

        public ViewHolderDatos(View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
            ivFoto = (ImageView) itemView.findViewById(R.id.iv_foto);
            swAutorizar = (Switch) itemView.findViewById(R.id.switch1);
        }

        public void asignarDatos(TrackinModel trackinModel) {
            tvNombre.setText(trackinModel.getNombre());
            tvEmail.setText(trackinModel.getEmail());
            Picasso.get().load(trackinModel.getPathImagen()).transform(new CircleTransform()).into(ivFoto);


        }
    }
}
