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


import com.example.dam_m13_act4_grupo4.POJO.Consejo;
import com.example.dam_m13_act4_grupo4.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertarConsejoVeterinario extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton volver;
    private Button insertar;
    private EditText titulo, contenido, enlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insertar_consejo_veterinario);

        //Enlazamos las variables con los elementos del layout
        volver = findViewById(R.id.imageButton17);
        insertar = findViewById(R.id.button3);
        titulo = findViewById(R.id.editTextTextMultiLineTit);
        contenido = findViewById(R.id.editTextTextMultiLineDesc);
        enlace = findViewById(R.id.editTextTextMultiLineEnlace);

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertarConsejoVeterinario.this, ConsejosVeterinario.class);
                startActivity(intent);
                finish();
            }
        });

        //En caso de pulsar el botón volver, se intentará insertar los datos.
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titulo.getText().toString().isEmpty() || contenido.getText().toString().isEmpty()){
                    Toast.makeText(InsertarConsejoVeterinario.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tit = titulo.getText().toString();
                String desc = contenido.getText().toString();
                String img = enlace.getText().toString();
                Consejo c = new Consejo(0, tit, desc, img);

                new InsertarConsejoTask().execute(c);
            }
        });
    }

    //Clase encargada de insertar los datos del nuevo seguimiento en la BBDD a través de un .php
    private class InsertarConsejoTask extends AsyncTask<Consejo, String, String> {
        @Override
        protected String doInBackground(Consejo... consejo) {
            String mensaje = "";

            try {
                //Escribimos la dirección del .php
                String url = "http://192.168.0.14/controlpaw/insertarConsejo.php"; //Sustituye por tu IPv4

                //Guardamos los parametros
                String parametros ="&titulo=" + consejo[0].getTitulo() +
                        "&descripcion=" + consejo[0].getDescripcion() +
                        "&img=" + consejo[0].getUrlImagen();

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

                //Cerramos la conexión
                entrada.close();
                conexion.disconnect();

                //Comprobamos la respuesta de la BD para comprobar que no hay errores
                if (respuesta.toString().contains("Error al insertar datos de consejo")) {
                    //Almacenamos el error
                    mensaje = respuesta.toString();
                } else {
                    mensaje = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mensaje;
        }

        //Cuando se termine de ejecutar...
        @Override
        protected void onPostExecute(String mensaje) {
            super.onPostExecute(mensaje);

            if (mensaje.isEmpty()) {
                //Si no hay mensaje de error, indicamos que salio correctamente y volvemos a la actividad anterior.
                Toast.makeText(InsertarConsejoVeterinario.this, "DATOS INSERTADOS CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InsertarConsejoVeterinario.this, ConsejosVeterinario.class);
                startActivity(intent);
                finish();
            } else {
                //Si ocurre algo, mostramos el error
                Toast.makeText(InsertarConsejoVeterinario.this, "Ha ocurrido un error al insertar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

}