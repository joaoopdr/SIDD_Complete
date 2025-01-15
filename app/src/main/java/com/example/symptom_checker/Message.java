package com.example.symptom_checker;

import java.util.List;

public class Message {
    private String text;
    private boolean sentByUser;
    private List<String> suggestions;

    public Message(String text, boolean sentByUser) {
        this.text = text;
        this.sentByUser = sentByUser;
    }

    public Message(String text, boolean sentByUser, List<String> suggestions) {
        this.text = text;
        this.sentByUser = sentByUser;
        this.suggestions = suggestions;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByUser() {
        return sentByUser;
    }

    public String getDateTime() {
        // Return formatted date/time if needed
        return "12:00 PM";
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public boolean hasSuggestions() {
        return suggestions != null && !suggestions.isEmpty();
    }

    public String getSuggestionsAsString() {
        return String.join(", ", suggestions);
    }
}