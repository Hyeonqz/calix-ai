plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "core-backend"
include("invest-api")
include("invest-batch")
include("invest-domain")
include("invest-external")
include("module-shared")