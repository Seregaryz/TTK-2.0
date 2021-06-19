package com.example.ttk_20.system.message

data class SystemMessage(
    val text: String,
    val type: SystemMessageType = SystemMessageType.ALERT
)