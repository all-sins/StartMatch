package org.startschool;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMEventListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SMEventListener.class);
    private final String CHANNEL_ID;
    private final MatchMaker matchMaker = MatchMaker.getInstance();

    public SMEventListener(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent msgRecEvent) {
        if ( msgRecEvent.getAuthor().isBot() ) {return;}
        MessageChannelUnion channel = msgRecEvent.getChannel();
        if ( !channel.getId().equals(CHANNEL_ID) ) {return;}
        Message msg = msgRecEvent.getMessage();
        String content = msg.getContentRaw();
        switch (content) {
            case "!startMatch" -> {
                if ( matchMaker.getMmInProgress() ) {return;}
                Commands.startMatch(channel);
            }
            case "!endMatch" -> Commands.endMatch(channel);
        }
    }
}
