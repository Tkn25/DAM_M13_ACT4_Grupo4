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
    private ImageButton volver;
    private RecyclerView lista;
    private final ArrayList<Cita> citas = new ArrayList<>();
    private final ArrayList<Mascota> mascotasCliente = new ArrayList<>();
    private CitasVeterinario.AdaptadorCitas adaptador;
    private static String idVet;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citas_veterinario);
        volver = findViewById(R.id.imageButton13);
        lista = findViewById(R.id.recyclerCitas);

        //region Recibimos el intent
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            idVet = extras.getString("idEmpleado");
        }
        //endregion

        //region Creación de adaptador
        adaptador = new CitasVeterinario.AdaptadorCitas(citas);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        //region Listener del botón para volver al menú principal de veterinario
        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitasVeterinario.this, PrincipalVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Obtenemos datos de la DB
        new CitasVeterinario.ObtenerMascotasTask().execute(idVet);
        //endregion

    }

    //region Clase que obtiene datos de la DB a través de un archivo PHP
    private class ObtenerMascotasTask extends AsyncTask<String, Void, ArrayList<Mascota>>
    {
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        //region Realizamos la conexión con la DB e introducimos los datos en mascotasList
        @Override
        protected ArrayList<Mascota> doInBackground(String... dueno)
        {
            String url = "http://192.168.0.14/controlpaw/citasMascotaVeterinario.php"; // Sustituye por tu IPv4

            try
            {
                //region Establecemos conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Enviamos ID del dueño
                String parametros = "dueno=" + dueno[0];
                conexion.getOutputStream().write(parametros.getBytes());
                //endregion

                //region Leemos las líneas en la respuesta del PHP
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion

                //region Introducimos datos en un documento y obtenemos los datos de cada mascota
                Document document = Global.convertirStringToXMLDocument(respuesta.toString());
                NodeList listaMascotas = document.getElementsByTagName("mascota");
                for (int i = 0; i < listaMascotas.getLength(); i++)
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

                    //region Introducimos los datos obtenidos en un nuevo objeto Mascota
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
        //endregion

        //region Tras la ejecución
        @Override
        protected void onPostExecute(ArrayList<Mascota> mascotasList)
        {
            super.onPostExecute(mascotasList);

            //region Actualizamos la interfaz
            if (mascotasList != null && !mascotasList.isEmpty())
            {
                mascotasCliente.clear();
                mascotasCliente.addAll(mascotasList);
                adaptador.notifyDataSetChanged();
                new CitasVeterinario.ObtenerCitasTask().execute();
            }
            //endregion
            else
            //region En caso de producirse un error
            {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
            //endregion
        }
        //endregion
    }
    //endregion

    //region Obtenemos datos de las citas mediante documento PHP
    private class ObtenerCitasTask extends AsyncTask<Void, Void, ArrayList<Cita>>
    {
        ArrayList<Cita> citasList = new ArrayList<>();

        //region Realizamos la conexión con la DB e introducimos los datos en mascotasList
        @Override
        protected ArrayList<Cita> doInBackground(Void... Void)
        {
            String url = "http://192.168.0.14/controlpaw/citasVeterinario.php"; // Sustituye por tu IPv4

            try
            {
                //region Creamos conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Leemos las líneas en la respuesta del PHP
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion


                //region Introducimos datos en un documento y obtenemos los datos de cada consulta
                Document document = Global.convertirStringToXMLDocument(respuesta.toString());
                NodeList listaConsultas = document.getElementsByTagName("consulta");
                for (int i = 0; i < listaConsultas.getLength(); i++)
                {
                    Element element = (Element) listaConsultas.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String motivo = element.getElementsByTagName("titulo").item(0).getTextContent();
                    int idMascota = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);
                    for (Mascota mascota : mascotasCliente)
                    {
                        if (mascota.getId() == idMascota)
                        {
                            Cita c = new Cita(id, motivo, mascota, fecha);
                            citasList.add(c);
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
            return citasList;
        }
        //endregion

        //region Tras la ejecución
        @Override
        protected void onPostExecute(ArrayList<Cita> citasList)
        {
            super.onPostExecute(citasList);
            //region Comprobamos que se han recibido datos
            if (citasList != null && !citasList.isEmpty())
            {
                citas.clear();
                citas.addAll(citasList);
                adaptador.notifyDataSetChanged();
            }
            //endregion
            else
            //region En caso de producirse un error, notificamos al usuario
            {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
            //endregion
        }
        //endregion
    }

    //region Clase para creación del adaptador para RecyclerView
    private class AdaptadorCitas extends RecyclerView.Adapter<CitasVeterinario.AdaptadorCitas.ViewHolder>
    {
        private final ArrayList<Cita> citas;
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            //region Declaración de variables de cada item
            private final TextView mascota;
            private final TextView motivo;
            private final TextView fecha;
            //endregion

            //region Enlazamos las variables con cada elemento del layout
            public ViewHolder(View view)
            {
                super(view);
                mascota = view.findViewById(R.id.textViewMascotaCita);
                motivo = view.findViewById(R.id.textViewMotivoCita);
                fecha = view.findViewById(R.id.textViewFechaCita);
            }
            //endregion
        }

        //region Constructor
        public AdaptadorCitas(ArrayList<Cita> citas) {
            this.citas = citas;
        }
        //endregion

        //region Creamos y devolvemos un ViewHolder que contiene la vista inflada para mostrar la consulta en la lista
        @NonNull
        @Override
        public CitasVeterinario.AdaptadorCitas.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.citas_layout, viewGroup, false);
            return new CitasVeterinario.AdaptadorCitas.ViewHolder(view);
        }
        //endregion

        //region Método para asignar valores de las consultas a los campos en la vista
        @Override
        public void onBindViewHolder(@NonNull CitasVeterinario.AdaptadorCitas.ViewHolder holder, @SuppressLint("RecyclerView") int position)
        {
            //Asignamos el texto con el valor de las citas a los campos
            Cita cita = citas.get(position);
            holder.mascota.setText(cita.getMascota().getNombre());
            holder.motivo.setText(String.valueOf(cita.getMotivo()));
            holder.fecha.setText(String.valueOf(cita.getFecha()));

            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                //region Listener para desplazar al usuario a otra actividad con la información de la cita
                @Override
                public void onClick(View v)
                {
                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, VerCitaVeterinario.class);
                    int idCita = cita.getId();
                    int idMascota = cita.getMascota().getId();
                    String nombre = cita.getMascota().getNombre();
                    String motivo = cita.getMotivo();
                    String fecha = cita.getFecha();
                    intent.putExtra("idConsulta", idCita);
                    intent.putExtra("idMascota", idMascota);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("motivo", motivo);
                    intent.putExtra("fecha", fecha);
                    startActivity(intent);
                    finish();
                }
                //endregion
            });
        }
        //endregion

        @Override
        public int getItemCount() {
            return citas.size();
        }
    }
    //endregion
}