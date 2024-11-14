package com.example.techsupport1;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements TicketAdapter.OnTicketClickListener {
    private EditText clientNameInput;
    private EditText subjectInput;
    private EditText descriptionInput;
    private Spinner statusSpinner;
    private Spinner prioritySpinner;
    private Button addButton;
    private RecyclerView ticketsRecyclerView;
    private TicketAdapter ticketAdapter;
    private ArrayList<Ticket> tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupSpinners();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        clientNameInput = findViewById(R.id.clientNameInput);
        subjectInput = findViewById(R.id.subjectInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        statusSpinner = findViewById(R.id.statusSpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        addButton = findViewById(R.id.addButton);
        ticketsRecyclerView = findViewById(R.id.ticketsRecyclerView);
    }

    private void setupSpinners() {
        // Setup Status Spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.status_array, android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Setup Priority Spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_array, android.R.layout.simple_spinner_item
        );
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);
    }

    private void setupRecyclerView() {
        tickets = new ArrayList<>();
        ticketAdapter = new TicketAdapter(tickets, this);
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ticketsRecyclerView.setAdapter(ticketAdapter);

        // Добавляем поддержку свайпа для удаления
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(ticketsRecyclerView);
    }

    private void setupListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTicket();
            }
        });
    }

    private void addNewTicket() {
        String clientName = clientNameInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();
        String priority = prioritySpinner.getSelectedItem().toString();

        if (clientName.isEmpty()) {
            clientNameInput.setError("Введите имя клиента");
            return;
        }

        if (subject.isEmpty()) {
            subjectInput.setError("Введите тему");
            return;
        }

        Ticket ticket = new Ticket(
                tickets.size() + 1,
                clientName,
                subject,
                description,
                status,
                priority,
                new Date(),
                new Date()
        );

        tickets.add(0, ticket);
        ticketAdapter.notifyItemInserted(0);
        clearInputs();
        showToast("Заявка успешно создана");

        // Прокручиваем список к началу, где добавлена новая заявка
        ticketsRecyclerView.smoothScrollToPosition(0);
    }

    private void clearInputs() {
        clientNameInput.setText("");
        subjectInput.setText("");
        descriptionInput.setText("");
        statusSpinner.setSelection(0);
        prioritySpinner.setSelection(0);

        // Убираем фокус с полей ввода
        clientNameInput.clearFocus();
        subjectInput.clearFocus();
        descriptionInput.clearFocus();
    }

    private void showDeleteConfirmationDialog(final int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Удаление заявки")
                .setMessage("Вы действительно хотите удалить эту заявку?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    deleteTicket(position);
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    ticketAdapter.notifyItemChanged(position);
                })
                .setOnCancelListener(dialog -> {
                    ticketAdapter.notifyItemChanged(position);
                })
                .show();
    }

    private void deleteTicket(int position) {
        if (position >= 0 && position < tickets.size()) {
            tickets.remove(position);
            ticketAdapter.notifyItemRemoved(position);
            showToast("Заявка удалена");

            // Обновляем ID оставшихся заявок
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                ticket.setId(i + 1);
            }
            ticketAdapter.notifyItemRangeChanged(0, tickets.size());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        showDeleteConfirmationDialog(position);
    }

    // Метод для обновления статуса заявки
    public void updateTicketStatus(int position, String newStatus) {
        if (position >= 0 && position < tickets.size()) {
            Ticket ticket = tickets.get(position);
            ticket.setStatus(newStatus);
            ticketAdapter.notifyItemChanged(position);
            showToast("Статус заявки обновлен");
        }
    }

    // Метод для поиска заявок
    public void searchTickets(String query) {
        ArrayList<Ticket> filteredList = new ArrayList<>();
        query = query.toLowerCase().trim();

        for (Ticket ticket : tickets) {
            if (ticket.getClientName().toLowerCase().contains(query) ||
                    ticket.getSubject().toLowerCase().contains(query) ||
                    ticket.getDescription().toLowerCase().contains(query) ||
                    String.valueOf(ticket.getId()).contains(query)) {
                filteredList.add(ticket);
            }
        }

        if (filteredList.isEmpty()) {
            showToast("Заявки не найдены");
        }

        tickets.clear();
        tickets.addAll(filteredList);
        ticketAdapter.notifyDataSetChanged();
    }

    // Метод для фильтрации по статусу
    public void filterByStatus(String status) {
        ArrayList<Ticket> filteredList = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.getStatus().equals(status)) {
                filteredList.add(ticket);
            }
        }

        if (filteredList.isEmpty()) {
            showToast("Заявки с таким статусом не найдены");
        }

        tickets.clear();
        tickets.addAll(filteredList);
        ticketAdapter.notifyDataSetChanged();
    }

    // Метод для фильтрации по приоритету
    public void filterByPriority(String priority) {
        ArrayList<Ticket> filteredList = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.getPriority().equals(priority)) {
                filteredList.add(ticket);
            }
        }

        if (filteredList.isEmpty()) {
            showToast("Заявки с таким приоритетом не найдены");
        }

        tickets.clear();
        tickets.addAll(filteredList);
        ticketAdapter.notifyDataSetChanged();
    }

    // Метод для сброса всех фильтров
    public void resetFilters() {
        // Здесь должна быть логика загрузки всех заявок из базы данных
        // Пока просто очищаем список
        tickets.clear();
        ticketAdapter.notifyDataSetChanged();
        showToast("Фильтры сброшены");
    }
}