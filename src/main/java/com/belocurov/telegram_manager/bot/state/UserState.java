package com.belocurov.telegram_manager.bot.state;

import lombok.Getter;
import lombok.Setter;

import static com.belocurov.telegram_manager.bot.state.UserStateEnum.ENTERING_TASK_TEXT;

@Getter
@Setter
public class UserState {
    private UserStateEnum userState = UserStateEnum.NONE;
    private String taskText;

    public void setTaskText(String text) {
        if (userState == ENTERING_TASK_TEXT) {
            taskText = text;
        }
    }

}
