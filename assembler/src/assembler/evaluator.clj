(ns assembler.evaluator)

;; Will take a parsed instruction vector.
;; Passes the vector to a function that calculates the full symbol table.
;; Passes the vector to a function that swaps symbols for addresses.
;; Passes the vector to a function that maps the instructions to their binary equivalent.
;; The final result is returned as a vector of binary strings.

(def default-symbol-table
  {:SP     0
   :LCL    1
   :ARG    2
   :THIS   3
   :THAT   4
   :R0     0
   :R1     1
   :R2     2
   :R3     3
   :R4     4
   :R5     5
   :R6     6
   :R7     7
   :R8     8
   :R9     9
   :R10    10
   :R11    11
   :R12    12
   :R13    13
   :R14    14
   :R15    15
   :SCREEN 16384
   :KBD    24576})

(defn is-symbol?
  "Checks if the given symbol exists in the given symbol table or the default table."
  [symbols symbol]
  (or (contains? symbols symbol)
      (contains? default-symbol-table symbol)))

(defn get-symbols
  "Constructs a symbol table for the given instructions."
  [instructions]
  (loop [ins (filter #(contains? % :symbol) instructions)
         symbols {}]
    (if (empty? ins)
      symbols
      (let [symbol (keyword ((first ins) :symbol))
            next-id (+ 16 (count symbols))]
        (recur (rest ins)
               (if (is-symbol? symbols symbol)
                 symbols
                 (assoc symbols symbol next-id)))))))

(defn assign-symbols
  "Assigns the given symbol table to the instructions, so symbols will be
  replaced with their numerical value."
  [symbols instructions]
  (map
    (fn [inst]
      (let [symbol (keyword (inst :symbol))
            value (symbols symbol)]
        (if (and symbol value)
          (assoc inst :value value)
          inst)))
    instructions))

(defn get-binary
  "Looks up the binary counterpart of the given instruction."
  [instruction]
  "")

(defn evaluate
  "Evaluates the given instructions by mapping their symbols and converting
  them all to binary. A vector of the binary representations is returned."
  [instructions]
  (let [symbols (merge default-symbol-table (get-symbols instructions))]
    (map get-binary (assign-symbols symbols instructions))))
