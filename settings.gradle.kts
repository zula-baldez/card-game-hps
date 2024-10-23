pluginManagement {
    plugins {
        kotlin("jvm") version "1.8.21"
    }
}
rootProject.name = "card-game-hps"

include(
    "auth-service",
    "common",
    "personal-account",
    "room-service",
    "game-handler",
    "eureka-server"
)
