# フォーマット + リファクタリング運用ガイド

## 目的
- エディタの整形ルールを統一する
- 保存時・ビルド時に自動適用する
- OpenRewrite → EditorConfig → Spotless の順で実行する

## 対象
- Spotless（google-java-format）
- EditorConfig
- OpenRewrite

## 役割/構成
### Spotless + google-java-format
- Spotless は Gradle プラグインで `spotlessCheck` / `spotlessApply` を提供します。
- Java は google-java-format を使用します。Google Java Style を実装しており、**フォーマット設定は不可**です。

### EditorConfig
- エディタレベルの空白・改行ルール（インデント、行末、最終改行、末尾空白）を統一します。
- `.editorconfig` は Google Java Style（2 スペース、100 桁上限）に合わせます。

### OpenRewrite
- 自動リファクタリングレシピを実行します。
- **整形より先に実行**し、最終コードを Spotless / EditorConfig で整えます。
- 適用レシピ:
  - `UpgradeSpringBoot_3_5`（Spring Boot 3.5 へのアップグレード）
  - `FindDeprecatedUses`（非推奨 API の使用箇所検出）
  - `RemoveUnusedImports`（未使用 import の削除）
  - `CodeCleanup`（一般的なコードクリーンアップ）

## VS Code セットアップ
### 拡張機能
- EditorConfig for VS Code（`editorconfig.editorconfig`）
- Spotless Gradle（`richardwillis.vscode-spotless-gradle`）
- Gradle for Java（Spotless Gradle の前提）

### 拡張機能の概要と必要設定
#### EditorConfig for VS Code（`editorconfig.editorconfig`）
- `.editorconfig` を読み取り、インデント・行末・末尾空白・最終改行の設定をエディタへ自動反映します。
- 必要設定: 基本は不要です。**プロジェクトルートに `.editorconfig` を置く**ことが前提です。
- 競合回避: ワークスペース設定で `editor.tabSize` / `editor.insertSpaces` などを固定すると EditorConfig の適用が上書きされるため、原則は EditorConfig 側に寄せます。

#### Spotless Gradle（`richardwillis.vscode-spotless-gradle`）
- Gradle の `spotlessApply` / `spotlessCheck` を呼び出して整形・検証を行います。
- 必要条件: `./gradlew` が利用可能であること、**Gradle for Java 拡張**が有効であること。
- 保存時に走らせるための設定:
  - `editor.codeActionsOnSave` に `source.fixAll.spotlessGradle` を `explicit` で設定
  - `editor.defaultFormatter` を Spotless Gradle に設定（Java / Gradle）
  - Java 標準フォーマッタは無効化（`java.format.enabled: false`）

### `.vscode/settings.json` 例
```json
{
  "java.format.enabled": false,
  "spotlessGradle.diagnostics.enable": true,
  "spotlessGradle.format.enable": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.spotlessGradle": "explicit"
  },
  "[java]": {
    "editor.defaultFormatter": "richardwillis.vscode-spotless-gradle"
  },
  "[gradle]": {
    "editor.defaultFormatter": "richardwillis.vscode-spotless-gradle"
  }
}
```
保存時に Spotless が実行され、Java / Gradle で既定フォーマッタとして動きます。

### WSL / Remote 用の設定
WSL や Remote 環境では、拡張機能が **Remote Settings** を参照します。
SonarQube for IDE などのツール設定は、Workspace ではなく Remote 側の
`settings.json` に入れる必要があります。

例: `~/.vscode-server/data/Machine/settings.json`
```json
{
  "sonarlint.ls.javaHome": "${env:JAVA_HOME}",
  "sonarlint.pathToNodeExecutable": "/home/yourname/.nvm/versions/node/vXX.YY.ZZ/bin/node"
}
```
※ `sonarlint.pathToNodeExecutable` は `nvm which default` の結果を設定します。

## プロジェクト設定
### `.editorconfig`
Google Java Style に合わせる例（2 スペース、100 桁）。
```
root = true

[*]
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
charset = utf-8

[*.{java,gradle,md}]
indent_style = space
indent_size = 2
tab_width = 2
max_line_length = 100
```
補足:
- VS Code の EditorConfig 拡張は `indent_style` / `indent_size` / `tab_width` / `end_of_line` / `insert_final_newline` / `trim_trailing_whitespace` を扱います。
- その他のプロパティはツールによって無視される場合があります。

### `build.gradle`（Groovy）
```groovy
plugins {
  id 'java'
  id 'com.diffplug.spotless' version '8.1.0'
  id 'org.ec4j.editorconfig' version '0.1.0'
  id 'org.openrewrite.rewrite' version '7.25.0'
}

repositories {
  mavenCentral()
}

dependencies {
  // レシピ BOM と必要なレシピを追加
  rewrite platform('org.openrewrite.recipe:rewrite-recipe-bom:latest.release')
  rewrite 'org.openrewrite.recipe:rewrite-spring'
}

spotless {
  java {
    // google-java-format を固定
    googleJavaFormat('1.33.0')
    target 'src/**/*.java'
  }
  format('misc') {
    target '*.gradle', '*.md', '.gitignore'
    trimTrailingWhitespace()
    endWithNewline()
  }
}

editorconfig {
  includes = ['**/*.java', '**/*.gradle', '**/*.md']
  excludes = ['build/**', '.gradle/**']
}

rewrite {
  activeRecipe('org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_5')
  activeRecipe('org.openrewrite.java.search.FindDeprecatedUses')
  activeRecipe('org.openrewrite.java.RemoveUnusedImports')
  activeRecipe('org.openrewrite.java.cleanup.CodeCleanup')
}

// 実行順序: rewrite -> editorconfig -> spotless

tasks.named('editorconfigFormat') {
  mustRunAfter 'rewriteRun'
}

tasks.named('spotlessApply') {
  mustRunAfter 'rewriteRun', 'editorconfigFormat'
}

tasks.named('build') {
  // build 時は保存時フォーマット（Spotless）を最終結果にする
  dependsOn 'spotlessApply'
}
```

## 運用
### ローカル
- OpenRewrite を適用: `./gradlew rewriteRun`
- OpenRewrite のドライラン: `./gradlew rewriteDryRun`
- EditorConfig 適用: `./gradlew editorconfigFormat`
- Spotless 適用: `./gradlew spotlessApply`

### CI / 品質ゲート
- `./gradlew editorconfigCheck`
- `./gradlew spotlessCheck`
- 任意: `./gradlew rewriteDryRun`（`failOnDryRunResults = true` を使う）

## 補足・注意点
- Spotless Gradle 拡張は `spotlessApply` を利用して単一ファイル整形します。
- EditorConfig Gradle プラグインは全てのプロパティを修正できるわけではありません（行末や末尾空白などが中心）。
- google-java-format は Google Java Style 準拠（2 スペース・100 桁）です。

## 最新バージョン（2026-01-26 時点）
- Spotless Gradle plugin: 8.1.0
- OpenRewrite Gradle plugin: 7.25.0
- EditorConfig Gradle plugin (ec4j): 0.1.0
- google-java-format: 1.33.0

※ 導入時に必ず公式ソースで最新版を確認してください。
