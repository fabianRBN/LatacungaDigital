package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.github.florent37.expansionpanel.ExpansionLayout;

import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

// Clase para manipular la informacion de los atractivos y ponerles en sus respectivos componetes para agregarles en el RecycleView.
public class AttractiveMessageHolder extends RecyclerView.ViewHolder {
    // Declaracion de varaibles de acuerdo a los componentes que comprenden el layout: attractive_information.xml
    private TextView txtNameAttractive, txtInformationAttractive, txtMoreInformation, txtSubTypeAttractive;
    private CircleImageView imgPlacesInformation;
    private ViewPager viewPager;
    private RatingBar ratingBar;
    private CircleIndicator circleIndicator;
    private ExpansionLayout expansionLayout;

    // Variable atractivo
    private AttractiveClass attractive;

    public AttractiveMessageHolder(View itemView) {
        super(itemView);

        txtNameAttractive = itemView.findViewById(R.id.txtTitlePlacesInformation);
        txtSubTypeAttractive = itemView.findViewById(R.id.txtSubTypePlacesInformation);
        txtInformationAttractive = itemView.findViewById(R.id.txtInformationAttractive);
        txtMoreInformation = itemView.findViewById(R.id.txtMoreInformation);
        imgPlacesInformation = itemView.findViewById(R.id.imgPlacesInformation);
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
        attractive = message.getAttractive();
        // Se envia el nombre del atractivo a su respetivo TextView.
        txtNameAttractive.setText(attractive.getNameAttractive());
        // Se envia la categoria que pertenece el atractivo a su respetivo TextView.
        txtSubTypeAttractive.setText(attractive.getSubType());
        // Se envia el rating del atractivo.
        ratingBar.setRating(Float.parseFloat(attractive.getRating()));

        // Se envia la categoría, dirección y la descripción del atractivo a su respetivo TextView.
        txtInformationAttractive.setText(Html.fromHtml("<b>Categoría: </b>" + attractive.getCategory() + "<br><br>" +
                "<b>Dirección: </b>" + attractive.getAddress()+ "<br><br>" +
                "<b>Descripción: </b>" + attractive.getDescription().replaceAll("\\\\n", "<br><br>").
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

        setImageBySubType(); // Damos la imagen de acuerdo al subtipo del atractivo.
    }

    // Método para dar el icono respectivo segun el sub tipo del atractivo.
    private void setImageBySubType() {
        switch (attractive.getSubType()) {
            case ChatBotReferences.ARQUITECTURA_CIVIL:
                imgPlacesInformation.setImageResource(R.drawable.ic_civil);
                break;
            case ChatBotReferences.ARQUITECTURA_RELIGIOSA:
                imgPlacesInformation.setImageResource(R.drawable.ic_church);
                break;
            case ChatBotReferences.MUSEO:
                imgPlacesInformation.setImageResource(R.drawable.ic_museo);
                break;
            case ChatBotReferences.MUSEO_HISTORICO:
                imgPlacesInformation.setImageResource(R.drawable.ic_museum);
                break;
            default:
                imgPlacesInformation.setImageResource(R.drawable.ic_museum_select);
                break;
        }
    }

    // Método para eliminar los datos duplicados de la lista de imagenes.
    public List<String> deleteDuplicateImageData(int position, List<TextMessageModel> listChatModel) {
        List<String> listImagesAttractive = listChatModel.get(position).getAttractive().getListImages();
        HashSet<String> hashSet = new HashSet<>(listImagesAttractive);
        listImagesAttractive.clear();
        listImagesAttractive.addAll(hashSet);
        return listImagesAttractive;
    }
}
