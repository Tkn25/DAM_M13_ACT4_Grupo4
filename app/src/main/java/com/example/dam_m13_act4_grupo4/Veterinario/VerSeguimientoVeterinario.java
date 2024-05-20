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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.POJO.Seguimiento;
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

public class VerSeguimientoVeterinario extends AppCompatActivity
{

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private RecyclerView lista;
    private static String idDueno;
    private final ArrayList<Seguimiento> seguimientos = new ArrayList<>();
    private final ArrayList<Mascota> mascotasCliente = new ArrayList<>();
    private VerSeguimientoVeterinario.AdaptadorSeguimientos adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_seguimiento_veterinario);

        volver = findViewById(R.id.imageButton14);
        lista = findViewById(R.id.recyclerViewSeguimientos);

        //region Creamos el adaptador y lo asociamos a la RecyclerView
        adaptador = new VerSeguimientoVeterinario.AdaptadorSeguimientos(seguimientos);
        lista.setAdapter(adaptador);
        lista.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            idDueno = extras.getString("user");
        }

        //region Listener del botón para volver a la lista de tratamientos
        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VerSeguimientoVeterinario.this, SaludVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        new VerSeguimientoVeterinario.ObtenerMascotasClienteTask().execute(idDueno);
    }

    private class ObtenerMascotasClienteTask extends AsyncTask<String, Void, ArrayList<Mascota>>
    {
        ArrayList<Mascota> mascotasList = new ArrayList<>();

        @Override
        protected ArrayList<Mascota> doInBackground(String... dueno) {
            String url = "http://192.168.0.14/controlpaw/saludMascotasVeterinario.php"; // Sustituye por tu IPv4

            try
            {
                //region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Leemos cada línea de la respuesta de la DB
                String parametros = "dueno=" + dueno[0];
                conexion.getOutputStream().write(parametros.getBytes());

                //Leemos la respuesta de la BD hasta que no haya mas lineas para leer.
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
                new VerSeguimientoVeterinario.ObtenerSeguimientosTask().execute();
            }
            //endregion
            else
            {
                //Si hay algún error mostramos un mensaje por pantalla
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion
    }

    //region Clase utilizada para obtener los datos de los seguimientos en la DB mediante documento PHP
    private class ObtenerSeguimientosTask extends AsyncTask<Void, Void, ArrayList<Seguimiento>>
    {
        ArrayList<Seguimiento> seguimientosList = new ArrayList<>();

        @Override
        protected ArrayList<Seguimiento> doInBackground(Void... Void)
        {
            String url = "http://192.168.0.14/controlpaw/verSeguimientoVeterinario.php"; // Sustituye por tu IPv4

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
                NodeList listaConsultas = document.getElementsByTagName("seguimiento");
                //endregion

                for (int i = 0; i < listaConsultas.getLength(); i++)
                //region Extraemos los datos de cada seguimiento
                {
                    Element element = (Element) listaConsultas.item(i);
                    int idMascota = Integer.parseInt(element.getElementsByTagName("idMascota").item(0).getTextContent());
                    String descripcion = element.getElementsByTagName("descripcion").item(0).getTextContent();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCita = dateFormat.parse(element.getElementsByTagName("fecha").item(0).getTextContent());
                    String fecha = dateFormat.format(fechaCita);
                    String imagen = element.getElementsByTagName("imagen").item(0).getTextContent();
                    for (Mascota mascota : mascotasCliente)
                    {
                        if (mascota.getId() == idMascota)
                        {
                            //region Introducimos los datos en un nuevo objeto Seguimiento, el cual guardamos en la lista
                            Seguimiento t = new Seguimiento(idMascota, mascota, descripcion, imagen, fecha );
                            seguimientosList.add(t);
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
            return seguimientosList;
        }

        //region Tras la ejecucion
        @Override
        protected void onPostExecute(ArrayList<Seguimiento> seguimientosList)
        {
            super.onPostExecute(seguimientosList);
            if (seguimientosList != null && !seguimientosList.isEmpty())
            //region Si la lista no está vacía
            {
                seguimientos.clear();
                seguimientos.addAll(seguimientosList);
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

    //region Clase que crea el adaptador para la RecyclerView con el objeto Seguimiento
    private class AdaptadorSeguimientos extends RecyclerView.Adapter<VerSeguimientoVeterinario.AdaptadorSeguimientos.ViewHolder>
    {
        private final ArrayList<Seguimiento> seguimientos;

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            //region Variables de elementos de layout
            private final TextView mascota;
            private final TextView descripcion;
            private final TextView fecha;
            private final TextView imagen;
            //endregion

            //region Relacionamos las variables con cada elemento
            public ViewHolder(View view)
            {
                super(view);
                mascota = view.findViewById(R.id.textViewMascotaSeguimiento);
                descripcion = view.findViewById(R.id.textViewDescripcionSeguimiento);
                fecha = view.findViewById(R.id.textViewFechaSeguimiento);
                imagen = view.findViewById(R.id.textViewImagenSeguimiento);
            }
            //endregion
        }

        public AdaptadorSeguimientos(ArrayList<Seguimiento> seguimientos)
        {
            this.seguimientos = seguimientos;
        }

        //region Establecemos el layout de los items
        @NonNull
        @Override
        public VerSeguimientoVeterinario.AdaptadorSeguimientos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
        {
            //Indicamos cual es el layout de los items
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seguimientos_layout, viewGroup, false);
            return new VerSeguimientoVeterinario.AdaptadorSeguimientos.ViewHolder(view);
        }
        //endregion

        //region Introducimos los datos en los elementos de layout
        @Override
        public void onBindViewHolder(@NonNull VerSeguimientoVeterinario.AdaptadorSeguimientos.ViewHolder holder, @SuppressLint("RecyclerView") int position)
        {
            //Asignamos el texto con el valor de los seguimientos a los campos
            Seguimiento seguimiento = seguimientos.get(position);
            holder.mascota.setText(seguimiento.getMascota().getNombre());
            holder.descripcion.setText(String.valueOf(seguimiento.getDescripcion()));
            holder.fecha.setText(String.valueOf(seguimiento.getFecha()));
            holder.imagen.setText(String.valueOf(seguimiento.getImagen()));
        }
        //endregion

        @Override
        public int getItemCount() {
            return seguimientos.size();
        }
    }
    //endregion
}