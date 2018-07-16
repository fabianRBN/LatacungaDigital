package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.github.florent37.expansionpanel.ExpansionLayout;

import me.relex.circleindicator.CircleIndicator;

// Clase para manipular la informacion de los atractivos y ponerles en sus respectivos componetes para agregarles en el RecycleView.
public class AttractiveMessageHolder extends RecyclerView.ViewHolder {
    // Declaracion de varaibles de acuerdo a los componentes que comprenden el layout: attractive_information.xml
    private TextView txtNameAttractive, txtInformationAttractive, txtMoreInformation, txtSubTypeAttractive;
    private ViewPager viewPager;
    private RatingBar ratingBar;
    private CircleIndicator circleIndicator;
    private ExpansionLayout expansionLayout;

    public AttractiveMessageHolder(View itemView) {
        super(itemView);

        txtNameAttractive = itemView.findViewById(R.id.txtTitlePlacesInformation);
        txtSubTypeAttractive = itemView.findViewById(R.id.txtSubTypePlacesInformation);
        txtInformationAttractive = itemView.findViewById(R.id.txtInformationAttractive);
        txtMoreInformation = itemView.findViewById(R.id.txtMoreInformation);
        viewPager = itemView.findViewById(R.id.vpPlacesInformation);
        circleIndicator = itemView.findViewById(R.id.ciViewPagerImages);
        ratingBar = itemView.findViewById(R.id.ratingBarPlacesInformation);
        expansionLayout = itemView.findViewById(R.id.expansionLayout);
    }

    public ViewPager getViewPager() { return viewPager; }

    public CircleIndicator getCircleIndicator() { return circleIndicator; }

    public ExpansionLayout getExpansionLayout() { return expansionLayout; }

    @SuppressLint("SetTextI18n")
    public void bind(TextMessageModel message) { // Se asigna la informacion consultada a los TextViews.
        // Se envia el nombre del atractivo a su respetivo TextView.
        txtNameAttractive.setText(message.getNameAttractive());
        // Se envia la categoria que pertenece el atractivo a su respetivo TextView.
        txtSubTypeAttractive.setText(message.getSubTypeAttractive());
        // Se envia el rating del atractivo.
        ratingBar.setRating(message.getRatingAttractive());

        // Se envia la categoría, dirección y la descripción del atractivo a su respetivo TextView.
        txtInformationAttractive.setText(Html.fromHtml("<b>Categoría: </b>" + message.getCategoryAttactive() + "<br><br>" +
                "<b>Dirección: </b>" + message.getAddressAttractive() + "<br><br>" +
                "<b>Descripción: </b>" + message.getDescriptionAttractive().replaceAll("\\\\n", "<br><br>").
                replaceAll("\\\\", "\"")));

        txtMoreInformation.setText("Ver Información");

        expansionLayout.addListener(new ExpansionLayout.Listener() {
            @Override
            public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {
                if (expanded) {
                    txtMoreInformation.setText("Ocultar Información");
                } else {
                    txtMoreInformation.setText("Ver Información");
                }
            }
        });
    }
}
