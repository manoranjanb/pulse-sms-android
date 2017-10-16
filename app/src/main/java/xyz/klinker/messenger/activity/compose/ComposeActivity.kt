/*
 * Copyright (C) 2017 Luke Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.klinker.messenger.activity.compose

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import xyz.klinker.messenger.R
import xyz.klinker.messenger.api.implementation.Account
import xyz.klinker.messenger.api.implementation.ApiUtils
import xyz.klinker.messenger.shared.data.Settings
import xyz.klinker.messenger.shared.util.ActivityUtils
import xyz.klinker.messenger.shared.util.ColorUtils

/**
 * Activity to display UI for creating a new conversation.
 */
class ComposeActivity : AppCompatActivity() {

    internal val contactsProvider = ComposeContactsProvider(this)
    internal val vCardSender = ComposeVCardSender(this)
    internal val shareHandler = ComposeShareHandler(this)
    internal val sender = ComposeSendHelper(this)

    private val intentHandler = ComposeIntentHandler(this)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setBackgroundColor(Settings.mainColorSet.color)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = " "


        findViewById<View>(R.id.toolbar_holder).setBackgroundColor(Settings.mainColorSet.color)
        ActivityUtils.setStatusBarColor(this, Settings.mainColorSet.colorDark)
        ActivityUtils.setTaskDescription(this)
        ColorUtils.checkBlackBackground(this)

        sender.setupViews()
        contactsProvider.setupViews()
        intentHandler.handle(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_compose, menu)

        val item = menu.findItem(R.id.menu_mobile_only)
        item.isChecked = Settings.mobileOnly

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_mobile_only -> {
                val newValue = !item.isChecked

                item.isChecked = newValue

                Settings.setValue(this, getString(R.string.pref_mobile_only), newValue)
                Settings.forceUpdate(this)

                contactsProvider.toggleMobileOnly(item.isChecked)
                ApiUtils.updateMobileOnly(Account.accountId, newValue)

                return true
            }
        }

        return false
    }
}