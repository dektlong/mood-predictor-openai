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

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DalleImageGeneratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DalleImageGeneratorService.class);
    private final OpenAiConfiguration openai;

    public DalleImageGeneratorService(OpenAiConfiguration openai) {
        this.openai = openai;
    }

    public String generateImage(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw new IllegalArgumentException("Prompt must not be empty");
        }
        LOGGER.info("Sending request to OpenAI: {}", prompt);
        OpenAiService service = new OpenAiService(openai.key());

        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .prompt(prompt)
                .build();

        List<Image> choices = service.createImage(createImageRequest).getData();
        LOGGER.info("Received " + choices.size() + " choices");
        return choices.get(0).getUrl();
    }
}
