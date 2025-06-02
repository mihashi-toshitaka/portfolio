# ベースイメージ（Java 21 対応）
FROM eclipse-temurin:21-jdk-jammy

# 作業ディレクトリ作成
WORKDIR /app

# JAR ファイルをコンテナにコピー（build/libs に生成されている前提）
COPY app.jar app.jar

# 必要に応じてポートを公開
EXPOSE 80

# Spring Boot アプリケーションの実行
ENTRYPOINT ["java", "-jar", "app.jar"]