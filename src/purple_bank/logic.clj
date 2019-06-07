(ns purple-bank.logic
  (:import [java.util UUID]))

(def operations-set #{:credit :debit})

(defn new-user [name]
  "Builds a new user from params and return it."
  {:id (UUID/randomUUID)
   :name name
   :balance 0.00M
   :transactions []})

(defn validate-user [{:keys [name] :as user}]
  "Validates user and returns it if it's valid or false if its not."
  (and (string? name) (not (clojure.string/blank? name)) user))

(defn new-transaction [operation amount]
  "Builds a new transaction."
  {:id (UUID/randomUUID)
   :operation (keyword operation)
   :amount  amount})

(defn validate-transaction [{:keys [operation amount] :as transaction}]
  "Validates transaction values and returns a boolean."
  (and (contains? operations-set operation)
       (number? amount)
       (< 0 amount)
       transaction))

(defn get-transaction-value
  "Returns transaction absolute value according to its operation."
  [{operation :operation amount :amount}]
  (if (= operation :debit)
    (* amount -1)
    amount))

(defn calculate-user-balance [transaction {:keys [balance] :as user}]
  "Calculates user balance with the new transaction."
  (->> (bigdec (get-transaction-value transaction))
       (+ balance)
       (assoc user :balance)))


(defn validate-user-balance [user {:keys [operation] :as transaction}]
  "Validates if user has enough balance to realize the new transaction."
  (if (= operation :debit)
    (-> (calculate-user-balance transaction user)
        (get :balance)
        (>= 0))
    true))

(defn process-user-transaction [user transaction]
  "Adds transaction to user transactions, updates user balance and returns the user."
  (->> (conj (:transactions user) transaction)
       (assoc-in user [:transactions])
       (calculate-user-balance transaction)))

(defn consolidate-user-balance [user]
  "Consolidates user balance from its transactions."
  (->> (:transactions user)
       (map #(bigdec (get-transaction-value %)))
       (reduce +)
       (assoc user :balance)))





