package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class Protectoras extends AppCompatActivity {

    //Creamos las variables globales de la clase
    private ImageButton svpap, spac, volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_protectoras);

        //Asociamos las variables con sus elementos del layout
        svpap = findViewById(R.id.imageButton9);
        spac = findViewById(R.id.imageButton10);
        volver = findViewById(R.id.imageButton8);

        //En caso de pulsar el botón svpap, se abrirá la actividad con su web de adopciones.
        svpap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Protectoras.this, ProtectoraSvpap.class);
                startActivity(intent);
                finish();
            }
        });

        //En caso de pulsar el botón spac, se abrirá la actividad con su web de adopciones.
        spac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Protectoras.this, ProtectoraSpac.class);
                startActivity(intent);
                finish();
            }
        });

        //En caso de pulsar el botón volver, se volverá a la actividad anterior.
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Protectoras.this, PrincipalCliente.class);
                startActivity(intent);
                finish();
            }
        });
    }
}