package com.elevatemc.elib.util.message;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    public static MessageBuilder standard(String message) {
        return new MessageBuilder(MessageColor.GOLD, MessageColor.WHITE, MessageColor.YELLOW, MessageColor.GRAY).message(message);
    }

    public static MessageBuilder error(String message) {
        return new MessageBuilder(MessageColor.RED, MessageColor.WHITE, MessageColor.RED, MessageColor.RED).message(message);
    }

    public static String construct(String message, Object... elements) {
        MessageBuilder builder = MessageBuilder.standard(message);
        for (Object element : elements) {
            builder.element(element);
        }
        return builder.build();
    }

    public static String constructError(String message, Object... elements) {
        MessageBuilder builder = MessageBuilder.error(message);
        for (Object element : elements) {
            builder.element(element);
        }
        return builder.build();
    }

    private final MessageColor primaryColor, secondaryColor, tertiaryColor, prefixColor;
    private final List<Object> elements;

    private String message, prefix;

    public MessageBuilder(MessageColor primaryColor, MessageColor secondaryColor, MessageColor tertiaryColor, MessageColor prefixColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.tertiaryColor = tertiaryColor;
        this.prefixColor = prefixColor;
        this.elements = new ArrayList<>();
        this.message = null;
        this.prefix = null;
    }

    public MessageBuilder message(String message) {
        this.message = MessageTranslator.translate(message);
        return this;
    }

    public MessageBuilder element(Object element) {
        this.elements.add(element);
        return this;
    }

    public MessageBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String build() {
        if (this.message == null) {
            return "";
        }

        int currentElement = 0;
        String coloredPrefix = this.prefix == null ? "" : this.prefixColor + "[" + this.prefix + "] ";
        StringBuilder builder = new StringBuilder(coloredPrefix + this.primaryColor);

        for (int index = 0; index < this.message.length(); index++) {
            char currentChar = this.message.charAt(index);

            int relativeIndex = index + 1;

            if (relativeIndex >= this.message.length()) {
                builder.append(this.translateChar(currentChar, true));
                continue;
            }

            char relativeChar = this.message.charAt(relativeIndex);

            if (!(currentChar == '{' && relativeChar == '}')) {
                builder.append(this.translateChar(currentChar, false));
                continue;
            }


            if (currentElement >= this.elements.size()) {
                builder.append(this.translateChar(currentChar, false));
                continue;
            }

            builder.append(this.secondaryColor.toString());
            builder.append(this.elements.get(currentElement++));
            builder.append(this.primaryColor);
            index++;
        }

        return builder.toString();
    }

    private String translateChar(char currentChar, boolean ignorePrimary) {
        StringBuilder builder = new StringBuilder();
        boolean flag = MessageConstants.ALPHANUMERIC_PATTERN.matcher(String.valueOf(currentChar)).find();

        if (currentChar == MessageConstants.COLOR_SYMBOL || currentChar == ' ') {
            flag = false;
        }

        if (flag) {
            builder.append(this.tertiaryColor.toString());
            builder.append(currentChar);

            if (!ignorePrimary) {
                builder.append(this.primaryColor);
            }

            return builder.toString();
        }

        builder.append(currentChar);
        return builder.toString();
    }
}
