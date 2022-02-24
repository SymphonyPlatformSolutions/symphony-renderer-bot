package com.symphony.devrel;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.V1IMAttributes;
import com.symphony.bdk.gen.api.model.V4InstantMessageCreated;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4User;
import com.symphony.bdk.spring.annotation.Slash;
import com.symphony.bdk.spring.events.RealTimeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitIM {
    private final MessageService messages;
    private final StreamService streams;

    @EventListener
    public void onInstantMessageCreated(RealTimeEvent<V4InstantMessageCreated> event) {
        V4User user = event.getInitiator().getUser();
        log.info("New IM created with: {} ({})", user.getDisplayName(), user.getEmail());
        init(event.getSource().getStream().getStreamId());
    }

    @Slash(value = "/init", mentionBot = false)
    public void onInit(CommandContext context) {
        String streamType = context.getSourceEvent().getMessage().getStream().getStreamType();
        if (!streamType.equals("IM")) {
            return;
        }
        V4User user = context.getInitiator().getUser();
        log.info("/init called by: {} ({})", user.getDisplayName(), user.getEmail());

        init(context.getStreamId());
    }

    private void init(String streamId) {
        String welcomeML = messages.templates().newTemplateFromClasspath("welcome.ftl").process(Map.of());
        V4Message welcomeMsg = messages.send(streamId, welcomeML);
        V1IMAttributes attributes = streams.getInstantMessageInfo(streamId).getV1IMAttributes();
        attributes.setPinnedMessageId(welcomeMsg.getMessageId());
        streams.updateInstantMessage(streamId, attributes);
    }
}
