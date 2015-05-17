(ns assembler.evaluator)

;; Will take a parsed instruction vector.
;; Passes the vector to a function that calculates the full symbol table.
;; Passes the vector to a function that swaps symbols for addresses.
;; Passes the vector to a function that maps the instructions to their binary equivalent.
;; The final result is returned as a vector of binary strings.

(defn eval
  "Evaluates the given instructions by mapping their symbols and converting
  them all to binary. A vector of the binary representations is returned."
  [instructions]
  (let [symbols (get-symbols instructions)]
    (map get-binary (assign-symbols symbols instructions))))

(defn get-symbols
  "Constructs a symbol table for the given instructions."
  [instructions]
  [])

(defn assign-symbols
  "Assigns the given symbol table to the instructions, so symbols will be
  replaced with they're numerical value."
  [symbols instructions]
  [])

(defn get-binary
  "Looks up the binary counterpart of the given instruction."
  [instruction]
  "")
