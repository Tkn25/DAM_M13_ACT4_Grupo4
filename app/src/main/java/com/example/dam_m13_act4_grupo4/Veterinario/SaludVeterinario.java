package com.example.dam_m13_act4_grupo4.Veterinario;

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

public class SaludVeterinario extends AppCompatActivity
{

    private ImageButton volver;
    private RecyclerView lista;
    private Button seguimiento, verSeguimiento, tratamiento;
    private static String idDueno;
    private final ArrayList<Tratamiento> tratamientos = new ArrayList<>();
    private final ArrayList<Mascota> mascotasCliente = new ArrayList<>();
    private SaludVeterinario.AdaptadorTratamientos adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salud_veterinario);
        volver = findViewById(R.id.imageButton14);
        lista = findViewById(R.id.recyclerViewTratamientos);
        seguimiento = findViewById(R.id.buttonSeguimiento);
        verSeguimiento = findViewById(R.id.buttonVerSeguimiento);
        tratamiento = findViewById(R.id.buttonTratamiento);

        //region Creamos el adaptador y lo asociamos a la RecyclerView
        adaptador = new SaludVeterinario.AdaptadorTratamientos(tratamientos);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            idDueno = extras.getString("user");
        }

        //region Listener del botón para volver al menú principal
        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SaludVeterinario.this, PrincipalVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Botón para añadir un nuevo seguimiento
        seguimiento.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SaludVeterinario.this, SeguimientoVeterinario.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
            }
        });
        //endregion

        //region Botón para acceder a la lista de seguimientos
        verSeguimiento.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SaludVeterinario.this, VerSeguimientoVeterinario.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
            }
        });
        //endregion

        //region Botón para añadir un nuevo tratamiento
        tratamiento.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SaludVeterinario.this, TratamientoVeterinario.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
            }
        });
        //endregion

        new SaludVeterinario.ObtenerMascotasClienteTask().execute(idDueno);
    }

    private class ObtenerMascotasClienteTask extends AsyncTask<String, Void, ArrayList<Mascota>>
    {
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(String... dueno) {
            String url = "http://192.168.1.143/controlpaw/saludMascotasVeterinario.php"; // Sustituye por tu IPv4

            try
            {
                //region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Leemos cada línea de la respuesta de la DB
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

                    //region Introducimos los datos en un nuevo objeto Mascota, el cual guardamos en la lista
                    Mascota m = new Mascota(id, idDueno, idEspecie, raza, nombre, idGenero, microchip, castrado, enfermedad, baja, peso, fecha);
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
            //region Actualizamos la interfaz
            {
                mascotasCliente.clear();
                mascotasCliente.addAll(mascotasList);
                adaptador.notifyDataSetChanged();
                new SaludVeterinario.ObtenerTratamientosTask().execute();
            }
            //endregion
            else
            {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion
    }

    //region Clase utilizada para obtener los datos de los tratamientos en la DB mediante documento PHP
    private class ObtenerTratamientosTask extends AsyncTask<Void, Void, ArrayList<Tratamiento>>
    {
        ArrayList<Tratamiento> tratamientosList = new ArrayList<>();

        @Override
        protected ArrayList<Tratamiento> doInBackground(Void... Void)
        {
            String url = "http://192.168.1.143/controlpaw/tratamientosVeterinario.php"; // Sustituye por tu IPv4

            try
            {
                //region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Leemos cada línea de la respuesta de la DB
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion

                //region Metemos los elementos en un document
                Document document = Global.convertirStringToXMLDocument(respuesta.toString());
                NodeList listaConsultas = document.getElementsByTagName("tratamiento");
                //endregion

                for (int i = 0; i < listaConsultas.getLength(); i++)
                //region Extraemos los datos de cada tratamiento
                {
                    Element element = (Element) listaConsultas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    int idMascota = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    String descripcion = element.getElementsByTagName("descripcion").item(0).getTextContent();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);
                    int finalizado = Integer.parseInt(element.getElementsByTagName("finalizado").item(0).getTextContent());
                    for (Mascota mascota : mascotasCliente)
                    {
                        if (mascota.getId() == idMascota)
                        {
                            //region Introducimos los datos en un nuevo objeto Tratamiento, el cual guardamos en la lista
                            Tratamiento t = new Tratamiento(id, mascota, descripcion, fecha, finalizado);
                            tratamientosList.add(t);
                            //endregion
                            break;
                        }
                    }
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
            return tratamientosList;
        }

        //region Tras la ejecucion
        @Override
        protected void onPostExecute(ArrayList<Tratamiento> tratamientosList)
        {
            super.onPostExecute(tratamientosList);
            if (tratamientosList != null && !tratamientosList.isEmpty())
            //region Si la lista no está vacía
            {
                tratamientos.clear();
                tratamientos.addAll(tratamientosList);
                adaptador.notifyDataSetChanged();
            }
            //endregion
            else
            //region Si la lista está vacía
            {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
            //endregion
        }
        //endregion
    }
    //endregion

    //region Clase que crea el adaptador para la RecyclerView con el objeto Tratamiento
    private class AdaptadorTratamientos extends RecyclerView.Adapter<SaludVeterinario.AdaptadorTratamientos.ViewHolder>
    {
        private final ArrayList<Tratamiento> tratamientos;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            //region Variables de elementos de layout
            private final TextView mascota;
            private final TextView descripcion;
            private final TextView fecha;
            //endregion

            //region Relacionamos las variables con cada elemento
            public ViewHolder(View view)
            {
                super(view);
                mascota = view.findViewById(R.id.textViewMascotaTratamiento);
                descripcion = view.findViewById(R.id.textViewDescripcionTratamiento);
                fecha = view.findViewById(R.id.textViewFechaTratamiento);
            }
            //endregion
        }

        public AdaptadorTratamientos(ArrayList<Tratamiento> tratamientos)
        {
            this.tratamientos = tratamientos;
        }

        //region Establecemos el layout de los items
        @NonNull
        @Override
        public SaludVeterinario.AdaptadorTratamientos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tratamientos_layout, viewGroup, false);
            return new SaludVeterinario.AdaptadorTratamientos.ViewHolder(view);
        }
        //endregion

        //region Introducimos los datos en los elementos de layout
        @Override
        public void onBindViewHolder(@NonNull SaludVeterinario.AdaptadorTratamientos.ViewHolder holder, @SuppressLint("RecyclerView") int position)
        {
            Tratamiento tratamiento = tratamientos.get(position);
            holder.mascota.setText(tratamiento.getMascota().getNombre());
            holder.descripcion.setText(String.valueOf(tratamiento.getDescripcion()));
            holder.fecha.setText(String.valueOf(tratamiento.getFecha()));
        }
        //endregion

        @Override
        public int getItemCount() {
            return tratamientos.size();
        }
    }
    //endregion
}