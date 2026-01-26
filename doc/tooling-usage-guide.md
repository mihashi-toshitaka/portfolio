# 開発ツールの使い方まとめ

このドキュメントは、`formatting-and-rewrite.md` と
`quality-and-security-tools.md` で設定した各機能の
**実際の使い方**を短く整理したものです。

## 1. フォーマット / リライト（OpenRewrite + EditorConfig + Spotless）

### 目的
1) OpenRewrite で自動リファクタリング
2) EditorConfig で空白・改行を統一
3) Spotless（google-java-format）で最終整形

### 保存時（VS Code）
- 保存すると `spotlessApply` が動き、Java / Gradle を整形します。
- VS Code の設定は `.vscode/settings.json` で管理します。

### コマンド（ローカル）
```bash
# 1) 自動リファクタリング
./gradlew rewriteRun

# 2) EditorConfig で整形
./gradlew editorconfigFormat

# 3) Spotless で最終整形
./gradlew spotlessApply
```

### CI / 品質ゲート
```bash
./gradlew editorconfigCheck
./gradlew spotlessCheck
# 任意: 変更がある場合に失敗
./gradlew rewriteDryRun
```

---

## 2. 品質 / セキュリティ解析（Checkstyle / Error Prone / SpotBugs / OWASP）

### 目的
- **IDE**: 入力中に問題を検出
- **ビルド**: 自動解析で品質 / セキュリティ問題を検出

### 2.1 IDE（VS Code）で使う

#### SonarQube for IDE
- リアルタイムに品質 / セキュリティ警告を出す
- JRE 17+ が必要
- Node.js 20.12+ が必要（WSL は nvm で入れる）

#### Error Lens
- 問題の表示を強調（行末のメッセージなど）

#### Checkstyle for Java
- 命名 / Javadoc のみをチェック（フォーマット系は無効化）

### 2.2 ビルド時に使う

```bash
# check タスクに品質ゲートをまとめて実行
./gradlew check
```

`check` に含まれる内容:
- `spotlessCheck`
- `editorconfigCheck`
- `checkstyleMain` / `checkstyleTest`
- `spotbugsMain` / `spotbugsTest`
- `dependencyCheckAnalyze`（明示的に有効化した場合のみ）

### 2.3 依存脆弱性スキャン（OWASP Dependency-Check）

デフォルトは **無効**。有効化したいときだけ実行します。

```bash
# 環境変数で有効化
export DEPENDENCY_CHECK_ANALYZE_ENABLED=true
./gradlew dependencyCheckAnalyze

# あるいは Gradle プロパティで有効化
./gradlew dependencyCheckAnalyze -PdependencyCheckAnalyzeEnabled=true
```

---

## 3. よく使うコマンド一覧（まとめ）

```bash
# OpenRewrite（自動リファクタリング）
./gradlew rewriteRun
./gradlew rewriteDryRun

# EditorConfig / Spotless
./gradlew editorconfigFormat
./gradlew spotlessApply

# 品質ゲート一括
./gradlew check

# 依存脆弱性スキャン（任意）
./gradlew dependencyCheckAnalyze -PdependencyCheckAnalyzeEnabled=true
```

---

## 4. 注意点
- Checkstyle は命名 / Javadoc のみ（フォーマット系は無効）
- Spotless は google-java-format 固定（設定不可）
- OpenRewrite → EditorConfig → Spotless の順で実行する
