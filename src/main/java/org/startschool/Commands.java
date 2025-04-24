package org.startschool;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Commands {

    private static final MatchMaker matchMaker = MatchMaker.getInstance();
    private static final Logger log = LoggerFactory.getLogger(Commands.class);
    private static final List<User> mmUsers = new ArrayList<>();

    private static final String INSTRUCTION_TEXT = "***# How to use StartMatch? #*** \n" +
            "If you want to participate, please **react to the \"StartMatch\" " +
            "bot message following this message**. The bot **will randomly pair** " +
            "up willing applicants. **Each pair is encouraged to connect** for a " +
            "quick 15-minute chat about anything. You can **click and call or " +
            "hit DM up to partner** to figure out time you both can connect. " +
            "It's a great way to connect with fellow community members and " +
            "share ideas (or **just enjoy a casual conversation**)!";
    private static final String ENGAGEMENT_PROMPT = "**# Press the reaction button bellow to join! #**";

    private static String ENGAGEMENT_PROMPT_MESSAGE_ID;

    public static boolean startMatch(MessageChannelUnion channel) {
        if (matchMaker.getMmInProgress()) {
            log.error("Cannot start new match while one is already in progress. Aborting!");
            return false;
        }
        log.info("Starting new matchmaking round!");
        matchMaker.setMmInProgress(true);
        channel.sendMessage(INSTRUCTION_TEXT).queue();
        channel.sendMessage(ENGAGEMENT_PROMPT).queue(msg -> {
            ENGAGEMENT_PROMPT_MESSAGE_ID = msg.getId();
            msg.addReaction(Emoji.fromUnicode("U+2705")).queue();
        });
        return true;
    }

    public static boolean endMatch(MessageChannelUnion channel) {
        if (!matchMaker.getMmInProgress()) {
            log.error("Cannot end match while none are active. Aborting!");
            return false;
        }
        mmUsers.clear();
        log.info("Ending current matchmaking round!");
        channel.retrieveMessageById(ENGAGEMENT_PROMPT_MESSAGE_ID).queue(msg -> {
            List<MessageReaction> reactionList = msg.getReactions();
            if (reactionList.isEmpty()) {
                sendResults(channel);
                return;
            }
            final int[] pending = {reactionList.size()};
            for (MessageReaction msgReaction : reactionList) {
                msgReaction.retrieveUsers().queue(users -> {
                    for (User user : users) {
                        if (!user.isBot() && !mmUsers.contains(user)) {
                            mmUsers.add(user);
                            log.info("{} : {}", user.getName(), user.getId());
                        }
                    }
                    pending[0]--;
                    if (pending[0] == 0) {
                        sendResults(channel);
                    }
                });
            }
        });
        matchMaker.setMmInProgress(false);
        return true;
    }

    static void sendResults(MessageChannelUnion channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Matchmaking results!");
        StringBuilder stringBuilder = new StringBuilder();
        boolean flipSwitch = true;
        for (User mmUser : mmUsers) {
            stringBuilder.append("<@");
            stringBuilder.append(mmUser.getId());
            if (flipSwitch) {
                stringBuilder.append("> ");
            } else {
                stringBuilder.append(">\n");
            }
            flipSwitch = !flipSwitch;
        }
        log.info("StringBuilder: "+stringBuilder.toString());
        embedBuilder.setDescription(stringBuilder.toString());
        embedBuilder.setFooter("Powered by StartSchool Batch 0");
        embedBuilder.setImage("https://static.wixstatic.com/media/b8cdba_452806092ca744d99de93677f591cd2f~mv2.png/v1/fill/w_270,h_45,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/logo-pinkpng.png");
        Color embedColor = new Random().nextBoolean() ? new Color(0xFF78C8) : new Color(0x0106FF);
        embedBuilder.setColor(embedColor);
        MessageEmbed embed = embedBuilder.build();
        MessageCreateData messageCreateData = new MessageCreateBuilder()
                .addEmbeds(embed)
                .build();
        channel.sendMessage(messageCreateData).queue();
    }
}
