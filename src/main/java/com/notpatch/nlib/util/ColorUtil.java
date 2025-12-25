package com.notpatch.nlib.util;

import com.notpatch.nlib.NLib;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorUtil {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String hexColor(String message) {
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, "" + ChatColor.of(color));
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> getColoredList(List<String> list) {
        return list.stream()
                .map(ColorUtil::hexColor)
                .collect(Collectors.toList());
    }

    public static List<String> getColoredList(List<String> list, Map<String, String> replacements) {
        return list.stream()
                .map(ColorUtil::hexColor)
                .map(text -> {
                    String result = text;
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        result = result.replace(entry.getKey(), entry.getValue());
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

    public static String applyPlaceholders(String text){
        if(NLib.getInstance().getPlugin().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            PlaceholderAPI.setPlaceholders(null, text);
            return text;
        }
        return text;
    }

}