# Plames-Assembler
Plames Assembler - system with GUI web interface for automatic assembly Plames bundle (program jar also additionals files like configs). Is a part of Plames distribution service.

## Plames bundle build path
1) Create and init request.
2) Download sources of selected modules.
3) Create docker container.
4) Build project by Gradle in docker container.
5) Obfuscate.
6) Assembly bundle.
7) Clear.
