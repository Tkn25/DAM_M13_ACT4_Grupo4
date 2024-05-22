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

import com.example.dam_m13_act4_grupo4.POJO.Cita;
import com.example.dam_m13_act4_grupo4.POJO.Mascota;
import com.example.dam_m13_act4_grupo4.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InsertarCitaVeterinario extends AppCompatActivity {

    private EditText id;
    private EditText motivo;
    private EditText idMascota;
    private EditText fecha;
    private Button insertar;
    private ImageButton volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insertar_cita_veterinario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Enlazamos las variables con los elementos del layout
        id = findViewById(R.id.editTextIdInsertarConsulta);
        motivo = findViewById(R.id.editTextMotivoInsertarConsulta);
        idMascota = findViewById(R.id.editTextIdMascotaInsertar);
        fecha = findViewById(R.id.editTextFechaInsertarConsulta);
        insertar = findViewById(R.id.buttonInsertarConsulta);
        volver = findViewById(R.id.ButtonVolverInsertarConsulta);

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //En caso de que no esten todos los datos rellenos, no se enviaran los datos y saltará un mensaje.
                if (id.getText().toString().isEmpty() || motivo.getText().toString().isEmpty() || idMascota.getText().toString().isEmpty() || fecha.getText().toString().isEmpty()) {
                    Toast.makeText(InsertarCitaVeterinario.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Lo mismo pasará si la fecha no tiene el formato correcto
                String fechaString = fecha.getText().toString();
                if (!fechaString.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    Toast.makeText(InsertarCitaVeterinario.this, "La fecha debe tener formato YYYY-mm-dd", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Obtenemos los datos de la fecha actual
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String fechaActual = format.format(calendar.getTime());
                //Obtenemos los datos de la fecha introducida
                String newFecha = fecha.getText().toString();
                //Si la fecha introducida no es mayor a la fecha actrual, no se podrá agregar la cita.
                if (newFecha.compareTo(fechaActual) <= 0) {
                    Toast.makeText(InsertarCitaVeterinario.this, "La fecha debe ser mayor que la fecha actual", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Si los datos son correctos, se guardarán en nuevas variables para, posteriormente, guardarlos en un objeto Consultas.
                int id2 = Integer.parseInt(id.getText().toString());
                String newMotivo = motivo.getText().toString();
                Mascota mascota = new Mascota(Integer.parseInt(idMascota.getText().toString()));
                Cita c = new Cita(id2, newMotivo, mascota, newFecha);

                //Ejecutamos la clase encargada de enviar los datos a la BD.
                new InsertarConsultaTask().execute(c);
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un intent para volver a la actividad anterior y lo ejecutamos
                Intent intent = new Intent(InsertarCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                //Cerramos la actividad actual
                finish();
            }
        });
    }

    //Clase encargada de insertar los datos de la consulta en la BD a través de un .php
    private class InsertarConsultaTask extends AsyncTask<Cita, String, String> {
        @Override
        protected String doInBackground(Cita... consultas) {
            String mensaje = "";

            try {
                //Escribimos la dirección del .php
                String url = "http://192.168.0.14/ControlPaw/insertarConsulta.php"; //Sustituye por tu IPv4

                //Guardamos los parametros
                String parametros = "idConsulta=" + consultas[0].getId() +
                        "&motivo=" + consultas[0].getMotivo() +
                        "&idMascota=" + consultas[0].getMascota().getId() +
                        "&fecha=" + consultas[0].getFecha();

                //Establecemos la conexión
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                //Enviamos los datos a la BD
                OutputStream outputStream = conexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                outputStream.close();

                //Leemos la respuesta de la BD
                InputStream entrada = conexion.getInputStream();
                BufferedReader lector = new BufferedReader(new InputStreamReader(entrada));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                //CerramoS la conexión
                entrada.close();
                conexion.disconnect();

                //Comprobamos la respuesta de la BD para no duplicar la ID de la consulta y comprobar que que la ID de la mascota sea correcta
                if (respuesta.toString().contains("Error: La mascota con el ID proporcionado no existe") || respuesta.toString().contains("Error: Ya existe una consulta con la ID introducida")) {
                    // Almacenamos el error
                    mensaje = respuesta.toString();
                } else {
                    mensaje = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                mensaje = "Error " + e.getMessage();
            }
            return mensaje;
        }

        // Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(String mensaje) {
            super.onPostExecute(mensaje);

            if (mensaje.isEmpty()) {
                //Si no hay mensaje de error, indicamos que salio correctamente y volvemos a la actividad anterior.
                Toast.makeText(InsertarCitaVeterinario.this, "DATOS INSERTADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InsertarCitaVeterinario.this, CitasVeterinario.class);
                startActivity(intent);
                finish();
            } else {
                //Mostramos el error
                Toast.makeText(InsertarCitaVeterinario.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}