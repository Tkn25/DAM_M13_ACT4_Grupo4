package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.POJO.Seguimiento;
import com.example.dam_m13_act4_grupo4.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeguimientoCliente extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private Spinner nombre;
    private EditText descripcion, fecha, enlace;
    private Button enviar;
    private final ArrayList<Mascota> mascotas = new ArrayList<>();
    private final List<Integer> ids = new ArrayList<>();
    private int idFinal;
    private String idDueno;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimiento_cliente);

        //Asociamos las variables con sus elementos del layout
        volver = findViewById(R.id.imageButton15);
        nombre = findViewById(R.id.spinnerNombre);
        descripcion = findViewById(R.id.editText2);
        fecha = findViewById(R.id.editTextDate);
        enlace = findViewById(R.id.editText3);
        enviar = findViewById(R.id.button);

        //Obtenemos la ID del usuario cliente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idDueno = extras.getString("user");
        }

        //Obtenemos la lista de mascotas del usuario cliente
        new ObtenerMascotasTask().execute(idDueno);

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeguimientoCliente.this, SaludCliente.class);
                startActivity(intent);
                finish();
            }
        });

        //En caso de pulsar el botón enviar, se intentará enviar los nuevos datos de seguimiento a la BBDD
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //En caso de que no esten todos los datos rellenos, no se enviaran los datos y saltará un mensaje.
                if (descripcion.getText().toString().isEmpty() || fecha.getText().toString().isEmpty() || enlace.getText().toString().isEmpty()) {
                    Toast.makeText(SeguimientoCliente.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Lo mismo pasará si la fecha no tiene el formato correcto
                String fechaString = fecha.getText().toString();
                if (!fechaString.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    Toast.makeText(SeguimientoCliente.this, "La fecha debe tener formato YYYY-mm-dd", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Almacenamos los datos para posteriormente crear un objeto Seguimiento
                int posicionSeleccionada = nombre.getSelectedItemPosition();
                idFinal = ids.get(posicionSeleccionada);
                String descripcionSeguimiento = descripcion.getText().toString();
                String imagen = enlace.getText().toString();

                Seguimiento s = new Seguimiento(0, idFinal, descripcionSeguimiento, imagen, fechaString);
                new InsertarSeguimientoTask().execute(s);
            }
        });

    }

    //Clase encargada de insertar los datos del nuevo seguimiento en la BBDD a través de un .php
    private class InsertarSeguimientoTask extends AsyncTask<Seguimiento, String, String> {
        @Override
        protected String doInBackground(Seguimiento... seguimiento) {
            String mensaje = "";

            try {
                //Escribimos la dirección del .php
                String url = "http://192.168.0.14/controlpaw/seguimientoCliente.php"; //Sustituye por tu IPv4

                //Guardamos los parametros
                String parametros = "&idMascota=" + seguimiento[0].getIdMascota() +
                        "&descripcion=" + seguimiento[0].getDescripcion() +
                        "&fecha=" + seguimiento[0].getFecha() +
                        "&img=" + seguimiento[0].getImagen();

                //Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                //Enviamos los datos a la BD
                OutputStream outputStream = conexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                outputStream.close();

                //Leemos la respuesta de la BD
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();

                //Comprobamos la respuesta de la BD para comprobar que no hay errores
                if (respuesta.toString().contains("Error al insertar datos de seguimiento")) {
                    //Almacenamos el error
                    mensaje = respuesta.toString();
                } else {
                    mensaje = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mensaje;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(String mensaje) {
            super.onPostExecute(mensaje);

            if (mensaje.isEmpty()) {
                //Si no hay mensaje de error, indicamos que salio correctamente y volvemos a la actividad anterior.
                Toast.makeText(SeguimientoCliente.this, "DATOS INSERTADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SeguimientoCliente.this, SaludCliente.class);
                startActivity(intent);
                finish();
            } else {
                //Si ocurre algo, mostramos el error
                Toast.makeText(SeguimientoCliente.this, "Ha ocurrido un error al insertar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase encargada de obtener los datos de las mascotas en la BBDD a través de un .php
    private class ObtenerMascotasTask extends AsyncTask<String, Void, ArrayList<Mascota>> {
        //Creamos el array donde almacenaremos todos los datos de las mascotas
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(String... dueno) {
            //Ponemos la dirección del .php
            String url = "http://192.168.0.14/controlpaw/mascotasCliente.php"; //Sustituye por tu IPv4

            try {
                //Creamos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                //Enviamos la id del dueño como parámetro
                String parametros = "dueno=" + dueno[0];
                conexion.getOutputStream().write(parametros.getBytes());

                //Leemos la respuesta de la BD hasta que no haya mas lineas para leer.
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                //Convertimos los datos recibidos en un Document
                Document document = Global.convertirStringToXMLDocument(respuesta.toString());
                //Obtenemos los elementos de cada mascota
                NodeList listaMascotas = document.getElementsByTagName("mascota");
                //Con este bucle conseguimos los datos de cada mascota
                for (int i = 0; i < listaMascotas.getLength(); i++) {
                    Element element = (Element) listaMascotas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    int idPropietario = Integer.parseInt(element.getElementsByTagName("idDueno").item(0).getTextContent());
                    int idEspecie = Integer.parseInt(element.getElementsByTagName("idEspecie").item(0).getTextContent());
                    String raza = element.getElementsByTagName("raza").item(0).getTextContent();
                    String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                    int idGenero = Integer.parseInt(element.getElementsByTagName("idGenero").item(0).getTextContent());
                    String microchip = element.getElementsByTagName("microchip").item(0).getTextContent();
                    int castrado = Integer.parseInt(element.getElementsByTagName("castrado").item(0).getTextContent());
                    boolean enfermedad = Boolean.parseBoolean(element.getElementsByTagName("enfermedad").item(0).getTextContent());
                    boolean baja = Boolean.parseBoolean(element.getElementsByTagName("baja").item(0).getTextContent());
                    float peso = Float.parseFloat(element.getElementsByTagName("peso").item(0).getTextContent());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaNacimiento = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaNacimiento);
                    //Creamos un objeto Mascota con los datos obtenidos
                    Mascota m = new Mascota(id, idPropietario, idEspecie, raza, nombre, idGenero, microchip, castrado, enfermedad, baja, peso, fecha);
                    mascotasList.add(m);
                }

                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mascotasList;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(ArrayList<Mascota> mascotasList) {
            super.onPostExecute(mascotasList);
            if (mascotasList != null && !mascotasList.isEmpty()) {
                //Limpiamos la lista para evitar errores
                mascotas.clear();
                //Agregamos las mascotas encontradas a la lista de mascotas
                mascotas.addAll(mascotasList);

                //Creamos una lista con el nombre de las mascotas
                List<String> nombres = new ArrayList<>();
                for (Mascota mascota : mascotasList) {
                    //Almacenamos los nombres en la lista
                    nombres.add(mascota.getNombre());
                    //Almacenamos las ID's en la lista de ID's
                    ids.add(mascota.getId());
                }
                //Creamos el adaptador para el spinner seleccionando la lista de nombres
                adapter = new ArrayAdapter<>(SeguimientoCliente.this, android.R.layout.simple_spinner_item, nombres);
                //Asignamos el adaptador al spinner.
                nombre.setAdapter(adapter);
            } else {
                //En caso de no encontrarse mascotas asociadas al usuario saltará un mensaje
                Toast.makeText(getApplicationContext(), "No se encontraron mascotas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}