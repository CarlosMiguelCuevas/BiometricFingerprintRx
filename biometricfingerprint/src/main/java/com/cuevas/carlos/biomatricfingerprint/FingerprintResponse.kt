package com.cuevas.carlos.biomatricfingerprint

enum class FingerprintResponse(var code: Int? = null, var message: CharSequence? = null) {
    SUCCESS(),
    CANCELLED(),
    HELP(),
    ERROR(),
    FAILED()
}