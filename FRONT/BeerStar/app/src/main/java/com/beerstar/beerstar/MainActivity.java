package com.beerstar.beerstar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.beerstar.beerstar.fragment.fragmentCarrito;
import com.beerstar.beerstar.fragment.fragmentChat;
import com.beerstar.beerstar.fragment.fragmentLotes;
import com.beerstar.beerstar.fragment.fragmentProveedores;
import com.beerstar.beerstar.fragment.fragmentTienda;
import com.beerstar.beerstar.fragment.fragmentUsuario;
import com.beerstar.beerstar.service.RetrofitClient;
import com.beerstar.beerstar.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ImageButton userButton;
    private int lastSelectedSpinnerPosition = -1; // <--- Añadido para forzar la recarga del fragmento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(getApplicationContext());
        RetrofitClient.init(getApplicationContext(), sessionManager);

        Spinner menuSpinner = findViewById(R.id.menu_spinner);
        String[] options = {"Tienda", "Lotes", "Proveedores", "Ayuda y Contacto"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_custom_layout,
                R.id.spinner_text,
                options
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView icon = view.findViewById(R.id.spinner_icon);
                icon.setImageResource(R.drawable.menu);
                TextView text = view.findViewById(R.id.spinner_text);
                text.setVisibility(View.GONE);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ImageView icon = view.findViewById(R.id.spinner_icon);
                icon.setVisibility(View.GONE);
                TextView text = view.findViewById(R.id.spinner_text);
                text.setVisibility(View.VISIBLE);
                return view;
            }
        };

        menuSpinner.setAdapter(adapter);

        // Este listener permite recargar incluso si seleccionas la misma opción
        menuSpinner.setOnTouchListener((v, event) -> {
            lastSelectedSpinnerPosition = menuSpinner.getSelectedItemPosition();
            return false;
        });

        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == lastSelectedSpinnerPosition) {
                    // Forzar recarga del fragmento aunque sea la misma opción
                    Log.d("MainActivity", "Forzando recarga del fragmento: " + options[position]);
                }
                switch (options[position]) {
                    case "Proveedores":
                        loadFragment(new fragmentProveedores());
                        break;
                    case "Tienda":
                        loadFragment(new fragmentTienda());
                        break;
                    case "Lotes":
                        loadFragment(new fragmentLotes());
                        break;
                    case "Ayuda y Contacto":
                        loadFragment(new fragmentChat());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ImageButton cartButton = findViewById(R.id.cart_button);
        cartButton.setOnClickListener(v -> loadFragment(new fragmentCarrito()));

        userButton = findViewById(R.id.user_button);
        userButton.setOnClickListener(v -> loadFragment(new fragmentUsuario()));

        if (savedInstanceState == null) {
            loadFragment(new fragmentTienda());
        }

        actualizarIconoUsuario(sessionManager.isLoggedIn());
    }

    public void actualizarIconoUsuario(boolean logueado) {
        Log.d("MainActivity", "actualizarIconoUsuario llamado con loggedIn: " + logueado);
        if (userButton == null) {
            userButton = findViewById(R.id.user_button);
            Log.d("MainActivity", "userButton era null, lo hemos encontrado.");
        }
        if (logueado) {
            userButton.setImageResource(R.drawable.usuarioconectado);
            Log.d("MainActivity", "Cambiando a usuarioconectado (verde).");
        } else {
            userButton.setImageResource(R.drawable.usuario);
            Log.d("MainActivity", "Cambiando a usuario (negro/gris).");
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
