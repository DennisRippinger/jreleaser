/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.maven.plugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 0.3.0
 */
public class Upload implements EnabledAware {
    private final Map<String, Artifactory> artifactory = new LinkedHashMap<>();
    private final Map<String, Http> http = new LinkedHashMap<>();
    private Boolean enabled;

    void setAll(Upload assemble) {
        this.enabled = assemble.enabled;
        setArtifactory(assemble.artifactory);
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabledSet() {
        return enabled != null;
    }

    public Map<String, Artifactory> getArtifactory() {
        return artifactory;
    }

    public void setArtifactory(Map<String, Artifactory> artifactory) {
        this.artifactory.clear();
        this.artifactory.putAll(artifactory);
    }

    public Map<String, Http> getHttp() {
        return http;
    }

    public void setHttp(Map<String, Http> http) {
        this.http.clear();
        this.http.putAll(http);
    }
}
