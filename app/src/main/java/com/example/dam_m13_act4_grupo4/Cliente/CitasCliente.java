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

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private RecyclerView lista;
    private final ArrayList<Cita> citas = new ArrayList<>();
    private AdaptadorCitas adaptador;
    private static String idDueno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citas_cliente);

        //Asociamos las variables con sus elementos del layout
        volver = findViewById(R.id.imageButton13);
        lista = findViewById(R.id.recyclerCitas);

        //Obtenemos la ID del usuario cliente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idDueno = extras.getString("user");
        }

        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new AdaptadorCitas(citas);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitasCliente.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });

        //Obtenemos las citas a partir del cliente
        new ObtenerCitasTask().execute(idDueno);

    }

    //Clase encargada de obtener los datos de las citas en la BBDD a través de un .php
    private class ObtenerCitasTask extends AsyncTask<String, Void, ArrayList<Cita>> {
        ArrayList<Cita> citasList = new ArrayList<>();

        @Override
        protected ArrayList<Cita> doInBackground(String... dueno) {
            String url = "http://192.168.1.143/controlpaw/citasMascotaCliente.php"; // Sustituye por tu IPv4

            try {
                //Creamos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                //Enviamos la id del dueño como parámetro
                String parametros = "idCliente=" + dueno[0];
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

                //Obtenemos los elementos de cada consulta
                NodeList listaConsultas = document.getElementsByTagName("consulta");
                for (int i = 0; i < listaConsultas.getLength(); i++) {
                    Element element = (Element) listaConsultas.item(i);
                    String mascotaNombre = element.getElementsByTagName("mascota").item(0).getTextContent();
                    String motivo = element.getElementsByTagName("motivo").item(0).getTextContent();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);

                    //Creamos un objeto cita con los datos obtenidos
                    Cita c = new Cita(motivo, fecha, mascotaNombre);
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

        @Override
        protected void onPostExecute(ArrayList<Cita> citasList) {
            super.onPostExecute(citasList);
            if (citasList != null && !citasList.isEmpty()) {
                citas.clear();
                citas.addAll(citasList);
                adaptador.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase que se encarga de crear el adaptador para la recycler con el objeto citas
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
            //Asignamos el texto con el valor de las citas a los campos
            Cita cita = citas.get(position);
            holder.mascota.setText(String.valueOf(cita.getNombreMascota()));
            holder.motivo.setText(String.valueOf(cita.getMotivo()));
            holder.fecha.setText(String.valueOf(cita.getFecha()));
        }

        @Override
        public int getItemCount() {
            return citas.size();
        }
    }
}