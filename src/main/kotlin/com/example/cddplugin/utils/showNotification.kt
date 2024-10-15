package com.example.cddplugin.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

fun showNotification(project: Project, title: String, message: String) {
    val notification = Notification(
        "Code Notification",
        title,
        message,
        NotificationType.WARNING
    )
    Notifications.Bus.notify(notification, project)
}