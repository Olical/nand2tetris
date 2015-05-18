(ns assembler.evaluator)

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

(def bin-comp
  {:0   "0101010"
   :1   "0111111"
   :-1  "0111010"
   :D   "0001100"
   :A   "0110000"
   :!D  "0001101"
   :!A  "0110001"
   :-D  "0001111"
   :-A  "0110011"
   :D+1 "0011111"
   :A+1 "0110111"
   :D-1 "0001110"
   :A-1 "0110010"
   :D+A "0000010"
   :D-A "0010011"
   :A-D "0000111"
   :D&A "0000000"
   :D|A "0010101"
   :M   "1110000"
   :!M  "1110001"
   :-M  "1110011"
   :M+1 "1110111"
   :M-1 "1110010"
   :D+M "1000010"
   :D-M "1010011"
   :M-D "1000111"
   :D&M "1000000"
   :D|M "1010101"})

(def bin-dest
  {nil  "000"
   :M   "001"
   :D   "010"
   :MD  "011"
   :A   "100"
   :AM  "101"
   :AD  "110"
   :AMD "111"})

(def bin-jump
  {nil  "000"
   :JGT "001"
   :JEQ "010"
   :JGE "011"
   :JLT "100"
   :JNE "101"
   :JLE "110"
   :JMP "111"})

(defn is-symbol?
  "Checks if the given symbol exists in the given symbol table or the default table."
  [symbols symbol]
  (or (contains? symbols symbol)
      (contains? default-symbol-table symbol)))

(defn is-label?
  "Checks if an instruction is a label."
  [inst]
  (= (inst :type) :label))

(defn extract-labels
  "Pull labels out of an instruction vector and create a symbol table for them."
  [instructions]
  (loop [ins instructions
         labels {}]
    (if (empty? ins)
      labels
      (let [head (first ins)
            label (keyword (head :symbol))
            index (- (.indexOf instructions head) (count labels))]
        (recur (rest ins)
               (if (is-label? head)
                 (assoc labels label index)
                 labels))))))

(defn get-symbols
  "Constructs a symbol table for the given label table and instructions."
  [labels instructions]
  (loop [ins (filter #(contains? % :symbol) instructions)
         symbols {}]
    (if (empty? ins)
      (merge labels symbols)
      (let [symbol (keyword ((first ins) :symbol))
            next-id (+ 16 (count symbols))]
        (recur (rest ins)
               (if (is-symbol? (merge labels symbols) symbol)
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

(defn get-address-binary
  "Constructs the binary string for an address instruction."
  [{:keys [value]}]
  (let [binary (Integer/toString value 2)
        missing (- 16 (count binary))
        padding (apply str (repeat missing "0"))]
    (str padding binary)))

(defn get-command-binary
  "Constructs the binary string for a command instruction."
  [{:keys [comp dest jump]}]
  (let [bcomp (bin-comp comp)
        bdest (bin-dest dest)
        bjump (bin-jump jump)]
    (str "111" bcomp bdest bjump)))

(defn get-binary
  "Looks up the binary counterpart of the given instruction."
  [instruction]
  (condp = (instruction :type)
    :address (get-address-binary instruction)
    :command (get-command-binary instruction)
    nil))

(defn evaluate
  "Evaluates the given instructions by mapping their symbols and converting
  them all to binary. A vector of the binary representations is returned."
  [instructions]
  (let [labels (extract-labels instructions)
        clean-instructions (remove is-label? instructions)
        symbols (merge default-symbol-table (get-symbols labels clean-instructions))]
    (map get-binary (assign-symbols symbols clean-instructions))))
