FROM selenium/standalone-chrome:latest
USER 0

COPY . .
USER 1200
