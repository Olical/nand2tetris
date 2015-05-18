(ns assembler.core
  (:require [assembler.evaluator :refer [evaluate]]
            [assembler.parser :refer [parse-source]]
            [clojure.string :as str]))

(defn -main
  "Takes stdin, pipes it through the assembler parts and pipes the result through stdout."
  []
  (->> (slurp *in*)
       parse-source
       evaluate
       (remove nil?)
       (str/join "\n")
       println))
