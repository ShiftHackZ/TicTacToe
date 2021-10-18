package com.dmitriy.moroz.tictactoe.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.dmitriy.moroz.tictactoe.R

fun <A : Activity> A.openInBrowser(url: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW)
    browserIntent.data = Uri.parse(url)
    startActivity(browserIntent)
}

@SuppressLint("NewApi")
fun <A : Activity> A.openInChromeTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(getColor(R.color.colorPrimary))
    builder.setShowTitle(true)
    builder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
    builder.build().launchUrl(this, Uri.parse(url))
}

fun <A : Activity> A.share(title: String, url: String) {
    val share = Intent(Intent.ACTION_SEND);
    share.type = "text/plain";
    //share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    share.putExtra(Intent.EXTRA_SUBJECT, title);
    share.putExtra(Intent.EXTRA_TEXT, url);
    startActivity(Intent.createChooser(share, getString(R.string.share_app)))
}