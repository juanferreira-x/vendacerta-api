# Imagem da API em duas etapas (multi-stage):
# 1) "build": imagem grande com JDK + Maven para compilar e gerar o .jar
# 2) "runtime": imagem pequena só com o JRE, rodando com um usuário sem privilégios (não root)

FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Baixa as dependências primeiro: enquanto o pom.xml não mudar, o Docker reaproveita esta camada.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -q -DskipTests package && cp target/*.jar app.jar


FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /workspace/app.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
