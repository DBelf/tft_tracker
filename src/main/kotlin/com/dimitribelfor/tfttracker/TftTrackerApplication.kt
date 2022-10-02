package com.dimitribelfor.tfttracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TftTrackerApplication

fun main(args: Array<String>) {
    runApplication<TftTrackerApplication>(*args)
}
