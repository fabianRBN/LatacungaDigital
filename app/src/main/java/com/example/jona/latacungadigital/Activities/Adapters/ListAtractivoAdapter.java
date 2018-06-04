package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.example.jona.latacungadigital.Activities.AtractivoActivity;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.R;

import java.util.ArrayList;

/**
 * Created by fabia on 03/06/2018.
 */

public class ListAtractivoAdapter  extends BaseAdapter  {

    Context context;
    ArrayList<AtractivoModel> listAtractivo = new ArrayList<>();
    private static LayoutInflater inflater = null;
    TextView txtTituloAtractivo;
    ImageView imageView;

    public ListAtractivoAdapter(Context context, ArrayList<AtractivoModel> listAtractivo) {
        this.context = context;
        this.listAtractivo = listAtractivo;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listAtractivo.size();
    }

    @Override
    public Object getItem(int position) {
        return listAtractivo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null)
            view = inflater.inflate(R.layout.atractivo_item,null);
        txtTituloAtractivo = view.findViewById(R.id.titulo_item_atractivo);
        txtTituloAtractivo.setText(listAtractivo.get(position).getNombre());
        imageView = (ImageView) view.findViewById(R.id.imgView_item_atractivo);
        Glide.with(context).load(listAtractivo.get(position).getPathImagen()).centerCrop().into(imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AtractivoActivity.class);
                intent.putExtra("atractivoKey", listAtractivo.get(position).getKey());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
