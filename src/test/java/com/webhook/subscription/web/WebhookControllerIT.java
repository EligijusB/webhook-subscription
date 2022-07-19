package com.webhook.subscription.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webhook.subscription.model.Webhook;
import com.webhook.subscription.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class WebhookControllerIT {

    private final static String TEST_URL = "http://someHost.webhook.com";
    private final static Webhook TEST_WEBHOOK =  new Webhook(TEST_URL, 1500L);

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void registerWebhook() throws Exception {
        //Given
        TEST_WEBHOOK.setInterval(1500L);
        mockServer.expect(ExpectedCount.times(2), MockRestRequestMatchers.requestTo(TEST_URL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                        .andRespond(MockRestResponseCreators.withSuccess());

        //When
        registerWebhook(TEST_WEBHOOK);

        Thread.sleep(2000);
        mockServer.verify();
        cleanup();
    }

    @Test
    void updateWebhook() throws Exception {
        //Given
        TEST_WEBHOOK.setInterval(10000L);
        mockServer.expect(ExpectedCount.times(2), MockRestRequestMatchers.requestTo(TEST_URL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess());
        registerWebhook(TEST_WEBHOOK);

        TEST_WEBHOOK.setInterval(1000L);
        //When
        mockMvc.perform(MockMvcRequestBuilders.put("/api/webhook").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(TEST_WEBHOOK))).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Thread.sleep(1000);
        mockServer.verify();
        cleanup();
    }

    @Test
    void unregisterWebhook() throws Exception {
        //Given
        TEST_WEBHOOK.setInterval(100L);
        mockServer.expect(ExpectedCount.times(1), MockRestRequestMatchers.requestTo(TEST_URL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess());
        registerWebhook(TEST_WEBHOOK);

        //When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/webhook").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(TEST_WEBHOOK))).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        Thread.sleep(1500);
        mockServer.verify();
    }

    private void registerWebhook(Webhook webhook) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/webhook").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(webhook))).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    private void cleanup(){
        webhookService.unregisterWebhook(TEST_URL);
    }
}