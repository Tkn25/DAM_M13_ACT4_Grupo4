package com.example.dam_m13_act4_grupo4.Veterinario;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dam_m13_act4_grupo4.Cliente.DatosMascotaCliente;
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

import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;

public class MascotasVeterinario extends AppCompatActivity {
    private RecyclerView recycler;
    private final ArrayList<Mascota> mascotas = new ArrayList<>();
    private AdaptadorMain adaptador;
    private ImageButton volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mascotas_veterinario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinnerFiltro = findViewById(R.id.spinnerFiltro);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.opciones_filtro, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String opcionSeleccionada = adapterView.getItemAtPosition(position).toString();
                // Cambiar criterio de filtrado
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private class ObtenerMascotasTask extends AsyncTask<Void, Void, ArrayList<Mascota>> {
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(Void... voids) {
            String url = "http://192.168.1.143/mascotasCliente.php"; //Sustituye por tu IPv4

            try {
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }


                Document document = Global.convertirStringToXMLDocument(respuesta.toString());

                NodeList listaMascotas = document.getElementsByTagName("mascota");

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

                    Mascota m = new Mascota(id, idDueno, idEspecie, raza, nombre, idGenero, microchip, castrado, enfermedad, baja, peso, fecha);
                    mascotasList.add(m);
                }


                entrada.close();
                conexion.disconnect();

            } catch (Exception e) {
                e.printStackTrace();

            }

            return mascotasList;
        }


        @Override
        protected void onPostExecute(ArrayList<Mascota> mascotasList) {
            super.onPostExecute(mascotasList);


            if (mascotasList != null && !mascotasList.isEmpty()) {

                mascotas.clear();

                mascotas.addAll(mascotasList);


            } else {

                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AdaptadorMain extends RecyclerView.Adapter<AdaptadorMain.ViewHolder> implements Filterable{
        private final ArrayList<Mascota> mascotas;

        private ArrayList<Mascota> mascotasFiltradas;
        private String criterioFiltro;


        public class ViewHolder extends RecyclerView.ViewHolder {

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

            this.mascotasFiltradas = new ArrayList<>(mascotas); // Inicializar lista filtrada con todos los datos
            this.criterioFiltro = "nombre"; // Criterio de filtrado predeterminado
            //nuevo codigo
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String filtro = charSequence.toString().toLowerCase().trim();
                    ArrayList<Mascota> listaFiltrada = new ArrayList<>();


                    if (criterioFiltro.equals("nombre")) {
                        for (Mascota mascota : mascotas) {
                            if (mascota.getNombre().toLowerCase().contains(filtro)) {
                                listaFiltrada.add(mascota);
                            }
                        }
                    } else if (criterioFiltro.equals("dni")) {

                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = listaFiltrada;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mascotasFiltradas = (ArrayList<Mascota>) filterResults.values;
                    notifyDataSetChanged(); // Notificar al adaptador sobre los cambios en la lista filtrada
                }
            };
        }

        public void setCriterioFiltro(String criterioFiltro) {
            this.criterioFiltro = criterioFiltro;
        }

        @NonNull
        @Override
        public AdaptadorMain.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_layout, viewGroup, false);
            return new AdaptadorMain.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorMain.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

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


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, DatosMascotaCliente.class);
                    String nombre = mascota.getNombre();
                    int especie = mascota.getIdEspecie();
                    String raza = mascota.getRaza();
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
}