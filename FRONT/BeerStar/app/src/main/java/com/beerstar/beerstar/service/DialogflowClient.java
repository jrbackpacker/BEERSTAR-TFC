package com.beerstar.beerstar.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;


import com.beerstar.beerstar.R;

import java.io.InputStream;


public class DialogflowClient {

    private static final String TAG = "DialogflowClient";
    private SessionsClient sessionsClient;
    private String projectId;

    public DialogflowClient(Context context, String projectId) {
        this.projectId = projectId;
        setUpDialogflow(context);
    }

    private void setUpDialogflow(Context context) {
        try {
            // Cargar las credenciales desde un archivo JSON en res/raw
            // Asegúrate de que el nombre del archivo coincida con el que has descargado y colocado
            InputStream stream = context.getResources().openRawResource(R.raw.dialogflow_credentials); // <-- ¡IMPORTANTE! Asegúrate que este archivo existe en res/raw
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);

            // Opcional: Verificar el Project ID desde las credenciales si no lo pasas como parámetro
            // String projectIdFromCredentials = ((com.google.auth.oauth2.ServiceAccountCredentials) credentials).getProjectId();
            // Log.d(TAG, "Project ID from credentials: " + projectIdFromCredentials);


            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            Log.d(TAG, "Dialogflow client initialized successfully.");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up Dialogflow: " + e.getMessage(), e);
            // Podrías considerar cómo manejar este error en la UI, por ejemplo, deshabilitando la entrada de chat
        }
    }


    public void sendMessage(String message, String userId, final DialogflowResponseCallback callback) {
        if (sessionsClient == null) {
            Log.e(TAG, "Dialogflow client is not initialized.");
            callback.onError("Dialogflow client not initialized. Check credentials and project ID.");
            return;
        }

        // Dialogflow utiliza un Session ID para mantener el contexto de la conversación.
        // Puedes usar el userId existente (generado como UUID en fragmentChat) como Session ID.
        SessionName currentSession = SessionName.of(projectId, userId);


        // Crear el QueryInput con el texto del usuario
        TextInput.Builder textInput = TextInput.newBuilder().setText(message).setLanguageCode("es"); // <-- Ajusta el código de idioma si es necesario (ej: "en-US")
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

        // Ejecutar la detección de intención en un AsyncTask para no bloquear el hilo principal
        new DetectIntentTask(sessionsClient, currentSession, queryInput, callback).execute();
    }

    // AsyncTask para realizar la llamada a Dialogflow en segundo plano
    private static class DetectIntentTask extends AsyncTask<Void, Void, DetectIntentResponse> {

        private SessionsClient sessionsClient;
        private SessionName sessionName;
        private QueryInput queryInput;
        private DialogflowResponseCallback callback;
        private Exception exception;

        DetectIntentTask(SessionsClient sessionsClient, SessionName sessionName, QueryInput queryInput, DialogflowResponseCallback callback) {
            this.sessionsClient = sessionsClient;
            this.sessionName = sessionName;
            this.queryInput = queryInput;
            this.callback = callback;
        }

        @Override
        protected DetectIntentResponse doInBackground(Void... voids) {
            try {
                DetectIntentRequest detectIntentRequest = DetectIntentRequest.newBuilder()
                        .setSession(sessionName.toString())
                        .setQueryInput(queryInput)
                        .build();
                DetectIntentResponse response = sessionsClient.detectIntent(detectIntentRequest);
                return response;
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(DetectIntentResponse response) {
            if (response != null) {
                callback.onSuccess(response);
            } else {
                callback.onError(exception != null ? exception.getMessage() : "Unknown error during Dialogflow detection.");
            }
        }
    }


    public void shutdown() {
        if (sessionsClient != null) {
            try {
                sessionsClient.shutdownNow();
                Log.d(TAG, "Dialogflow client shut down.");
            } catch (Exception e) {
                Log.e(TAG, "Error shutting down Dialogflow client: " + e.getMessage(), e);
            }
        }
    }
}