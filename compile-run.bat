cd ..
cd "Plames-ECO"
call webpack
cd ..
cd "Plames-Assembler"
call webpack
call gradlew bootJar
XCOPY "build\libs\Plames-Assembler-0.0.1-SNAPSHOT.jar" "test work directory\assembler.jar" /f /i /y /s 
CD "test work directory"
java -Xmx2G -jar assembler.jar
PAUSE