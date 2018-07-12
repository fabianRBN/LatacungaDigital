package com.example.jona.latacungadigital.Activities.Clases;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.model.AIOutputContext;
import ai.api.model.Result;

public class WeatherClass {

    private RequestQueue queue;
    private String resultCurrentWeather;
    private Result result;
    private DialogflowClass dialogflowClass;
    private boolean isWeatherFoundInformation = true; // Para controlar si se encontro la informacion del clima que pide el usuario.
    private double degrees;
    private boolean futureTime;
    private String currentDate;

    WeatherClass() { }

    public void setVariables(Context context, DialogflowClass dialogflowClass, Result result) {
        this.dialogflowClass = dialogflowClass;
        queue = Volley.newRequestQueue(context.getApplicationContext());
        this.result = result;

        // Se obtiene la fecha del día actual.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = dateFormat.format(new Date());
    }

    public void WeatherResponse() {
        // Enviamos una peticion a la API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrlAPIXU(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!getDateFromDialogflow().equals("")) {
                    if (getDateFromDialogflow().equals(currentDate)) {
                        dialogflowClass.MessageSendToDialogflow(currentWeather(response)); // Enviamos el clima del dia actual.
                    } else {
                        dialogflowClass.MessageSendToDialogflow(forecastWeather(response)); // Enviamos el clima segun el dia que quiere saber el usuario.
                    }
                } else {
                    dialogflowClass.MessageSendToDialogflow(currentWeather(response)); // Enviamos el clima del dia actual.
                }
                if (result.getAction().equals("weatherAction") && isWeatherFoundInformation) {
                    // Preguntamos al usuario si desea saber que vestimenta llevar para su recorrido turistico.
                    dialogflowClass.MessageSendToDialogflow("¿Deseas saber que vestimenta o accesorios usar para tú recorrido turístico?");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogflowClass.MessageSendToDialogflow("Lo siento, ocurrio un error inesperado o el servidor del clima esta temporalmente caído, " +
                        "por el momento no puedo ofrecerle una respuesta a su pregunta. Por favor, intentelo más luego.");
            }
        });
        queue.add(jsonObjectRequest);
    }

    // Método para obtener el pronostico del clima actual.
    private String currentWeather(JSONObject response) {
        try {
            JSONObject jsonObjectLocation = response.getJSONObject("location");
            String locationName = jsonObjectLocation.getString("name"); // Nombre de la ciudad.

            JSONObject jsonObjectCurrent = response.getJSONObject("current");
            String humidity = jsonObjectCurrent.getInt("humidity") + "%"; // Porcentaje de la humedad.
            String degreesC = Math.round(jsonObjectCurrent.getDouble("temp_c")) + "°C";
            String degreesF = Math.round(jsonObjectCurrent.getDouble("temp_f")) + "°F";
            String windPerHour = jsonObjectCurrent.getString("wind_kph") + "km/h"; // Velocidad del viento en kilómetros por hora.
            String currentCondition = jsonObjectCurrent.getJSONObject("condition").getString("text"); // Condición en la que se encuentra el clima.

            this.degrees = Math.round(jsonObjectCurrent.getDouble("temp_c"));
            this.futureTime = false;

            // Enviamos la respuesta con los datos obtenidos de la API del clima.
            resultCurrentWeather = "La ciudad de " + locationName + " se encuentra " + currentCondition + " con una humedad del "
                    + humidity + ". Además, la velocidad del viento ésta a " + windPerHour + " con una temperatura de "
                    + degreesC + " o " + degreesF + ".";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultCurrentWeather;
    }

    // Método para dar el pronostico de los dias que pida el usuario y este esta limitado como maximo hasta 6 dias a partir del dia actual.
    private String forecastWeather(JSONObject response) {
        try {
            JSONObject jsonObjectLocation = response.getJSONObject("location");
            String locationName = jsonObjectLocation.getString("name"); // Nombre de la ciudad.

            JSONObject jsonObjectForecast = response.getJSONObject("forecast");
            JSONArray forecastday = jsonObjectForecast.getJSONArray("forecastday");

            if (forecastday.length() != 0) {
                JSONObject dayForecast = forecastday.getJSONObject(0);

                String date = dayForecast.getString("date"); // Se obtiene la fecha del dia requerido por el usuario.
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale ( "es" , "ES" ));
                Date formartDate = null;

                try {
                    formartDate = dateFormat.parse(date); // Convertimos de String a Date.
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Se guarda la fecha obtenida de la API del clima y se pone un nuevo formato para mejor entendimiento del usuario.
                String dateConvert = DateFormat.getDateInstance(DateFormat.FULL).format(formartDate);

                JSONObject jsonObjectDaysForecast = dayForecast.getJSONObject("day");
                String humidity = jsonObjectDaysForecast.getInt("avghumidity") + "%"; // Porcentaje de la humedad.
                String degreesC = Math.round(jsonObjectDaysForecast.getDouble("maxtemp_c")) + "°C";
                String degreesF = Math.round(jsonObjectDaysForecast.getDouble("maxtemp_f")) + "°F";
                String windPerHour = jsonObjectDaysForecast.getString("maxwind_kph") + "km/h"; // Velocidad del viento en kilómetros por hora.
                String currentCondition = jsonObjectDaysForecast.getJSONObject("condition").getString("text"); // Condición en la que se encuentra el clima.

                this.degrees = Math.round(jsonObjectDaysForecast.getDouble("maxtemp_c"));
                this.futureTime = true;

                // Enviamos la respuesta con los datos obtenidos de la API del clima.
                resultCurrentWeather = "La ciudad de " + locationName + " en el día " + dateConvert + ", se encontrará con " + currentCondition +
                        ", con una humedad promedio del " + humidity + ". Además, la velocidad máxima del viento éstará a " + windPerHour +
                        " con una temperatura máxima de " + degreesC + " o " + degreesF + ".";

                isWeatherFoundInformation = true; // Para controlar si se encontro la informacion del clima.
            } else {
                // Enviamos una respuesta de que no se encontro nada.
                resultCurrentWeather = "Lo siento, no he podido encontrar nada sobre la fecha solicitada por usted. "
                        + " Mi límite máximo para dar un pronóstico del clima es de 16 días a partir del día actual.";

                isWeatherFoundInformation = false; // Para controlar si no se encontro nada de informacion del clima.
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultCurrentWeather;
    }

    // Método para obtener la fecha de Dialogflow.
    private String getDateFromDialogflow() {
        String parameterNumberDays = "";

        List<AIOutputContext> contexts = result.getContexts(); // Creamos una lista de contextos obtenidos de Dialogflow.
        if (contexts != null && !contexts.isEmpty()) {
            for (AIOutputContext contextAux : contexts) { // Recorremos la lista de contextos.
                Map<String, JsonElement> params = contextAux.getParameters();
                if (params != null && !params.isEmpty()) {
                    parameterNumberDays = params.get("date").toString().replace("\"", "");
                }
            }
        }
        return parameterNumberDays; // Retornamos la fecha consultado de Dialogflow.
    }

    // Obtener la url para la api basado en la fecha
    private String getUrlAPIXU(){
        String urlCurrentWeather = "https://api.apixu.com/v1/current.json?key=" + ChatBotReferences.APIXU_API_CLIENT + "&q=Latacunga&lang=es";
        String urlForecast = "https://api.apixu.com/v1/forecast.json?key=" + ChatBotReferences.APIXU_API_CLIENT +
                "&q=Latacunga&lang=es&dt=" + getDateFromDialogflow();

        if (!getDateFromDialogflow().equals("")) {
            if (getDateFromDialogflow().equals(currentDate)) {
                return urlCurrentWeather;
            } else {
                return urlForecast;
            }
        } else {
            return urlCurrentWeather;
        }
    }

    // Método para determinar el tipo de clima de la ciudad.
    public String recommendAccordingWeather() {
        String recommendation = "", toBe, uses, recommendationTime;

        if (!futureTime) {
            toBe = "está";
            uses = "usar";
            recommendationTime = "recomiendo usar";
        } else {
            toBe = "estará";
            uses = "puedes usar";
            recommendationTime = "recomiendo que uses";
        }

        // Los numeros estan determinados por °C.
        if (degrees < 7) { // Clima Helado
            recommendation = "Según mi pronóstico el clima " + toBe + " helado, por lo tanto, te " + recommendationTime + " pantalones gruesos que " +
                    "cubran los tobillos, trata de cubrirte el cuello, las manos y la cabeza con accesorios como gorros de lana, guantes o bufandas. " +
                    "Además, " + uses + " calcetines abrigados con calzado alto, chompa muy abrigada.";
        } else if (degrees >= 7 && degrees < 13) { // Clima Frio
            recommendation = "Según mi pronóstico el clima " + toBe + " frío, por lo tanto, te " + recommendationTime + " pantalón largo de jean o de tela " +
                    "media gruesa, suéter con cuello o chompa con una camiseta por dentro, calcetines gruesos con calzado abrigado.";
        } else if (degrees >= 13 && degrees < 18) { // Clima Fresco
            recommendation = "Según mi pronóstico el clima " + toBe + " fresco, por lo tanto, te " + recommendationTime + " pantalón jean o de tela, " +
                    "suéter con una camiseta liviana, calcetines ligeros y calzado cómodo.";
        } else if (degrees >= 18 && degrees < 24) { // Clima Comodo
            recommendation = "Según mi pronóstico el clima " + toBe + " cómodo, por lo tanto, te " + recommendationTime + " pantalones ligeros, " +
                    "chaqueta liviana de manga larga, camiseta manga corta, calcetines ligeros y zapatos cómodos.";
        } else if (degrees >= 24) { // Clima Caliente
            recommendation = "Según mi pronóstico el clima " + toBe + " caliente, por lo tanto, te " + recommendationTime + " pantalones ligeros o " +
                    "si esposible " + uses + " shorts, camiseta manga corta, gafas o lentes de sol, calzado abierto o semicerrado que deje respirar tus pies.";
        }
        return recommendation;
    }
}