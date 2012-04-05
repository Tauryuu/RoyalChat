package org.royaldev.royalchat.utils;

public class RUtils {
    public static String colorize(String text) {
        return text.replaceAll("(&([a-f0-9k-orR]))", "\u00A7$2");
    }
}
