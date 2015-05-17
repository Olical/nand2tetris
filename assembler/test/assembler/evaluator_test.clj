(ns assembler.evaluator-test
  (:require [clojure.test :refer :all]
            [assembler.evaluator :refer :all]))

(def instructions
  [{:type :label
    :symbol "LOOP"}
   {:type :command
    :comp :M
    :jump :JMP}
   {:type :address
    :symbol "i"}
   {:type :label
    :symbol "RECUR"}
   {:type :command
    :dest :D
    :comp :1}
   {:type :address
    :symbol "a"}])

(defn check [f instructions expected]
  (let [result (f instructions)]
    (is (= result expected))))

(defn check-assign [symbols instructions expected]
  (check #(assign-symbols symbols %) instructions expected))

(def check-symbols (partial check get-symbols))

(deftest is-symbol-test
  (testing "Empty symbols, so checks default."
    (is (is-symbol? {} :R13)))
  (testing "Checking for a custom symbol."
    (is (is-symbol? {:foo 1} :foo)))
  (testing "A symbol that doesn't exist."
    (is (not (is-symbol? {} :foo)))))

(deftest get-symbols-test
  (testing "Empty instructions return nothing."
    (check-symbols [] {}))
  (testing "Instructions with unresolved symbols resolve to the default symbol table."
    (check-symbols instructions
                   {:LOOP 16
                    :i 17
                    :RECUR 18
                    :a 19})))

(deftest assign-symbols-test
  (testing "Passing no symbols returns the same instructions."
    (check-assign {} instructions instructions))
  (testing "Passing symbols returns the mapped instructions."
    (check-assign
      {:RECUR 42}
      instructions
      (assoc-in instructions [3 :value] 42))))

;; This should just test one of each type.
;; No point exhaustive testing, it's just lookup tables.
;; As long as the tables are hooked up correctly, we're good.
(deftest get-binary-test)
