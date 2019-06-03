(ns purple-bank.logic
  (:import [java.util UUID]))


(defn new-user [params]
  {:id (UUID/randomUUID)
   :name (:name params)
   :balance 0.0
   :transactions []})

(defn validate-user [{:keys [name] :as user}]
  (and (string? name) user))

(defn new-transaction [params]
  {:id (UUID/randomUUID)
   :operation (keyword (:operation params))
   :amount  (:amount params)}) ; convert amount->money

(def operations-set #{:credit :debit})

(defn validate-transaction [{:keys [operation amount] :as transaction}]
  (and (contains? operations-set operation)
       (double? amount)
       (< 0 amount)
       transaction))

(defn get-transaction-value
  [{operation :operation
    amount :amount}]
  (if (= operation :debit)
    (* amount -1)
    amount))

(defn consolidate-user-balance [transaction {:keys [balance] :as user}]
  (->> (+ balance (get-transaction-value transaction))
       (assoc user :balance)))

(defn validate-operation [user {:keys [operation] :as transaction}]
  "Check if user has enough balance to realize the operation"
  (if (= operation :debit)
    (-> (consolidate-user-balance transaction user)
        (get :balance)
        (>= 0))
    true))



;(defn consolidate-user-balance [user]
;  (->> (:transactions user)
;       (map #(get-transaction-value %))
;       (reduce +)
;       (assoc user :balance)))


