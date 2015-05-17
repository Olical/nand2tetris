(ns assembler.evaluator)

;; Will take a parsed instruction vector.
;; Passes the vector to a function that calculates the full symbol table.
;; Passes the vector to a function that swaps symbols for addresses.
;; Passes the vector to a function that maps the instructions to their binary equivalent.
;; The final binary is returned as a vector of binary strings.
