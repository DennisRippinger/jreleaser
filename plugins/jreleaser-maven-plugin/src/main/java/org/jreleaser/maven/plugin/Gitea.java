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

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public class Gitea extends GitService {
    private boolean draft;
    private Boolean prerelease;

    public Gitea() {
        setHost("try.gitea.io");
        setRepoUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}");
        setRepoCloneUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}.git");
        setCommitUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/commits");
        setDownloadUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/download/v{{projectVersion}}/{{artifactFileName}}");
        setReleaseNotesUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/tag/v{{projectVersion}}");
        setLatestReleaseUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/latest");
        setIssueTrackerUrlFormat("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/issues");
    }

    void setAll(Gitea service) {
        super.setAll(service);
        this.draft = service.draft;
        this.prerelease = service.prerelease;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isPrerelease() {
        return prerelease != null && prerelease;
    }

    public void setPrerelease(Boolean prerelease) {
        this.prerelease = prerelease;
    }

    public boolean isPrereleaseSet() {
        return prerelease != null;
    }
}
