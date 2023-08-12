package com.belocurov.telegram_manager.bot;

import com.belocurov.telegram_manager.bot.buttons.Button;
import com.belocurov.telegram_manager.bot.messages.BotMessage;
import com.belocurov.telegram_manager.bot.messages.TaskMessage;
import com.belocurov.telegram_manager.bot.state.UserState;
import com.belocurov.telegram_manager.bot.state.UserStateEnum;
import com.belocurov.telegram_manager.models.Task;
import com.belocurov.telegram_manager.repositories.UserRepository;
import com.belocurov.telegram_manager.services.TaskService;
import com.belocurov.telegram_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

//    private static Bot instance;

    private final String botName;
    UserState userState = new UserState();
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public Bot(TelegramBotsApi telegramBotsApi, @Value("${bot.token}") String token,
               @Value("${bot.name}") String botName, UserRepository userRepository, TaskService taskService, UserService userService) throws TelegramApiException {
        super(token);
        this.botName = botName;
        this.taskService = taskService;
        this.userService = userService;
        telegramBotsApi.registerBot(this);
    }

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
        if (userState.getUserState() == UserStateEnum.ENTERING_TASK_TEXT) {
            sendMessage(TaskMessage.sendTaskConfirmation(message.getChatId(), message.getText(), userState));
        }
        if ("/start".equals(message.getText())) {
            sendWelcomeMessage(message.getChatId());
            sendCustomKeyboard(message.getChatId());
        } else if ("Show all 📃".equals(message.getText())) {
            for (Task task : taskService.getTasksByUserId(message.getChatId())) {
                sendMessage(new TaskMessage(task, message.getMessageId()).getTaskBodyMessage(message.getChatId()));
            }
        } else if ("Add new task ➕".equals(message.getText())) {
            sendMessage(TaskMessage.getTaskCreationRequestMessage(message.getChatId(), userState));
        }
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        if ("hello".equals(callbackData)) {
            updateMessage(chatId, messageId, "Button clicked! New text.", "new_button", true);
        } else if ("new_button".equals(callbackData)) {
            updateMessage(chatId, messageId, "New button clicked! More text.", "back_button", true);
        } else if ("back_button".equals(callbackData)) {
            updateMessage(chatId, messageId, "Click the button:", "hello", false);
        } else if ("confirmTask".equals(callbackData)) {
            Task task = taskService.createTask(chatId, userState.getTaskText());
            if (task != null) {
                deleteMessage(chatId, messageId);
                userState.setUserState(UserStateEnum.NONE);
            }
            sendMessage(BotMessage.getSimpleMessage(chatId, "Completed! You can continue using the bot " +
                    "\n /start to get command keyboard"));
        } else if ("cancelTask".equals(callbackData)) {
            deleteMessage(chatId, messageId);
            userState.setUserState(UserStateEnum.NONE);
            sendMessage(BotMessage.getSimpleMessage(chatId, "Use /start to begin using the bot"));

        } else if ("deleteTask".equals(callbackData)) {
            deleteMessage(chatId, messageId);

            userState.setUserState(UserStateEnum.NONE);
            sendMessage(BotMessage.getSimpleMessage(chatId, "Completed! You can continue using the bot " +
                    "\n /start to get command keyboard"));

        } else if ("setDoneTask".equals(callbackData)) {
            deleteMessage(chatId, messageId);
            userState.setUserState(UserStateEnum.NONE);
            sendMessage(BotMessage.getSimpleMessage(chatId, "Welcome! use /start to begin using the bot"));
        }
    }


    private void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hello! Welcome to the bot.");

        try {
            execute(message);
            userService.createNewUserIfUnique(chatId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendMessage(BotApiMethod botMessage) {
        try {
            execute(botMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void updateMessage(long chatId, int messageId, String newText, String newCallbackData, boolean showBackButton) {

        List<Button> buttons = new ArrayList<>();
        Button button = new Button();
        button.setText("Hello");
        button.setCallbackData(newCallbackData);
        buttons.add(button);


        if (showBackButton) {
            Button backButton = new Button();
            backButton.setText("Back");
            backButton.setCallbackData("back_button");
            buttons.add(backButton);
        }


        BotMessage botMessage =
                new BotMessage(newText, chatId, buttons);


        sendMessage(botMessage.getEditMessage(messageId));
    }


    public void sendCustomKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose an option:");

        // Create keyboard rows
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Show all 📃");
        row1.add("Add new task ➕");


        // Add rows to the keyboard
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row1);


        // Create the keyboard markup and attach it to the message
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setResizeKeyboard(true);

        message.setReplyMarkup(replyKeyboardMarkup);


        sendMessage(message);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
