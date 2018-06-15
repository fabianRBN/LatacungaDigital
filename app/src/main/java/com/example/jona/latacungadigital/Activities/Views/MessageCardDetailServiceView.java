package com.example.jona.latacungadigital.Activities.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.R;

public class MessageCardDetailServiceView extends LinearLayout {
    //Variables de la clase
    private Context context;
    private ServiceClass service;

    // Varaibles de acuerdo a los componentes que comprenden el layout: message_cv_detail_service.xml
    protected TextView txtTitulo;
    protected TextView txtCategoria;
    protected TextView txtDireccion;
    protected TextView txtContacto;
    protected TextView txtWeb;

    public MessageCardDetailServiceView(Context context) {
        super(context);
        setView(context);
    }

    public MessageCardDetailServiceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setView(context);
    }

    // Instanciar y dar valores a los componetes de la vista
    private void setView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.message_cv_detail_service, this);
        txtTitulo = (TextView) view.findViewById(R.id.txt_titulo);
        txtCategoria = (TextView) view.findViewById(R.id.txt_categoria);
        txtDireccion = (TextView) view.findViewById(R.id.txt_direccion);
        txtContacto = (TextView) view.findViewById(R.id.txt_contacto);
        txtWeb = (TextView) view.findViewById(R.id.txt_web);
        this.context = context;
    }

    private void setValues(){
        txtTitulo.setText(service.getName());
        txtCategoria.setText(Html.fromHtml("<b>Tipo de Actividad: </b>" + service.getTypeOfActivity()));
        txtDireccion.setText(Html.fromHtml("<b>Dirección: </b>" + service.getAddress()));
        txtContacto.setText(Html.fromHtml("<b>Contacto: </b>" + service.getContact()));
        txtWeb.setText(Html.fromHtml("<b>Web: </b>" + service.getWeb()));
    }

    public void setService(ServiceClass service) {
        this.service = service;
        setValues();
    }
}
