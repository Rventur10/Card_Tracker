package com.example.cardtracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CardTrackerApplication

fun main(args: Array<String>) {
    runApplication<CardTrackerApplication>(*args)
}