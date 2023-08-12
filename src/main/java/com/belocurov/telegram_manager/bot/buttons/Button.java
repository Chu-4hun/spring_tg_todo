package com.belocurov.telegram_manager.bot.buttons;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Getter
@Setter
public class Button {
   private InlineKeyboardButton button;
   private String text;
   private String callbackData;

    public InlineKeyboardButton getButton() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }


}
