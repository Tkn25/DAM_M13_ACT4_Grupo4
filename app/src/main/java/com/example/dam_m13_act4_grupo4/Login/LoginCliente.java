package com.example.dam_m13_act4_grupo4.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dam_m13_act4_grupo4.Cliente.PrincipalCliente;
import com.example.dam_m13_act4_grupo4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LoginCliente extends AppCompatActivity {
    private TextInputEditText textInputEditTextUsername, textInputEditTextPassword;
    private Button buttonLogin;
    private ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.login_btn);
        goBack = findViewById(R.id.imageButton14);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = textInputEditTextUsername.getText().toString();
                final String password = hashPassword(textInputEditTextPassword.getText().toString());

                if (!username.isEmpty() && !password.isEmpty())
                //region Si hay texto en los inputs
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String[] campo = {"username", "password"};
                            String[] datos = {username, password};
                            PutData putData = new PutData("http://192.168.0.14/ControlPaw/loginCliente.php", "POST", campo, datos);
                            if (putData.startPut())
                            //region Si logramos acceder al PHP
                            {
                                if (putData.onComplete())
                                //region Al terminar de ejecutarse el PHP
                                {
                                    final String result = putData.getResult();
                                    runOnUiThread(new Runnable() {
                                        //region Comprobación de login
                                        @Override
                                        public void run() {
                                            if (result.startsWith("<?xml"))
                                            //region Si recibimos el XML correctamente
                                            {
                                                try
                                                {
                                                    //region Parseamos el XML
                                                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                                    DocumentBuilder builder = factory.newDocumentBuilder();
                                                    InputSource is = new InputSource(new StringReader(result));
                                                    Document document = builder.parse(is);
                                                    //endregion

                                                    //region Extraemos los datos del XML

                                                    final NodeList msgNode = document.getElementsByTagName("msg");
                                                    final NodeList checkNode = document.getElementsByTagName("success");
                                                    final String check = checkNode.item(0).getTextContent();
                                                    final String msg = msgNode.item(0).getTextContent();

                                                    //endregion
                                                    if (check.equals("true"))
                                                    //region En caso de haber recibido una ID: Login correcto
                                                    {
                                                        final NodeList idNode = document.getElementsByTagName("idCliente");
                                                        final String idCliente = idNode.item(0).getTextContent();
                                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(LoginCliente.this, PrincipalCliente.class);
                                                        intent.putExtra("idCliente", idCliente);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    //endregion
                                                    else
                                                    //region En caso de no haber recibido una ID: Credenciales incorrectas
                                                    {
                                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                    //endregion
                                                }
                                                catch (Exception e)
                                                //region En caso de respuesta no XML (Esto no es relevante, a no ser que modifiquemos los PHP en el futuro)
                                                {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                                }
                                                //endregion
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        //endregion
                                    });
                                }
                                //endregion
                            }
                            //endregion
                            else
                            //region En caso de no poder acceder al PHP
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            //endregion
                        }
                    }).start();
                }
                //endregion
                else
                //region Si no hay texto en los inputs
                {
                    Toast.makeText(getApplicationContext(), "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }
                //endregion
            }
        });

        //region Listener de botón para volver a selección de login
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginCliente.this, LoginSeleccion.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion


    }
    //region Función para hashear contraseña
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    //endregion
}