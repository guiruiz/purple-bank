(ns purple-bank.logic
  (:import [java.util UUID]))

(def operations-set #{:credit :debit})

(defn new-user [params]
  "Builds a new user from params and return it."
  {:id (UUID/randomUUID)
   :name (:name params)
   :balance 0.00M
   :transactions []})

(defn validate-user [{:keys [name] :as user}]
  "Validates user and returns it if it's valid or false if its not."
  (and (string? name) (not (clojure.string/blank? name)) user))

(defn new-transaction [params]
  "Builds a new transaction from params and return it."
  {:id (UUID/randomUUID)
   :operation (keyword (:operation params))
   :amount  (:amount params)})

(defn validate-transaction [{:keys [operation amount] :as transaction}]
  "Validates transaction and returns it if it's valid or false if its not."
  (and (contains? operations-set operation)
       (number? amount)
       (< 0 amount)
       transaction))

(defn get-transaction-value
  "Returns transaction absolute value according to its operation type."
  [{operation :operation
    amount :amount}]
  (if (= operation :debit)
    (* amount -1)
    amount))

(defn calculate-user-balance [transaction {:keys [balance] :as user}]
  "Calculates user balance with transaction."
  (->> (bigdec (get-transaction-value transaction))
       (+ balance)
       (assoc user :balance)))


(defn validate-operation [user {:keys [operation] :as transaction}]
  "Validates operation checking if user has enough balance to realize the transaction."
  (if (= operation :debit)
    (-> (calculate-user-balance transaction user)
        (get :balance)
        (>= 0))
    true))

;(defn consolidate-user-balance [user]
;  "Consolidates user balance from its transactions.
;  (->> (:transactions user)
;       (map #(get-transaction-value %))
;       (reduce +)
;       (assoc user :balance)))





