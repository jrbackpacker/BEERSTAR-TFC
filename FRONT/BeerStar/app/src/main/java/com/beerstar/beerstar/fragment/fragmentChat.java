package com.beerstar.beerstar.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.chat.ChatAdapter;
import com.beerstar.beerstar.chat.ChatMessage;
// ELIMINA estas importaciones antiguas
// import com.example.beerstar.service.ResponseCallback;
// import com.example.beerstar.service.VoiceflowClient;

// AÑADE estas importaciones para Dialogflow
import com.beerstar.beerstar.service.DialogflowClient;
import com.beerstar.beerstar.service.DialogflowResponseCallback;

// Importa la clase de respuesta de Dialogflow
import com.google.cloud.dialogflow.v2.DetectIntentResponse;


import java.util.UUID;

public class fragmentChat extends Fragment {

    private RecyclerView recyclerView;
    private EditText userInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;

    // ELIMINA esta variable específica de Voiceflow
    // private static final String VOICEFLOW_VERSION_ID = "6813334e87e329c87bbb68d4";

    private final String sessionId = UUID.randomUUID().toString(); // Usar como Session ID en Dialogflow

    // Cambia la declaración para usar el nuevo cliente de Dialogflow
    private DialogflowClient dialogflowClient;

    public fragmentChat() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chatRecycler);
        userInput = view.findViewById(R.id.userInput);
        sendButton = view.findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        // Inicializar el DialogflowClient con tu Project ID real
        // El Project ID que identificamos de tu archivo de credenciales
        dialogflowClient = new DialogflowClient(requireContext(), "imagenes-fb98d"); // <-- Tu Project ID integrado aquí

        sendButton.setOnClickListener(v -> {
            String message = userInput.getText().toString();
            if (!message.trim().isEmpty()) {
                sendUserMessage(message);
                userInput.setText("");
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cierra el cliente de Dialogflow cuando la vista del fragment sea destruida
        if (dialogflowClient != null) {
            dialogflowClient.shutdown();
        }
    }

    private void sendUserMessage(String message) {
        // Añadir el mensaje del usuario al chat
        chatAdapter.addMessage(new ChatMessage(message, true));

        // Usamos la instancia del DialogflowClient para enviar el mensaje
        // Pasamos el mensaje, el userId (sessionId) y la nueva callback
        dialogflowClient.sendMessage(message, sessionId, new DialogflowResponseCallback() {
            @Override
            public void onSuccess(DetectIntentResponse response) {
                Log.d("Dialogflow", "Respuesta Dialogflow recibida");

                // Declaramos 'reply' como final o efectivamente final para usar en la lambda
                final String reply;

                if (response != null && response.getQueryResult() != null) {
                    // Dialogflow normalmente proporciona la respuesta de texto en getFulfillmentText()
                    String fulfillmentText = response.getQueryResult().getFulfillmentText();

                    if (fulfillmentText != null && !fulfillmentText.trim().isEmpty()) {
                        reply = fulfillmentText; // Asignación única
                    } else {
                        reply = "No se detectó una respuesta de texto en Dialogflow."; // Asignación única
                        Log.w("Dialogflow", "Fulfillment text is empty.");
                    }


                } else {
                    Log.e("Dialogflow", "DetectIntentResponse or QueryResult is null.");
                    reply = "Error interno al procesar la respuesta de Dialogflow."; // Asignación única
                }


                // Asegurarse de actualizar la UI en el hilo principal
                if (getActivity() != null) { // Verificar que el fragmento sigue adjunto
                    getActivity().runOnUiThread(() -> {
                        // Aquí 'reply' es efectivamente final
                        chatAdapter.addMessage(new ChatMessage(reply, false));
                        // Desplazar automáticamente hacia el último mensaje
                        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Dialogflow", "Error sending message to Dialogflow: " + error);
                // Mostrar un mensaje de error en el chat
                final String errorMessage = "Bot error (Dialogflow): " + error; // Declarada como final
                if (getActivity() != null) { // Verificar que el fragmento sigue adjunto
                    getActivity().runOnUiThread(() -> {
                        chatAdapter.addMessage(new ChatMessage(errorMessage, false));
                        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    });
                }
            }
        });
    }
}