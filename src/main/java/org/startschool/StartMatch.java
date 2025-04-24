package org.startschool;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartMatch {

    private static final String TOKEN = System.getenv("STARTMATCH_TOKEN");
    private static final String CHANNEL_ID = System.getenv("STARTMATCH_CHANNEL_ID");
    private static final Logger log = LoggerFactory.getLogger(StartMatch.class);

    public static void main(String[] args) {

        // Check if token is null or empty.
        if ( TOKEN == null || TOKEN.isEmpty()) {
            throw new RuntimeException("Token is null or empty.");
        }

        // Setup event listener.
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);
        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA jda = jdaBuilder.build();
        SMEventListener smel = new SMEventListener(CHANNEL_ID);
        jda.addEventListener(smel);

        // Attempt to build the JDA object.
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to build JDA object.");
        }

        // Assert that the channel ID exists.
        TextChannel channel = jda.getTextChannelById(CHANNEL_ID);
        if (channel == null) {
            throw new RuntimeException("No such channel exists.");
        }

    }
}