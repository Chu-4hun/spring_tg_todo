package com.belocurov.telegram_manager.bot.messages;

import com.belocurov.telegram_manager.bot.buttons.Button;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BotMessage {
    private String text;
    private List<Button> buttons;
    private long chatId;
    List<InlineKeyboardButton> row;
    List<List<InlineKeyboardButton>> rowsInline;
    InlineKeyboardMarkup inlineKeyboardMarkup;
    private List<BotMessage> messageHistory;

    public BotMessage(String text, long chatId, List<Button> buttons) {
        this.text = text;
        this.buttons = buttons;
        this.chatId = chatId;

        clearKeyboard();
        buildKeyboard();
    }

    public static SendMessage getSimpleMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("html");
        message.setText(text);
        return message;
    }

    public SendMessage getMessageWithButtons() {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(inlineKeyboardMarkup);
        messageHistory = new ArrayList<>();
        messageHistory.add(this);
        return message;
    }

    public EditMessageText getEditMessage(int messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(text);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }


    private void buildKeyboard() {
        for (Button button : buttons) {
            row.add(button.getButton());
        }
        rowsInline.add(row);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
    }

    private void clearKeyboard() {
        row = new ArrayList<>();
        rowsInline = new ArrayList<>();
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
    }


}
