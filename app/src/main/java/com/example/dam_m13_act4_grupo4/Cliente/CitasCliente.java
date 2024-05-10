package com.example.dam_m13_act4_grupo4.Cliente;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CitasCliente extends AppCompatActivity {

    private ImageButton volver;
    private RecyclerView lista;
    private final ArrayList<Cita> citas = new ArrayList<>();
    private final ArrayList<Mascota> mascotas = new ArrayList<>();
    private AdaptadorCitas adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citas_cliente);
        volver = findViewById(R.id.imageButton13);
        lista = findViewById(R.id.recyclerCitas);

        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new AdaptadorCitas(citas);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitasCliente.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });

        new ObtenerMascotasTask().execute();
        new ObtenerCitasTask().execute();
    }


    private class ObtenerMascotasTask extends AsyncTask<Void, Void, ArrayList<Mascota>> {
        //Creamos el array donde almacenaremos todos los datos de las mascotas
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(Void... voids) {
            //Ponemos la dirección del .php
            String url = "http://192.168.1.143/mascotasCliente.php"; //Sustituye por tu IPv4

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
                mascotas.clear();
                //Agregamos las nuevas mascotas a la lista
                mascotas.addAll(mascotasList);
                //Notificamos al adaptador los cambios
                adaptador.notifyDataSetChanged();
            } else {
                //Si hay algún error mostramos un mensaje por pantalla
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ObtenerCitasTask extends AsyncTask<Void, Void, ArrayList<Cita>> {
        //Creamos el array donde almacenaremos todos los datos de las mascotas
        ArrayList<Cita> citasList = new ArrayList<>();

        @Override
        protected ArrayList<Cita> doInBackground(Void... voids) {
            //Ponemos la dirección del .php
            String url = "http://192.168.1.143/citasCliente.php"; //Sustituye por tu IPv4

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

                //Obtenemos los elementos de cada mascota
                NodeList listaConsultas = document.getElementsByTagName("consulta");
                //Con este bucle conseguimos los datos de cada mascota
                for (int i = 0; i < listaConsultas.getLength(); i++) {
                    Element element = (Element) listaConsultas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String motivo = element.getElementsByTagName("titulo").item(0).getTextContent();
                    int idMascota = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);
                    Mascota m = obtenerMascota(idMascota);
                    //Creamos un objeto Mascota con los datos obtenidos
                    Cita c = new Cita(id, motivo, m, fecha);
                    citasList.add(c);
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

            //Actualizamos la interfaz
            if (citasList != null && !citasList.isEmpty()) {
                //Limpiamos la lista actual
                citas.clear();
                //Agregamos las nuevas mascotas a la lista
                citas.addAll(citasList);
                //Notificamos al adaptador los cambios
                adaptador.notifyDataSetChanged();
            } else {
                //Si hay algún error mostramos un mensaje por pantalla
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase que se encarga de crear el adaptador para la recycler con el objeto mascotas
    private class AdaptadorCitas extends RecyclerView.Adapter<AdaptadorCitas.ViewHolder> {
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
        public AdaptadorCitas.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.citas_layout, viewGroup, false);
            return new AdaptadorCitas.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorCitas.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Asignamos el texto con el valor de las mascotas a los campos
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

    public Mascota obtenerMascota(int idMascota){
        for (Mascota mascota : mascotas) {
            if (mascota.getId() == idMascota) {
                return mascota;
            }
        }
        return null;
    }
}