package com.example.techsupport1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private ArrayList<Ticket> tickets;
    private SimpleDateFormat dateFormat;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onDeleteClick(int position);
    }

    public TicketAdapter(ArrayList<Ticket> tickets, OnTicketClickListener listener) {
        this.tickets = tickets;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        holder.idText.setText(String.format("№%d", ticket.getId()));
        holder.clientNameText.setText(ticket.getClientName());
        holder.subjectText.setText(ticket.getSubject());
        holder.descriptionText.setText(ticket.getDescription());
        holder.statusText.setText(ticket.getStatus());
        holder.priorityText.setText(ticket.getPriority());
        holder.createdText.setText(String.format("Создано: %s",
                dateFormat.format(ticket.getCreated())));
        holder.updatedText.setText(String.format("Обновлено: %s",
                dateFormat.format(ticket.getUpdated())));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public void removeItem(int position) {
        tickets.remove(position);
        notifyItemRemoved(position);
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView idText;
        TextView clientNameText;
        TextView subjectText;
        TextView descriptionText;
        TextView statusText;
        TextView priorityText;
        TextView createdText;
        TextView updatedText;
        View deleteButton;

        public TicketViewHolder(@NonNull View itemView, final OnTicketClickListener listener) {
            super(itemView);
            idText = itemView.findViewById(R.id.idText);
            clientNameText = itemView.findViewById(R.id.clientNameText);
            subjectText = itemView.findViewById(R.id.subjectText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            statusText = itemView.findViewById(R.id.statusText);
            priorityText = itemView.findViewById(R.id.priorityText);
            createdText = itemView.findViewById(R.id.createdText);
            updatedText = itemView.findViewById(R.id.updatedText);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}