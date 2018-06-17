package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.github.florent37.expansionpanel.ExpansionLayout;

import me.relex.circleindicator.CircleIndicator;

// Clase para manipular la informacion de los atractivos y ponerles en sus respectivos componetes para agregarles en el RecycleView.
public class AttractiveMessageHolder extends RecyclerView.ViewHolder {
    // Declaracion de varaibles de acuerdo a los componentes que comprenden el layout: attractive_information.xml
    private TextView txtNameAttractive, txtInformationAttractive, txtMoreInformation;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private ExpansionLayout expansionLayout;

    public AttractiveMessageHolder(View itemView) {
        super(itemView);

        txtNameAttractive = itemView.findViewById(R.id.txtTitlePlacesInformation);
        txtInformationAttractive = itemView.findViewById(R.id.txtInformationAttractive);
        txtMoreInformation = itemView.findViewById(R.id.txtMoreInformation);
        viewPager = itemView.findViewById(R.id.vpPlacesInformation);
        circleIndicator = itemView.findViewById(R.id.ciViewPagerImages);
        expansionLayout = itemView.findViewById(R.id.expansionLayout);
    }

    public ViewPager getViewPager() { return viewPager; }

    public CircleIndicator getCircleIndicator() { return circleIndicator; }

    public ExpansionLayout getExpansionLayout() { return expansionLayout; }

    public void bind(TextMessageModel message) { // Se asigna la informacion consultada a los TextViews.
        // Se envia el nombre del atractivo a su respetivo TextView.
        txtNameAttractive.setText(message.getNameAttractive());

        // Se envia la categoria y la descripcion del atractivo a su respetivo TextView.
        txtInformationAttractive.setText(Html.fromHtml("<b>Categoría: </b>" + message.getCategoryAttactive() + "<br><br>" +
                "<b>Descripción: </b>" + message.getDescriptionAttractive()));

        txtMoreInformation.setText("Ver información");

        expansionLayout.addListener(new ExpansionLayout.Listener() {
            @Override
            public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {
                if (expanded) {
                    txtMoreInformation.setText("Ocultar Información");
                } else {
                    txtMoreInformation.setText("Ver información");
                }
            }
        });
    }
}
