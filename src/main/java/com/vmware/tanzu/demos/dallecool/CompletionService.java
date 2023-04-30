/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.dallecool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CompletionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionService.class);
    private final OpenAiConfiguration openai;
    private final WebClient client;
    private final String apiEndpoint = "/v1/completions";

    public static final String COMPLETION_MODEL = "text-davinci-003";
    public static final int MAX_TOKENS = 100;

    public CompletionService(OpenAiConfiguration openai, WebClient client) {
        this.openai = openai;
        this.client = client;
    }

    public Mono<String> generateCompletion(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw new IllegalArgumentException("Prompt must not be empty");
        }
        LOGGER.info("Sending request to OpenAI: {}", prompt);
        final var req = new CompletionRequest(prompt, COMPLETION_MODEL, MAX_TOKENS);
        return client.post().uri(openai.api() + apiEndpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openai.key())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CompletionResponse.class)
                .doOnSuccess(resp -> {
                    LOGGER.info("Received response from OpenAI for request: {}", prompt);
                })
                .filter(resp -> resp.choices.length > 0)
                .map(resp -> resp.choices[0].text);
    }

    private record CompletionResponse(CompletionResponseChoice[] choices) {
    }

    private record CompletionResponseChoice(String text) {
    }

    private record CompletionRequest(String prompt, String model, int max_tokens) {
    }
}
