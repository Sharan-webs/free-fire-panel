#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
exec /opt/gradle/gradle-8.4/bin/gradle "$@"
