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
package org.jreleaser.gradle.plugin.internal.dsl

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.jreleaser.gradle.plugin.dsl.GenericGit

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.4.0
 */
@CompileStatic
class GenericGitImpl extends AbstractGitService implements GenericGit {
    final ChangelogImpl changelog
    final MilestoneImpl milestone
    final CommitAuthorImpl commitAuthor

    @Inject
    GenericGitImpl(ObjectFactory objects) {
        super(objects)
        changelog = objects.newInstance(ChangelogImpl, objects)
        milestone = objects.newInstance(MilestoneImpl, objects)
        commitAuthor = objects.newInstance(CommitAuthorImpl, objects)
    }

    @Override
    @Internal
    boolean isSet() {
        super.isSet() ||
            changelog.isSet() ||
            milestone.isSet() ||
            commitAuthor.isSet()
    }

    org.jreleaser.model.GenericGit toModel() {
        org.jreleaser.model.GenericGit service = new org.jreleaser.model.GenericGit()
        toModel(service)
        if (changelog.isSet()) service.changelog = changelog.toModel()
        if (milestone.isSet()) service.milestone = milestone.toModel()
        if (commitAuthor.isSet()) service.commitAuthor = commitAuthor.toModel()
        service
    }
}
