package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.Login.LoginSeleccion;
import com.example.dam_m13_act4_grupo4.R;

public class PrincipalCliente extends AppCompatActivity {

    private ImageButton mascotas, salud, citas, consejos, protectoras, logout;
    private static String idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.principal_cliente);
        mascotas = findViewById(R.id.imageButton);
        salud = findViewById(R.id.imageButton2);
        citas = findViewById(R.id.imageButton3);
        consejos = findViewById(R.id.imageButton4);
        protectoras = findViewById(R.id.imageButton5);
        logout = findViewById(R.id.imageButton16);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idCliente = extras.getString("idCliente");
        }

        mascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, MascotasCliente.class);
                intent.putExtra("user", idCliente);
                startActivity(intent);
            }
        });

        salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, SaludCliente.class);
                intent.putExtra("user", idCliente);
                startActivity(intent);
            }
        });

        citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, CitasCliente.class);
                intent.putExtra("user", idCliente);
                startActivity(intent);
            }
        });

        consejos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, ConsejosCliente.class);
                startActivity(intent);
            }
        });

        protectoras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, Protectoras.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, LoginSeleccion.class);
                startActivity(intent);
            }
        });
    }
}