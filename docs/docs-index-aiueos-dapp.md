# app-docs（文書 index）— itonami（dapp）上の分散型設計

状態: 第 1 スライス実装（2026-10-20）。正本はこの文書。
追跡: Linear JUN-128（親 JUN-116）。

## 位置づけ

app-docs は **Google Docs v1（structural）と MS Word（plaintext）の両方に互換な
文書 API**（docs.etzhayyim.com、canonical は kotoba datomic `docs-v1`）。
workspace-compat の第 4（最終）垂直。本スライスは **文書ボディの
グローバル character index の判定**（`docbody.cljk` — Google Docs の
index 対応）を判定コア化。

## このスライス: 文書 index 判定コア

| 層 | 実体 | 状態 |
|---|---|---|
| 判定コア guest | `aiueos/docs_index.cljk` — span-end（endIndex = start+len）/ next-start（**改行 1 ぶんのセパレータ**: 次の start = start+len+1）/ clamp-offset（local offset は 0 未満にならない = 末尾クランプ）/ in-span / merge-kind-wins（**要素をまたぐ delete の merge は start 側の kind が勝つ**） | **実装済み・実測** |
| 記述子 | `aiueos/docs-index.edn` | **実装済み** |
| parity | `test-shims/lg_docs/index_parity_test.cljc` — **実物の docbody を bb で実行**（with-indices / index-to-pos / apply-request）して一致 | **実装済み・実測** |

実測: `amu test aiueos/docs_index.cljk` → **18/18**（jvm-kir / js / wasm）。
parity → 2 tests / 6 assertions / 0 failures（bb 実行）。

## 正本の正確な規則（guest はこれに追従）

- 各要素は text 長ぶんの index 位置 + **1 つの改行セパレータ**を貢献する。
  `with-indices` は `:startIndex` / `:endIndex` を計算し、index-to-pos は
  グローバル index を [要素 offset] に写して**文書末にクランプ**する
  （空ボディは [0 0]）。
- 要素をまたぐ `deleteRange` は部分要素を merge し **start 側の kind が勝つ**。
- `apply-request` は更新後のボディを**返す**（破壊しない）。リクエストの
  op キーは **`:op`**（"deleteRange" 等）—— parity 実行で実測。

## 未実装境界

- batchUpdate の op 全体（appendParagraph / insertHeading / replaceText /
  insertText の判定は span/offset 計算に帰着するが未個別判定）。
- Google skin の structural reshape / Word の plaintext 化（flatten-text の
  向き）は host（CF Worker）。
- ids / mapping / handlers の判定。
- mon（DNA）化、aiueos session 載せ（fleet 共通）。
