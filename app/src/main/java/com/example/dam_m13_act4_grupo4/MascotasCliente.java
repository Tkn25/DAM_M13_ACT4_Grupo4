package com.example.dam_m13_act4_grupo4;

import android.annotation.SuppressLint;
import android.content.Context;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MascotasCliente extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<Mascota> mascotas = new ArrayList<>();
    private AdaptadorMain adaptador;
    private ImageButton volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mascotas_cliente);

        volver = findViewById(R.id.imageButtonVolver);
        recycler = findViewById(R.id.recycler);
        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new AdaptadorMain(mascotas);
        recycler.setAdapter(adaptador);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MascotasCliente.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });

        new ObtenerMascotasTask().execute();
        }

    //Método que se encarga de convertir un String de un XML en un DOCUMENT
    public static Document convertirStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                Document document = convertirStringToXMLDocument(respuesta.toString());
                //Obtenemos los elementos de cada mascota
                NodeList listaMascotas = document.getElementsByTagName("mascota");
                //Con este bucle conseguimos los datos de cada mascota
                for (int i = 0; i < listaMascotas.getLength(); i++) {
                    Element element = (Element) listaMascotas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    int idDueno = Integer.parseInt(element.getElementsByTagName("idDueno").item(0).getTextContent());
                    int idEspecie = Integer.parseInt(element.getElementsByTagName("idEspecie").item(0).getTextContent());
                    int idRaza = Integer.parseInt(element.getElementsByTagName("idRaza").item(0).getTextContent());
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
                    Mascota m = new Mascota(id, idDueno, idEspecie, idRaza, nombre, idGenero, microchip, castrado, enfermedad, baja, peso, fecha);
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

    //Clase que se encarga de crear el adaptador para la recycler con el objeto mascotas
    private class AdaptadorMain extends RecyclerView.Adapter<AdaptadorMain.ViewHolder> {
        private ArrayList<Mascota> mascotas;

        public class ViewHolder extends RecyclerView.ViewHolder {
            //Creamos la variables con los elementos del layout de cada item
            private TextView nombre, especie, peso, fechaNacimiento;

            public ViewHolder(View view) {
                super(view);
                //Enlazamos las variables con el layout de cada item
                nombre = view.findViewById(R.id.textViewNombreItem);
                especie = view.findViewById(R.id.textViewEspecieItem);
                peso = view.findViewById(R.id.textViewPesoItem);
                fechaNacimiento = view.findViewById(R.id.textViewFechaItem);
            }
        }

        public AdaptadorMain(ArrayList<Mascota> mascotas) {
            this.mascotas = mascotas;
        }

        @NonNull
        @Override
        public AdaptadorMain.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorMain.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Asignamos el texto con el valor de las mascotas a los campos
            Mascota mascota = mascotas.get(position);
            holder.nombre.setText(mascota.getNombre());
            holder.peso.setText(String.valueOf(mascota.getPeso()));
            holder.fechaNacimiento.setText(String.valueOf(mascota.getFechaNacimiento()));

            //Según la ID de la especie se mostrará un nombre u otro
            if (mascota.getIdEspecie() == 1) {
                holder.especie.setText("Perro");
            } else if (mascota.getIdEspecie() == 2) {
                holder.especie.setText("Gato");
            } else if (mascota.getIdEspecie() == 3) {
                holder.especie.setText("Hurón");
            } else if (mascota.getIdEspecie() == 4) {
                holder.especie.setText("Hamster");
            }

            //Cuando presionamos en un item...
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Se guardan los datos y se envian a la siguiente actividad a través de un intent
                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, DatosMascotaCliente.class);
                    String nombre = mascota.getNombre();
                    int especie = mascota.getIdEspecie();
                    int raza = mascota.getIdRaza();
                    int genero = mascota.getidGenero();
                    Float peso = mascota.getPeso();
                    String fecha = mascota.getFechaNacimiento();
                    int castrado = mascota.getCastrado();
                    String microchip = mascota.getMicrochip();
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("raza", raza);
                    intent.putExtra("genero", genero);
                    intent.putExtra("castrado", castrado);
                    intent.putExtra("especie", especie);
                    intent.putExtra("peso", peso);
                    intent.putExtra("fecha", fecha);
                    intent.putExtra("microchip", microchip);
                    //Iniciamos la siguiente actividad y cerramos la actual
                    startActivity(intent);
                    finish();
                }
            });
        }

        //Devuelve el numero de mascotas
        @Override
        public int getItemCount() {
            return mascotas.size();
        }
    }

}