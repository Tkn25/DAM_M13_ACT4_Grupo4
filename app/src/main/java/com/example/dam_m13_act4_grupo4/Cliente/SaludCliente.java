package com.example.dam_m13_act4_grupo4.Cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.POJO.Tratamiento;
import com.example.dam_m13_act4_grupo4.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaludCliente extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private RecyclerView lista;
    private Button seguimiento;
    private static String idDueno;
    private final ArrayList<Tratamiento> tratamientos = new ArrayList<>();
    private final ArrayList<Mascota> mascotasCliente = new ArrayList<>();
    private AdaptadorTratamientos adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salud_cliente);

        //Asociamos las variables con sus elementos del layout
        volver = findViewById(R.id.imageButton14);
        lista = findViewById(R.id.recyclerViewTratamientos);
        seguimiento = findViewById(R.id.buttonNew);

        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new AdaptadorTratamientos(tratamientos);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idDueno = extras.getString("user");
        }

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaludCliente.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });

        //En caso de pulsar el botón seguimiento, se abrirá la actividad para añadir un nuevo seguimiento
        seguimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaludCliente.this, SeguimientoCliente.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
            }
        });
        //Obtenemos la lista de mascotas según el cliente
        new ObtenerMascotasClienteTask().execute(idDueno);
    }

    private class ObtenerMascotasClienteTask extends AsyncTask<String, Void, ArrayList<Mascota>> {
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
                    int idDueno = Integer.parseInt(element.getElementsByTagName("idDueno").item(0).getTextContent());
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
                    Mascota m = new Mascota(id, idDueno, idEspecie, raza, nombre, idGenero, microchip, castrado, enfermedad, baja, peso, fecha);
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

            //Actualizamos la interfaz
            if (mascotasList != null && !mascotasList.isEmpty()) {
                //Limpiamos la lista actual
                mascotasCliente.clear();
                //Agregamos las nuevas mascotas a la lista
                mascotasCliente.addAll(mascotasList);
                //Notificamos al adaptador los cambios
                adaptador.notifyDataSetChanged();
                //Ahora obtenemos la lista de tratamientos
                new ObtenerTratamientosTask().execute();
            } else {
                //Si hay algún error mostramos un mensaje por pantalla
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase encargada de obtener los datos de los tratamientos en la BBDD a través de un .php
    private class ObtenerTratamientosTask extends AsyncTask<Void, Void, ArrayList<Tratamiento>> {
        //Creamos el array donde almacenaremos todos los datos de los tratamientos
        ArrayList<Tratamiento> tratamientosList = new ArrayList<>();

        @Override
        protected ArrayList<Tratamiento> doInBackground(Void... Void) {
            //Ponemos la dirección del .php
            String url = "http://192.168.1.143/controlpaw/tratamientosCliente.php"; //Sustituye por tu IPv4

            try {
                //Creamos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);


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

                //Obtenemos los elementos de cada tratamiento
                NodeList listaConsultas = document.getElementsByTagName("tratamiento");
                //Con este bucle conseguimos los datos de cada tratamiento
                for (int i = 0; i < listaConsultas.getLength(); i++) {
                    Element element = (Element) listaConsultas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    int idMascota = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    String descripcion = element.getElementsByTagName("descripcion").item(0).getTextContent();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);
                    int finalizado = Integer.parseInt(element.getElementsByTagName("finalizado").item(0).getTextContent());
                    for (Mascota mascota : mascotasCliente) {
                        if (mascota.getId() == idMascota) {
                            // Creamos un objeto tratamiento con los datos obtenidos
                            Tratamiento t = new Tratamiento(id, mascota, descripcion, fecha, finalizado);
                            tratamientosList.add(t);
                            break;
                        }
                    }
                }
                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tratamientosList;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(ArrayList<Tratamiento> tratamientosList) {
            super.onPostExecute(tratamientosList);
            //Comprobamos que la lista no este vacía
            if (tratamientosList != null && !tratamientosList.isEmpty()) {
                //Limpiamos la lista actual para evitar errores
                tratamientos.clear();
                //Agregamos los tratamientos encontradas a la lista de tratamientos
                tratamientos.addAll(tratamientosList);
                //Notificamos al adaptador los cambios
                adaptador.notifyDataSetChanged();
            } else {
                //Si hay algun error mostramos un mensaje
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase que se encarga de crear el adaptador para la recycler con el objeto tratamiento
    private class AdaptadorTratamientos extends RecyclerView.Adapter<AdaptadorTratamientos.ViewHolder> {
        private final ArrayList<Tratamiento> tratamientos;

        public class ViewHolder extends RecyclerView.ViewHolder {
            //Creamos la variables con los elementos del layout de cada item
            private final TextView mascota;
            private final TextView descripcion;
            private final TextView fecha;

            public ViewHolder(View view) {
                super(view);
                //Enlazamos las variables con el layout de cada item
                mascota = view.findViewById(R.id.textViewMascotaTratamiento);
                descripcion = view.findViewById(R.id.textViewDescripcionTratamiento);
                fecha = view.findViewById(R.id.textViewFechaTratamiento);
            }
        }

        public AdaptadorTratamientos(ArrayList<Tratamiento> tratamientos) {
            this.tratamientos = tratamientos;
        }

        @NonNull
        @Override
        public AdaptadorTratamientos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tratamientos_layout, viewGroup, false);
            return new AdaptadorTratamientos.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorTratamientos.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Asignamos el texto con el valor de los tratamientos a los campos
            Tratamiento tratamiento = tratamientos.get(position);
            holder.mascota.setText(tratamiento.getMascota().getNombre());
            holder.descripcion.setText(String.valueOf(tratamiento.getDescripcion()));
            holder.fecha.setText(String.valueOf(tratamiento.getFecha()));
        }

        @Override
        public int getItemCount() {
            return tratamientos.size();
        }
    }
}