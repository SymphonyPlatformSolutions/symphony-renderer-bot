package com.symphony.devrel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.gen.api.model.V4User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Renderer extends FormReplyActivity<FormReplyContext> {
    private final MessageService messages;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected ActivityMatcher<FormReplyContext> matcher() throws EventException {
        return c -> "render-form".equals(c.getFormId());
    }

    @Override
    protected void onActivity(FormReplyContext context) throws EventException {
        String messageML = context.getFormValue("messageml");
        V4User user = context.getInitiator().getUser();
        log.info("Received MessageML from {} ({}):\n{}", user.getDisplayName(), user.getEmail(), messageML);

        try {
            messages.send(context.getStreamId(), messageML);
        } catch (Exception e) {
            try {
                String message = objectMapper.readTree(e.getCause().getMessage()).path("message").asText();
                messages.send(context.getStreamId(), "Error: " + message);
            } catch (JsonProcessingException ignore) {}
        }
    }

    @Override
    protected ActivityInfo info() {
        return new ActivityInfo()
            .name("Renderer")
            .description("Renders incoming MessageML");
    }
}
