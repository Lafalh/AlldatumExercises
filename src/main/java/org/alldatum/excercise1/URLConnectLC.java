package org.alldatum.excercise1;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 *
 *
 *CÓDIGO DEL PRIMER EJERCICIO CON COMENTARIOS MÍNIMOS
 *
 *
 * */
public class URLConnectLC {

    public static void main(String[] args) throws Exception {//FUNCIÓN PRINCIPAL

        //Var para la Suma de Scores.
        long total_sum = 0;
        //lista de strings para el JSON
        List<String> list = new ArrayList<String>();
        //Array del objeto ClientData
        ArrayList<ClientDataLC> data_list = new ArrayList<>();

        // Declaramos un JSONArray y llamamos a la función que nos va a dar los datos obtenidos del servicio web
        JSONArray name = readJsonFromUrl().getJSONArray("Values");

        // LOOP que almacena los datos del JSONArray en la lista
        for(int i = 0;i < name.length();i++){
            list.add(name.getJSONObject(i).getString("NAME"));
        }

        //función sort(), para ordenar alfabéticamente nuestra lista
        Collections.sort(list);

        // LOOP que va a almacenar los datos en el Array de objeto ClientData
        for(int i = 0;i < name.length();i++){

            /*Por cada iteración, añadimos 4 valores a data_list: nombre
             número de lista, valor del nombre y el Score del Nombre.*/

            data_list.add(new ClientDataLC(list.get(i),i+1,
                    AlphabeticScore(list.get(i).toString()),
                    (long)AlphabeticScore(list.get(i))*(i+1)));

            //Realizamos la suma de TODOS los SCORES de los nombres y lo almacenamos en total_sum
            total_sum = (total_sum + data_list.get(i).name_score);

            /*FUNCIONES PRINT MERAMENTE PARA DEBUG O VISUALIZACIÓN
            System.out.println("Name: "+data_list.get(i).name
             +" Sort Alphabetic: " + data_list.get(i).alph_sort
             + " Score Alphabetic: " + data_list.get(i).alph_score
             + " Name Score: " + data_list.get(i).name_score);*/
        }
        //System.out.println("Total Score: "+total_sum);
        //Enviamos el resultado final a la función SendScore(). (ver linea 93)
        SendScore(total_sum);

    }
    //FUNCIÓN encargada de conectarse a la URL y obtener los datos
    public static JSONObject readJsonFromUrl() throws IOException, JSONException {

        /*Declaramos una Variable URL y se define a que URL hay que conectarse
        especificando los parámetros necesarios*/

        URL url = new URL("https://elastic.snaplogic.com/api/1/rest" +
                "/slsched/feed/Partners/AllDatum/Entrevista_Integracion/Lee" +
                "ArchivoNombresTask?archivo=first_names&extension=txt");

        /*Se abre la conexión y se definen los Headers, incluyendo el token
         de acceso y el método de solicitud*/
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization","Bearer "+"h8JLQvfj5Yl1iQeOvBT43d17RoDBO6UQ");
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestMethod("GET");

        //Leemos los datos y almacenamos en unsa String
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String jsonText = "{\"Values\":"+readAll(reader) + "}";

        //Convertimos el String a un objeto JSON y lo regresamos a la función Main() en la línea 26
        JSONObject json = new JSONObject(jsonText);
        return json;
    }
    //FUNCIÓN encargada de leer todo el Stream de datos obtenido de la URL
    private static String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int i;
        //Usando un StringBuilder vamos a ir adjuntando los datos recibidos en un solo String
        while ((i = reader.read()) != -1) {
            builder.append((char) i);
        }
        //Regresamos el String final a la línea 68
        return builder.toString();
    }
    //FUNCIÓN encargada de calcular el Valor numérico del nombre
    public static int AlphabeticScore(String name){

        //inicializamos una variable int para la suma
        int sum = 0;

        //con un Loop, vamos sumando el valor de cada letra
        for(int s = 0; s< name.length();s++ ){

            //con getNum() obtenemos el valor de cada letra
            sum = sum + Alphabet.getNum(name.charAt(s));

        }

        //Regresamos la suma final a las líneas 37 y 38
        return sum;
    }
    //FUNCIÓN encargada de regresar el resultado final al URL Target
    public static void SendScore(long final_score){
        //Creamos un Cliente HTTP
        HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            /*Declaramos un Http POST y se define a que URL hay que conectarse
                especificando los parámetros necesarios*/
            HttpPost request = new HttpPost("https://elastic.snaplogic.com" +
                    "/api/1/rest/slsched/feed/Partners/AllDatum/Entrevista_Integracion" +
                    "/VerificaResultadoEjercicioTecnico1ALLDATUMTask?" +
                    "archivo=first_names&extension=txt&nombre=EduardoOlafAdamedelaRosa&prueba=0");

            //Declaramos el parámetro que se va a enviar en un formato JSON con el Score Final
            StringEntity params = new StringEntity("{\"ResultadoObtenido\":"+final_score+"}");

            //Agregamos los Headers de autorización y contenido a la solicitud de conexión
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", "Bearer giqJWNuzhOnDTYaa1Diy1jw7FQhqZSwl");
            request.setEntity(params);

            //Se ejecuta la solicitud
            HttpResponse response = httpclient.execute(request);

            //Se imprime la respuesta del servidor para comprobar el resultado
            System.out.println("RESULT: "+response.toString());
        } catch (Exception ex) {
        }
    }

    //ENUMERACIÓN del Alfabeto que contiene los valores de cada letra
    public enum Alphabet {
        A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;

        //FUNCIÓN que regresa el valor numérico de la letra especificada
        public static int getNum(char targ) {
            return valueOf(String.valueOf(targ)).ordinal()+1;
        }
    }

}

class ClientDataLC {
    /*CLASE personalizada que contiene los nombres, números de lista
    valor numérico de nombre y Score de nombre*/
    String name;
    int alph_sort;
    int alph_score;
    long name_score;

    //CONSTRUCTOR de la clase
    public ClientDataLC(String name, int alph_sort, int alph_score, long name_score) {
        this.name = name;
        this.alph_sort = alph_sort;
        this.alph_score = alph_score;
        this.name_score = name_score;
    }
}