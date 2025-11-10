package org.cubicdev.plugin.amazingchat.model;

import java.util.List;

public class FormatElement {
    private String text;
    private List<String> hoverText;
    private String clickAction;

    public FormatElement(String text, List<String> hoverText, String clickAction){
        this.text = text;
        this.hoverText = hoverText;
        this.clickAction = clickAction;
    }

    public String getText() {
        return text;
    }

    public List<String> getHoverText() {
        return hoverText;
    }

    public String getClickActions() {
        return clickAction;
    }
}
