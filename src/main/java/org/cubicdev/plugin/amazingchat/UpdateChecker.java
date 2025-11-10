/*
 * Copyright (c) 2025 CubicLemming749
 * Licensed under the MIT License.
 */

package org.cubicdev.plugin.amazingchat;

import com.google.gson.*;
import org.cubicdev.plugin.amazingchat.utils.HttpUtils;

import java.net.http.HttpResponse;

public class UpdateChecker {
    public static boolean IS_NEW_UPDATE;
    public static String NEW_VERSION;

    private Gson gson;

    public UpdateChecker(){
        IS_NEW_UPDATE = false;
        NEW_VERSION = AmazingChat.PLUGIN_VERSION;

        this.gson = new Gson();
    }

    public void checkForUpdates(){
        String url = "https://api.modrinth.com/v2/project/amazingchat/version";
        HttpResponse<String> response = HttpUtils.callApi(url);

        if(response.statusCode() != 200){
            return;
        }

        JsonArray jsonArray = (JsonArray) JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

        String versionObtained = jsonObject.get("version_number").getAsString();

        if(!NEW_VERSION.equals(versionObtained)){
            NEW_VERSION = versionObtained;
            IS_NEW_UPDATE = true;
        }
    }
}
