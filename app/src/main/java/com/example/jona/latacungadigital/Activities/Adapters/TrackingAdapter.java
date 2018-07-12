package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Fragments.TrackinFragment;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.TrackinModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
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
        ((ViewHolderDatos) holder).btnEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFirebase = FirebaseAuth.getInstance();
                String uid = userFirebase.getCurrentUser().getUid();
                mDatabase = FirebaseDatabase.getInstance();
                mDatabase.getReference("meAutorizaron").child(listaAmigos.get(position).getKey()).child(uid).getRef().removeValue();
                mDatabase.getReference("autorizados").child(uid).child(listaAmigos.get(position).getKey()).getRef().removeValue();
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
        Button btnEliminarUsuario;

        public ViewHolderDatos(View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_email);
            ivFoto = (ImageView) itemView.findViewById(R.id.iv_foto);
            btnEliminarUsuario = (Button) itemView.findViewById(R.id.btn_eliminarUsuario);
        }

        public void asignarDatos(TrackinModel trackinModel) {
            tvNombre.setText(trackinModel.getNombre());
            tvEmail.setText(trackinModel.getEmail());
            Picasso.get().load(trackinModel.getPathImagen()).transform(new CircleTransform()).into(ivFoto);


        }
    }
}
