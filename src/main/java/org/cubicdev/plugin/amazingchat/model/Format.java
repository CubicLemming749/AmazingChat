package org.cubicdev.plugin.amazingchat.model;

import java.util.List;

public class Format {
    private String name;
    private String permission;
    private List<FormatElement> elements;
    private String messageSeparator;
    private String message;

    public Format(String name, String permission, List<FormatElement> elements, String messageSeparator, String message) {
        this.name = name;
        this.permission = permission;
        this.elements = elements;
        this.messageSeparator = messageSeparator;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public List<FormatElement> getElements() {
        return elements;
    }

    public String getMessageSeparator() {
        return messageSeparator;
    }

    public String getMessage() {
        return message;
    }
}
