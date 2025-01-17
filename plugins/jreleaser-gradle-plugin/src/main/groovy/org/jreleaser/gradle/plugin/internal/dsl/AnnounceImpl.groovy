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
import org.gradle.api.Action
import org.gradle.api.internal.provider.Providers
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.jreleaser.gradle.plugin.dsl.Announce
import org.jreleaser.gradle.plugin.dsl.Discord
import org.jreleaser.gradle.plugin.dsl.Discussions
import org.jreleaser.gradle.plugin.dsl.Gitter
import org.jreleaser.gradle.plugin.dsl.Mail
import org.jreleaser.gradle.plugin.dsl.Mastodon
import org.jreleaser.gradle.plugin.dsl.Sdkman
import org.jreleaser.gradle.plugin.dsl.Slack
import org.jreleaser.gradle.plugin.dsl.Teams
import org.jreleaser.gradle.plugin.dsl.Twitter
import org.jreleaser.gradle.plugin.dsl.Zulip
import org.kordamp.gradle.util.ConfigureUtil

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class AnnounceImpl implements Announce {
    final Property<Boolean> enabled
    final DiscordImpl discord
    final DiscussionsImpl discussions
    final GitterImpl gitter
    final MailImpl mail
    final MastodonImpl mastodon
    final SdkmanImpl sdkman
    final SlackImpl slack
    final TeamsImpl teams
    final TwitterImpl twitter
    final ZulipImpl zulip

    @Inject
    AnnounceImpl(ObjectFactory objects) {
        enabled = objects.property(Boolean).convention(Providers.notDefined())
        discord = objects.newInstance(DiscordImpl, objects)
        discussions = objects.newInstance(DiscussionsImpl, objects)
        gitter = objects.newInstance(GitterImpl, objects)
        mail = objects.newInstance(MailImpl, objects)
        mastodon = objects.newInstance(MastodonImpl, objects)
        sdkman = objects.newInstance(SdkmanImpl, objects)
        slack = objects.newInstance(SlackImpl, objects)
        teams = objects.newInstance(TeamsImpl, objects)
        twitter = objects.newInstance(TwitterImpl, objects)
        zulip = objects.newInstance(ZulipImpl, objects)
    }

    @Override
    void discord(Action<? super Discord> action) {
        action.execute(discord)
    }

    @Override
    void discussions(Action<? super Discussions> action) {
        action.execute(discussions)
    }

    @Override
    void gitter(Action<? super Gitter> action) {
        action.execute(gitter)
    }

    @Override
    void mail(Action<? super Mail> action) {
        action.execute(mail)
    }

    @Override
    void mastodon(Action<? super Mastodon> action) {
        action.execute(mastodon)
    }

    @Override
    void sdkman(Action<? super Sdkman> action) {
        action.execute(sdkman)
    }

    @Override
    void slack(Action<? super Slack> action) {
        action.execute(slack)
    }

    @Override
    void teams(Action<? super Teams> action) {
        action.execute(teams)
    }

    @Override
    void twitter(Action<? super Twitter> action) {
        action.execute(twitter)
    }

    @Override
    void zulip(Action<? super Zulip> action) {
        action.execute(zulip)
    }

    @Override
    void discord(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Discord) Closure<Void> action) {
        ConfigureUtil.configure(action, discord)
    }

    @Override
    void discussions(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Discussions) Closure<Void> action) {
        ConfigureUtil.configure(action, discussions)
    }

    @Override
    void gitter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Gitter) Closure<Void> action) {
        ConfigureUtil.configure(action, gitter)
    }

    @Override
    void mail(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Mail) Closure<Void> action) {
        ConfigureUtil.configure(action, mail)
    }

    @Override
    void mastodon(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Mastodon) Closure<Void> action) {
        ConfigureUtil.configure(action, mastodon)
    }

    @Override
    void sdkman(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Sdkman) Closure<Void> action) {
        ConfigureUtil.configure(action, sdkman)
    }

    @Override
    void slack(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Slack) Closure<Void> action) {
        ConfigureUtil.configure(action, slack)
    }

    @Override
    void teams(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Teams) Closure<Void> action) {
        ConfigureUtil.configure(action, teams)
    }

    @Override
    void twitter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Twitter) Closure<Void> action) {
        ConfigureUtil.configure(action, twitter)
    }

    @Override
    void zulip(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Zulip) Closure<Void> action) {
        ConfigureUtil.configure(action, zulip)
    }

    org.jreleaser.model.Announce toModel() {
        org.jreleaser.model.Announce announce = new org.jreleaser.model.Announce()
        if (enabled.present) announce.enabled = enabled.get()
        if (discord.isSet()) announce.discord = discord.toModel()
        if (discussions.isSet()) announce.discussions = discussions.toModel()
        if (gitter.isSet()) announce.gitter = gitter.toModel()
        if (mail.isSet()) announce.mail = mail.toModel()
        if (mastodon.isSet()) announce.mastodon = mastodon.toModel()
        if (sdkman.isSet()) announce.sdkman = sdkman.toModel()
        if (slack.isSet()) announce.slack = slack.toModel()
        if (teams.isSet()) announce.teams = teams.toModel()
        if (twitter.isSet()) announce.twitter = twitter.toModel()
        if (zulip.isSet()) announce.zulip = zulip.toModel()
        announce
    }
}
