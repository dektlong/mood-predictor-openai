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

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;

@Service
public class CompletionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionService.class);
    public static final int TIMEOUT_SECONDS = 20;
    private final OpenAiConfiguration openai;

    public static final String COMPLETION_MODEL = "text-davinci-003";
    public static final int MAX_TOKENS = 1024;

    public CompletionService(OpenAiConfiguration openai) {
        this.openai = openai;
    }

    public String generateCompletion(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw new IllegalArgumentException("Prompt must not be empty");
        }
        OpenAiService service = new OpenAiService(openai.key(), Duration.ofSeconds(TIMEOUT_SECONDS));

        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model(COMPLETION_MODEL)
                .maxTokens(MAX_TOKENS)
                .echo(false)
                .build();
        LOGGER.info("Sending request to OpenAI: {}", completionRequest);

        List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
        LOGGER.info("Received " + choices.size() + " choices");
        return choices.get(0).getText();
    }

}
