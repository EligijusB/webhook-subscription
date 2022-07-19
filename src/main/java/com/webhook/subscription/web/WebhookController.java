package com.webhook.subscription.web;

import com.webhook.subscription.model.FullInfo;
import com.webhook.subscription.model.PartialInfo;
import com.webhook.subscription.model.Webhook;
import com.webhook.subscription.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/api/webhook")
    public ResponseEntity registerWebhook(@Validated(FullInfo.class) @RequestBody Webhook webhook, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        webhookService.registerWebhook(webhook);
        return ResponseEntity.ok("Webhook registered successfully.");
    }

    @PutMapping("/api/webhook")
    public ResponseEntity updateWebhook(@Validated(FullInfo.class) @RequestBody Webhook webhook, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        webhookService.updateInterval(webhook);
        return ResponseEntity.ok("Webhook updated successfully.");
    }

    @DeleteMapping("/api/webhook")
    public ResponseEntity unregisterWebhook(@Validated(PartialInfo.class) @RequestBody Webhook webhook, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        webhookService.unregisterWebhook(webhook.getUrl());
        return ResponseEntity.ok("Webhook de-registered successfully.");
    }
}
