package com.example.dam_m13_act4_grupo4.Veterinario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class PrincipalVeterinario extends AppCompatActivity {

    private ImageButton mascotas, salud, citas, consejos, registrar;
    private static String idVet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal_veterinario);

        mascotas = findViewById(R.id.imageButton);
        salud = findViewById(R.id.imageButton2);
        citas = findViewById(R.id.imageButton3);
        consejos = findViewById(R.id.imageButton4);
        registrar = findViewById(R.id.imageButton5);

        //region Recogemos la ID del usuario que nos ha pasado el login
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idVet = extras.getString("idEmpleado");
        }
        //endregion

        //region Listener del botón para acceder a la sección de mascotas
        mascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalVeterinario.this, MascotasVeterinario.class);
                intent.putExtra("user", idVet);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Listener del botón para acceder a la sección de salud
        salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalVeterinario.this, SaludVeterinario.class);
                intent.putExtra("user", idVet);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Listener del botón para acceder a la sección de citas
        citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalVeterinario.this, CitasVeterinario.class);
                intent.putExtra("user", idVet);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Listener del botón para acceder a la sección de consejos
        consejos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalVeterinario.this, ConsejosVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion

        //region Listener del botón para acceder a la sección de registro de nuevos usuarios
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalVeterinario.this, RegistrarVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion
    }
}