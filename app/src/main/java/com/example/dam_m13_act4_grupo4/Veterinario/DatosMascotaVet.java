package com.example.dam_m13_act4_grupo4.Veterinario;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dam_m13_act4_grupo4.POJO.Dueno;
import com.example.dam_m13_act4_grupo4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DatosMascotaVet extends AppCompatActivity {
    private EditText nombre, raza, peso, fechaNacimiento, microchip;
    private ImageButton volver;
    private Spinner spGenero;
    private Spinner spEspecie;
    private Spinner spCastrado;
    private Spinner spEnfermedad;
    private Spinner spBaja;
    private int idMascota;
    private String nombreMascota;
    private int generoMascota;
    private int especieMascota;
    private String razaMascota;
    private float pesoMascota;
    private int castradoMascota;
    private  String fechaMascota;
    private String microchipMascota;
    private boolean enfermedad;
    private boolean baja;

    private String clave;
    ArrayList<Dueno> duenoList = new ArrayList<>();
    Spinner spinnerClientes;

    int idCliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_mascota_vet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombre = findViewById(R.id.editTextTextNom);
        spGenero = findViewById(R.id.spGenero);
        spEspecie = findViewById(R.id.spEspecie);
        raza = findViewById(R.id.editTextRaza);
        peso = findViewById(R.id.editTextTextPes);
        spCastrado = findViewById(R.id.spCastrado);
        spEnfermedad = findViewById(R.id.spinner2);
        spBaja = findViewById(R.id.spinner3);
        fechaNacimiento = findViewById(R.id.editTextTextFec);
        microchip = findViewById(R.id.editTextTextMic);
        //Rellenamos los espiner con su respectiva informacion
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.opciones_genero, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenero.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.opciones_especie, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEspecie.setAdapter(adapter2);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.opciones_si_no, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCastrado.setAdapter(adapter4);
        volver = findViewById(R.id.imageButton6);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this,
                R.array.opciones_si_no, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEnfermedad.setAdapter(adapter5);
        volver = findViewById(R.id.imageButton6);
        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(this,
                R.array.opciones_si_no, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBaja.setAdapter(adapter6);
        volver = findViewById(R.id.imageButton6);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        FloatingActionButton floEliminar = findViewById(R.id.eliminar);
        //Boton para eliminar una mascota
        floEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eliminar();
            }
        });
        //Boton para guardar cambios
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                guardarCambios();
            }
        });
        //Boton para volver
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llamos a la vista mascota veterinario y cerramos esta
                Intent intent = new Intent(DatosMascotaVet.this, MascotasVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //Recibimos el intent de la actividad anterior con los datos de la BBDD.
        Intent intent = getIntent();
        clave = intent.getStringExtra("clave");
        if ("insertarMascota".equals(clave)) {
            floEliminar.setVisibility(View.GONE);  // Oculta el botón
        }
         idMascota = intent.getIntExtra("idMascota",0);
        nombreMascota = intent.getStringExtra("nombre");
        generoMascota = intent.getIntExtra("genero", 0);
        especieMascota = intent.getIntExtra("especie", 0);
        razaMascota = intent.getStringExtra("raza");
        pesoMascota = intent.getFloatExtra("peso", 0);
        castradoMascota = intent.getIntExtra("castrado", 0);
        fechaMascota = intent.getStringExtra("fecha");
        microchipMascota = intent.getStringExtra("microchip");
        idCliente = intent.getIntExtra("idCliente", -1);
        enfermedad = Boolean.parseBoolean(intent.getStringExtra("enfermedad"));
        baja = Boolean.parseBoolean(intent.getStringExtra("baja"));
        nombre.setText(nombreMascota);
        spGenero.setSelection(generoMascota-1);
        spEspecie.setSelection(especieMascota-1);
        raza.setText(razaMascota);
        peso.setText(String.valueOf(pesoMascota));
        spCastrado.setSelection(castradoMascota-1);
         if (enfermedad == true)
         {
             spEnfermedad.setSelection(1);
         }
         else {
             spEnfermedad.setSelection(0);
         }
        if (baja == true)
        {
            spBaja.setSelection(1);
        }
        else {
            spBaja.setSelection(0);
        }

        fechaNacimiento.setText(fechaMascota);
        microchip.setText(microchipMascota);
        new ObtenerClientesTask(idCliente).execute();
    }
    private void guardarCambios() {
        //La clave viene rellena de la pantalla anterior
        new ModificarMascotaTask(clave).execute();
    }
    private void eliminar() {
        //Creamos un popup para que el usuario confirme la eliminacion de la mascota
        AlertDialog.Builder builder = new AlertDialog.Builder(DatosMascotaVet.this);
        builder.setMessage("¿Estás seguro de que quieres eliminar esta mascota?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clave = "eliminarMascota";
                        new ModificarMascotaTask(clave).execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Metodo para almacenar el objeto dueño seleccionado en el espiner
    private Dueno getDuenoSeleccionado() {
        Spinner spinnerClientes = findViewById(R.id.spinner);
        int position = spinnerClientes.getSelectedItemPosition();
        return duenoList.get(position);
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
    // Clase para la modificacion de datos de mascota en tabla
    private class ModificarMascotaTask extends AsyncTask<Void, Void, String> {
        String clave;
        String data;
        public ModificarMascotaTask(String clave)
        {
            this.clave = clave;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String resultado = "";
            String url = "http://192.168.0.14/ControlPaw/"+clave+".php";

            try {

            // Recoger los datos de los campos, asignando una cadena vacía si están vacíos.
            String nombreStr = nombre.getText().toString().isEmpty() ? "" : nombre.getText().toString();
            String razaStr = raza.getText().toString().isEmpty() ? "" : raza.getText().toString();
            String pesoStr = peso.getText().toString().isEmpty() ? "" : peso.getText().toString();
            String fechaNacimientoStr = fechaNacimiento.getText().toString().isEmpty() ? "" : fechaNacimiento.getText().toString();
            String microchipStr = microchip.getText().toString().isEmpty() ? "" : microchip.getText().toString();

            // Construir la cadena de datos
            data = "idMascota=" + idMascota
                    + "&nombre=" + nombreStr
                    + "&idDueno=" + getDuenoSeleccionado().getIdCliente()
                    + "&idGenero=" + (spGenero.getSelectedItemPosition() + 1)
                    + "&raza=" + razaStr
                    + "&idEspecie=" + (spEspecie.getSelectedItemPosition() + 1)
                    + "&peso=" + pesoStr
                    + "&castrado=" + (spCastrado.getSelectedItemPosition() + 1)
                    + "&fechaNacimiento=" + fechaNacimientoStr
                    + "&microchip=" + microchipStr
                    + "&enfermedad=" + spEnfermedad.getSelectedItemPosition()
                    + "&baja=" + spBaja.getSelectedItemPosition();

                // Convertir la cadena de datos a bytes
                byte[] postData = data.getBytes(StandardCharsets.UTF_8);

                // Crear la conexión HTTP
                URL direccion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) direccion.openConnection();

                // Configurar la conexión para enviar datos y usar el método POST
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                // Escribir los datos en el cuerpo de la solicitud
                try (DataOutputStream wr = new DataOutputStream(conexion.getOutputStream())) {
                    wr.write(postData);
                }

                // Leer la respuesta del servidor
                BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }
                resultado = respuesta.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(String resultado) {
            // Si la consulta no devuelve fallos mostraremos un toast indicando que ha ido bien
            Toast.makeText(DatosMascotaVet.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DatosMascotaVet.this, MascotasVeterinario.class);
            startActivity(intent);
            finish();
        }
    }
    // Clase para rellenar el epinner de clientes
    private class ObtenerClientesTask extends AsyncTask<Void, Void, ArrayList<Dueno>> {
        int idCli;
    public ObtenerClientesTask(int idCli)
    {
        this.idCli = idCli;
    }
        @Override
        protected ArrayList<Dueno> doInBackground(Void... voids) {
            String url = "http://192.168.0.14/ControlPaw/Clientes.php"; //Sustituye por tu IPv4

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


                Document document = convertirStringToXMLDocument(respuesta.toString());

                NodeList listaDueño = document.getElementsByTagName("cliente");

                for (int i = 0; i < listaDueño.getLength(); i++) {
                    Element element = (Element) listaDueño.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("idCliente").item(0).getTextContent());
                    String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                    Dueno d = new Dueno(id, nombre);
                    duenoList.add(d);
                }
                entrada.close();
                conexion.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return duenoList;
        }
        @Override
        protected void onPostExecute(ArrayList<Dueno> duenoList) {
            super.onPostExecute(duenoList);

            // Rellenar el Spinner con la lista de dueños
            spinnerClientes = findViewById(R.id.spinner);
            ArrayAdapter<Dueno> adapter = new ArrayAdapter<>(DatosMascotaVet.this, android.R.layout.simple_spinner_item, duenoList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClientes.setAdapter(adapter);

            // Configurar el listener del Spinner
           /* spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Obtener el cliente seleccionado
                    Dueno clienteSeleccionado = (Dueno) parentView.getItemAtPosition(position);

                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });*/
            // Obtener el cliente seleccionado
            if(this.idCli == -1)
            {
                this.idCli = 1;
            }
            for (int i = 0; i < duenoList.size(); i++) {
                if (duenoList.get(i).getIdCliente() == idCliente) {
                    spinnerClientes.setSelection(i);
                    break;
                }
            }
        }

    }
}