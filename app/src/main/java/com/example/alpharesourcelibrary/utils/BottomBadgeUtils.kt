package com.movieplayer.android.utils

import android.graphics.Color
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomBadgeUtils {

    companion object {
        val shared = BottomBadgeUtils()
    }

    fun showBadge(
        navView: BottomNavigationView,
        menuItemId: Int,
        count: Int,
        backGroundColor: Int = Color.RED,
        badgeTextColor: Int = Color.WHITE,
        maxCharacterCount: Int = 500
    ) {
        val badge: BadgeDrawable = navView.getOrCreateBadge(menuItemId)
        badge.isVisible = true
        badge.number = count
        badge.maxCharacterCount = maxCharacterCount
        badge.badgeTextColor = badgeTextColor
        badge.backgroundColor = backGroundColor
    }

    fun hideBadge(
        navView: BottomNavigationView,
        menuItemId: Int
    ) {
        val badgeDrawable = navView.getBadge(menuItemId)
        if (badgeDrawable != null) {
            badgeDrawable.isVisible = false
            badgeDrawable.clearNumber()
        }
    }

    fun removeBadge(
        navView: BottomNavigationView,
        menuItemId: Int
    ) {
        navView.removeBadge(menuItemId)
    }
}