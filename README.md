Application POSTs a message to a registered WebHook URL using the interval specified in ms. You can run the application using the JAR provided.

The application starts on Port 8080.

Endpoints:

- POST: /api/webhook  { url: string, interval: long }
- DELETE: /api/webhook { url: string }
- PUT: /api/webhook { url: string, interval: long }

You can use the following website to generate test Webhooks -> https://webhook.site/