package com.viniciuscoscia.anilistapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform