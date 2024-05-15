package com.example.dam_m13_act4_grupo4.Veterinario;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dam_m13_act4_grupo4.Cliente.CitasCliente;
import com.example.dam_m13_act4_grupo4.Cliente.PrincipalCliente;
import com.example.dam_m13_act4_grupo4.POJO.Cita;
import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CitasVeterinario extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private RecyclerView lista;
    private final ArrayList<Cita> citas = new ArrayList<>();
    private final ArrayList<Mascota> mascotasCliente = new ArrayList<>();
    private CitasVeterinario.AdaptadorCitas adaptador;
    private static String idDueno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citas_veterinario);

        //Asociamos las variables con sus elementos del layout
        volver = findViewById(R.id.imageButton13);
        lista = findViewById(R.id.recyclerCitas);

        //Obtenemos la ID del usuario cliente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idDueno = extras.getString("user");
        }

        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new CitasVeterinario.AdaptadorCitas(citas);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitasVeterinario.this, PrincipalVeterinario.class);
                startActivity(intent);
                finish();
            }
        });

        //Obtenemos las citas a partir del cliente
        new CitasVeterinario.ObtenerMascotasTask().execute(idDueno);

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
                //Ahora obtenemos las citas
                new CitasVeterinario.ObtenerCitasTask().execute();
            } else {
                //Si hay algún error mostramos un mensaje por pantalla
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase encargada de obtener los datos de las citas en la BBDD a través de un .php
    private class ObtenerCitasTask extends AsyncTask<Void, Void, ArrayList<Cita>> {
        //Creamos el array donde almacenaremos todos los datos de las citas
        ArrayList<Cita> citasList = new ArrayList<>();

        @Override
        protected ArrayList<Cita> doInBackground(Void... Void) {
            //Ponemos la dirección del .php
            String url = "http://192.168.0.14/controlpaw/citasVeterinario.php"; //Sustituye por tu IPv4

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

                //Obtenemos los elementos de cada consulta
                NodeList listaConsultas = document.getElementsByTagName("consulta");
                //Con este bucle conseguimos los datos de cada consulta
                for (int i = 0; i < listaConsultas.getLength(); i++) {
                    Element element = (Element) listaConsultas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String motivo = element.getElementsByTagName("titulo").item(0).getTextContent();
                    int idMascota = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);
                    //Recorremos las mascotas del usuario para solamente mostrar las citas asociadas a esas mascotas.
                    for (Mascota mascota : mascotasCliente) {
                        //Comprobamos que su ID coincida
                        if (mascota.getId() == idMascota) {
                            //Creamos un objeto cita con los datos obtenidos
                            Cita c = new Cita(id, motivo, mascota, fecha);
                            //Añadimos el objeto a la lista de citas
                            citasList.add(c);
                            break; //Salimos del bucle
                        }
                    }
                }
                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return citasList;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(ArrayList<Cita> citasList) {
            super.onPostExecute(citasList);
            //Comprobamos que se han recibido datos
            if (citasList != null && !citasList.isEmpty()) {
                //Limpiamos la lista para evitar errores
                citas.clear();
                //Agregamos las citas encontradas a la lista de citas
                citas.addAll(citasList);
                //Notificamos los cambios al adaptador
                adaptador.notifyDataSetChanged();
            } else {
                //Si hay algun error mostramos un mensaje
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase que se encarga de crear el adaptador para la recycler con el objeto citas
    private class AdaptadorCitas extends RecyclerView.Adapter<CitasVeterinario.AdaptadorCitas.ViewHolder> {
        private final ArrayList<Cita> citas;

        public class ViewHolder extends RecyclerView.ViewHolder {
            //Creamos la variables con los elementos del layout de cada item
            private final TextView mascota;
            private final TextView motivo;
            private final TextView fecha;

            public ViewHolder(View view) {
                super(view);
                //Enlazamos las variables con el layout de cada item
                mascota = view.findViewById(R.id.textViewMascotaCita);
                motivo = view.findViewById(R.id.textViewMotivoCita);
                fecha = view.findViewById(R.id.textViewFechaCita);
            }
        }

        public AdaptadorCitas(ArrayList<Cita> citas) {
            this.citas = citas;
        }

        @NonNull
        @Override
        public CitasVeterinario.AdaptadorCitas.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.citas_layout, viewGroup, false);
            return new CitasVeterinario.AdaptadorCitas.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CitasVeterinario.AdaptadorCitas.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Asignamos el texto con el valor de las citas a los campos
            Cita cita = citas.get(position);
            holder.mascota.setText(cita.getMascota().getNombre());
            holder.motivo.setText(String.valueOf(cita.getMotivo()));
            holder.fecha.setText(String.valueOf(cita.getFecha()));
        }

        @Override
        public int getItemCount() {
            return citas.size();
        }
    }
}