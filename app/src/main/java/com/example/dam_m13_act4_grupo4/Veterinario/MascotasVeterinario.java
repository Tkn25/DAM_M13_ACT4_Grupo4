package com.example.dam_m13_act4_grupo4.Veterinario;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class MascotasVeterinario extends AppCompatActivity {
    // Declaramos variables globales.
    private RecyclerView recycler;
    private final ArrayList<Mascota> mascotas = new ArrayList<>();

    private AdaptadorMain adaptador;
    private ImageButton volver;
    private Spinner spinnerFiltro;
    private SearchView searchView;
    FloatingActionButton fab;
    //Variable que se usa para guardar el ultimo Id para tener una referencia al insertar una nueva mascota.
    private int ultimoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Inicializar layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mascotas_veterinario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Vinculamos los elementos de la vista con objetos Java
        fab = findViewById(R.id.floatingActionButton);
        volver = findViewById(R.id.imageButton6);
        recycler = findViewById(R.id.recycler);
        searchView = findViewById(R.id.searchView);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        //Evento del boton flotante al hacer click sobre el
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Metodo que abre una nueva vista para añadir una mascota
                añadir();
            }
        });
        //Metodo que al hacer click sobre el boton vuelve a la pantalla anterior
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MascotasVeterinario.this, PrincipalVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //Inicializa la lista para ver los datos de cada mascota en el layout
        recycler.setLayoutManager(new LinearLayoutManager(this));
        // Instancia y ejecuta la tarea asíncrona para obtener las mascotas
        ObtenerMascotasTask obtenerMascotasTask = new ObtenerMascotasTask();
        obtenerMascotasTask.execute();
        //Adaptador que recoge los datos de opciones filtro y los añade al espinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.opciones_filtro, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);

        //Metodo de escucha search view 
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Aplicar el filtro cada vez que cambia el texto del SearchView
                adaptador.getFilter().filter(newText);
                adaptador.notifyDataSetChanged();
                return false;
            }
        });
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

    //Clase para la obtencion de datos de la mascota
    private class ObtenerMascotasTask extends AsyncTask<Void, Void, ArrayList<Mascota>> {
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(Void... voids) {
            String url = "http://192.168.0.14/ControlPaw/mascotasVeterinario.php";
            //Conexion a php
            try {
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                //Obtemos la informacion
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                Document document = convertirStringToXMLDocument(respuesta.toString());

                NodeList listaMascotas = document.getElementsByTagName("mascota");
                //Mapear informacion
                for (int i = 0; i < listaMascotas.getLength(); i++) {
                    Element element = (Element) listaMascotas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    int idDueno = Integer.parseInt(element.getElementsByTagName("idCliente").item(0).getTextContent());
                    int idEspecie = Integer
                            .parseInt(element.getElementsByTagName("idEspecie").item(0).getTextContent());
                    String raza = element.getElementsByTagName("raza").item(0).getTextContent();
                    String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                    String dni = element.getElementsByTagName("DNI").item(0).getTextContent();
                    int idGenero = Integer.parseInt(element.getElementsByTagName("idGenero").item(0).getTextContent());
                    String microchip = element.getElementsByTagName("microchip").item(0).getTextContent();
                    int castrado = Integer.parseInt(element.getElementsByTagName("castrado").item(0).getTextContent());
                    boolean enfermedad = Boolean
                            .parseBoolean(element.getElementsByTagName("enfermedad").item(0).getTextContent());
                    boolean baja = Boolean.parseBoolean(element.getElementsByTagName("baja").item(0).getTextContent());
                    float peso = Float.parseFloat(element.getElementsByTagName("peso").item(0).getTextContent());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaNacimiento = dateFormat
                            .parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaNacimiento);

                    Mascota m = new Mascota(id, idDueno, idEspecie, raza, nombre, idGenero, microchip, castrado,
                            enfermedad, baja, peso, fecha, dni);
                    mascotasList.add(m);
                }

                entrada.close();
                conexion.disconnect();

            } catch (Exception e) {
                e.printStackTrace();

            }
            //Buscamos el ultimo registro y nos guardamos su ID para posteriormente insertar una nueva mascota y ponerle el ultimo ID +1
            ultimoId = mascotasList.get(mascotasList.size() - 1).getId();
            return mascotasList;
        }

        @Override
        protected void onPostExecute(ArrayList<Mascota> mascotasList) {
            super.onPostExecute(mascotasList);

            if (mascotasList != null && !mascotasList.isEmpty()) {

                mascotas.clear();

                mascotas.addAll(mascotasList);
                // Inicializar el adaptador con la lista de mascotas
                adaptador = new AdaptadorMain(mascotas);
                // Asignar el adaptador al RecyclerView
                recycler.setAdapter(adaptador);
            } else {
                Toast.makeText(getApplicationContext(),
                        "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
//Adaptador para visualizacion de los datosde la mascota
    private class AdaptadorMain extends RecyclerView.Adapter<AdaptadorMain.ViewHolder> implements Filterable {
        private ArrayList<Mascota> mascotas;

        private ArrayList<Mascota> allMascota;

        private ArrayList<Mascota> mascotasFiltradas;
        private String criterioFiltro;

        public class ViewHolder extends RecyclerView.ViewHolder {
//Creamos el diseño del item de la lista
            private final TextView nombre;
            private final TextView especie;
            private final TextView peso;
            private final TextView fechaNacimiento;

            public ViewHolder(View view) {
                super(view);

                nombre = view.findViewById(R.id.textViewNombreItem);
                especie = view.findViewById(R.id.textViewEspecieItem);
                peso = view.findViewById(R.id.textViewPesoItem);
                fechaNacimiento = view.findViewById(R.id.textViewFechaItem);
            }

        }

        public AdaptadorMain(ArrayList<Mascota> mascotas) {
            this.mascotas = mascotas;
            // Copia de la lista de mascotas
            this.allMascota = mascotas;
            this.mascotasFiltradas = new ArrayList<>(mascotas);
            this.criterioFiltro = "nombre";

        }
// Filtro de la lista de mascotas
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String filtro = charSequence.toString().toLowerCase().trim();
                    ArrayList<Mascota> listaFiltrada = new ArrayList<>();

                   // Cada vez que se filtra lo hacemos desde de la copia que tiene todos los datos de todas las mascotas
                    String palabra = "";
                    mascotas = allMascota;
                    // Si el search view esta vacio añadimos todas las mascotas
                    if (filtro.equals("")) {
                        listaFiltrada = mascotas;
                    } else {
                        // Recorremos todas las listas de mascotas
                        for (Mascota mascota : mascotas) {
                            // Buscamos que palabra del espinner hay puesta y obtenemos el texto introducido del search view
                            palabra = (criterioFiltro.equals("nombre")) ? mascota.getNombre().toLowerCase() : mascota.getDni().toLowerCase();
                            // Si el texto coincide con el dato de la mascota nos lo guardamos para enseñar la lista filtrada
                            if (palabra.contains(filtro.toLowerCase())) {
                                listaFiltrada.add(mascota);
                            }
                        }
                    }
                    mascotas = listaFiltrada;
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = listaFiltrada;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mascotasFiltradas = (ArrayList<Mascota>) filterResults.values;
                    // Notificar al adaptador sobre los cambios en la lista filtrada
                    notifyDataSetChanged();
                }
            };
        }

       /* public void setCriterioFiltro(String criterioFiltro) {
            this.criterioFiltro = criterioFiltro;
        }*/

        @NonNull
        @Override
        public AdaptadorMain.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_layout, viewGroup, false);
            return new AdaptadorMain.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorMain.ViewHolder holder,
                                     @SuppressLint("RecyclerView") int position) {

            Mascota mascota = mascotas.get(position);
            holder.nombre.setText(mascota.getNombre());
            holder.peso.setText(String.valueOf(mascota.getPeso()));
            holder.fechaNacimiento.setText(String.valueOf(mascota.getFechaNacimiento()));

            if (mascota.getIdEspecie() == 1) {
                holder.especie.setText("Perro");
            } else if (mascota.getIdEspecie() == 2) {
                holder.especie.setText("Gato");
            } else if (mascota.getIdEspecie() == 3) {
                holder.especie.setText("Hurón");
            } else if (mascota.getIdEspecie() == 4) {
                holder.especie.setText("Hamster");
            }
            // Al pulsar sobre cualquier item enviaremos los datos del item a una nueva vista para verlo en detalle
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, DatosMascotaVet.class);
                    String nombre = mascota.getNombre();
                    int id = mascota.getId();
                    int especie = mascota.getIdEspecie();
                    String raza = mascota.getRaza();
                    int genero = mascota.getidGenero();
                    Float peso = mascota.getPeso();
                    String fecha = mascota.getFechaNacimiento();
                    int castrado = mascota.getCastrado();
                    int idCliente = mascota.getIdPropietario();
                    boolean enfermedad = mascota.isEnfermedad();
                    boolean baja = mascota.isBaja();
                    String microchip = mascota.getMicrochip();
                    intent.putExtra("clave", "modificarMascota");
                    intent.putExtra("idMascota", id);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("raza", raza);
                    intent.putExtra("genero", genero);
                    intent.putExtra("castrado", castrado);
                    intent.putExtra("especie", especie);
                    intent.putExtra("peso", peso);
                    intent.putExtra("fecha", fecha);
                    intent.putExtra("microchip", microchip);

                    intent.putExtra("idCliente", idCliente);
                    intent.putExtra("enfermedad", enfermedad);
                    intent.putExtra("baja", baja);

                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mascotas.size();
        }
    }

    private void añadir() {
// Abre una nueva vista para insertar una nueva mascota
        Intent intent = new Intent(this, DatosMascotaVet.class);
        // Le pasamos el dato insertar mascota como parametro para saber en la otra vista que hemos accedido para insertar
        intent.putExtra("clave", "insertarMascota");

        intent.putExtra("idMascota", ultimoId + 1);
        startActivity(intent);
        finish();
    }
}