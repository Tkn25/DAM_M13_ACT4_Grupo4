package com.example.dam_m13_act4_grupo4.Veterinario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dam_m13_act4_grupo4.POJO.Dueno;
import com.example.dam_m13_act4_grupo4.POJO.Global;
import com.example.dam_m13_act4_grupo4.R;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserlistVeterinario extends AppCompatActivity
{

    private ImageButton volver;
    private RecyclerView recyclerView;
    private ClienteAdapter adapter;
    private List<Dueno> clienteList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist_veterinario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        volver = findViewById(R.id.imageButton13);
        recyclerView = findViewById(R.id.recyclerCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clienteList = new ArrayList<>();
        adapter = new ClienteAdapter(clienteList);
        recyclerView.setAdapter(adapter);

        new FetchDataTask().execute("http://192.168.0.14/controlpaw/verUserlist.php");

        //region Listener del botón para volver al menú principal de veterinario
        volver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(UserlistVeterinario.this, PrincipalVeterinario.class);
                startActivity(intent);
                finish();
            }
        });
        //endregion
    }

    private class FetchDataTask extends AsyncTask<String, Void, List<Dueno>>
    {
        @Override
        protected List<Dueno> doInBackground(String... strings)
        {
            String urlString = strings[0];
            List<Dueno> clientes = new ArrayList<>();
            try
            {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    in.close();

                    Document doc = Global.convertirStringToXMLDocument(response.toString());
                    NodeList idList = doc.getElementsByTagName("idCliente");
                    NodeList nombreList = doc.getElementsByTagName("nombre");
                    NodeList dniList = doc.getElementsByTagName("DNI");
                    NodeList telefonoList = doc.getElementsByTagName("telefono");
                    NodeList direccionList = doc.getElementsByTagName("direccion");

                    for (int i = 0; i < idList.getLength(); i++)
                    {
                        int idCliente = Integer.parseInt(idList.item(i).getTextContent());
                        String nombre = nombreList.item(i).getTextContent();
                        String dni = dniList.item(i).getTextContent();
                        String telefono = telefonoList.item(i).getTextContent();
                        String direccion = direccionList.item(i).getTextContent();

                        Dueno cliente = new Dueno(idCliente, nombre, dni, telefono, direccion);
                        clientes.add(cliente);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return clientes;
        }

        @Override
        protected void onPostExecute(List<Dueno> clientes)
        {
            super.onPostExecute(clientes);
            if (clienteList != null && !clienteList.isEmpty())
            {
                clienteList.clear();
                clienteList.addAll(clientes);
                adapter.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No se ha podido establecer la conexión. Por favor, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>
    {

        private List<Dueno> clienteList;

        public ClienteAdapter(List<Dueno> clienteList) {
            this.clienteList = clienteList;
        }

        @NonNull
        @Override
        public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_layout_userlist, parent, false);
            return new ClienteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position)
        {
            Dueno cliente = clienteList.get(position);
            holder.idCliente.setText(String.valueOf(cliente.getIdCliente()));
            holder.nombre.setText(cliente.getNombre());
            holder.dni.setText(cliente.getDNI());
            holder.telefono.setText(cliente.getTelefono());
            holder.direccion.setText(cliente.getDireccion());
        }

        @Override
        public int getItemCount() {
            return clienteList.size();
        }

        public class ClienteViewHolder extends RecyclerView.ViewHolder
        {
            TextView idCliente, nombre, dni, telefono, direccion;

            public ClienteViewHolder(@NonNull View itemView)
            {
                super(itemView);
                idCliente = itemView.findViewById(R.id.textViewIdClienteItem);
                nombre = itemView.findViewById(R.id.textViewNombreItem);
                dni = itemView.findViewById(R.id.textViewDNIItem);
                telefono = itemView.findViewById(R.id.textViewTelefonoItem);
                direccion = itemView.findViewById(R.id.textViewDireccionItem);
            }
        }
    }
}
