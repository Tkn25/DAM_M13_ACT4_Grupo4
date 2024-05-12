package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class PrincipalCliente extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton mascotas, salud, citas, consejos, protectoras;
    private static String idDueno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.principal_cliente);

        //Asociamos las variables con sus elementos del layout
        mascotas = findViewById(R.id.imageButton);
        salud = findViewById(R.id.imageButton2);
        citas = findViewById(R.id.imageButton3);
        consejos = findViewById(R.id.imageButton4);
        protectoras = findViewById(R.id.imageButton5);

        //Obtenemos la ID del usuario cliente
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idDueno = extras.getString("user");
        }

        //Valor de prueba
        idDueno = "2";

        //En caso de clicar el botón mascotas, se accederá a la actividad que muestra la lista de mascotas del usuario
        mascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, MascotasCliente.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
                finish();
            }
        });

        //En caso de clicar el botón mascotas, se accederá a la actividad que muestra la lista de tratamientos del usuario
        salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, SaludCliente.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
                finish();
            }
        });

        //En caso de clicar el botón mascotas, se accederá a la actividad que muestra la lista de citas del usuario
        citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, CitasCliente.class);
                intent.putExtra("user", idDueno);
                startActivity(intent);
                finish();
            }
        });

        //En caso de clicar el botón mascotas, se accederá a la actividad que muestra una lista de consejos
        consejos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, ConsejosCliente.class);
                startActivity(intent);
                finish();
            }
        });

        //En caso de clicar el botón mascotas, se accederá a la actividad que muestra info de unas protectoras
        protectoras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, Protectoras.class);
                startActivity(intent);
                finish();
            }
        });
    }
}