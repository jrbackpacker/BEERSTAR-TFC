package com.beerstar.beerstar.service;

import android.content.Context;

import com.beerstar.beerstar.utils.AuthInterceptor;
import com.beerstar.beerstar.utils.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitClient {

    // Cambia esta URL para tu entorno de desarrollo (local o desplegado)
    // Para emulador:
    private static final String BASE_URL = "https://tfc-beerstar.onrender.com";


    private static Retrofit retrofit;
    private static ApiService apiService;
    private static SessionManager sessionManager;

    private RetrofitClient() {
        // Evita instancias
    }

    /**
     * Debes llamarlo una sola vez (por ejemplo en Application o MainActivity onCreate)
     */
    public static void init(Context context, SessionManager sessionManager) {
        if (RetrofitClient.sessionManager == null) {
            RetrofitClient.sessionManager = sessionManager; // Usa el sessionManager pasado o crea uno nuevo
        }
    }

    /**
     * Devuelve la instancia única de ApiService.
     * Lanza excepción si olvidas init().
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            if (sessionManager == null) {
                throw new IllegalStateException(
                        "RetrofitClient.init(context, sessionManager) debe llamarse antes de usar getApiService()");
            }

            // Interceptor que muestra cuerpo de la petición/respuesta en Logcat
            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sessionManager)) // <-- Token aquí
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}