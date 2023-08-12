package com.belocurov.telegram_manager.bot.messages;

import com.belocurov.telegram_manager.bot.state.UserState;
import com.belocurov.telegram_manager.bot.state.UserStateEnum;
import com.belocurov.telegram_manager.models.Task;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TaskMessage {
    private Task task;
    private int messageId;

    public TaskMessage(Task task, int messageId) {
        this.task = task;
        this.messageId = messageId;
    }

    public SendMessage getTaskBodyMessage(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("html");
        message.setText("<b>Text:</b> \n" + task.getText() + "\n" + "<b>Status: </b>" + (task.isDone() ? "✔" : "❌"));
        message.setReplyMarkup(createTaskMessageMarkup());
        return message;
    }
    public static SendMessage getTaskCreationRequestMessage(long chatId, UserState userState){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please enter the task text: ");
        userState.setUserState(UserStateEnum.ENTERING_TASK_TEXT);
        return message;
    }
    public static SendMessage sendTaskConfirmation(long chatId, String confirmMessage, UserState userState) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Confirm the task text: \n" + confirmMessage);
        message.setReplyMarkup(createTaskPromptMarkup());
        userState.setTaskText(confirmMessage);
        return message;
    }

    //----------------------------Markups------------------------------//
     private static InlineKeyboardMarkup createTaskMessageMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("Delete 🗑");
        deleteButton.setCallbackData("deleteTask");

        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("Set done ✅");
        confirmButton.setCallbackData("setDoneTask");

        row1.add(deleteButton);
        row1.add(confirmButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);

        markup.setKeyboard(rowsInline);
        return markup;
    }

      private static InlineKeyboardMarkup createTaskPromptMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("Confirm ✅");
        confirmButton.setCallbackData("confirmTask");

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Cancel ❌");
        cancelButton.setCallbackData("cancelTask");
        row1.add(confirmButton);
        row1.add(cancelButton);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(row1);

        markup.setKeyboard(rowsInline);
        return markup;
    }

}
