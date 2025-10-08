#Compilar
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

#Copiar el pom y instalar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

#Copiar el codigo fuente y compilar
COPY . .
RUN mvn package -DskipTests

#Imagen final
FROM eclipse-temurin:21-jre

WORKDIR /app

#Copiar el jar compilado
COPY --from=build /app/target/*.jar app.jar

#Comando para ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]