## Ticketing GUI

![Build status](https://github.com/azihassan/ticketing-gui/actions/workflows/build.yml/badge.svg "Build status")

### Installation

You can build the project from source by following these steps:

- git clone https://github.com/azihassan/ticketing-gui
- cd ticketing-gui
- mvn package
- java -jar target/ticketing-gui-1.0-SNAPSHOT-jar-with-dependencies.jar

Alternatively, you can download a prebuilt artifact from https://github.com/azihassan/ticketing-gui/actions (needs a Github account)

~~A release will be published soon~~ 
Releases are now published in: https://github.com/azihassan/ticketing-gui/releases

The project expects a REST API to be running on http://localhost:8080

You can find instructions for setting up the REST API here: https://github.com/azihassan/ticketing
