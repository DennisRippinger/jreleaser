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
package org.jreleaser.model;

import org.jreleaser.util.Env;
import org.jreleaser.util.MustacheUtils;
import org.jreleaser.util.OsDetector;
import org.jreleaser.util.PlatformUtils;
import org.jreleaser.util.Version;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jreleaser.util.Constants.HIDE;
import static org.jreleaser.util.Constants.KEY_CANONICAL_REPO_NAME;
import static org.jreleaser.util.Constants.KEY_COMMIT_URL;
import static org.jreleaser.util.Constants.KEY_ISSUE_TRACKER_URL;
import static org.jreleaser.util.Constants.KEY_LATEST_RELEASE_URL;
import static org.jreleaser.util.Constants.KEY_MILESTONE_NAME;
import static org.jreleaser.util.Constants.KEY_OS_ARCH;
import static org.jreleaser.util.Constants.KEY_OS_NAME;
import static org.jreleaser.util.Constants.KEY_OS_PLATFORM;
import static org.jreleaser.util.Constants.KEY_OS_VERSION;
import static org.jreleaser.util.Constants.KEY_PROJECT_DESCRIPTION;
import static org.jreleaser.util.Constants.KEY_PROJECT_EFFECTIVE_VERSION;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_ARTIFACT_ID;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_GROUP_ID;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_MAIN_CLASS;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_VERSION;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_VERSION_BUILD;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_VERSION_MAJOR;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_VERSION_MINOR;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_VERSION_PATCH;
import static org.jreleaser.util.Constants.KEY_PROJECT_JAVA_VERSION_TAG;
import static org.jreleaser.util.Constants.KEY_PROJECT_LICENSE;
import static org.jreleaser.util.Constants.KEY_PROJECT_LONG_DESCRIPTION;
import static org.jreleaser.util.Constants.KEY_PROJECT_NAME;
import static org.jreleaser.util.Constants.KEY_PROJECT_NAME_CAPITALIZED;
import static org.jreleaser.util.Constants.KEY_PROJECT_VERSION;
import static org.jreleaser.util.Constants.KEY_PROJECT_WEBSITE;
import static org.jreleaser.util.Constants.KEY_RELEASE_NAME;
import static org.jreleaser.util.Constants.KEY_RELEASE_NOTES_URL;
import static org.jreleaser.util.Constants.KEY_REPO_BRANCH;
import static org.jreleaser.util.Constants.KEY_REPO_CLONE_URL;
import static org.jreleaser.util.Constants.KEY_REPO_HOST;
import static org.jreleaser.util.Constants.KEY_REPO_NAME;
import static org.jreleaser.util.Constants.KEY_REPO_OWNER;
import static org.jreleaser.util.Constants.KEY_REPO_URL;
import static org.jreleaser.util.Constants.KEY_REVERSE_REPO_HOST;
import static org.jreleaser.util.Constants.KEY_TAG_NAME;
import static org.jreleaser.util.Constants.UNSET;
import static org.jreleaser.util.MustacheUtils.applyTemplate;
import static org.jreleaser.util.StringUtils.getClassNameForLowerCaseHyphenSeparatedName;
import static org.jreleaser.util.StringUtils.isBlank;
import static org.jreleaser.util.StringUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class GitService implements Releaser, CommitAuthorAware, OwnerAware, TimeoutAware {
    public static final String TAG_NAME = "TAG_NAME";
    public static final String RELEASE_NAME = "RELEASE_NAME";
    public static final String OVERWRITE = "OVERWRITE";
    public static final String UPDATE = "UPDATE";
    public static final String PRERELEASE = "PRERELEASE";
    public static final String DRAFT = "DRAFT";
    public static final String SKIP_TAG = "SKIP_TAG";
    public static final String BRANCH = "BRANCH";

    public static final String TAG_EARLY_ACCESS = "early-access";

    private final String serviceName;
    private final Changelog changelog = new Changelog();
    private final Milestone milestone = new Milestone();
    private final CommitAuthor commitAuthor = new CommitAuthor();
    private final boolean releaseSupported;

    protected Boolean enabled;
    private String host;
    private String owner;
    private String name;
    private String repoUrlFormat;
    private String repoCloneUrlFormat;
    private String commitUrlFormat;
    private String downloadUrlFormat;
    private String releaseNotesUrlFormat;
    private String latestReleaseUrlFormat;
    private String issueTrackerUrlFormat;
    private String username;
    private String token;
    private String tagName;
    private String releaseName;
    private String branch;
    private boolean sign;
    private Boolean skipTag;
    private Boolean overwrite;
    private Boolean update;
    private String apiEndpoint;
    private int connectTimeout;
    private int readTimeout;

    private String cachedTagName;
    private String cachedReleaseName;

    protected GitService(String serviceName, boolean releaseSupported) {
        this.serviceName = serviceName;
        this.releaseSupported = releaseSupported;
    }

    @Override
    public boolean isReleaseSupported() {
        return releaseSupported;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    void setAll(GitService service) {
        this.enabled = service.enabled;
        this.host = service.host;
        this.owner = service.owner;
        this.name = service.name;
        this.repoUrlFormat = service.repoUrlFormat;
        this.repoCloneUrlFormat = service.repoCloneUrlFormat;
        this.commitUrlFormat = service.commitUrlFormat;
        this.downloadUrlFormat = service.downloadUrlFormat;
        this.releaseNotesUrlFormat = service.releaseNotesUrlFormat;
        this.latestReleaseUrlFormat = service.latestReleaseUrlFormat;
        this.issueTrackerUrlFormat = service.issueTrackerUrlFormat;
        this.username = service.username;
        this.token = service.token;
        this.tagName = service.tagName;
        this.releaseName = service.releaseName;
        this.branch = service.branch;
        this.sign = service.sign;
        this.skipTag = service.skipTag;
        this.overwrite = service.overwrite;
        this.update = service.update;
        this.apiEndpoint = service.apiEndpoint;
        this.connectTimeout = service.connectTimeout;
        this.readTimeout = service.readTimeout;
        setCommitAuthor(service.commitAuthor);
        setChangelog(service.changelog);
        setMilestone(service.milestone);
    }

    public String getCanonicalRepoName() {
        if (isNotBlank(owner)) {
            return owner + "/" + name;
        }
        return name;
    }

    public abstract String getReverseRepoHost();

    public String getConfiguredTagName() {
        return Env.resolve(TAG_NAME, tagName);
    }

    public String getResolvedTagName(JReleaserModel model) {
        if (isBlank(cachedTagName)) {
            cachedTagName = Env.resolve(TAG_NAME, cachedTagName);
        }

        if (isBlank(cachedTagName)) {
            cachedTagName = applyTemplate(tagName, props(model));
        } else if (cachedTagName.contains("{{")) {
            cachedTagName = applyTemplate(cachedTagName, props(model));
        }

        return cachedTagName;
    }

    public String getEffectiveTagName(JReleaserModel model) {
        if (model.getProject().isSnapshot()) {
            return TAG_EARLY_ACCESS;
        }

        return cachedTagName;
    }

    public String getResolvedReleaseName(JReleaserModel model) {
        if (isBlank(cachedReleaseName)) {
            cachedReleaseName = Env.resolve(RELEASE_NAME, cachedReleaseName);
        }

        if (isBlank(cachedReleaseName)) {
            cachedReleaseName = applyTemplate(releaseName, props(model));
        } else if (cachedReleaseName.contains("{{")) {
            cachedReleaseName = applyTemplate(cachedReleaseName, props(model));
        }

        return cachedReleaseName;
    }

    public String getEffectiveReleaseName() {
        return cachedReleaseName;
    }

    public String getResolvedRepoUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(repoUrlFormat, props(model));
    }

    public String getResolvedRepoCloneUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(repoCloneUrlFormat, props(model));
    }

    public String getResolvedRepoUrl(JReleaserModel model, String repoOwner, String repoName) {
        if (!releaseSupported) return "";
        Map<String, Object> props = props(model);
        props.put(KEY_REPO_OWNER, repoOwner);
        props.put(KEY_REPO_NAME, repoName);
        return applyTemplate(repoUrlFormat, props);
    }

    public String getResolvedRepoCloneUrl(JReleaserModel model, String repoOwner, String repoName) {
        if (!releaseSupported) return "";
        Map<String, Object> props = props(model);
        props.put(KEY_REPO_OWNER, repoOwner);
        props.put(KEY_REPO_NAME, repoName);
        return applyTemplate(repoCloneUrlFormat, props);
    }

    public String getResolvedCommitUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(commitUrlFormat, props(model));
    }

    public String getResolvedDownloadUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(downloadUrlFormat, props(model));
    }

    public String getResolvedReleaseNotesUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(releaseNotesUrlFormat, props(model));
    }

    public String getResolvedLatestReleaseUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(latestReleaseUrlFormat, props(model));
    }

    public String getResolvedIssueTrackerUrl(JReleaserModel model) {
        if (!releaseSupported) return "";
        return applyTemplate(issueTrackerUrlFormat, props(model));
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepoUrlFormat() {
        return repoUrlFormat;
    }

    public void setRepoUrlFormat(String repoUrlFormat) {
        this.repoUrlFormat = repoUrlFormat;
    }

    public String getRepoCloneUrlFormat() {
        return repoCloneUrlFormat;
    }

    public void setRepoCloneUrlFormat(String repoCloneUrlFormat) {
        this.repoCloneUrlFormat = repoCloneUrlFormat;
    }

    public String getCommitUrlFormat() {
        return commitUrlFormat;
    }

    public void setCommitUrlFormat(String commitUrlFormat) {
        this.commitUrlFormat = commitUrlFormat;
    }

    public String getDownloadUrlFormat() {
        return downloadUrlFormat;
    }

    public void setDownloadUrlFormat(String downloadUrlFormat) {
        this.downloadUrlFormat = downloadUrlFormat;
    }

    public String getReleaseNotesUrlFormat() {
        return releaseNotesUrlFormat;
    }

    public void setReleaseNotesUrlFormat(String releaseNotesUrlFormat) {
        this.releaseNotesUrlFormat = releaseNotesUrlFormat;
    }

    public String getLatestReleaseUrlFormat() {
        return latestReleaseUrlFormat;
    }

    public void setLatestReleaseUrlFormat(String latestReleaseUrlFormat) {
        this.latestReleaseUrlFormat = latestReleaseUrlFormat;
    }

    public String getIssueTrackerUrlFormat() {
        return issueTrackerUrlFormat;
    }

    public void setIssueTrackerUrlFormat(String issueTrackerUrlFormat) {
        this.issueTrackerUrlFormat = issueTrackerUrlFormat;
    }

    public String getResolvedToken() {
        return Env.resolve(Env.toVar(getServiceName()) + "_TOKEN", token);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public CommitAuthor getCommitAuthor() {
        return commitAuthor;
    }

    @Override
    public void setCommitAuthor(CommitAuthor commitAuthor) {
        this.commitAuthor.setAll(commitAuthor);
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public Changelog getChangelog() {
        return changelog;
    }

    public void setChangelog(Changelog changelog) {
        this.changelog.setAll(changelog);
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone.setAll(milestone);
    }

    public boolean isSkipTag() {
        return skipTag != null && skipTag;
    }

    public void setSkipTag(Boolean skipTag) {
        this.skipTag = skipTag;
    }

    public boolean isSkipTagSet() {
        return skipTag != null;
    }

    public boolean isOverwrite() {
        return overwrite != null && overwrite;
    }

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isOverwriteSet() {
        return overwrite != null;
    }

    public boolean isUpdate() {
        return update != null && update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public boolean isUpdateSet() {
        return update != null;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("enabled", isEnabled());
        map.put("host", host);
        map.put("owner", owner);
        map.put("name", name);
        map.put("username", username);
        map.put("token", isNotBlank(getResolvedToken()) ? HIDE : UNSET);
        if (releaseSupported) {
            map.put("repoUrlFormat", repoUrlFormat);
            map.put("repoCloneUrlFormat", repoCloneUrlFormat);
            map.put("commitUrlFormat", commitUrlFormat);
            map.put("downloadUrlFormat", downloadUrlFormat);
            map.put("releaseNotesUrlFormat", releaseNotesUrlFormat);
            map.put("latestReleaseUrlFormat", latestReleaseUrlFormat);
            map.put("issueTrackerUrlFormat", issueTrackerUrlFormat);
        }
        map.put("tagName", tagName);
        if (releaseSupported) {
            map.put("releaseName", releaseName);
        }
        map.put("branch", branch);
        map.put("commitAuthor", commitAuthor.asMap(full));
        map.put("sign", sign);
        map.put("skipTag", isSkipTag());
        map.put("overwrite", isOverwrite());
        if (releaseSupported) {
            map.put("update", isUpdate());
            map.put("apiEndpoint", apiEndpoint);
            map.put("connectTimeout", connectTimeout);
            map.put("readTimeout", readTimeout);
        }
        map.put("changelog", changelog.asMap(full));
        if (releaseSupported) {
            map.put("milestone", milestone.asMap(full));
        }
        return map;
    }

    public Map<String, Object> props(JReleaserModel model) {
        // duplicate from JReleaserModel to avoid endless recursion
        Map<String, Object> props = new LinkedHashMap<>();
        Project project = model.getProject();
        props.putAll(model.getEnvironment().getProperties());
        props.put(KEY_PROJECT_NAME, project.getName());
        props.put(KEY_PROJECT_NAME_CAPITALIZED, getClassNameForLowerCaseHyphenSeparatedName(project.getName()));
        props.put(KEY_PROJECT_VERSION, project.getVersion());
        props.put(KEY_PROJECT_EFFECTIVE_VERSION, project.getEffectiveVersion());
        if (isNotBlank(project.getDescription())) {
            props.put(KEY_PROJECT_DESCRIPTION, MustacheUtils.passThrough(project.getDescription()));
        }
        if (isNotBlank(project.getLongDescription())) {
            props.put(KEY_PROJECT_LONG_DESCRIPTION, MustacheUtils.passThrough(project.getLongDescription()));
        }
        if (isNotBlank(project.getWebsite())) {
            props.put(KEY_PROJECT_WEBSITE, project.getWebsite());
        }
        if (isNotBlank(project.getLicense())) {
            props.put(KEY_PROJECT_LICENSE, project.getLicense());
        }

        if (project.getJava().isEnabled()) {
            props.putAll(project.getJava().getResolvedExtraProperties());
            props.put(KEY_PROJECT_JAVA_GROUP_ID, project.getJava().getGroupId());
            props.put(KEY_PROJECT_JAVA_ARTIFACT_ID, project.getJava().getArtifactId());
            props.put(KEY_PROJECT_JAVA_VERSION, project.getJava().getVersion());
            props.put(KEY_PROJECT_JAVA_MAIN_CLASS, project.getJava().getMainClass());
            Version jv = Version.of(project.getJava().getVersion());
            props.put(KEY_PROJECT_JAVA_VERSION_MAJOR, jv.getMajor());
            if (jv.hasMinor()) props.put(KEY_PROJECT_JAVA_VERSION_MINOR, jv.getMinor());
            if (jv.hasPatch()) props.put(KEY_PROJECT_JAVA_VERSION_PATCH, jv.getPatch());
            if (jv.hasTag()) props.put(KEY_PROJECT_JAVA_VERSION_TAG, jv.getTag());
            if (jv.hasBuild()) props.put(KEY_PROJECT_JAVA_VERSION_BUILD, jv.getBuild());
        }

        props.putAll(project.getResolvedExtraProperties());

        String osName = PlatformUtils.getOsDetector().get(OsDetector.DETECTED_NAME);
        String osArch = PlatformUtils.getOsDetector().get(OsDetector.DETECTED_ARCH);
        props.put(KEY_OS_NAME, osName);
        props.put(KEY_OS_ARCH, osArch);
        props.put(KEY_OS_PLATFORM, osName + "-" + osArch);
        props.put(KEY_OS_VERSION, PlatformUtils.getOsDetector().get(OsDetector.DETECTED_VERSION));

        props.put(KEY_REPO_HOST, host);
        props.put(KEY_REPO_OWNER, owner);
        props.put(KEY_REPO_NAME, name);
        props.put(KEY_REPO_BRANCH, branch);
        props.put(KEY_REVERSE_REPO_HOST, getReverseRepoHost());
        props.put(KEY_CANONICAL_REPO_NAME, getCanonicalRepoName());
        props.put(KEY_TAG_NAME, project.isSnapshot() ? TAG_EARLY_ACCESS : cachedTagName);
        props.put(KEY_RELEASE_NAME, cachedReleaseName);
        props.put(KEY_MILESTONE_NAME, milestone.getEffectiveName());
        return props;
    }

    public void fillProps(Map<String, Object> props, JReleaserModel model) {
        props.put(KEY_REPO_HOST, host);
        props.put(KEY_REPO_OWNER, owner);
        props.put(KEY_REPO_NAME, name);
        props.put(KEY_REPO_BRANCH, branch);
        props.put(KEY_REVERSE_REPO_HOST, getReverseRepoHost());
        props.put(KEY_CANONICAL_REPO_NAME, getCanonicalRepoName());
        props.put(KEY_TAG_NAME, getEffectiveTagName(model));
        props.put(KEY_RELEASE_NAME, getEffectiveReleaseName());
        props.put(KEY_MILESTONE_NAME, milestone.getEffectiveName());
        props.put(KEY_REPO_URL, getResolvedRepoUrl(model));
        props.put(KEY_REPO_CLONE_URL, getResolvedRepoCloneUrl(model));
        props.put(KEY_COMMIT_URL, getResolvedCommitUrl(model));
        props.put(KEY_RELEASE_NOTES_URL, getResolvedReleaseNotesUrl(model));
        props.put(KEY_LATEST_RELEASE_URL, getResolvedLatestReleaseUrl(model));
        props.put(KEY_ISSUE_TRACKER_URL, getResolvedIssueTrackerUrl(model));
    }
}
