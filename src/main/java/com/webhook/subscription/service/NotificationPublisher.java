package com.webhook.subscription.service;

import com.webhook.subscription.model.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class NotificationPublisher implements Runnable {

    private final Webhook webhook;
    private final RestTemplate restTemplate;
    private final WebhookService webhookService;

    @Override
    public void run() {
        String url = webhook.getUrl();
        log.info("Notifying {} of time {}", url, LocalDateTime.now());
        try{
            restTemplate.postForLocation(URI.create(url), Map.of("currentTime", LocalDateTime.now().toString()));
        }catch (Exception e){
            log.error("Error while notifying url {}, message {}", url, e.getMessage());
            webhookService.unregisterWebhook(url);
        }
    }
}
