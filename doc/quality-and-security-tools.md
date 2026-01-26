# 品質/セキュリティ解析ツール導入ガイド

## 目的
- タイピング中: IDE 上でリアルタイムに問題を見つける
- ビルド時: 自動解析で品質/セキュリティの問題を検出し、必要に応じてビルド失敗にする
- 役割分担: フォーマッタ（google-java-format / Spotless）と競合しないようにする

## 対象
- タイピング中: SonarQube for IDE（旧 SonarLint）、Error Lens、Checkstyle for Java
- ビルド時: Checkstyle、Error Prone、SpotBugs + FindSecBugs、OWASP Dependency-Check

## 役割/構成
- Checkstyle は **命名規則や Javadoc 有無など、フォーマッタが扱わないルールのみ**に限定する
- Error Prone / SpotBugs はビルド失敗を推奨する
- OWASP Dependency-Check はレポート出力のみを推奨する

## VS Code セットアップ
### 拡張機能
- SonarQube for IDE（旧 SonarLint）
- Error Lens
- Checkstyle for Java

### 拡張機能の概要と必要設定
#### SonarQube for IDE（旧 SonarLint）
**概要**
- IDE 上でリアルタイムにコード品質・セキュリティの問題を検出し、クイックフィックスも提供します。
- SonarQube Cloud/Server と連携する「Connected Mode」でより高度な解析が可能です。

**インストール**
- VS Code の拡張機能検索で `SonarQube for IDE` をインストール。

**必要要件・設定**
- JRE 17+ が必要です。
- 既存の JDK/JRE を使いたい場合は `sonarlint.ls.javaHome` を設定します。

例: `.vscode/settings.json`
```json
{
  "sonarlint.ls.javaHome": "/path/to/jdk17-or-21"
}
```

**Node.js 要件（JSON 解析）と nvm での対応（WSL / Ubuntu）**
- SonarQube for IDE は JSON 解析に Node.js を使用します。
- エラー例: `Node.js runtime version 20.12.0 or later is required. Current version is 18.19.1.`
- WSL/Ubuntu では OS 付属の Node が古いことが多いため、**nvm でユーザー領域に新しい Node を入れる**のが安全です。

手順:
1) nvm をインストール
```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash
```

2) nvm を読み込み（またはターミナルを再起動）
```bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"
```

3) Node.js をインストール（LTS を推奨）
```bash
nvm install --lts
nvm alias default "lts/*"
node -v
```

4) VS Code が nvm の Node を使うように設定
```json
{
  "sonarlint.pathToNodeExecutable": "/home/yourname/.nvm/versions/node/vXX.YY.ZZ/bin/node"
}
```
※ 実パスは `nvm which default` で確認できます。

#### Error Lens
**概要**
- VS Code の診断表示を強化し、行全体のハイライトや行末のインラインメッセージ表示を行います。
- エラー/警告/情報などの表示レベルやメッセージのテンプレートを設定できます。

**インストール**
- VS Code の拡張機能検索で `Error Lens` をインストール。

**推奨設定**
- ノイズを抑えるため、`error` と `warning` に限定する例です。

例: `.vscode/settings.json`
```json
{
  "errorLens.enabled": true,
  "errorLens.enabledDiagnosticLevels": ["error", "warning"],
  "errorLens.messageTemplate": "$message"
}
```

#### Checkstyle for Java
**特徴**
- Checkstyle の構成ファイルを指定して Java のスタイル違反を検出します。
- 拡張機能側で Checkstyle バージョンを選択できます。

**必要要件**
- JDK 1.8+ / VS Code 1.30+ / Language Support for Java by Red Hat

**主な設定キー**
- `java.checkstyle.version`: Checkstyle バージョン
- `java.checkstyle.configuration`: 設定ファイル（ローカル or URL）
- `java.checkstyle.properties`: 設定ファイルのプロパティ
- `java.checkstyle.modules`: 追加のモジュール
- `java.checkstyle.autocheck`: 自動チェックの有無

例: `.vscode/settings.json`
```json
{
  "java.checkstyle.version": "12.1.2",
  "java.checkstyle.configuration": "${workspaceFolder}/config/checkstyle/checkstyle.xml",
  "java.checkstyle.autocheck": true
}
```

## プロジェクト設定
### Checkstyle（非必須）
**概要**
- google-java-format と競合する整形系のチェックは無効化し、
  **命名規則や Javadoc 有無など、フォーマッタが扱わないルールのみ**に限定します。
- ビルド時に失敗させる運用を推奨します。

**Gradle 設定（ビルド失敗推奨）**
```groovy
checkstyle {
  toolVersion = "12.1.2"
  configFile = file("$rootDir/config/checkstyle/checkstyle.xml")
  maxWarnings = 0
}

tasks.named("check") {
  dependsOn "checkstyleMain", "checkstyleTest"
}
```

**ルールを命名/Javadoc中心に絞る例（checkstyle.xml）**
google-java-format と競合しやすい「インデント」「空白」「改行」「波括弧位置」などの
フォーマット系チェックは入れず、命名規則と Javadoc のみを有効にした例です。
このプロジェクトの `config/checkstyle/checkstyle.xml` は同じ思想で最小構成にしています。

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
  "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
  "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
  <module name="TreeWalker">
    <!-- 命名規則 -->
    <module name="TypeName"/>
    <module name="MethodName"/>
    <module name="ParameterName"/>
    <module name="MemberName"/>
    <module name="LocalVariableName"/>
    <module name="ClassTypeParameterName"/>
    <module name="MethodTypeParameterName"/>
    <module name="InterfaceTypeParameterName"/>

    <!-- Javadoc -->
    <module name="JavadocType"/>
    <module name="JavadocMethod">
      <property name="allowMissingParamTags" value="false"/>
      <property name="allowMissingReturnTag" value="false"/>
      <property name="validateThrows" value="true"/>
      <property name="accessModifiers" value="public,protected"/>
    </module>
  </module>
</module>
```

### Error Prone
**概要**
- Java コンパイル時にバグを検出するコンパイラプラグインです。
- JDK 21+ での実行が必要です。

**Gradle 設定（ビルド失敗推奨）**
```groovy
plugins {
  id "net.ltgt.errorprone" version "4.3.0"
}

dependencies {
  errorprone "com.google.errorprone:error_prone_core:2.45.0"
}
```

### SpotBugs + FindSecBugs
**概要**
- SpotBugs はバイトコード解析でバグを検出します。
- FindSecBugs は SpotBugs のセキュリティ拡張です。
- HTML レポート出力を推奨します。

**Gradle 設定（ビルド失敗推奨）**
```groovy
plugins {
  id "com.github.spotbugs" version "6.4.8"
}

dependencies {
  spotbugsPlugins "com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0"
}

tasks.withType(com.github.spotbugs.snom.SpotBugsTask).configureEach {
  reports {
    html.required = true
    xml.required = false
  }
  ignoreFailures = false
}
```

### OWASP Dependency-Check
**概要**
- 依存関係の既知の脆弱性を検出する SCA ツールです。
- まずはレポート出力のみを推奨します。

**Gradle 設定（レポート出力のみ推奨）**
```groovy
plugins {
  id "org.owasp.dependencycheck" version "12.2.0"
}

dependencyCheck {
  failBuildOnCVSS = 11
  failOnError = false
  formats = ["HTML", "JSON"]
}
```

## 運用
- Checkstyle / Error Prone / SpotBugs はビルド失敗を推奨します。
- OWASP Dependency-Check はレポート出力のみを推奨します。

## 補足・注意点
- 命名規則を強めたい場合は `ConstantName` などの命名系モジュールを追加します。
- フォーマット系の代表例: `Indentation` / `WhitespaceAround` / `WhitespaceAfter` /
  `EmptyLineSeparator` / `LineLength` / `LeftCurly` / `RightCurly` などは無効化します。

## 最新バージョン（2026-01-26 時点）
- SonarQube for IDE: VS Code 版（SonarLint から改名）
- Error Lens: VS Code Marketplace で配布
- Checkstyle: 12.1.2
- Error Prone Gradle plugin: 4.3.0
- error_prone_core: 2.45.0
- SpotBugs Gradle plugin: 6.4.8
- FindSecBugs: 1.13.0
- OWASP Dependency-Check Gradle plugin: 12.2.0

※ バージョンは導入時に必ず公式ソースで再確認してください。
