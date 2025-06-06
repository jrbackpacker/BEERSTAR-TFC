package com.beerstar.beerstar.utils; // Puedes crear un paquete 'utils'

import android.content.Context;
import android.content.SharedPreferences;

import com.beerstar.beerstar.response.responseUsuario; // Importa tu responseUsuario

public class SessionManager {

    private static final String PREF_NAME = "BeerStarSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_TOKEN = "userToken";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TIPO = "userTipoUsuario";
    // Añadir claves para nombre, telefono, direccion si se guardan
    private static final String KEY_USER_NOMBRE = "userNombre";
    private static final String KEY_USER_TELEFONO = "userTelefono";
    private static final String KEY_USER_DIRECCION = "userDireccion";


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    // El constructor necesita el contexto para acceder a SharedPreferences
    public SessionManager(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }


    public void createLoginSession(responseUsuario user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putLong(KEY_USER_ID, user.getIdUsuario());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_TIPO, user.getTipoUsuario());
        editor.putString(KEY_USER_NOMBRE, user.getNombre());
        editor.putString(KEY_USER_TELEFONO, user.getTelefono());
        editor.putString(KEY_USER_DIRECCION, user.getDireccion());

        editor.apply(); // Guarda los cambios de forma asíncrona
    }


    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public String getUserToken() {
        return prefs.getString(KEY_TOKEN, null);
    }


    public responseUsuario getLoggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }

        responseUsuario user = new responseUsuario(); // Usar constructor vacío
        user.setSuccess(true); // Asumimos éxito si hay sesión
        user.setToken(prefs.getString(KEY_TOKEN, null));
        user.setIdUsuario(prefs.getLong(KEY_USER_ID, -1));
        user.setEmail(prefs.getString(KEY_USER_EMAIL, null));
        user.setTipoUsuario(prefs.getString(KEY_USER_TIPO, null));
        user.setNombre(prefs.getString(KEY_USER_NOMBRE, null));
        user.setTelefono(prefs.getString(KEY_USER_TELEFONO, null));
        user.setDireccion(prefs.getString(KEY_USER_DIRECCION, null));



        return user;
    }


    public void logoutUser() {
        editor.clear();
        editor.apply();

    }
}