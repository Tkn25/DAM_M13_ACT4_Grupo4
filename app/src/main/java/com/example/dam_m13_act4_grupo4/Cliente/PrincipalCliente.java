package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class PrincipalCliente extends AppCompatActivity {

    private ImageButton mascotas, salud, citas, consejos, protectoras;
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
        mascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, MascotasCliente.class);
                startActivity(intent);
                finish();
            }
        });

        salud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, SaludCliente.class);
                startActivity(intent);
                finish();
            }
        });

        citas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, CitasCliente.class);
                startActivity(intent);
                finish();
            }
        });

        consejos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, ConsejosCliente.class);
                startActivity(intent);
                finish();
            }
        });

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