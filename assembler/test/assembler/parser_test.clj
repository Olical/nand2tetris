(ns assembler.parser-test
  (:require [clojure.test :refer :all]
            [assembler.parser :refer :all]))

(defn check [f asm expected]
  (let [result (f asm)]
    (is (= result expected))))

(def check-token (partial check parse-token))
(def check-tokenise (partial check tokenise))
(def check-parse (partial check parse-source))

(deftest parse-token-test
  (testing "A instruction"
    (check-token "@10"
                 {:type :address
                  :value 10 }))
  (testing "C instruction"
    (check-token "M=1"
                 {:type :command
                  :dest :M
                  :comp :1})
    (check-token "M;JMP"
                 {:type :command
                  :comp :M
                  :jump :JMP})
    (check-token "A=A+1;JEQ"
                 {:type :command
                  :dest :A
                  :comp :A+1
                  :jump :JEQ}))
  (testing "Labels"
    (check-token "(LOOP)"
                 {:type :label
                  :symbol "LOOP"}))
  (testing "Unknown"
    (check-token "LOLOLOL"
                 {:type :unknown
                  :token "LOLOLOL"})))

(def complex-source
  "(LOOP)
  // Hello, World! M;JMP
  M;JMP
  @i
  D=1 // Ignore me plz
  @a")

(deftest tokenise-test
  (testing "Empty source."
    (check-tokenise "" []))
  (testing "Complex source."
    (check-tokenise complex-source
                    ["(LOOP)"
                     "M;JMP"
                     "@i"
                     "D=1"
                     "@a"])))

(deftest parse-test
  (testing "Empty source."
    (check-parse "" []))
  (testing "Complex source."
    (check-parse complex-source
                    [{:type :label
                      :symbol "LOOP"}
                     {:type :command
                      :comp :M
                      :jump :JMP}
                     {:type :address
                      :symbol "i"}
                     {:type :command
                      :dest :D
                      :comp :1}
                     {:type :address
                      :symbol "a"}])))
