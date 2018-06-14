package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.ComentarioModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by fabia on 13/06/2018.
 */

public class ListaComentarioAdapter extends RecyclerView.Adapter<ListaComentarioAdapter.ViewHolderDatos> {

    Context context;
    ArrayList<ComentarioModel> listComentario = new ArrayList<>();
    private static LayoutInflater inflater = null;


    public ListaComentarioAdapter(Context context, ArrayList<ComentarioModel> listComentario) {
        this.context = context;
        this.listComentario = listComentario;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comentario_item,null,false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(listComentario.get(position));

    }

    @Override
    public int getItemCount() {
        return  listComentario.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        TextView txt_nombre_usuario;
        TextView txt_contenido;
        TextView txt_fecha;
        ImageView imageView_usuario;
        private RatingBar mBar;
        DatabaseReference mData;


        public ViewHolderDatos(View itemView) {
            super(itemView);
            txt_nombre_usuario = itemView.findViewById(R.id.item_comenteario_nombre);
            imageView_usuario = (ImageView) itemView.findViewById(R.id.item_comentario_imagen);
            mBar = (RatingBar) itemView.findViewById(R.id.item_ratingBar);
            txt_contenido = itemView.findViewById(R.id.item_comentario_contenido);
            txt_fecha = itemView.findViewById(R.id.item_comentario_fecha);
            mData = FirebaseDatabase.getInstance().getReference().child("cliente");
        }

        public void asignarDatos(ComentarioModel comentarioModel) {

             mData.child(comentarioModel.getUidUsuario()).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     txt_nombre_usuario.setText(dataSnapshot.child("nombre").getValue().toString());
                     Picasso.get().load(dataSnapshot.child("pathImagen").getValue().toString())
                             .transform(new CircleTransform()).into(imageView_usuario);
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });

            
            mBar.setRating((float) comentarioModel.getCalificacion());
            txt_nombre_usuario.setText(comentarioModel.getUidUsuario());

            txt_contenido.setText(comentarioModel.getContenido());

            txt_fecha.setText(comentarioModel.getFecha());

        }
    }
}
