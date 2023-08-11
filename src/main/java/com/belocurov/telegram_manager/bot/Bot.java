package com.belocurov.telegram_manager.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

//    private static Bot instance;

    private final String botName;

    public Bot(TelegramBotsApi telegramBotsApi, @Value("${bot.token}") String token, @Value("${bot.name}") String botName) throws TelegramApiException {
        super(token);
        this.botName = botName;
        telegramBotsApi.registerBot(this);
    }
//    @Override
//    public void onUpdateReceived(Update update) {
//        // We check if the update has a message and the message has text
//        if (update.hasMessage() && update.getMessage().hasText()) {
//             String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//
//            if ("/start".equals(messageText)) {
//                sendHelloButton(chatId);
//            }
//        }
//    }
//     private void sendHelloButton(long chatId) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton button = new InlineKeyboardButton();
//        button.setText("Hello");
//        button.setCallbackData("hello");
//
//        // Add the button to the first row of the inline keyboard
//        List<InlineKeyboardButton> row = new ArrayList<>();
//        row.add(button);
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//        rowsInline.add(row);
//
//        inlineKeyboardMarkup.setKeyboard(rowsInline);
//
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("Click the button:");
//        message.setReplyMarkup(inlineKeyboardMarkup);
//
//
//        try {
//            execute(message); // Sending the message with the inline keyboard
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }

     @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Process regular messages
            processRegularMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            // Process callback queries
            processCallbackQuery(update.getCallbackQuery());
        }
    }
private void processRegularMessage(Message message) {
        if ("/start".equals(message.getText())) {
            sendHelloButton(message.getChatId());
        }
    }
   private void processCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        if ("hello".equals(callbackData)) {
            updateMessage(chatId, messageId, "Button clicked! New text.", "new_button", true);
        } else if ("new_button".equals(callbackData)) {
            updateMessage(chatId, messageId, "New button clicked! More text.", "back_button", false);
        } else if ("back_button".equals(callbackData)) {
            updateMessage(chatId, messageId, "Click the button:", "hello", false);
        }
    }

    private void sendHelloButton(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Hello");
        button.setCallbackData("hello");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Click the button:");
                message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void updateMessage(long chatId, int messageId, String newText, String newCallbackData, boolean showBackButton) {
        EditMessageText newMessage = new EditMessageText();
                newMessage.setChatId(chatId);
                newMessage.setMessageId(messageId);
                newMessage.setText(newText);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("New Button");
        button.setCallbackData(newCallbackData);

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);

        if (showBackButton) {
            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("Back");
            backButton.setCallbackData("back_button");
            row.add(backButton);
        }

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        newMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
