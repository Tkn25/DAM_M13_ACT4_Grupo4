package com.example.dam_m13_act4_grupo4.Veterinario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dam_m13_act4_grupo4.R;
import com.example.dam_m13_act4_grupo4.POJO.Cita;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VerCitaVeterinario extends AppCompatActivity {

    private EditText idConsulta;
    private EditText idAnimal;
    private EditText nombreMascota;
    private EditText motivoConsulta;
    private EditText fechaConsulta;
    private ImageButton volverCitas;
    private Button modificarConsulta;
    private Button borrarConsulta;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_cita_veterinario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //region Elementos layout
        idConsulta = findViewById(R.id.editTextIDVerConsulta);
        idAnimal = findViewById(R.id.editTextIDAnimal);
        nombreMascota = findViewById(R.id.editTextNombreVerConsulta);
        motivoConsulta = findViewById(R.id.editTextMotivoVerConsulta);
        fechaConsulta = findViewById(R.id.editTextFechaVerConsulta);
        volverCitas = findViewById(R.id.ButtonVolverConsultas);
        modificarConsulta = findViewById(R.id.buttonModificarConsulta);
        borrarConsulta = findViewById(R.id.buttonBorrarConsulta);
        //endregion

        //region Recogemos los datos del intent
        Intent intent = getIntent();
        if (intent != null)
        {
            int id = intent.getIntExtra("idConsulta", 0);
            int idMascota = intent.getIntExtra("idMascota", 0);
            String nombre = intent.getStringExtra("nombre");
            String motivo = intent.getStringExtra("motivo");
            String fecha = intent.getStringExtra("fecha");

            idConsulta.setText(String.valueOf(id));
            idAnimal.setText(String.valueOf(idMascota));
            nombreMascota.setText(nombre);
            motivoConsulta.setText(motivo);
            fechaConsulta.setText(fecha);
        }
        //endregion

        //region Botón para volver a la lista de citas
        volverCitas.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VerCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Listener del botón de modificar
        modificarConsulta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (modificarConsulta.getText().equals("MODIFICAR"))
                //region Bloque condicional, cambia el texto del botón y activa los campos
                {
                    modificarConsulta.setText("GUARDAR");
                    idAnimal.setEnabled(true);
                    motivoConsulta.setEnabled(true);
                    fechaConsulta.setEnabled(true);
                    borrarConsulta.setEnabled(false);
                }
                //endregion
                else
                {
                    if (motivoConsulta.getText().toString().isEmpty() || nombreMascota.getText().toString().isEmpty() || fechaConsulta.getText().toString().isEmpty())
                    //region En caso de que no todos los campos tengan datos
                    {
                        Toast.makeText(VerCitaVeterinario.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //endregion
                    String fechaString = fechaConsulta.getText().toString();
                    if (!fechaString.matches("\\d{4}-\\d{2}-\\d{2}"))
                    //region En caso de que la fecha no tenga el formato correcto
                    {
                        Toast.makeText(VerCitaVeterinario.this, "La fecha debe tener formato YYYY-mm-dd", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //endregion

                    //region Datos para comparar la fecha actual con la introducida
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String fechaActual = format.format(calendar.getTime());
                    String newFecha = fechaConsulta.getText().toString();
                    //endregion

                    if (newFecha.compareTo(fechaActual) <= 0)
                    //region En caso de introducirse una fecha anterior a la actual, no se modificará la consulta
                    {
                        Toast.makeText(VerCitaVeterinario.this, "La fecha debe ser mayor que la fecha actual", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //endregion

                    //region Introducimos los datos en variables y los introducimos en un objeto Cita
                    int id2 = Integer.parseInt(idConsulta.getText().toString());
                    String newMotivo = motivoConsulta.getText().toString();
                    Mascota mascota = new Mascota(Integer.parseInt(idAnimal.getText().toString()), nombreMascota.getText().toString());
                    Cita citaObj = new Cita(id2, newMotivo, mascota, newFecha); //id, nombre animal, motivo, fecha
                    //endregion

                    //region Ejecutamos la modificación
                    new ModificarConsultaTask().execute(citaObj);
                    //endregion

                    modificarConsulta.setText("MODIFICAR");
                    idConsulta.setEnabled(false);
                    motivoConsulta.setEnabled(false);
                    fechaConsulta.setEnabled(false);
                    borrarConsulta.setEnabled(true);
                }
            }
        });
        //endregion

        //region Listender del botón para borrar la consulta actual
        borrarConsulta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int id2 = Integer.parseInt(idConsulta.getText().toString());
                Cita c = new Cita(id2, null, null, null);
                new BorrarConsultaTask().execute(c);
            }
        });
        //endregion
    }

    //region Borrar la consulta actual
    private class BorrarConsultaTask extends AsyncTask<Cita, Void, Void> {
        @Override
        protected Void doInBackground(Cita... consulta)
        {
            String url = "http://192.168.1.143/ControlPaw/borrarConsulta.php"; // Sustituye por tu IPv4

            try
            {
                //region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Almacenamos la ID de la cita en una variable
                String parametros = "id=" + consulta[0].getId();
                //endregion

                //region Enviamos los datos
                OutputStream outputStream = conexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                outputStream.close();
                //endregion

                //region Recibimos la respuesta
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
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
            return null;
        }

        //region Tras la ejecución, notificamos al usuario y devolvemos al usuario a la lista de citas
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Toast.makeText(VerCitaVeterinario.this, "DATOS BORRADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VerCitaVeterinario.this, CitasVeterinario.class);
            startActivity(intent);
            finish();
        }
        //endregion
    }
    //endregion

    //region Modificar la consulta actual
    private class ModificarConsultaTask extends AsyncTask<Cita, Void, String>
    {
        @Override
        protected String doInBackground(Cita... consultas)
        {
            String mensaje = "";
            String url = "http://192.168.1.143/ControlPaw/actualizarConsulta.php"; // Sustituye por tu IPv4

            try
            {
                // region Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                //endregion

                //region Guardamos los parámetros
                Cita consulta = consultas[0];
                String parametros = "idConsulta=" + consulta.getId() +
                        "&motivo=" + consulta.getMotivo() +
                        "&idMascota=" + consulta.getMascota().getId() +
                        "&fecha=" + consulta.getFecha();
                //endregion

                //region Enviamos los datos al PHP
                OutputStream outputStream = conexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                outputStream.close();
                //endregion

                //region Leemos la respuesta del PHP
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null)
                {
                    respuesta.append(linea);
                }
                //endregion

                //region Cerramos la conexión
                entrada.close();
                conexion.disconnect();
                //endregion

                //region Comprobamos la respuesta de la base de datos
                if (respuesta.toString().contains("Error: No existe una mascota con ese ID."))
                {
                    mensaje = "Error: " + respuesta.toString();
                }
                else
                {
                    mensaje = "";
                }
                //endregion
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return mensaje;
        }

        //region Tras la ejecución
        @Override
        protected void onPostExecute(String mensaje)
        {
            super.onPostExecute(mensaje);
            if (mensaje.isEmpty())
            {
                //region Notificamos al usuario de que se ha realizado la actualización
                Toast.makeText(VerCitaVeterinario.this, "DATOS MODIFICADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                //endregion

                //region Volvemos a la lista de consultas
                Intent intent = new Intent(VerCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                finish();
                //endregion
            }
            else
            {
                //region Notificamos al usuario de que se ha producido un error
                Toast.makeText(VerCitaVeterinario.this, mensaje + " DATOS NO GUARDADOS", Toast.LENGTH_SHORT).show();
                //endregion

                //region Volvemos a la lista de consultas
                Intent intent = new Intent(VerCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                finish();
                //endregion
            }
        }
        //endregion
    }
    //endregion
}