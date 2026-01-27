# build.gradle タスクの処理フローまとめ

この資料は `build.gradle` に明示的に定義されているタスクについて、実行時にどのような処理フローになるかを整理したものです。

## 対象タスク一覧

- `test`
- `spotlessApply`
- `editorconfigFormat`
- `build`
- `check`
- `generateLicenseReport`

> 補足: 上記以外にも `tasks.withType(com.github.spotbugs.snom.SpotBugsTask)` の共通設定や `dependencyCheck` ブロックの設定があり、`check` 実行時の依存タスクに影響します。

## 1. `test`

**目的**: テスト実行時に JUnit 5（JUnit Platform）を使用するよう指定。

**フロー**

1. `test` タスクの実行時に `useJUnitPlatform()` を有効化する。
2. これにより `src/test/java` 配下の JUnit 5 テストが検出・実行される。

## 2. `spotlessApply`

**目的**: コードフォーマットの適用。

**フロー**

1. `rewriteRun` が実行された場合、`spotlessApply` は **必ず後続**で実行される（`mustRunAfter 'rewriteRun'`）。
2. Java ソースに Google Java Format、その他ファイルにトリム/改行付与が適用される。

## 3. `editorconfigFormat`

**目的**: `.editorconfig` のルール適用。

**フロー**

1. `rewriteRun` が実行された場合、`editorconfigFormat` は **必ず後続**で実行される（`mustRunAfter 'rewriteRun'`）。
2. `**/*.java`, `**/*.gradle`, `**/*.md` に EditorConfig のルールが適用される（`build/**`, `.gradle/**` は除外）。

## 4. `build`

**目的**: ビルド時に最終的なフォーマット結果を保証する。

**フロー**

1. `build` 実行時、`spotlessApply` を **必ず先に実行**する（`dependsOn 'spotlessApply'`）。
2. これにより、ビルド成果物は常に Spotless のフォーマット適用済み状態になる。

## 5. `check`

**目的**: 品質ゲートをまとめて実行する。

**フロー**

1. `check` 実行時、以下の品質チェックを **必ず先に実行**する：
   - `spotlessCheck`
   - `editorconfigCheck`
   - `checkstyleMain`
   - `checkstyleTest`
   - `spotbugsMain`
   - `spotbugsTest`
2. `dependencyCheckAnalyze` は **任意実行**で、以下のいずれかが `true` の場合にのみ追加される：
   - `.env` の `DEPENDENCY_CHECK_ANALYZE_ENABLED`
   - 環境変数 `DEPENDENCY_CHECK_ANALYZE_ENABLED`
   - Gradle プロパティ `dependencyCheckAnalyzeEnabled`
3. 上記が `true` の場合のみ `dependencyCheckAnalyze` が `check` の依存タスクに追加される。

## 6. `generateLicenseReport`

**目的**: 依存ライブラリのライセンスレポート生成と整理。

**フロー**

1. `licenseReport` の設定で以下を出力する：
   - `THIRD-PARTY.txt`
   - `licenses.html`
   - 出力先は `src/main/resources/static`
2. `generateLicenseReport` 実行後（`doLast`）に、出力ディレクトリ配下を走査。
3. `THIRD-PARTY.txt` / `licenses.html` 以外で **`.jar` 拡張子のディレクトリ**が存在する場合は削除する。

## 関連する共通設定（参考）

- **SpotBugs タスク共通設定**: `SpotBugsTask` は HTML レポート出力を必須にし、XML 出力は無効。失敗時はビルドを失敗させる（`ignoreFailures = false`）。
- **dependencyCheck 設定**: 失敗しきい値は `CVSS=11`（事実上警告のみ）、`HTML`/`JSON` レポートを出力し、NVD API キーがある場合のみ有効化。

