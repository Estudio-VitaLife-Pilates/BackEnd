#!/bin/sh
set -eu

: "${DB_URL:?DB_URL must be set}"
: "${DB_USERNAME:?DB_USERNAME must be set}"
: "${DB_PASSWORD:?DB_PASSWORD must be set}"
: "${JWT_SECRET:?JWT_SECRET must be set}"

export DB_URL DB_USERNAME DB_PASSWORD JWT_SECRET
exec java -jar /app/app.jar
