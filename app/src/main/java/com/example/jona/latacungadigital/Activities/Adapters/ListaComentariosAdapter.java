package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
 * Created by fabia on 09/06/2018.
 */

public class ListaComentariosAdapter  extends BaseAdapter {

    Context context;
    ArrayList<ComentarioModel> listComentario = new ArrayList<>();
    private static LayoutInflater inflater = null;
    TextView txt_nombre_usuario;
    TextView txt_contenido;
    TextView txt_fecha;
    ImageView imageView_usuario;
    private RatingBar mBar;

    public ListaComentariosAdapter(Context context, ArrayList<ComentarioModel> listComentario) {
        this.context = context;
        this.listComentario = listComentario;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (listComentario != null) {
            return listComentario.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return  listComentario.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;


       if(view == null)
            view = inflater.inflate(R.layout.comentario_item,null);

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("cliente").child(listComentario.get(position).getUidUsuario());

        final View finalView = view;
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txt_nombre_usuario = finalView.findViewById(R.id.item_comenteario_nombre);
                txt_nombre_usuario.setText(dataSnapshot.child("nombre").getValue().toString());
                imageView_usuario = (ImageView) finalView.findViewById(R.id.item_comentario_imagen);
                Picasso.get().load(dataSnapshot.child("pathImagen").getValue().toString())
                        .transform(new CircleTransform()).into(imageView_usuario);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mBar = (RatingBar) view.findViewById(R.id.item_ratingBar);
        mBar.setRating((float) listComentario.get(position).getCalificacion());
        txt_nombre_usuario = view.findViewById(R.id.item_comenteario_nombre);
        txt_nombre_usuario.setText(listComentario.get(position).getUidUsuario());
        txt_contenido = view.findViewById(R.id.item_comentario_contenido);
        txt_contenido.setText(listComentario.get(position).getContenido());
        txt_fecha = view.findViewById(R.id.item_comentario_fecha);
        txt_fecha.setText(listComentario.get(position).getFecha());






        return view;
    }
}
