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
    :symbol "a"}
   {:type :address
    :symbol "FOO"}
   {:type :label
    :symbol "FOO"}])

(def clean-instructions
  "Basically without labels."
  (remove is-label? instructions))

(defn check [f instructions expected]
  (let [result (f instructions)]
    (is (= result expected))))

(defn check-assign [symbols instructions expected]
  (check #(assign-symbols symbols %) instructions expected))

(def check-symbols (partial check (partial get-symbols {})))
(def check-binary (partial check get-binary))
(def check-eval (partial check evaluate))
(def check-extract (partial check extract-labels))

(deftest extract-labels-test
  (testing "Empty instructions."
    (check-extract [] {}))
  (testing "Full instructions."
    (check-extract
      instructions
      {:LOOP 0
       :RECUR 2
       :FOO 5})))

(deftest is-label-test
  (testing "Label."
    (is (is-label? {:type :label})))
  (testing "Not label."
    (is (not (is-label? {:type :address})))))

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
    (check-symbols clean-instructions
                   {:i 16
                    :a 17
                    :FOO 18})))

(deftest assign-symbols-test
  (testing "Passing no symbols returns the same instructions."
    (check-assign {} instructions instructions))
  (testing "Passing symbols returns the mapped instructions."
    (check-assign
      {:RECUR 42}
      instructions
      (assoc-in instructions [3 :value] 42))))

(deftest get-binary-test
  (testing "A instructions."
    (check-binary
      {:type :address
       :value 0}
      "0000000000000000")
    (check-binary
      {:type :address
       :value 1000}
      "0000001111101000"))
  (testing "C instructions."
    (check-binary
      {:type :command
       :dest :M
       :comp :1}
      "1110111111001000")
    (check-binary
      {:type :command
       :comp :M
       :jump :JMP}
      "1111110000000111")
    (check-binary
      {:type :command
       :dest :A
       :comp :A+1
       :jump :JEQ}
      "1110110111100010"))
  (testing "Labels."
    (check-binary
      {:type :label
       :symbol "LOOP"}
      nil))
  (testing "Unknown."
    (check-binary
      {:type :unknown
       :token "LOLOL"}
      nil)))

(deftest evaluate-test
  (testing "Empty instructions."
    (check-eval [] []))
  (testing "Complex instructions."
    (check-eval
      instructions
      ["1111110000000111"
       "0000000000010000"
       "1110111111010000"
       "0000000000010001"
       "0000000000000101"])))
