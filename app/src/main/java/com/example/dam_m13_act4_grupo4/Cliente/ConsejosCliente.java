package com.example.dam_m13_act4_grupo4.Cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dam_m13_act4_grupo4.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.example.dam_m13_act4_grupo4.POJO.Consejo;
import com.example.dam_m13_act4_grupo4.POJO.Global;

public class ConsejosCliente extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private RecyclerView lista;
    private final ArrayList<Consejo> consejos = new ArrayList<>();
    private AdaptadorConsejos adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consejos_cliente);

        //Asociamos las variables con sus elementos del layout
        volver = findViewById(R.id.imageButton7);
        lista = findViewById(R.id.listaConsejos);

        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new AdaptadorConsejos(consejos);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsejosCliente.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });
        //Obtenemos los consejos
        new ObtenerConsejosTask().execute();
    }

    //Clase encargada de obtener los datos de los cobsejos en la BBDD a través de un .php
    private class ObtenerConsejosTask extends AsyncTask<Void, Void, ArrayList<Consejo>> {
        //Creamos el array donde almacenaremos todos los datos de los consejos
        ArrayList<Consejo> consejosList = new ArrayList<>();

        @Override
        protected ArrayList<Consejo> doInBackground(Void... voids) {
            //Ponemos la dirección del .php
            String url = "http://192.168.0.14/controlpaw/consejosLista.php"; //Sustituye por tu IPv4

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

                //Obtenemos los elementos de cada consejo
                NodeList listaConsultas = document.getElementsByTagName("consejo");
                //Con este bucle conseguimos los datos de cada consejo
                for (int i = 0; i < listaConsultas.getLength(); i++) {
                    Element element = (Element) listaConsultas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String titulo = element.getElementsByTagName("titulo").item(0).getTextContent();
                    String descripcion = element.getElementsByTagName("descripcion").item(0).getTextContent();
                    String imagen = element.getElementsByTagName("img").item(0).getTextContent();

                    //Creamos un objeto Consejo con los datos obtenidos
                    Consejo c = new Consejo(id, titulo, descripcion, imagen);
                    consejosList.add(c);
                }
                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return consejosList;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(ArrayList<Consejo> consejosList) {
            super.onPostExecute(consejosList);

            //Comprobamos que la lista no este vacía
            if (consejosList != null && !consejosList.isEmpty()) {
                //Limpiamos la lista actual para evitar errores
                consejos.clear();
                //Agregamos los consejos encontrados a la lista de consejos
                consejos.addAll(consejosList);
                //Notificamos los cambios al adaptador
                adaptador.notifyDataSetChanged();
            } else {
                //Si hay algun error mostramos un mensaje
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Clase que se encarga de crear el adaptador para la recycler con el objeto consejos
    private class AdaptadorConsejos extends RecyclerView.Adapter<AdaptadorConsejos.ViewHolder> {
        private final ArrayList<Consejo> consejos;

        public class ViewHolder extends RecyclerView.ViewHolder {
            //Creamos la variables con los elementos del layout de cada item
            private final TextView titulo;
            private final TextView descripcion;
            private final ImageView imagen;
            public ViewHolder(View view) {
                super(view);
                //Enlazamos las variables con el layout de cada item
                titulo = view.findViewById(R.id.textViewTitulo);
                descripcion = view.findViewById(R.id.textViewContenido);
                imagen = view.findViewById(R.id.imageViewImagen);
            }
        }

        public AdaptadorConsejos(ArrayList<Consejo> consejos) {
            this.consejos = consejos;
        }

        @NonNull
        @Override
        public AdaptadorConsejos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.consejos_layout, viewGroup, false);
            return new AdaptadorConsejos.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorConsejos.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Asignamos el texto con el valor de los consejos a los campos
            Consejo consejo = consejos.get(position);
            holder.titulo.setText(consejo.getTitulo());
            holder.descripcion.setText(consejo.getDescripcion());
            new DownloadImageTask(holder.imagen).execute(consejo.getUrlImagen());
        }

        @Override
        public int getItemCount() {
            return consejos.size();
        }
    }

    //Clase encargada de convertir un enlace en un Bitmap para utilizarlo en un ImageView
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String enlace = urls[0];
            Bitmap imagen = null;
            try {
                InputStream in = new URL(enlace).openStream();
                imagen = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imagen;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}