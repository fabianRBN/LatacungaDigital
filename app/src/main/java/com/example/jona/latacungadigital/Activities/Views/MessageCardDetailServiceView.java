package com.example.jona.latacungadigital.Activities.Views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.R;
import com.github.florent37.expansionpanel.ExpansionLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageCardDetailServiceView extends LinearLayout {
    //Variables de la clase
    private Context context;
    private ServiceClass service;
    private ExpansionLayout expansionLayout;
    private LinearLayout horarioLayout;

    // Varaibles de acuerdo a los componentes que comprenden el layout: message_cv_detail_service.xml
    protected TextView txtAbierto;
    protected TextView txtTitulo;
    protected TextView txtSubTipoDeActividad;
    protected TextView txtCategoria;
    protected TextView txtDireccion;
    protected TextView txtContacto;
    protected TextView txtCorreo;
    protected TextView txtWeb;

    protected TextView txtSiempreAbierto;
    protected TextView txtLunes;
    protected TextView txtMartes;
    protected TextView txtMiercoles;
    protected TextView txtJueves;
    protected TextView txtViernes;
    protected TextView txtSabado;
    protected TextView txtDomingo;

    protected CircleImageView imageCvMapDetailService;
    protected ImageButton btnFacebook;

    public MessageCardDetailServiceView(Context context) {
        super(context);
        setView(context);
    }

    public MessageCardDetailServiceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setView(context);
    }

    // Instanciar y dar valores a los componetes de la vista
    private void setView(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.message_cv_detail_service, this);
        txtTitulo = view.findViewById(R.id.txt_titulo);
        txtSubTipoDeActividad = view.findViewById(R.id.txt_subTipoActividad);
        txtCategoria = view.findViewById(R.id.txt_categoria);
        txtDireccion = view.findViewById(R.id.txt_direccion);
        txtContacto = view.findViewById(R.id.txt_contacto);
        txtCorreo = view.findViewById(R.id.txt_correo);
        txtWeb = view.findViewById(R.id.txt_web);
        txtAbierto = view.findViewById(R.id.txt_abierto);

        expansionLayout = view.findViewById(R.id.scheduleExpansionLayout);
        horarioLayout = view.findViewById(R.id.horarioLayout);

        txtSiempreAbierto = view.findViewById(R.id.txt_siempre_abierto);
        txtLunes = view.findViewById(R.id.txt_lunes);
        txtMartes = view.findViewById(R.id.txt_martes);
        txtMiercoles = view.findViewById(R.id.txt_miercoles);
        txtJueves = view.findViewById(R.id.txt_jueves);
        txtViernes = view.findViewById(R.id.txt_viernes);
        txtSabado = view.findViewById(R.id.txt_sabado);
        txtDomingo = view.findViewById(R.id.txt_domingo);

        imageCvMapDetailService = view.findViewById(R.id.imageCvMapDetailService);
        btnFacebook = view.findViewById(R.id.btn_facebook);
        this.context = context;

        btnFacebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = newFacebookIntent(service.getFacebookPage());
                context.startActivity(facebookIntent);
            }
        });
    }

    private Intent newFacebookIntent(String url) {
        Uri uri = Uri.parse(url);
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private void setValues() {
        txtTitulo.setText(service.getName());
        txtSubTipoDeActividad.setText(service.getSubTypeOfActivity());
        txtCategoria.setText(Html.fromHtml("<b>Categoría: </b>" + service.getCategory()));
        txtDireccion.setText(Html.fromHtml("<b>Dirección: </b>" + service.getAddress()));
        txtContacto.setText(Html.fromHtml("<b>Contacto: </b>" + service.getContact()));
        txtCorreo.setText(Html.fromHtml("<b>Correo: </b>" + service.getEmail()));
        txtWeb.setText(Html.fromHtml("<b>Web: </b>" + service.getWeb()));

        setValuesForHorario();
        setVisibilities();
        setImageServiceByTypeOfActivity(service.getTypeOfActivity());

        if (service.getHorario().isHorarioDefinido()) {
            if(service.isOpen()){
                txtAbierto.setText(R.string.serviceOpen);
                txtAbierto.setTextColor(Color.GREEN);
            } else {
                txtAbierto.setText(R.string.serviceClosed);
                txtAbierto.setTextColor(Color.RED);
            }
        }else{
            txtAbierto.setText(R.string.scheduleNoDefined);
            txtAbierto.setTextColor(Color.DKGRAY);
        }
    }

    public void setService(ServiceClass service) {
        this.service = service;
        setValues();
    }

    private void setImageServiceByTypeOfActivity(String parameter) {
        switch (parameter) {
            case ChatBotReferences.AGENCIA_DE_VIAJE:
                imageCvMapDetailService.setImageResource(R.drawable.ic_agencias_de_viaje);
                break;
            case ChatBotReferences.ALOJAMIENTO:
                imageCvMapDetailService.setImageResource(R.drawable.ic_alojamiento);
                break;
            case ChatBotReferences.COMIDAS_Y_BEBIDAS:
                imageCvMapDetailService.setImageResource(R.drawable.ic_comidas_y_bebidas);
                break;
            case ChatBotReferences.RECREACION:
                imageCvMapDetailService.setImageResource(R.drawable.ic_recreacion);
                break;
            default:
                imageCvMapDetailService.setImageResource(R.drawable.ic_alojamiento);
                break;
        }
    }

    public void setValuesForHorario(){
        if(service.getHorario().isHorarioDefinido()){
            if(service.getHorario().isSiempreAbierto()){
                txtSiempreAbierto.setText(Html.fromHtml("<b>Siempre abierto</b>"));
            } else {
                if(service.getHorario().getLunes().isAbierto()){
                    txtLunes.setText(Html.fromHtml("<b>Lunes: </b>" + service.getHorario().getLunes().getHoraInicioAndHoraFinal()));
                }else {
                    txtLunes.setText(Html.fromHtml("<b>Lunes: </b>" + "Cerrado"));
                }
                if(service.getHorario().getMartes().isAbierto()){
                    txtMartes.setText(Html.fromHtml("<b>Martes: </b>" + service.getHorario().getMartes().getHoraInicioAndHoraFinal()));
                }else {
                    txtMartes.setText(Html.fromHtml("<b>Martes: </b>" + "Cerrado"));
                }
                if(service.getHorario().getMiercoles().isAbierto()){
                    txtMiercoles.setText(Html.fromHtml("<b>Miércoles: </b>" + service.getHorario().getMiercoles().getHoraInicioAndHoraFinal()));
                }else {
                    txtMiercoles.setText(Html.fromHtml("<b>Miércoles: </b>" + "Cerrado"));
                }
                if(service.getHorario().getJueves().isAbierto()){
                    txtJueves.setText(Html.fromHtml("<b>Jueves: </b>" + service.getHorario().getJueves().getHoraInicioAndHoraFinal()));
                }else {
                    txtJueves.setText(Html.fromHtml("<b>Jueves: </b>" + "Cerrado"));
                }
                if(service.getHorario().getViernes().isAbierto()){
                    txtViernes.setText(Html.fromHtml("<b>Viernes: </b>" + service.getHorario().getViernes().getHoraInicioAndHoraFinal()));
                }else {
                    txtViernes.setText(Html.fromHtml("<b>Viernes: </b>" + "Cerrado"));
                }
                if(service.getHorario().getSabado().isAbierto()){
                    txtSabado.setText(Html.fromHtml("<b>Sábado: </b>" + service.getHorario().getSabado().getHoraInicioAndHoraFinal()));
                }else {
                    txtSabado.setText(Html.fromHtml("<b>Sábado: </b>" + "Cerrado"));
                }
                if(service.getHorario().getDomingo().isAbierto()){
                    txtDomingo.setText(Html.fromHtml("<b>Domingo: </b>" + service.getHorario().getDomingo().getHoraInicioAndHoraFinal()));
                }else {
                    txtDomingo.setText(Html.fromHtml("<b>Domingo: </b>" + "Cerrado"));
                }
            }
        } else {
            txtSiempreAbierto.setText(Html.fromHtml("<b>Horario no definido</b>"));
        }
    }

    public void setVisibilities(){
        if(service.getFacebookPage().equalsIgnoreCase("ninguna")){
            btnFacebook.setVisibility(View.GONE);
        }

        if(!service.getHorario().isHorarioDefinido() || service.getHorario().isSiempreAbierto()){
            txtLunes.setVisibility(View.GONE);
            txtMartes.setVisibility(View.GONE);
            txtMiercoles.setVisibility(View.GONE);
            txtJueves.setVisibility(View.GONE);
            txtViernes.setVisibility(View.GONE);
            txtSabado.setVisibility(View.GONE);
            txtDomingo.setVisibility(View.GONE);

            txtSiempreAbierto.setVisibility(View.VISIBLE);
        }else{
            txtSiempreAbierto.setVisibility(View.GONE);

            txtLunes.setVisibility(View.VISIBLE);
            txtMartes.setVisibility(View.VISIBLE);
            txtMiercoles.setVisibility(View.VISIBLE);
            txtJueves.setVisibility(View.VISIBLE);
            txtViernes.setVisibility(View.VISIBLE);
            txtSabado.setVisibility(View.VISIBLE);
            txtDomingo.setVisibility(View.VISIBLE);
        }
    }

    public ExpansionLayout getExpansionLayout() {
        return expansionLayout;
    }
}
