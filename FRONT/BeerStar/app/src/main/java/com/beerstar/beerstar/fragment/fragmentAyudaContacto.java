package com.beerstar.beerstar.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.beerstar.beerstar.R;

public class fragmentAyudaContacto extends Fragment {

    private EditText editTextNombre, editTextEmail, editTextTelefono, editTextDescripcion;
    private Button botonEnviar;

    public fragmentAyudaContacto() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_ayuda_contacto, container, false);

        // Inicializar vistas
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextTelefono = view.findViewById(R.id.editTextTelefono);
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        botonEnviar = view.findViewById(R.id.botonEnviar);

        // Configurar el botón de enviar
        botonEnviar.setOnClickListener(v -> enviarFormulario());

        return view;
    }

    private void enviarFormulario() {
        // Obtener los datos del formulario
        String nombre = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simular el envío del formulario (puedes reemplazar esto con una llamada a una API)
        String mensaje = "Nombre: " + nombre + "\nEmail: " + email + "\nTeléfono: " + telefono + "\nDescripción: " + descripcion;
        Toast.makeText(getContext(), "Formulario enviado:\n" + mensaje, Toast.LENGTH_LONG).show();
    }
}