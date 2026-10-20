(ns lg-docs.index-parity-test
  "aiueos/docs_index.cljk（判定コア）と lg-docs.docbody の parity。実関数
  with-indices / index-to-pos / apply-request が guest の判定と一致する。"
  (:require [clojure.test :refer [deftest is testing]]
            [lg-docs.docbody :as d]))

(def body
  [{:elementId "e1" :kind "paragraph" :text "hello"}
   {:elementId "e2" :kind "heading" :headingLevel 1 :text "world"}
   {:elementId "e3" :kind "paragraph" :text "!"}])

(deftest index-math-matches-guest
  (testing "guest test-span / test-next: endIndex = start+len、次は +1（改行）"
    (is (= [{:elementId "e1" :kind "paragraph" :text "hello" :startIndex 0 :endIndex 5}
            {:elementId "e2" :kind "heading" :text "world" :headingLevel 1 :startIndex 6 :endIndex 11}
            {:elementId "e3" :kind "paragraph" :text "!" :startIndex 12 :endIndex 13}]
           (d/with-indices body))))
  (testing "guest test-clamp / test-in-span: index-to-pos は範囲写像 + 末尾クランプ"
    (is (= [0 3] (d/index-to-pos body 3)))
    (is (= [2 1] (d/index-to-pos body 13)))
    (is (= [2 1] (d/index-to-pos body 99)))))

(deftest merge-policy-matches-guest
  (testing "guest test-merge: 要素をまたぐ delete の merge は start 側の kind が勝つ"
    (let [r (d/apply-request body {:op "deleteRange" :startIndex 3 :endIndex 8})]
      (is (= 2 (count r)))
      (is (= "paragraph" (:kind (first r)))))))
