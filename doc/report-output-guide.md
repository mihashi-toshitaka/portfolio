# タスクのレポート出力ガイド

この資料は、`build.gradle` で設定されている **レポートを出力するタスク** について、
「どのようなレポートが出力されるか」と「レポートの読み方」をまとめたものです。

## 対象タスク一覧

- `generateLicenseReport`
- `spotbugsMain` / `spotbugsTest`
- `dependencyCheckAnalyze`
- `checkstyleMain` / `checkstyleTest`
- `test`

> 補足: `check` は各レポート生成タスクの実行トリガーですが、
> 本資料では **レポートを直接出力するタスク** に絞って説明します。

---

## 1. `generateLicenseReport`（依存ライブラリのライセンスレポート）

### 出力されるレポート

- `src/main/resources/static/THIRD-PARTY.txt`
  - 依存ライブラリごとのライセンス一覧（テキスト）
- `src/main/resources/static/licenses.html`
  - 依存ライブラリのライセンス一覧（HTML）

### レポートの読み方

- **THIRD-PARTY.txt**
  - ライブラリ名ごとにライセンス名と本文が並ぶ構成です。
  - 配布用・監査用のテキストとして利用できます。
- **licenses.html**
  - ライブラリ一覧とライセンス本文がブラウザで閲覧しやすい形式です。
  - スクロールで各ライブラリのライセンス本文に移動できます。

---

## 2. `spotbugsMain` / `spotbugsTest`（バグ検出レポート）

### 出力されるレポート

- HTML 形式の SpotBugs レポート
  - 出力先: `build/reports/spotbugs/` 配下
  - 例: `build/reports/spotbugs/main.html`, `build/reports/spotbugs/test.html`

### レポートの読み方

- **Summary** セクションで検出件数・優先度（Priority）・カテゴリを確認します。
- **Bug List** で指摘一覧を確認し、個別の指摘をクリックして詳細を確認します。
- 詳細画面では、該当クラス/メソッド・指摘内容・修正のヒントが表示されます。

---

## 3. `dependencyCheckAnalyze`（依存関係の脆弱性レポート）

### 出力されるレポート

- HTML / JSON 形式の OWASP Dependency-Check レポート
  - 出力先: `build/reports/` 配下
  - 例: `build/reports/dependency-check-report.html`,
    `build/reports/dependency-check-report.json`

### レポートの読み方

- **HTML レポート**
  - 依存ライブラリごとに検出された脆弱性（CVE）と CVSS スコアが表示されます。
  - 重要度の高いもの（CVSS が高いもの）から対応します。
- **JSON レポート**
  - CI や他ツール連携向けの構造化データです。
  - 自動集計・通知・チケット化に利用できます。

---

## 4. `checkstyleMain` / `checkstyleTest`（コーディング規約レポート）

### 出力されるレポート

- Checkstyle レポート
  - 出力先: `build/reports/checkstyle/` 配下
  - 例: `build/reports/checkstyle/main.xml`, `build/reports/checkstyle/test.xml`

### レポートの読み方

- XML 形式で、ファイルごとの違反内容・行番号・ルール名が記載されます。
- CI 連携や機械処理に向いており、HTML に変換して閲覧することも可能です。

---

## 5. `test`（JUnit テストレポート）

### 出力されるレポート

- JUnit テストレポート
  - 出力先: `build/reports/tests/test/` 配下
  - 例: `build/reports/tests/test/index.html`

### レポートの読み方

- **Packages / Classes** 単位でテストの成功・失敗・実行時間を確認できます。
- 失敗したテストは詳細画面でスタックトレースを確認し、原因箇所を特定します。

---

## レポート確認時のポイント（共通）

- **HTML は人が読むためのレポート**、XML/JSON は **ツール連携向け** と理解すると整理しやすいです。
- 重要度や優先度が付くレポート（SpotBugs / Dependency-Check）は、
  **高いものから対応**すると効率的です。
