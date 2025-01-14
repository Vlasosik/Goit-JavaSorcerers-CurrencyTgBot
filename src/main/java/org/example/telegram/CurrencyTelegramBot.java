package org.example.telegram;

import lombok.SneakyThrows;
import org.example.command.*;
import org.example.command.bankTgLogics.BankSelectionCommand;
import org.example.command.bankTgLogics.BanksCommand;
import org.example.command.currancyCommandLogic.CurrencyCommand;
import org.example.command.currancyCommandLogic.CurrencySelectionCommand;
import org.example.command.timeAndZone.*;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Pattern;

import static org.example.telegram.BotConstants.BOT_NAME;
import static org.example.telegram.BotConstants.BOT_TOKEN;


public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private static CurrencyTelegramBot instance;
    private final Pattern commandPattern = Pattern.compile("/\\w+");

    public static CurrencyTelegramBot getInstance() {
        if (instance == null) {
            instance = new CurrencyTelegramBot();
        }
        return instance;
    }

    @SneakyThrows
    private CurrencyTelegramBot() {
        register(new HelpCommand());
        register(new AlertTimesCommand());
        register(new AlertTimesResetCommand());
        register(new BackCommand());
        register(new BanksCommand());
        register(new CurrencyCommand());
        register(new DecimalPlaces());
        register(new SettingsCommand());
        register(new StartCommand());
        register(new InfoButtonCommand());
        register(new SignsAfterComaCommand());
        register(new TimeAndZoneCommand());
        register(new ZoneCommand());
        register(new ZoneResetCommand());
        register(new BankSelectionCommand());
        register(new TurnOffCommand());
        register(new CurrencySelectionCommand());
        registerDefaultAction(CurrencyTelegramBot::incorrectUserInput);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    @SneakyThrows
    public void processNonCommandUpdate(Update update) {
        System.out.println("Received update: " + update.toString());
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String action = callbackQuery.getData();
            String[] data = action.split("_");
            if ("settings".equals(data[0])) {
                SettingsCommand settingsCommand = new SettingsCommand();
                settingsCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("back".equals(data[0])) {// repeat
                BackCommand backCommand = new BackCommand();
                backCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("decimalPlaces".equals(data[0])) {
                SignsAfterComaCommand signsAfterComaCommand = new SignsAfterComaCommand(data[1], update);
                signsAfterComaCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("info".equals(data[0])) {
                InfoButtonCommand infoButtonCommand = new InfoButtonCommand();
                infoButtonCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("signsAfterComa".equals(data[0])) {
                DecimalPlaces decimalPlacesCommand = new DecimalPlaces();
                decimalPlacesCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("alert times".equals(data[0])) {
                AlertTimesCommand alertTimesCommand = new AlertTimesCommand();
                alertTimesCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("alertTimesCommand".equals(data[0])) {
                AlertTimesResetCommand alertTimesResetCommand = new AlertTimesResetCommand(data[1], update);
                alertTimesResetCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("TimeAndZone".equals(data[0])) {
                TimeAndZoneCommand timeAndZoneCommand = new TimeAndZoneCommand();
                timeAndZoneCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("zone".equals(data[0])) {
                ZoneCommand zoneCommand = new ZoneCommand();
                zoneCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("zoneResetCommand".equals(data[0])) {
                ZoneResetCommand zoneResetCommand = new ZoneResetCommand(data[1], update);
                zoneResetCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("bank".equals(data[0])) {
                BanksCommand banksCommand = new BanksCommand();
                banksCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("bankSelection".equals(data[0])) {
                BankSelectionCommand bankSelectionCommand = new BankSelectionCommand(data[1], update);
                bankSelectionCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("turnOff".equals(data[0])) {
                TurnOffCommand turnOffCommand = new TurnOffCommand(update);
                turnOffCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("currency".equals(data[0])) {
                CurrencyCommand currencyCommand = new CurrencyCommand();
                currencyCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            } else if ("currencySelection".equals(data[0])) {
                CurrencySelectionCommand currencySelectionCommand = new CurrencySelectionCommand(data[1], update);
                currencySelectionCommand.execute(this, callbackQuery.getFrom(), callbackQuery.getMessage().getChat(), null);
            }
        }
        commandHelp(update);
    }


    private void commandHelp(Update update) throws TelegramApiException {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if ("/help".equals(message)) {
                HelpCommand helpCommand = new HelpCommand();
                helpCommand.execute(this, update.getMessage().getFrom(), update.getMessage().getChat(), null);
            }
            if (!commandPattern.matcher(message).matches()) {
                SendMessage responseMessage = new SendMessage();
                responseMessage.setText("Ви ввели текст який бот не може розпізнати🤷🏼‍♂️\n" + "Це бот знає ось такі команди: \n" + "/start ~ /help");
                responseMessage.setChatId(update.getMessage().getChatId());
                execute(responseMessage);
            }
        }
    }

    @SneakyThrows
    private static void incorrectUserInput(AbsSender absSender, Message message) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setText("Ви ввели команду який бот не може розпізнати🤷🏼‍♂️\n" + "Це бот знає ось такі команди: \n" + "/start ~ /help");
        responseMessage.setChatId(message.getChatId());
        absSender.execute(responseMessage);
    }
}