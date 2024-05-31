package com.example.dam_m13_act4_grupo4.Veterinario;

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
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dam_m13_act4_grupo4.POJO.Cita;
import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InsertarCitaVeterinario extends AppCompatActivity {

    private EditText motivo;
    private EditText fecha;
    private Button insertar;
    private ImageButton volver;
    private Spinner nombre;
    private String idDueno;
    private final ArrayList<Mascota> mascotas = new ArrayList<>();
    private final List<Integer> ids = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insertar_cita_veterinario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Enlazamos las variables con los elementos del layout
        motivo = findViewById(R.id.editTextMotivoInsertarConsulta);
        nombre = findViewById(R.id.spinnerNombreMascota);
        fecha = findViewById(R.id.editTextFechaInsertarConsulta);
        insertar = findViewById(R.id.buttonInsertarConsulta);
        volver = findViewById(R.id.ButtonVolverInsertarConsulta);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            idDueno = extras.getString("user");
        }

        new InsertarCitaVeterinario.ObtenerMascotasTask().execute(idDueno);

        insertar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //En caso de que no esten todos los datos rellenos, no se enviaran los datos y saltará un mensaje.
                if (motivo.getText().toString().isEmpty() || fecha.getText().toString().isEmpty())
                {
                    Toast.makeText(InsertarCitaVeterinario.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Lo mismo pasará si la fecha no tiene el formato correcto
                String fechaString = fecha.getText().toString();
                if (!fechaString.matches("\\d{4}-\\d{2}-\\d{2}"))
                {
                    Toast.makeText(InsertarCitaVeterinario.this, "La fecha debe tener formato YYYY-mm-dd", Toast.LENGTH_SHORT).show();
                    return;
                }

                //region Obtenemos los datos de la fecha actual
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String fechaActual = format.format(calendar.getTime());
                //endregion

                //region Obtenemos los datos de la fecha introducida
                String newFecha = fecha.getText().toString();
                //endregion

                //region Si la fecha introducida no es mayor a la fecha actrual, no se podrá agregar la cita.
                if (newFecha.compareTo(fechaActual) <= 0)
                {
                    Toast.makeText(InsertarCitaVeterinario.this, "La fecha debe ser mayor que la fecha actual", Toast.LENGTH_SHORT).show();
                    return;
                }
                //endregion

                //region Si los datos son correctos, se guardarán en nuevas variables para, posteriormente, guardarlos en un objeto Consultas.
                String newMotivo = motivo.getText().toString();
                int idMascotaSeleccionada = getIdMascotaSeleccionada();
                Mascota mascota = new Mascota(idMascotaSeleccionada);
                Cita c = new Cita(0, newMotivo, mascota, newFecha);
                //endregion

                //region Ejecutamos la clase encargada de enviar los datos a la BD.
                new InsertarConsultaTask().execute(c);
                //endregion
            }
        });

        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(InsertarCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //region Método para obtener el ID de la mascota seleccionada
    private int getIdMascotaSeleccionada()
    {
        int position = nombre.getSelectedItemPosition();
        return ids.get(position);
    }
    //endregion

    //region Clase encargada de insertar los datos de la consulta en la BD a través de un .php
    private class InsertarConsultaTask extends AsyncTask<Cita, String, String>
    {
        @Override
        protected String doInBackground(Cita... consultas)
        {
            String mensaje = "";

            try
            {
                //region Escribimos la dirección del .php
                String url = "http://192.168.1.143/ControlPaw/insertarConsulta.php"; //Sustituye por tu IPv4
                Log.d("Motivo", consultas[0].getMotivo());
                Log.d("ID Mascota", String.valueOf(consultas[0].getMascota().getId()));
                //endregion

                //region Guardamos los parametros
                String parametros = "&motivo=" + consultas[0].getMotivo() +
                        "&idMascota=" + consultas[0].getMascota().getId() +
                        "&fecha=" + consultas[0].getFecha();
                //endregion

                //region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Enviamos los datos a la BD
                OutputStream outputStream = conexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                outputStream.close();
                //endregion

                //region Leemos la respuesta de la BD
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion

                //region Cerramo la conexión
                entrada.close();
                conexion.disconnect();
                //endregion

                //region Comprobamos la respuesta de la DB para no duplicar la ID de la consulta y comprobar que que la ID de la mascota sea correcta
                if (respuesta.toString().contains("Error: La mascota con el ID proporcionado no existe") || respuesta.toString().contains("Error: Ya existe una consulta con la ID introducida"))
                {
                    mensaje = respuesta.toString();
                }
                else
                {
                    mensaje = "";
                }
                //endregion
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mensaje = "Error " + e.getMessage();
            }
            return mensaje;
        }

        //region Tras la ejecución
        @Override
        protected void onPostExecute(String mensaje)
        {
            super.onPostExecute(mensaje);

            if (mensaje.isEmpty())
            {
                Toast.makeText(InsertarCitaVeterinario.this, "DATOS INSERTADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InsertarCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(InsertarCitaVeterinario.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
        //endregion
    }
    //endregion

    //region Clase encargada de obtener los datos de las mascotas en la DB a través de un PHP
    private class ObtenerMascotasTask extends AsyncTask<String, Void, ArrayList<Mascota>>
    {
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(String... dueno)
        {
            String url = "http://192.168.1.143/controlpaw/saludMascotasVeterinario.php"; // Sustituye por tu IPv4

            try
            {
                //region Creamos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Leemos todas las líneas de la respuesta de la DB
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion

                //region Metemos los elementos en un Document
                Document document = Global.convertirStringToXMLDocument(respuesta.toString());
                NodeList listaMascotas = document.getElementsByTagName("mascota");
                //endregion

                for (int i = 0; i < listaMascotas.getLength(); i++)
                //region Extraemos los datos de cada mascota
                {
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

                    //region Creamos un objeto Mascota con los datos obtenidos
                    Mascota m = new Mascota(id, idPropietario, idEspecie, raza, nombre, idGenero, microchip, castrado, enfermedad, baja, peso, fecha);
                    mascotasList.add(m);
                    //endregion
                }
                //endregion

                //region Cerramos la conexión
                entrada.close();
                conexion.disconnect();
                //endregion
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return mascotasList;
        }

        //region Tras la ejecución
        @Override
        protected void onPostExecute(ArrayList<Mascota> mascotasList)
        {
            super.onPostExecute(mascotasList);
            if (mascotasList != null && !mascotasList.isEmpty())
            {
                mascotas.clear();
                mascotas.addAll(mascotasList);

                //region Creamos una lista y almacenamos los nombres e IDs de las mascotas
                List<String> nombres = new ArrayList<>();
                for (Mascota mascota : mascotasList)
                {
                    nombres.add(mascota.getNombre());
                    ids.add(mascota.getId());
                }
                //region Creamos y asignamos el adaptador para el spinner
                adapter = new ArrayAdapter<>(InsertarCitaVeterinario.this, android.R.layout.simple_spinner_item, nombres);
                nombre.setAdapter(adapter);
                //endregion
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No se encontraron mascotas", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion
    }
    //endregion
}
