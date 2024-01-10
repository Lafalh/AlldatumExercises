package org.alldatum.excercise1;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
*
*
*CÓDIGO DEL PRIMER EJERCICIO CON COMENTARIOS DETALLADOS
*
*
* */

public class URLConnect {

    public static void main(String[] args) throws Exception {//FUNCIÓN PRINCIPAL, Aquí ejecutamos todo el programa

        long total_sum = 0;                                                                         // Declaramos la Variable para la Suma Total de Scores.
        List<String> list = new ArrayList<String>();                                                //Declaramos una lista de strings para almacenar los datos del JSON
        ArrayList<ClientDataLC> data_list = new ArrayList<>();                                        //Declaramos un Array con nuestro Objeto que va a almacenar nombre, no. de lista, valor del nombre y score del nombre
        JSONArray name = readJsonFromUrl().getJSONArray("Values");                             // Declaramos un JSONArray u llamamos a la función que nos va a dar los datos obtenidos del servicio web (ver linea 54)

        for(int i = 0;i < name.length();i++){                                                       // LOOP que almacena los datos del JSONArray en la lista
            list.add(name.getJSONObject(i).getString("NAME"));
        }

        Collections.sort(list);                                                                     //Llamamos a la función sort() de la Clase Collections que va a ordenar alfabéticamente nuestra lista

        for(int i = 0;i < name.length();i++){                                                       //Iniciamos el LOOP que va a almacenar los datos en el Array de objeto ClientData

            data_list.add(new ClientDataLC(list.get(i),i+1,                                 //Por cada iteración, añadimos 4 valores a data_list siendo el primero el nombre
                    AlphabeticScore(list.get(i).toString()),                                        //el segundo es el número de lista, el tercero es el valor del nombre (denominado como "Score Alphabetic")
                    (long)AlphabeticScore(list.get(i))*(i+1)));                                     //y por último el Score del Nombre.

            total_sum = (total_sum + data_list.get(i).name_score);                                  //Realizamos la suma de TODOS los SCORES de los nombres y lo almacenamos en total_sum

            System.out.println("Name: "+data_list.get(i).name
             +" Sort Alphabetic: " + data_list.get(i).alph_sort                                   //FUNCIONES PRINT MERAMENTE PARA DEBUG O VISUALIZACIÓN
             + " Score Alphabetic: " + data_list.get(i).alph_score
             + " Name Score: " + data_list.get(i).name_score);


        }

        System.out.println("Total Score: "+total_sum);                                            //FUNCIONES PRINT MERAMENTE PARA DEBUG O VISUALIZACIÓN

        SendScore(total_sum);                                                                       //Enviamos el resultado final a la función SendScore(). (ver linea 93)

    }

    public static JSONObject readJsonFromUrl() throws IOException, JSONException {                  //FUNCIÓN encargada de conectarse a la URL y obtener los datos

        URL url = new URL("https://elastic.snaplogic.com/api/1/rest" +                        //Declaramos una Variable URL y se define a que URL hay que conectarse
                "/slsched/feed/Partners/AllDatum/Entrevista_Integracion/Lee" +                      //especificando los parámetros necesarios
                "ArchivoNombresTask?archivo=first_names&extension=txt");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();                          //Se abre la conexión con el servidor
        conn.setRequestProperty("Authorization","Bearer "+"h8JLQvfj5Yl1iQeOvBT43d17RoDBO6UQ");      //Agregamos el token de autorización
        conn.setRequestProperty("Content-Type","application/json");                                 //Agregamos el tipo de contenido
        conn.setRequestMethod("GET");                                                               //Especificamos que método de solicitud necesitamos, siendo GET en este caso


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));   //Se almacena el Stream de datos en reader
            String jsonText = "{\"Values\":" + readAll(reader) + "}";                                     //Definimos la Cadena jsonText de forma que podamos convertirlo a un JSONObject.
            //se lee todo el stream de datos y se almacena en una cadena en formato reconocible por JSON.

            JSONObject json = new JSONObject(jsonText);                                                 //Convertimos la cadena a un objeto JSON y lo regresamos a la función Main() en la línea 26
            reader.close();
            return json;




    }
    private static String readAll(Reader reader) throws IOException {                               //FUNCIÓN encargada de leer todo el Stream de datos obtenido de la URL
        StringBuilder builder = new StringBuilder();
        int i;
        while ((i = reader.read()) != -1) {                                                         //Usando un StringBuilder vamos a ir adjuntando los datos recibidos en un solo String
            builder.append((char) i);
        }
        return builder.toString();                                                                  //Regresamos el String final a la línea 68
    }

    public static int AlphabeticScore(String name){                                                 //FUNCIÓN encargada de calcular el Valor numérico del nombre
        int sum = 0;                                                                                //inicializamos una variable int para la suma
        for(int s = 0; s< name.length();s++ ){
            sum = sum + Alphabet.getNum(name.charAt(s));                                            //con un Loop, recorremos toda la cadena ingresada y vamos sumando el valor de cada letra
                                                                                                    //utilizando getNum() de Enum Alphabet que contiene un listado de letras y su valor numérico
        }
        return sum;                                                                                 //Regresamos la suma final a las líneas 37 y 38
    }

    public static void SendScore(long final_score){                                                        //FUNCIÓN encargada de regresar el resultado final al URL Target
        HttpClient httpclient = HttpClientBuilder.create().build();                                        //Creamos un Cliente HTTP
        try {
            HttpPost request = new HttpPost("https://elastic.snaplogic.com" +                         //Declaramos una Variable URL y se define a que URL hay que conectarse
                    "/api/1/rest/slsched/feed/Partners/AllDatum/Entrevista_Integracion" +                  //especificando los parámetros necesarios
                    "/VerificaResultadoEjercicioTecnico1ALLDATUMTask?" +
                    "archivo=first_names&extension=txt&nombre=EduardoOlafAdamedelaRosa&prueba=0");

            StringEntity params = new StringEntity("{\"ResultadoObtenido\":"+final_score+"}");             //Declaramos el parámetro que se va a enviar en un formato JSON con el Score Final
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", "Bearer giqJWNuzhOnDTYaa1Diy1jw7FQhqZSwl");    //Agregamos los Headers de autorización y contenido a la solicitud de conexión
            request.setEntity(params);
            HttpResponse response = httpclient.execute(request);                                           //Se ejecuta la solicitud
            
            System.out.println("RESULT: "+response.toString());                                            //Se imprime la respuesta del servidor para comprobar el resultado
        } catch (Exception ex) {
        } finally {
        }
    }
    public enum Alphabet {                                                                          //ENUMERACIÓN del Alfabeto que contiene los valores de cada letra
        A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;
        public static int getNum(char targ) {                                                       //FUNCIÓN que regresa el valor numérico de la letra especificada
            return valueOf(String.valueOf(targ)).ordinal()+1;
        }
    }

}

class ClientData {                                                                                  //CLASE personalizada que contiene los nombres, números de lista
                                                                                                    //valor numérico de nombre y Score de nombre
    String name;
    int alph_sort;
    int alph_score;
    long name_score;

    public ClientData(String name, int alph_sort, int alph_score, long name_score) {                //CONSTRUCTOR de la clase
        this.name = name;
        this.alph_sort = alph_sort;
        this.alph_score = alph_score;
        this.name_score = name_score;
    }
}