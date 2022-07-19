package com.webhook.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Webhook {

    @NotEmpty(groups = {FullInfo.class, PartialInfo.class})
    private String url;
    @NotNull(groups = FullInfo.class)
    private Long interval;
}
