clone repo.
cd into root folder
run the command [./mvnw clean package] to build and generate the jar file. 
cd into the target folder and run the jar file.
java -jar SeriesApi-1.0-SNAPSHOT.jar

./mvnw is for a bash or zsh terminal, powershell can use mvn directly.

run localhost:8080/swagger-ui or localhost:8080/v3/api-docs/ to view api using openAPI.
