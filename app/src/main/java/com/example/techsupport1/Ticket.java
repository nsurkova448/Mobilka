package com.example.techsupport1;

import java.util.Date;

public class Ticket {
    private int id;
    private String clientName;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private Date created;
    private Date updated;

    public Ticket(int id, String clientName, String subject, String description,
                  String status, String priority, Date created, Date updated) {
        this.id = id;
        this.clientName = clientName;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.created = created;
        this.updated = updated;
    }

    // Геттеры
    public int getId() { return id; }
    public String getClientName() { return clientName; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public Date getCreated() { return created; }
    public Date getUpdated() { return updated; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }

    public void setStatus(String status) {
        this.status = status;
        this.updated = new Date(); // Обновляем дату при изменении статуса
    }

    public void setCreated(Date created) { this.created = created; }
    public void setUpdated(Date updated) { this.updated = updated; }
}