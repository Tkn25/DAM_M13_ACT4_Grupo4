package com.example.dam_m13_act4_grupo4.Veterinario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dam_m13_act4_grupo4.R;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrarVeterinario extends AppCompatActivity {

    private EditText editTextNombre, editTextDNI, editTextTelefono, editTextDireccion, editTextEmail, editTextPass;
    private Button buttonInsertarCliente;
    private ImageButton volver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_veterinario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //region Enlazamos variables con elementos de layout
        volver = findViewById(R.id.ButtonVolverInsertarCliente);
        editTextNombre = findViewById(R.id.editTextNombreInsertarCliente);
        editTextDNI = findViewById(R.id.editTextDNIInsertarCliente);
        editTextTelefono = findViewById(R.id.editTextTelefonoInsertarCliente);
        editTextDireccion = findViewById(R.id.editTextDireccionInsertarCliente);
        editTextEmail = findViewById(R.id.editTextEmailInsertarCliente);
        editTextPass = findViewById(R.id.editTextPassInsertarCliente);
        buttonInsertarCliente = findViewById(R.id.buttonInsertarCliente);
        //endregion

        buttonInsertarCliente.setOnClickListener(new View.OnClickListener()
        //region Al hacer click sobre el botón de insertar
        {
            @Override
            public void onClick(View v)
            {
                String nombre = editTextNombre.getText().toString();
                String dni = editTextDNI.getText().toString();
                String telefono = editTextTelefono.getText().toString();
                String direccion = editTextDireccion.getText().toString();
                String email = editTextEmail.getText().toString();
                String pass = editTextPass.getText().toString();

                if (!nombre.isEmpty() && !dni.isEmpty() && !telefono.isEmpty() && !direccion.isEmpty() && !email.isEmpty() && !pass.isEmpty())
                //region En caso de no haber campos vacios
                {
                    String hashPass = hashSHA256(pass);

                    //region Enviar datos al PHP
                    String[] field = {"tipo", "usuario", "pass", "DNI", "nombre", "telefono", "direccion"};
                    String[] data = {"2", email, hashPass, dni, nombre, telefono, direccion};
                    PutData putData = new PutData("http://192.168.1.143/ControlPaw/registrarCliente.php", "POST", field, data);
                    //endregion

                    if (putData.startPut())
                    {
                        if (putData.onComplete())
                        //region Al completarse la operación
                        {
                            String result = putData.getResult();

                            Log.d("phpRespuesta", result);
                            if(result.equals("Cliente insertado correctamente"))
                            //region En caso de realizarse la inserción correctamente
                            {
                                Toast.makeText(RegistrarVeterinario.this, result, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrarVeterinario.this, PrincipalVeterinario.class);
                                startActivity(intent);
                                finish();
                            }
                            //endregion
                            else
                            {
                                Toast.makeText(RegistrarVeterinario.this, "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        //endregion
                    }
                }
                //endregion
                else
                //region En caso de haber campos vacios
                {
                    Toast.makeText(RegistrarVeterinario.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                //endregion
            }
        });
        //endregion

        //region Listener de botón para volver al menú principal
        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegistrarVeterinario.this, PrincipalVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion
    }

    //region Función para hashear contraseña
    private String hashSHA256(String text)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //endregion
}
