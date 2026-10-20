(ns kotoba.lang.text
  "bb 用の最小 shim（parity 実行のみ）。clojure.string 委譲。"
  (:refer-clojure :exclude [replace])
  (:require [clojure.string :as cs]))

(defn trim [s] (cs/trim s))
(defn blank? [s] (cs/blank? s))
(defn includes? [s sub] (cs/includes? s sub))
(defn starts-with? [s pre] (cs/starts-with? s pre))
(defn ends-with? [s suf] (cs/ends-with? s suf))
(defn index-of [s sub] (cs/index-of s sub))
(defn upper [s] (cs/upper-case s))
(defn lower [s] (cs/lower-case s))
(defn replace [s re-or-sub sub] (cs/replace s re-or-sub sub))
(defn split [s re] (cs/split s re))
(defn join [sep coll] (cs/join sep coll))
