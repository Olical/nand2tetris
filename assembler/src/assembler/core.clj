(ns assembler.core)

(defn -main
  "Takes stdin, pipes it through the assembler parts and pipes the result through stdout."
  []
  (println (slurp *in*)))
