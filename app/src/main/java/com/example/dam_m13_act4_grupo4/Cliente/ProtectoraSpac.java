package com.example.dam_m13_act4_grupo4.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dam_m13_act4_grupo4.R;

public class ProtectoraSpac extends AppCompatActivity {

    private ImageButton volver;
    private WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_protectora_spac);

        volver = findViewById(R.id.imageButton12);
        web = findViewById(R.id.webSpac);

        web.setWebViewClient(new WebViewClient());
        web.loadUrl("https://spac.cat/listado");
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProtectoraSpac.this, Protectoras.class);
                startActivity(intent);
                finish();
            }
        });
    }
}