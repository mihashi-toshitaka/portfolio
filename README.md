# Project Tooling Notes

このプロジェクトでは、Spring Boot 開発の品質を揃えるために以下のツール設定を導入しています。

## フォーマット / 自動整形
- **Spotless + google-java-format**: `spotlessApply` で Java と補助ファイルを整形します。
- **EditorConfig**: `.editorconfig` に従ってインデントや改行を統一します。
- **OpenRewrite**: `rewriteRun` で Spring Boot 3.5 への更新や非推奨 API 検出などのレシピを実行します。

> 実行順序は `rewriteRun` → `editorconfigFormat` → `spotlessApply` となるように `build` タスクへ組み込んでいます。

## 静的解析 / セキュリティ
- **Checkstyle**: 命名規則や Javadoc など、フォーマットと競合しにくいルールのみチェックします。
- **Error Prone**: コンパイル時に潜在バグを検出します。
- **SpotBugs + FindSecBugs**: `spotbugsMain` / `spotbugsTest` で HTML レポートを生成します。
- **OWASP Dependency-Check**: 依存関係の脆弱性レポート（HTML / JSON）を出力します。

## アーキテクチャ整合性テスト
- **ArchUnit**: 依存関係として追加済みです。JUnit のテストとしてアーキテクチャ制約を記述できます。

## 主要タスク
- `./gradlew build`: OpenRewrite → EditorConfig → Spotless を順に実行してからビルドします。
- `./gradlew check`: フォーマット検証 + 静的解析 + 依存関係スキャンをまとめて実行します。
