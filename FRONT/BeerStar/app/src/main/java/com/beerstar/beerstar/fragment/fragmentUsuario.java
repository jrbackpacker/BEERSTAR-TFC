package com.beerstar.beerstar.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.service.RetrofitClient;
import com.beerstar.beerstar.service.ApiService;
import com.beerstar.beerstar.service.Usuario;
import com.beerstar.beerstar.response.responseUsuario; // Asegúrate de que este import apunta a tu responseUsuario correcto
import com.beerstar.beerstar.utils.SessionManager;
import com.beerstar.beerstar.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragmentUsuario extends Fragment {
    private static final String TAG = "fragmentUsuario";

    private EditText editTextEmailLogin, editTextPasswordLogin;
    private Button botonLogin, botonRegistro;
    private LinearLayout layoutLoginRegistro;

    private TextView textViewEmailPerfil;
    private EditText editTextNombrePerfil, editTextTelefonoPerfil, editTextDireccionPerfil;
    private Button botonActualizarPerfil, botonLogout;
    private LinearLayout layoutPerfilUsuario;

    private ApiService apiService;
    private SessionManager sessionManager;

    public fragmentUsuario() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        editTextEmailLogin = view.findViewById(R.id.editTextEmail);
        editTextPasswordLogin = view.findViewById(R.id.editTextPassword);
        botonLogin = view.findViewById(R.id.botonLogin);
        botonRegistro = view.findViewById(R.id.botonRegistro);
        layoutLoginRegistro = view.findViewById(R.id.layoutLoginRegistro);

        textViewEmailPerfil = view.findViewById(R.id.textViewEmailPerfil);
        editTextNombrePerfil = view.findViewById(R.id.editTextNombrePerfil);
        editTextTelefonoPerfil = view.findViewById(R.id.editTextTelefonoPerfil);
        editTextDireccionPerfil = view.findViewById(R.id.editTextDireccionPerfil);
        botonActualizarPerfil = view.findViewById(R.id.botonActualizarPerfil);
        botonLogout = view.findViewById(R.id.botonLogout);
        layoutPerfilUsuario = view.findViewById(R.id.layoutPerfilUsuario);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(requireActivity().getApplicationContext());

        updateUI();

        botonLogin.setOnClickListener(v -> iniciarSesion());
        botonRegistro.setOnClickListener(v -> registrarUsuario());
        botonActualizarPerfil.setOnClickListener(v -> actualizarPerfil());
        botonLogout.setOnClickListener(v -> logout());

        return view;
    }


    private void updateUI() {
        if (sessionManager.isLoggedIn()) {

            layoutLoginRegistro.setVisibility(View.GONE);
            layoutPerfilUsuario.setVisibility(View.VISIBLE);


            responseUsuario user = sessionManager.getLoggedInUser();
            if (user != null) {
                textViewEmailPerfil.setText("Email: " + user.getEmail()); // Mostrar email
                editTextNombrePerfil.setText(user.getNombre());
                editTextTelefonoPerfil.setText(user.getTelefono());
                editTextDireccionPerfil.setText(user.getDireccion());
            }

        } else {

            layoutLoginRegistro.setVisibility(View.VISIBLE);
            layoutPerfilUsuario.setVisibility(View.GONE);


            editTextEmailLogin.setText("");
            editTextPasswordLogin.setText("");
        }
    }

    private void iniciarSesion() {
        String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuarioLogin = new Usuario(email, password);

        Call<responseUsuario> call = apiService.loginUsuario(usuarioLogin);
        call.enqueue(new Callback<responseUsuario>() {
            @Override
            public void onResponse(Call<responseUsuario> call, Response<responseUsuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responseUsuario responseBody = response.body();

                    // *** AÑADIR ESTE LOG: Verificar el ID que se recibe del login API ***
                    Log.d(TAG, "Login exitoso. ID de usuario recibido de la API: " + responseBody.getIdUsuario());

                    Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    // Asegúrate de que createLoginSession guarda este responseBody completo
                    sessionManager.createLoginSession(responseBody);

                    Log.d(TAG, "Inicio de sesión exitoso. Comprobando Activity."); // Nuevo log

                    if (getActivity() != null) { // Verifica que la actividad no sea null
                        Log.d("FragmentUsuario", "getActivity() no es null."); // Nuevo log
                        if (getActivity() instanceof MainActivity) {
                            Log.d("FragmentUsuario", "getActivity() es una instancia de MainActivity. Llamando a actualizarIconoUsuario(true)."); // Nuevo log
                            ((MainActivity) getActivity()).actualizarIconoUsuario(true);
                        } else {
                            Log.e("FragmentUsuario", "getActivity() no es una instancia de MainActivity."); // Nuevo log de error
                        }
                    } else {
                        Log.e("FragmentUsuario", "getActivity() es null en iniciarSesion()."); // Nuevo log de error
                    }


                    updateUI();


                    editTextEmailLogin.setText("");
                    editTextPasswordLogin.setText("");

                    // Navegar a la pantalla principal (Tienda)
                    navigateToFragment(new fragmentTienda());
                } else {
                    String errorMensaje = "Error en la autenticación";
                    if (response.errorBody() != null) {
                        try {
                            errorMensaje += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMensaje += ". Detalles no disponibles.";
                        }
                    }
                    Toast.makeText(getContext(), errorMensaje + " (Código: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Login Fallido - Código: " + response.code() + ", Mensaje: " + errorMensaje);
                    sessionManager.logoutUser();
                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<responseUsuario> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fallo de conexión en Login: " + t.getMessage(), t);
                sessionManager.logoutUser();
                updateUI();
            }
        });
    }

    private void registrarUsuario() {
        String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete email y contraseña para registrarse", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario nuevoUsuario = new Usuario(email, password);

        Call<responseUsuario> call = apiService.registrarUsuario(nuevoUsuario);
        call.enqueue(new Callback<responseUsuario>() {
            @Override
            public void onResponse(Call<responseUsuario> call, Response<responseUsuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responseUsuario responseBody = response.body();
                    if (responseBody.isSuccess()) {
                        Toast.makeText(getContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al registrar el usuario", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMensaje = "Error en el registro";
                    if (response.errorBody() != null) {
                        try {
                            errorMensaje += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMensaje += ". Detalles no disponibles.";
                        }
                    }
                    Toast.makeText(getContext(), errorMensaje + " (Código: " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<responseUsuario> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarPerfil() {
        responseUsuario currentUser = sessionManager.getLoggedInUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "No hay usuario logueado para actualizar", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            updateUI();
            return;
        }

        // *** OBTENER EL ID DEL USUARIO LOGUEADO ***
        long userId = currentUser.getIdUsuario();

        String nombre = editTextNombrePerfil.getText().toString().trim();
        String telefono = editTextTelefonoPerfil.getText().toString().trim();
        String direccion = editTextDireccionPerfil.getText().toString().trim();
        // No necesitas obtener el email de currentUser si no lo vas a modificar
        // String email = currentUser.getEmail();

        // Crear un objeto Usuario solo con los datos a actualizar
        // No es necesario pasar email y password si no se actualizan
        Usuario usuarioActualizado = new Usuario(); // Usar constructor vacío
        // Si la API necesita el email en el cuerpo para identificar al usuario, inclúyelo:
        // usuarioActualizado.setEmail(currentUser.getEmail());
        usuarioActualizado.setNombre(nombre);
        usuarioActualizado.setTelefono(telefono);
        usuarioActualizado.setDireccion(direccion);

        Log.d(TAG, "Token antes de actualizar perfil: " + sessionManager.getUserToken());
        Log.d(TAG, "Intentando actualizar perfil para usuario ID: " + userId); // Log del ID

        // *** PASAR EL ID DEL USUARIO COMO PARÁMETRO DE RUTA ***
        Call<responseUsuario> call = apiService.actualizarUsuario(userId, usuarioActualizado); // Pasar userId aquí
        call.enqueue(new Callback<responseUsuario>() {
            @Override
            public void onResponse(Call<responseUsuario> call, Response<responseUsuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responseUsuario responseBody = response.body();

                    Toast.makeText(getContext(), "Perfil actualizado exitosamente: " + responseBody.getMensaje(), Toast.LENGTH_SHORT).show();


                    // Opcional: Si la API devuelve el usuario actualizado completo,
                    // puedes actualizar la sesión con los nuevos datos
                    // Asegúrate de que responseBody contiene el token actualizado si es necesario
                    sessionManager.createLoginSession(responseBody); // Actualizar la sesión con los nuevos datos del usuario
                    updateUI(); // Actualizar la interfaz de usuario con los datos recién guardados en sesión


                } else {
                    // Manejar error en la actualización
                    String errorMensaje = "Error al actualizar perfil";
                    if (response.errorBody() != null) {
                        try {
                            errorMensaje += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMensaje += ". Detalles no disponibles.";
                        }
                    } else if (response.body() != null && response.body().getMensaje() != null) {
                        errorMensaje += ": " + response.body().getMensaje();
                    }
                    Toast.makeText(getContext(), errorMensaje + " (Código: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Actualización Perfil Fallida - Código: " + response.code() + ", Mensaje: " + errorMensaje);

                    // Si el error es 401, puede que el token haya expirado. Cerrar sesión.
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Sesión expirada. Por favor, inicie sesión de nuevo.", Toast.LENGTH_SHORT).show();
                        sessionManager.logoutUser();
                        updateUI();
                        navigateToFragment(new fragmentUsuario()); // Navegar de vuelta a la pantalla de login
                    }

                }
            }

            @Override
            public void onFailure(Call<responseUsuario> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión al actualizar perfil: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fallo de conexión Actualizar Perfil: " + t.getMessage(), t);
            }
        });
    }


    private void logout() {
        sessionManager.logoutUser();
        Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).actualizarIconoUsuario(false);
        }


        updateUI();


        navigateToFragment(new fragmentTienda());
    }


    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        // Generalmente no añadimos a la pila de retroceso al navegar a la pantalla principal o login después de un logout
        // transaction.addToBackStack(null);
        transaction.commit();
    }
}