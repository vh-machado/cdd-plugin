package com.example.helloworld.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

fun showNotification(project: Project, message: String) {
    val notification = Notification(
        "Code Notification",
        "CDD Plugin",
        message,
        NotificationType.WARNING
    )
    Notifications.Bus.notify(notification, project)
}