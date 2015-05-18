(ns assembler.parser
  (:require [clojure.string :as str]))

(def token-expressions {:a-val   #"@(\d+)"
                        :a       #"@(.*)"
                        :c       #"(\w+)=(.*);(\w+)"
                        :c-assoc #"(\w+)=(.*)"
                        :c-jump  #"(.*);(\w+)"
                        :label   #"\((.*)\)"})

(def whitespace-expression #"(\s|//.*)+")

(def factories {:a-val   (fn [[_ symbol]]
                           {:type :address
                            :value (read-string symbol)})
                :a       (fn [[_ symbol]]
                           {:type :address
                            :symbol symbol})
                :c       (fn [[_ dest comp jump]]
                           {:type :command
                            :dest (keyword dest)
                            :comp (keyword comp)
                            :jump (keyword jump)})
                :c-assoc (fn [[_ dest comp]]
                           {:type :command
                            :dest (keyword dest)
                            :comp (keyword comp)})
                :c-jump  (fn [[_ comp jump]]
                           {:type :command
                            :comp (keyword comp)
                            :jump (keyword jump)})
                :label   (fn [[_ symbol]]
                           {:type :label
                            :symbol symbol})})

(defn tokenise
  "Takes a full ASM string and splits it into a vector of token stings."
  [source]
  (vec (filter (complement empty?) (str/split source whitespace-expression))))

(defn parse-token
  "Takes a single ASM token, returns an instruction hash-map."
  [token]
  (condp #(re-matches (token-expressions %1) %2) token
    :a-val   :>> (factories :a-val)
    :a       :>> (factories :a)
    :c       :>> (factories :c)
    :c-assoc :>> (factories :c-assoc)
    :c-jump  :>> (factories :c-jump)
    :label   :>> (factories :label)
    {:type :unknown
     :token token}))

(defn parse-source
  "Takes a ASM source string and parses it into an instruction hash-map."
  [source]
  (map parse-token (tokenise source)))
