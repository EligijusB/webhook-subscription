package com.webhook.subscription.service;

import com.webhook.subscription.model.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final TaskScheduler taskScheduler;
    private final RestTemplate restTemplate;

    private Map<String, ScheduledFuture<NotificationPublisher>> registeredWebhooks = new ConcurrentHashMap<>();

    public void registerWebhook(Webhook webhook){
        String url = webhook.getUrl();
        if(!registeredWebhooks.containsKey(url)) {
            NotificationPublisher notificationPublisher = new NotificationPublisher(webhook, restTemplate, this);
            ScheduledFuture scheduledTask = taskScheduler.scheduleAtFixedRate(notificationPublisher, webhook.getInterval());
            registeredWebhooks.putIfAbsent(url, scheduledTask);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Url - %s already exists.", url));
        }
    }

    public void updateInterval(Webhook webhook) {
        String url = webhook.getUrl();
        if(registeredWebhooks.containsKey(url)){
            unregisterWebhook(url);
            registerWebhook(webhook);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Url - %s not found.", url));
        }
    }

    public void unregisterWebhook(String url){
        ScheduledFuture job = registeredWebhooks.get(url);
        if(job != null){
            job.cancel(false);
            registeredWebhooks.remove(url);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Url - %s not found.", url));
        }
    }

}
