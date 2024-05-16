package com.example.dam_m13_act4_grupo4.Veterinario;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import com.example.dam_m13_act4_grupo4.Cliente.PrincipalCliente;
import com.example.dam_m13_act4_grupo4.Dueno;
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
    private EditText nombre, especie, raza, peso, castrado, fechaNacimiento, microchip;
    private ImageButton volver;
    private Spinner spGenero;
    private Spinner spEspecie;
    private Spinner spNombre;
    private Spinner spRaza;
    private Spinner spCastrado;
    private Spinner spEnfermedad;
    private Spinner spBaja;
    int idMascota;
    int idGenero;
    int idEspecie;
    String nombreMascota;
    int generoMascota;
    int especieMascota;
    String razaMascota;
    float pesoMascota;
    int castradoMascota;
    String fechaMascota;
    String microchipMascota;
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
        floEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eliminar();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                guardarCambios();
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatosMascotaVet.this, MascotasVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //Recibimos el intent de la actividad anterior con los datos de la BBDD.
        Intent intent = getIntent();
        clave = intent.getStringExtra("clave");
         idMascota = intent.getIntExtra("idMascota",0);
        String nombreMascota = intent.getStringExtra("nombre");
        int generoMascota = intent.getIntExtra("genero", 0);
        int especieMascota = intent.getIntExtra("especie", 0);
        String razaMascota = intent.getStringExtra("raza");
        float pesoMascota = intent.getFloatExtra("peso", 0);
        int castradoMascota = intent.getIntExtra("castrado", 0);
        String fechaMascota = intent.getStringExtra("fecha");
        String microchipMascota = intent.getStringExtra("microchip");

        idCliente = intent.getIntExtra("idCliente", -1);
        boolean enfermedad = Boolean.parseBoolean(intent.getStringExtra("enfermedad"));
      //  boolean tipo = Boolean.parseBoolean(intent.getStringExtra("tipo"));
        boolean baja = Boolean.parseBoolean(intent.getStringExtra("baja"));
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

        new ModificarMascotaTask(clave).execute();


    }
    private void eliminar() {
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
            String url = "http://192.168.1.179/ControlPaw/"+clave+".php";

            try {

                        data = "idMascota=" + idMascota + "&nombre=" + nombre.getText().toString() + "&idDueno=" + getDuenoSeleccionado().getIdCliente()
                                + "&idGenero=" + (spGenero.getSelectedItemPosition() + 1) + "&raza=" + raza.getText().toString() + "&idEspecie=" + (spEspecie.getSelectedItemPosition() + 1)
                                + "&peso=" + peso.getText().toString() + "&castrado=" + (spCastrado.getSelectedItemPosition() + 1) + "&fechaNacimiento=" + fechaNacimiento.getText().toString()
                                + "&microchip=" + microchip.getText().toString() + "&enfermedad=" + spEnfermedad.getSelectedItemPosition() + "&baja=" + spBaja.getSelectedItemPosition();



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
            Toast.makeText(DatosMascotaVet.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DatosMascotaVet.this, MascotasVeterinario.class);
            startActivity(intent);
            finish();
        }
    }
    private class ObtenerClientesTask extends AsyncTask<Void, Void, ArrayList<Dueno>> {
        int idCli;
    public ObtenerClientesTask(int idCli)
    {
        this.idCli = idCli;
    }
        @Override
        protected ArrayList<Dueno> doInBackground(Void... voids) {
            String url = "http://192.168.1.179/ControlPaw/Clientes.php"; //Sustituye por tu IPv4

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
                   // int tipo = Integer.parseInt(element.getElementsByTagName("tipo").item(0).getTextContent());
                   // int usuario = Integer.parseInt(element.getElementsByTagName("usuario").item(0).getTextContent());
                   // String contraseña = element.getElementsByTagName("contraseña").item(0).getTextContent();
                   // String dni = element.getElementsByTagName("DNI").item(0).getTextContent();
                    String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                   // String telefono = element.getElementsByTagName("telefono").item(0).getTextContent();
                   // String dir = element.getElementsByTagName("direccion").item(0).getTextContent();

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
            // Poblar el Spinner con la lista de dueños
            spinnerClientes = findViewById(R.id.spinner);
            ArrayAdapter<Dueno> adapter = new ArrayAdapter<>(DatosMascotaVet.this, android.R.layout.simple_spinner_item, duenoList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClientes.setAdapter(adapter);

            // Configurar el listener del Spinner
            spinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Obtener el cliente seleccionado
                    Dueno clienteSeleccionado = (Dueno) parentView.getItemAtPosition(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // No se seleccionó ningún cliente
                }
            });
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