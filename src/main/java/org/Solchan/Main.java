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
    // use the bot token with .main in the updates to retrieve correctly the variable.
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    // token in environment variable with TELEGRAM_BOT_TOKEN, it's currently hardcoded, but only because it stops working when using variable and I don't see why
    private static final String botToken = "7212202400:AAE4G5uoqPzZOnKtwmy-QFBckMT_IxDQ5zw";
    private static int offset = 0;

    public static void main(String[] args) {
        logger.info("Starting Main method.");

        logger.info("Bot token is set correctly: {}", botToken);
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

        logger.info("Ending Main method.");
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
                            [%s, joined Solchan yet?](https://www.solchan.org)
                            For real, get paid for posting on Solchan!
                            """;
                    String personalizedMessage = formatMessageWithUserHandle(update, messageTemplate);
                    bot.execute(new SendMessage(update.message().chat().id(), personalizedMessage).parseMode(ParseMode.Markdown));
                }
                case "/roadmap", "roadmap" -> {
                    String imageUrl = "https://imgur.com/a/wkaPixp"; // replace with your image URL
                    SendPhoto sendPhotoRequest = new SendPhoto(update.message().chat().id(), imageUrl);
                    bot.execute(sendPhotoRequest);
                    String RoadmapMessage = """
                            *Solchan Roadmap*
                            \s
                            *🌐 Phase 1: Rebuilding 🌐*
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
                            *Welcome in the Solchan ecosystem, %s!*
                            Solchan, the Image Board of Solana!
                            Get paid for posting on https://www.solchan.org today!
                            \s
                            *List of commands of @SolchanBot, please do /worklist*
                            \s
                            *Social Networks*
                            \s
                            📢 Solchan Ecosystem | [Solchan telegram](https://t.me/solchan_portal)
                            🐦 Solchan Ecosystem | [Solchan X](https://x.com/Solchan_org)
                            \s
                            *Market and DEFi*
                            \s
                            Buy on Raydium: [Start swapping](https://raydium.io/swap/?inputMint=sol&outputMint=ChanM2vka4gJ3ob1SejJXnxoNuoAXGGJxDMJRSLD7nzW)
                            CHART: [GeckoTerminal](https://www.geckoterminal.com/solana/pools/CRn7xMrVgKSVAy7wM2ZGV8RvpACtVBp5L85YuF14u6ss)
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
                            *RULES OF THUMBS*
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
                            *\uD83C\uDF10 SolchanBot default commands*
                            /work, /worklist, /roadmap, /invite, /scam
                            \s
                            *\uD83C\uDF10 Solchan partners*
                            """;
                    String personalizedMessage = formatMessageWithUserHandle(update, messageTemplate);
                    bot.execute(new SendMessage(update.message().chat().id(), personalizedMessage).parseMode(ParseMode.Markdown));
                }
            }
        }
    }
}
