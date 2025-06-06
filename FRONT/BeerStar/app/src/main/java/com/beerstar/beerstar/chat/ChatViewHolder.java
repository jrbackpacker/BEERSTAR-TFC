package com.beerstar.beerstar.chat;

import android.support.annotation.NonNull; // Nota: Posiblemente deberías usar androidx.annotation.NonNull
import android.view.View;
import android.widget.TextView;
import com.beerstar.beerstar.R;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    private final TextView messageText;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.messageText);
    }

    public void bind(ChatMessage message) {
        messageText.setText(message.getText());
    }
}