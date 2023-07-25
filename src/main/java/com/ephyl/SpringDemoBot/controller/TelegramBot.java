package com.ephyl.SpringDemoBot.controller;

import com.ephyl.SpringDemoBot.config.BotConfig;
import com.ephyl.SpringDemoBot.service.GlossaryService;
import com.ephyl.SpringDemoBot.service.LevelOfStudyService;

import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.filetypes.SpreadsheetFileType;
import com.groupdocs.conversion.options.convert.SpreadsheetConvertOptions;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import java.io.*;
import java.net.URL;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    private final GlossaryService glossaryService;
    private final LevelOfStudyService levelOfStudyService;

    private final String FILE_EXTENSION_XLS = "xls";
    private final String FILE_EXTENSION_XLSX = "xlsx";
    private final String SAVE_DIRECTORY = "src/main/resources/";

    public TelegramBot(BotConfig config, LevelOfStudyService levelOfStudyService,
                       GlossaryService glossaryService) {
        this.config = config;
        this.glossaryService = glossaryService;
        this.levelOfStudyService = levelOfStudyService;
        try {
            this.execute(Options.generatorMenu());
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageText(update, levelOfStudyService, glossaryService);
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            documentPull(update);
        } else if (Options.isPoolSend(update.getPoll(), levelOfStudyService)) {
            long chatId = Long.parseLong(levelOfStudyService.findByPollId(update.getPoll().getId())
                    .getChatId().trim());
            String userName = levelOfStudyService.findByPollId(update.getPoll().getId()).getUserName();
            HashMap<String, Object> send = Command.teachCommandReceived(userName, levelOfStudyService, glossaryService);
            List<String> option = Arrays.asList(send.get("options").toString()
                    .replace("[", "").replace("]", "").split(", "));
            sendMessage(chatId, String.valueOf(send.get("question")), option,
                    Integer.parseInt(String.valueOf(send.get("indexQuestion"))),
                    String.valueOf(send.get("userName")));
        } else if (Options.isExitPoolSend(update.getPoll(), levelOfStudyService)) {
            long chatId = Long.parseLong(levelOfStudyService.findByPollId(update.getPoll().getId())
                    .getChatId().trim());
            String userName = levelOfStudyService.findByPollId(update.getPoll().getId()).getUserName();
            sendMessage(chatId, "Your time class finish");
            levelOfStudyService.updatePoll(userName, null);
        }
    }

    private final String HELP_TEXT = "This bot is creating to English glossary.\n"
            + "Type /start too see a welcome message \n"
            + "Type /time change time quiz \n"
            + "Type /teach too see a quiz message \n"
            + "Type /level change level \n"
            + "Type /exit too see a exit message";

    private void messageText(Update update, LevelOfStudyService levelOfStudyService,
                             GlossaryService glossaryService) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getChat().getFirstName();
        String userName = update.getMessage().getChat().getUserName();
        messageText = Command.timeUpdate(levelOfStudyService, userName, messageText);
        HashMap<String, Object> send;
        switch (messageText) {
            case "/start":
                send = Command.startCommandReceived(firstName, userName, levelOfStudyService);
                if (send.containsKey("keyboard")) {
                    sendMessage(chatId, String.valueOf(send.get("answer")),
                            (ReplyKeyboardMarkup) send.get("keyboard"));
                } else {
                    sendMessage(chatId, String.valueOf(send.get("answer")));
                }
                break;
            case "A0":
            case "A1":
            case "A2":
            case "B1":
            case "B2":
            case "C1":
            case "C2":
                sendMessage(chatId, Command.levelCommandReceived(chatId, userName, messageText, levelOfStudyService));
                break;
            case "/time":
                send = Command.timeCommandReceived(chatId, userName, levelOfStudyService);
                sendMessage(chatId, String.valueOf(send.get("answer")),
                        (ReplyKeyboardMarkup) send.get("replyKeyboardMarkup"));
                break;
            case "/teach":
                levelOfStudyService.updateStudy(userName, true, System.currentTimeMillis());
                send = Command.teachCommandReceived(userName, levelOfStudyService, glossaryService);
                List<String> option = Arrays.asList(send.get("options").toString()
                        .replace("[", "").replace("]", "").split(", "));

                sendMessage(chatId, String.valueOf(send.get("question")), option,
                        Integer.parseInt(String.valueOf(send.get("indexQuestion"))),
                        String.valueOf(send.get("userName")));
                break;
            case "/exit":
                levelOfStudyService.updateStudy(userName, false, null);
                sendMessage(chatId, Command.exitCommandReceived(firstName));
                levelOfStudyService.updatePoll(userName, null);
                break;
            case "/help":
                sendMessage(chatId, HELP_TEXT);
                break;
            case "/level":
                send = Command.updateLevel();
                sendMessage(chatId, String.valueOf(send.get("answer")),
                        (ReplyKeyboardMarkup) send.get("keyboard"));
                break;
            default:
                sendMessage(chatId, "Sorry, command was not recognized");
        }
    }

    private void documentPull(Update update) {
        Document document = update.getMessage().getDocument();
        String name = document.getFileName();
        String extension = FilenameUtils.getExtension(name);
        Long chatId = update.getMessage().getChat().getId();
        if (extension.equals("xls") || extension.equals("xlsx")) {
            try {
                File load = loadFile(document);
                Converter converter = new Converter(load.getPath());
                SpreadsheetConvertOptions options = new SpreadsheetConvertOptions();
                options.setFormat(SpreadsheetFileType.Csv); // Specify the conversion format
                converter.convert("src/main/resources/" + name + ".csv", options);
                sendMessage(chatId, "Это нужный документ");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            sendMessage(chatId, "Это не верный формат");
        }
    }

    private File loadFile(Document document) throws IOException {
        String botToken = config.getToken();
        String fileId = document.getFileId();

        URL url = new URL("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);
        InputStream inputStream = url.openConnection().getInputStream();
        String response = new String(inputStream.readAllBytes());
        inputStream.close();

        JSONObject jsonObject = new JSONObject(response);
        boolean ok = jsonObject.getBoolean("ok");
        if (ok) {
            JSONObject result = jsonObject.getJSONObject("result");
            String filePath = result.getString("file_path");

            String fileName = new File(filePath).getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
            if (fileExtension.equalsIgnoreCase(FILE_EXTENSION_XLS) || fileExtension.equalsIgnoreCase(FILE_EXTENSION_XLSX)) {
                URL fileUrl = new URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath);
                InputStream fileInputStream = fileUrl.openConnection().getInputStream();

                String saveFilePath = SAVE_DIRECTORY + fileName;
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                fileInputStream.close();
                System.out.println("File saved to " + saveFilePath);
                return new File(saveFilePath);
            } else {
                System.out.println("File type not supported.");
                return null;
            }
        } else {
            System.out.println("Failed to get file information.");
            return null;
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(long chatId, String question, List<String> options,
                             Integer correctId, String userName) {
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(String.valueOf(chatId));
        sendPoll.setType("quiz");
        sendPoll.setQuestion(question);
        sendPoll.setIsAnonymous(true);
        sendPoll.setOptions(options);
        sendPoll.setCorrectOptionId(correctId);
        try {
            Poll poll = execute(sendPoll).getPoll();
            levelOfStudyService.updatePoll(userName, poll.getId());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
