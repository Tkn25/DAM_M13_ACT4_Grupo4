package com.example.dam_m13_act4_grupo4.Veterinario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class SeguimientoVeterinario extends AppCompatActivity
{
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimiento_veterinario);

        volver = findViewById(R.id.imageButton15);
        nombre = findViewById(R.id.spinnerNombre);
        descripcion = findViewById(R.id.editText2);
        fecha = findViewById(R.id.editTextDate);
        enlace = findViewById(R.id.editText3);
        enviar = findViewById(R.id.button);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            idDueno = extras.getString("user");
        }

        new SeguimientoVeterinario.ObtenerMascotasTask().execute(idDueno);

        //region Listener del botón para volver a la lista de tratamientos
        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SeguimientoVeterinario.this, SaludVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Listener del botón para enviar el nuevo seguimiento
        enviar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (descripcion.getText().toString().isEmpty() || fecha.getText().toString().isEmpty())
                //region En caso de haber campos vacios
                {
                    Toast.makeText(SeguimientoVeterinario.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //endregion

                String fechaString = fecha.getText().toString();
                if (!fechaString.matches("\\d{4}-\\d{2}-\\d{2}"))
                //region En caso de formato de fecha incorrecto
                {
                    Toast.makeText(SeguimientoVeterinario.this, "La fecha debe tener formato YYYY-mm-dd", Toast.LENGTH_SHORT).show();
                    return;
                }
                //endregion

                //region Almacenamos los datos y los introducimos en un objeto Seguimiento
                int posicionSeleccionada = nombre.getSelectedItemPosition();
                idFinal = ids.get(posicionSeleccionada);
                String descripcionSeguimiento = descripcion.getText().toString();
                String imagen = enlace.getText().toString();

                Seguimiento s = new Seguimiento(0, idFinal, descripcionSeguimiento, imagen, fechaString);
                new SeguimientoVeterinario.InsertarSeguimientoTask().execute(s);
                //endregion
            }
        });
        //endregion

    }

    //region Clase encargada de insertar los datos del nuevo seguimiento en la DB a través de un PHP
    private class InsertarSeguimientoTask extends AsyncTask<Seguimiento, String, String>
    {
        @Override
        protected String doInBackground(Seguimiento... seguimiento)
        {
            String mensaje = "";

            try
            {
                String url = "http://192.168.1.143/controlpaw/seguimientoVeterinario.php"; // Sustituye por tu IPv4

                //region Guardamos los parámetros
                String parametros = "&idMascota=" + seguimiento[0].getIdMascota() +
                        "&descripcion=" + seguimiento[0].getDescripcion() +
                        "&fecha=" + seguimiento[0].getFecha() +
                        "&img=" + seguimiento[0].getImagen();
                //endregion

                //region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Enviamos los datos al PHP
                OutputStream outputStream = conexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                outputStream.close();
                //endregion

                //regionLeemos la respuesta del PHP
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion

                //region Cerramos la conexión
                entrada.close();
                conexion.disconnect();
                //endregion

                //region Comprobamos la respuesta de la BD para comprobar que no hay errores
                if (respuesta.toString().contains("Error al insertar datos de seguimiento"))
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
            }
            return mensaje;
        }

        //region Tras la ejecución
        @Override
        protected void onPostExecute(String mensaje)
        {
            super.onPostExecute(mensaje);

            if (mensaje.isEmpty())
            //region En caso de no haber mensajes de error, notificamos al usuario y volvemos a la lista de tratamientos
            {
                Toast.makeText(SeguimientoVeterinario.this, "DATOS INSERTADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SeguimientoVeterinario.this, SaludVeterinario.class);
                startActivity(intent);
                finish();
            }
            //endregion
            else
            //region Notificamos al usuario en caso de haber algún error al insertar los datos
            {
                Toast.makeText(SeguimientoVeterinario.this, "Ha ocurrido un error al insertar los datos", Toast.LENGTH_SHORT).show();
            }
            //endregion
        }
        //endregion
    }
    //endregion

    //region Clase encargada de obtener los datos de las mascotas en la DB a través de un PH
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
                adapter = new ArrayAdapter<>(SeguimientoVeterinario.this, android.R.layout.simple_spinner_item, nombres);
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