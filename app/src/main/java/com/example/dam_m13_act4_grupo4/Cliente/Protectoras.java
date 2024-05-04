package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class Protectoras extends AppCompatActivity {

    private ImageButton svpap, spac, volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_protectoras);
        svpap = findViewById(R.id.imageButton9);
        spac = findViewById(R.id.imageButton10);
        volver = findViewById(R.id.imageButton8);

        svpap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Protectoras.this, ProtectoraSvpap.class);
                startActivity(intent);
                finish();
            }
        });

        spac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Protectoras.this, ProtectoraSpac.class);
                startActivity(intent);
                finish();
            }
        });

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