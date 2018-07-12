package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Fragments.FiltroCategoriasFragment;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.CategoriaModel;
import com.example.jona.latacungadigital.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoriasAdapter extends RecyclerView.Adapter {
    private ArrayList<CategoriaModel> listaCategorias;
    CategoriaModel cm = new CategoriaModel();
    FragmentTransaction fragmentTransaction;

    public CategoriasAdapter(ArrayList<CategoriaModel> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }

    @NonNull
    @Override
    public CategoriasAdapter.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoria_item, parent, false);
        return new CategoriasAdapter.ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final CategoriaModel categoriaModel = listaCategorias.get(position);
        final ArrayList<String> listaCategoriasS = new ArrayList<>();

        ((CategoriasAdapter.ViewHolderDatos) holder).asignarDatos(categoriaModel);
        ((ViewHolderDatos) holder).swMostrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    listaCategoriasS.add(listaCategorias.get(position).getKey());
                    //cm.setListaCategoriasSeleccionadas(listaCategoriasS);
                } else {
                    listaCategoriasS.remove(listaCategorias.get(position).getKey());
                }
                System.out.println("lista1: "+cm.getListaCategoriasSeleccionadas().size());
            }
        });


    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivFoto;
        Switch swMostrar;

        public ViewHolderDatos(View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombreCat);
            ivFoto = (ImageView) itemView.findViewById(R.id.iv_fotoCat);
            swMostrar = (Switch) itemView.findViewById(R.id.sw_mostrar);
        }

        public void asignarDatos(CategoriaModel categoriaModel) {
            tvNombre.setText(categoriaModel.getNombre());
            Picasso.get().load(categoriaModel.getPathImagen()).transform(new CircleTransform()).into(ivFoto);


        }
    }
}
