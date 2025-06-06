package com.beerstar.beerstar.chat;

import android.support.annotation.NonNull; // Nota: Posiblemente deberías usar androidx.annotation.NonNull
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.beerstar.beerstar.R;
import java.util.ArrayList;
import java.util.List;
import android.view.View;


public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private final List<ChatMessage> messages = new ArrayList<>();

    // Definimos los tipos de vista para mensajes de usuario y bot
    private static final int VIEW_TYPE_USER = 0; // O cualquier otro valor entero
    private static final int VIEW_TYPE_BOT = 1; // Que sea diferente del VIEW_TYPE_USER


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // Inflamos el layout correcto basado en el tipo de vista
        if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
        } else { // viewType == VIEW_TYPE_BOT
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bot, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Método clave para indicar qué tipo de vista usar en cada posición
    @Override
    public int getItemViewType(int position) {
        // Si el mensaje es de usuario, devuelve el tipo de vista de usuario, de lo contrario, el de bot.
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        // Notificar al adaptador que se ha añadido un elemento al final para que se actualice la UI
        notifyItemInserted(messages.size() - 1);
        // Opcional: Si quieres que se desplace al final, necesitas hacerlo desde donde llamas a addMessage (ej: fragmentChat)
    }

    // Método si quieres añadir una lista completa de mensajes (ej: al cargar historial)
    public void addMessages(List<ChatMessage> newMessages) {
        int startPosition = messages.size();
        messages.addAll(newMessages);
        notifyItemRangeInserted(startPosition, newMessages.size());
    }

    // Método para limpiar todos los mensajes
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }
}