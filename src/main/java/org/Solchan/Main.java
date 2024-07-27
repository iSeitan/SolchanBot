package org.Solchan;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendPhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import java.util.List;

public class Main {
    // Create a scheduled executor with 1 thread
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String botToken = System.getenv("MEMEUNITAPI").trim();
    private static int offset = 0;

    // create telegram api bot with bot token
    public static void main(String[] args) {
        if (botToken.isEmpty()) {
            logger.error("MEMEUNITAPI is not set or is invalid");
            return;
        }
        TelegramBot bot = new TelegramBot(botToken);

        // Poll for updates
        while (!Thread.currentThread().isInterrupted()) {
            try {
                GetUpdates getUpdates = new GetUpdates().limit(20).offset(offset).timeout(30);
                GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
                List<Update> updates = updatesResponse.updates();
                for (Update update : updates) {
                    processUpdate(bot, update);
                    offset = update.updateId() + 1;
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("Interrupted, shutting down.");
            } catch (Exception e) {
                logger.error("Error processing updates: {}", e.getMessage());
            }
        }
    }

    private static String formatMessageWithUserHandle(Update update, String messageTemplate) {
        String userHandle = update.message().from().username(); // Get the user's username
        if (userHandle == null) { // Check if the username is not set
            userHandle = "there"; // Use a default placeholder or first name
        } else {
            userHandle = "@" + userHandle; // Prefix with @ to denote a username
        }
        return String.format(messageTemplate, userHandle);
    }

    // Telegram bot process and commands
    private static void processUpdate(TelegramBot bot, Update update) {
        // Check if update is null
        if (update == null || update.message() == null || update.message().text() == null) {
            return;
        }
        String messageText = update.message().text();
        // Check if update is a message and is not null
        if (update.message() != null) {
            // If messageText is null, skip this iteration
            if (messageText == null) {
                return; // skips executing the rest of this iteration (i.e., goes to next update)
            }
            // If @ is present, this will separate the command from the bot's username.
            String[] split = messageText.split("@");
            String command = (split.length > 0) ? split[0] : "";
            // Process known commands
            switch (command) {
                case "/invite", "invite" -> {
                    String messageTemplate = """
                            [%s, joined Meme Unit yet?](https://getrichormemetrying.site)
                            Building the Meme Unit, one meme at a time.
                            [My story so far](https://www.youtube.com/watch?v=yL_sv70FUcQ&t=2s)
                            """;
                    String personalizedMessage = formatMessageWithUserHandle(update, messageTemplate);
                    bot.execute(new SendMessage(update.message().chat().id(), personalizedMessage).parseMode(ParseMode.Markdown));
                }
                case "/roadmap", "roadmap" -> {
                    String imageUrl = "https://imgur.com/a/07oSfAy"; // replace with your image URL
                    SendPhoto sendPhotoRequest = new SendPhoto(update.message().chat().id(), imageUrl);
                    bot.execute(sendPhotoRequest);
                    String RoadmapMessage = """
                            *Meme Unit Roadmap*
                            \s
                            *🌐 Phase 1: Building Meme Unit 🌐*
                            \s
                            ✅ or x mint |date|
                            \s
                            *And we won't stop there.*
                            \s
                            *🌐 Phase 2: 🌐*
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), RoadmapMessage).parseMode(ParseMode.Markdown));
                }
                case "/work" -> {
                    String messageTemplate = """
                            *%s, ready to dive into the crypto scene and be part of something groundbreaking?*
                            \s
                            *List of commands of @MemeUnitBot, please do /worklist*
                            \s
                            Meme Unit website: https://getrichormemetrying.site
                            \s
                            *Social Networks*
                            \s
                            📢 Website | [Meme Unit Website](https://getrichormemetrying.site)
                            💬 Community | [Telegram Chat](https://t.me/getrichormemetrying)
                            🐦 Meme Unit X | [Meme Unit X](https://x.com/memeunit_ent)
                            🎮 INSTAGRAM | [FOFTY $ENT Instagram](https://www.instagram.com/foftysent)
                            \s
                            *Market and DEFi*
                            \s
                            Buy on Raydium: [Start swapping]
                            CHART: [GeckoTerminal]
                            """;
                    String personalizedMessage = formatMessageWithUserHandle(update, messageTemplate);
                    bot.execute(new SendMessage(update.message().chat().id(), personalizedMessage).parseMode(ParseMode.Markdown));
                }
                case "/scam" -> {
                    String ScamMessage = """
                            *PLEASE, KEEP THE FOLLOWING IN MIND WHILE IN OUR ECOSYSTEM!*
                            \s
                            As our community continues to grow, it is important to stay vigilant against potential scams.
                            \s
                            *MEME UNIT RULES OF THUMBS*
                            \s
                            *1.* Do Not Trust Direct Messages (DMs): Our team will never reach out to you via direct message for personal information, investment opportunities, or any other sensitive matters.
                            \s
                            *2.* Follow Pinned Messages: Always refer to the pinned messages in our official channels for the most accurate and up-to-date information.
                            \s
                            *3.* Report Suspicious Activity: If you encounter any suspicious activity or receive unsolicited messages, please report them to our moderators immediately.
                            \s
                            *Your safety and security are our top priorities. Stay informed and protect yourself against scams.*
                            """;
                    bot.execute(new SendMessage(update.message().chat().id(), ScamMessage).parseMode(ParseMode.Markdown));
                }
                case "/worklist" -> {
                    String messageTemplate = """
                            *\uD83C\uDF1F Here's the full list of commands I can do! \uD83C\uDF1F*
                            \s
                            %s, happy to help you with the following:
                            \s
                            *\uD83C\uDF10 Meme Unit default commands*
                            /work, /worklist, /roadmap, /invite, /scam
                            \s
                            *\uD83C\uDF10 Meme Unit partners*
                            """;
                    String personalizedMessage = formatMessageWithUserHandle(update, messageTemplate);
                    bot.execute(new SendMessage(update.message().chat().id(), personalizedMessage).parseMode(ParseMode.Markdown));
                }
            }
        }
    }
}