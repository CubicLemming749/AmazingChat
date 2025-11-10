/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat.managers;

import org.cubicdev.plugin.amazingchat.formats.ChatFormatSerializer;
import org.cubicdev.plugin.amazingchat.model.Format;

import java.util.List;

public class FormatManager {
    private ChatFormatSerializer chatFormatSerializer;
    private List<Format> formats;

    public FormatManager(ChatFormatSerializer chatFormatSerializer){
        this.chatFormatSerializer = chatFormatSerializer;
        this.formats = chatFormatSerializer.deserializeFormats();
    }

    public void reloadFormats(){
        formats = chatFormatSerializer.deserializeFormats();
    }

    public List<Format> getFormats() {
        return formats;
    }
}
