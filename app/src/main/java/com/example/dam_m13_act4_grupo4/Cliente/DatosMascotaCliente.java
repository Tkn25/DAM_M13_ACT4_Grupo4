package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class DatosMascotaCliente extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private TextView nombre, genero, especie, raza, peso, castrado, fechaNacimiento, microchip;
    private ImageButton volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_mascota_cliente);

        //Asociamos las variables con sus elementos del layout
         nombre = findViewById(R.id.textViewNombreMCliente);
         genero = findViewById(R.id.textViewGeneroMCliente);
         especie = findViewById(R.id.textViewEspecieMCliente);
         raza = findViewById(R.id.textViewRazaMCliente);
         peso = findViewById(R.id.textViewPesoMCliente);
         castrado = findViewById(R.id.textViewCastradoMCliente);
         fechaNacimiento = findViewById(R.id.textViewFechaMCliente);
         microchip = findViewById(R.id.textViewMicrochipMCliente);
         volver = findViewById(R.id.imageButton6);

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatosMascotaCliente.this, MascotasCliente.class);
                startActivity(intent);
                finish();
            }
        });

        //Recibimos el intent de la actividad anterior con los datos de la BBDD.
            Intent intent = getIntent();
            String nombreMascota = intent.getStringExtra("nombre");
            int generoMascota = intent.getIntExtra("genero", 0);
            int especieMascota = intent.getIntExtra("especie", 0);
            String razaMascota = intent.getStringExtra("raza");
            float pesoMascota = intent.getFloatExtra("peso", 0);
            int castradoMascota = intent.getIntExtra("castrado", 0);
            String fechaMascota = intent.getStringExtra("fecha");
            String microchipMascota = intent.getStringExtra("microchip");
            //Asignamos el nombre según el dato recibido.
            nombre.setText(nombreMascota);

            //Dependiendo del número recibido, el genero será uno u otro.
            if(generoMascota == 1){
                genero.setText("Macho");
            } else if (generoMascota == 2){
                genero.setText("Hembra");
            } else {
                genero.setText("Desconocido");
            }

        //Dependiendo del número recibido, la especie será una u otra.
            if(especieMascota == 1){
                especie.setText("Perro");
            } else if(especieMascota == 2){
                especie.setText("Gato");
            } else if(especieMascota == 3){
                especie.setText("Hurón");
            } else if(especieMascota == 4){
                especie.setText("Hámster");
            } else {
                especie.setText("Desconocido");
            }

            //Asignamos la raza, el peso y la fecha de nacimiento según los datos recibidos.
            raza.setText(razaMascota);
            peso.setText(String.valueOf(pesoMascota));
            fechaNacimiento.setText(fechaMascota);

            //Si encuentra microchip ese será el número, sino, aparecerá un texto
            if(microchipMascota == null){
                microchip.setText("Animal sin microchip");
            } else {
                microchip.setText(microchipMascota);
            }

            //Según el número recibido aparecerá si el animal está castrado o no
            if (castradoMascota == 0){
                castrado.setText("No");
            } else if (castradoMascota == 1){
                castrado.setText("Sí");
            } else {
                castrado.setText("Desconocido");
            }
    }
}