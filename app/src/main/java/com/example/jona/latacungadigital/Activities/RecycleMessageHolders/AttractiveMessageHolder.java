package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;

import me.relex.circleindicator.CircleIndicator;

// Clase para manipular la informacion de los atractivos y ponerles en sus respectivos componetes para agregarles en el RecycleView.
public class AttractiveMessageHolder extends RecyclerView.ViewHolder {
    // Declaracion de varaibles de acuerdo a los componentes que comprenden el layout: attractive_information.xml
    private TextView txtNameAttractive, txtDescriptionAttractive, txtCategoryAttractive;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;

    public AttractiveMessageHolder(View itemView) {
        super(itemView);

        txtDescriptionAttractive = itemView.findViewById(R.id.txtDescriptionPlacesInformation);
        txtNameAttractive = itemView.findViewById(R.id.txtTitlePlacesInformation);
        txtCategoryAttractive = itemView.findViewById(R.id.txtCategoryPlacesInformation);
        viewPager = itemView.findViewById(R.id.vpPlacesInformation);
        circleIndicator = itemView.findViewById(R.id.ciViewPagerImages);
    }

    public ViewPager getViewPager() { return viewPager; }

    public CircleIndicator getCircleIndicator() { return circleIndicator; }

    public void bind(TextMessageModel message) { // Se asigna la informacion consultada a los TextViews.
        // Se envia el nombre del atractivo a su respetivo TextView.
        txtNameAttractive.setText(message.getNameAttractive());

        // Se envia la descripcion del atractivo a su respetivo TextView.
        txtDescriptionAttractive.setText(Html.fromHtml("<b>Descripción: </b>" + message.getDescriptionAttractive()));

        // Se envia la categoria del atractivo a su respetivo TextView.
        txtCategoryAttractive.setText(Html.fromHtml("<b>Categoría: </b>" + message.getCategoryAttactive()));
    }
}
