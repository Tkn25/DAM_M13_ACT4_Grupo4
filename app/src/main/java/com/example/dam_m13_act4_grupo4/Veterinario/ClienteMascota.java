package com.example.dam_m13_act4_grupo4.Veterinario;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dam_m13_act4_grupo4.Cliente.DatosMascotaCliente;
import com.example.dam_m13_act4_grupo4.Cliente.MascotasCliente;
import com.example.dam_m13_act4_grupo4.Cliente.PrincipalCliente;
import com.example.dam_m13_act4_grupo4.Dueno;
import com.example.dam_m13_act4_grupo4.R;

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

public class ClienteMascota extends AppCompatActivity {
    private RecyclerView recycler;
    private final ArrayList<Dueno> duenos = new ArrayList<>();
    private AdaptadorMain adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_mascota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton volver = findViewById(R.id.imageButtonVolver);
        recycler = findViewById(R.id.recycler);
        //Creamos un objeto de tipo adaptador y lo asignamos a la recycler
        adaptador = new AdaptadorMain(duenos);
        recycler.setAdapter(adaptador);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClienteMascota.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });

        new ObtenerClientesTask().execute();
    }
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
    private class AdaptadorMain extends RecyclerView.Adapter<AdaptadorMain.ViewHolder> {
        private final ArrayList<Dueno> duenos;

        public class ViewHolder extends RecyclerView.ViewHolder {
            //Creamos la variables con los elementos del layout de cada item

            private final TextView tipo;
            private final TextView usuario;
            private final TextView DNI;
            private final TextView nombre;
            private final TextView telefono;
            private final TextView direccion;


            public ViewHolder(View view) {
                super(view);
                //Enlazamos las variables con el layout de cada item
                tipo = view.findViewById(R.id.textViewTipoItem);
                usuario = view.findViewById(R.id.textViewUsuarioItem);
                DNI = view.findViewById(R.id.textViewDNIItem);
                nombre = view.findViewById(R.id.textViewNombreUsuItem);
                telefono = view.findViewById(R.id.textViewTelItem);
                direccion = view.findViewById(R.id.textViewDirItem);
            }
        }

        public AdaptadorMain(ArrayList<Dueno> duenos) {
            this.duenos = duenos;
        }

        @NonNull
        @Override
        public AdaptadorMain.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_layout_cliente, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorMain.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //Asignamos el texto con el valor de las mascotas a los campos
            Dueno dueno = duenos.get(position);
            holder.usuario.setText(String.valueOf(dueno.getUsuario()));
            holder.DNI.setText(String.valueOf(dueno.getDNI()));
            holder.nombre.setText(String.valueOf(dueno.getNombre()));
            holder.telefono.setText(String.valueOf(dueno.getTelefono()));
            holder.direccion.setText(String.valueOf(dueno.getDireccion()));

            //Según la ID de la especie se mostrará un nombre u otro
            if (dueno.getTipo() == 1) {
                holder.tipo.setText("Veterinario");
            } else if (dueno.getTipo() == 2) {
                holder.tipo.setText("Cliente");
            }


            //Cuando presionamos en un item...
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        //Devuelve el numero de mascotas
        @Override
        public int getItemCount() {
            return duenos.size();
        }
    }
    private class ObtenerClientesTask extends AsyncTask<Void, Void, ArrayList<Dueno>> {
        //Creamos el array donde almacenaremos todos los datos de las mascotas
        ArrayList<Dueno> duenosList = new ArrayList<>();

        @Override
        protected ArrayList<Dueno> doInBackground(Void... voids) {
            //Ponemos la dirección del .php
            String url = "http://192.168.1.179/ControlPaw/visualizarCliente.php"; //Sustituye por tu IPv4

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
                NodeList listaMascotas = document.getElementsByTagName("cliente");
                //Con este bucle conseguimos los datos de cada mascota
                for (int i = 0; i < listaMascotas.getLength(); i++) {
                    Element element = (Element) listaMascotas.item(i);
                    int idCliente = Integer.parseInt(element.getElementsByTagName("idCliente").item(0).getTextContent());
                    int tipo = Integer.parseInt(element.getElementsByTagName("tipo").item(0).getTextContent());
                    String usuario = element.getElementsByTagName("usuario").item(0).getTextContent();
                    String dni = element.getElementsByTagName("DNI").item(0).getTextContent();
                    String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                    String telefono = element.getElementsByTagName("telefono").item(0).getTextContent();
                    String dir = element.getElementsByTagName("direccion").item(0).getTextContent();
                    //Creamos un objeto Mascota con los datos obtenidos
                    Dueno m = new Dueno(idCliente, tipo, usuario, dni, nombre, nombre, telefono, dir);
                    duenosList.add(m);
                }

                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();

            } catch (Exception e) {
                e.printStackTrace();

            }

            return duenosList;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(ArrayList<Dueno> duenosList) {
            super.onPostExecute(duenosList);

            //Actualizamos la interfaz
            if (duenosList != null && !duenosList.isEmpty()) {
                //Limpiamos la lista actual
                duenos.clear();
                //Agregamos las nuevas mascotas a la lista
                duenos.addAll(duenosList);
                //Notificamos al adaptador los cambios
                adaptador.notifyDataSetChanged();
            } else {
                //Si hay algún error mostramos un mensaje por pantalla
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}