#!/bin/sh
cd "$(dirname "$0")"
java -jar SteamSaleScraper.jar & disown
exit