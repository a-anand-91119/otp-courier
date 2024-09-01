package dev.notyouraverage.otpcourier.models

data class SmsCourierPreference(
    var secretPassword: String = "",
    var whiteListedContactNumber: String = ""
)