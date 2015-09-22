(ns budget.core-test
  (:require [clojure.test :refer :all]
            [flipmunks.budget.core :refer :all]))


(deftest a-test
  (testing "deep merge"
    (is (= (deep-merge [{:a 1} {:b 2}]) {:a 1 :b 2}))
    (is (= (deep-merge [{:a 1} {:a 2}]) {:a 2}))
    (is (= (deep-merge [{:a 1}]) {:a 1}))
    (is (= (deep-merge [{:a 1 :b {:c 1}} {:b {:d 4}}]) {:a 1 :b {:c 1 :d 4}}))
    (is (= (deep-merge [{:a {:b {:c 2}}} {:a {:b {:d 3}}}]) {:a {:b {:c 2 :d 3}}}))
    ))

(deftest keys-test
  (testing "key intersection"
    (is (= (key-intersection [{:a 1} {:b 2}]) #{}))
    (is (= (key-intersection [{:a 1 :b 2} {:b 3}]) #{}))
    (is (= (key-intersection [{:a 1 :b {:c 1}} {:b {:d 4}}]) #{:b}))))