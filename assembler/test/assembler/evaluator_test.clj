(ns assembler.evaluator-test)

(def instructions
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
    :symbol "a"}])

(defn check [f asm expected]
  (let [result (f asm)]
    (is (= result expected))))
