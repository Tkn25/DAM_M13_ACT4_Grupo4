package com.example.dam_m13_act4_grupo4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PrincipalCliente extends AppCompatActivity {

    private ImageButton mascotas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.principal_cliente);
        mascotas = findViewById(R.id.imageButton);

        mascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalCliente.this, MascotasCliente.class);
                startActivity(intent);
                finish();
            }
        });
    }
}