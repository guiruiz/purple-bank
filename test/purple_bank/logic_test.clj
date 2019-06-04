(ns purple-bank.logic-test
  (:require [midje.sweet :refer :all]
            [purple-bank.logic :as logic])
  (:import [java.util UUID]))


(def credit-transaction-1 {:id (UUID/randomUUID)
                         :operation :credit
                         :amount 100})

(def credit-transaction-2 {:id (UUID/randomUUID)
                           :operation :credit
                           :amount 200})

(def debit-transaction-1 {:id (UUID/randomUUID)
                           :operation :debit
                           :amount 50})

(def debit-transaction-2 {:id (UUID/randomUUID)
                          :operation :debit
                          :amount 175})

(def user {:id (UUID/randomUUID)
           :name "Joao"
           :balance 0.00M
           :transactions []})

(def user-mock
  (->(assoc user :transactions [credit-transaction-1])
     (assoc :balance 100M)))

(defn process-mock-transactions [user]
  (-> (logic/process-user-transaction user debit-transaction-1)
      (logic/process-user-transaction credit-transaction-2)
      (logic/process-user-transaction debit-transaction-2)))

(facts "User"

       (fact "builds successful"
             (logic/new-user {:name "Joao"}) => (just {:id uuid?
                                                       :name "Joao"
                                                       :balance 0.00M
                                                       :transactions []}))
       (fact "validates valid name"
             (-> (logic/new-user {:name "Joao"})
                 (logic/validate-user)) => truthy)

       (fact "validates empty name"
             (-> (logic/new-user {:name ""})
                 (logic/validate-user)) => false?)

       (fact "validates invalid name"
             (-> (logic/new-user {:name 1234})
                 (logic/validate-user)) => false?)

       (fact "validates invalid params"
             (-> (logic/new-user {:foo 0000})
                 (logic/validate-user)) => false?)

       (fact "validates empty params"
             (-> (logic/new-user {})
                 (logic/validate-user)) => false?))

(facts "Transaction"

       (fact "builds successful"
             (logic/new-transaction {:operation "credit" :amount 20}) => (just {:id uuid?
                                                                                :operation :credit
                                                                                :amount 20}))
       (fact "validates valid params"
             (-> (logic/new-transaction {:operation "credit" :amount 20})
                 (logic/validate-transaction)) => truthy)

       (fact "validates invalid operation"
             (-> (logic/new-transaction {:operation "bar" :amount 20})
                 (logic/validate-transaction)) => false?)

       (fact "validates invalid amount"
             (-> (logic/new-transaction {:operation "debit" :amount {}})
                 (logic/validate-transaction)) => false?)

       (fact "validates invalid zero amount"
             (-> (logic/new-transaction {:operation "credit" :amount 0})
                 (logic/validate-transaction)) => false?)

       (fact "validates invalid negative amount"
             (-> (logic/new-transaction {:operation "credit" :amount -10})
                 (logic/validate-transaction)) => false?))

(facts "Transaction Process"

       (fact "gets valid absolute value from credit transaction"
             (logic/get-transaction-value credit-transaction-1) => 100)

       (fact "gets valid absolute value from debit transaction"
             (logic/get-transaction-value debit-transaction-1) => -50)

       (fact "validates valid transaction operation"
             (logic/validate-operation user-mock debit-transaction-1) => true?)

       (fact "validates invalid transaction operation"
             (logic/validate-operation user-mock debit-transaction-2) => false?)

       (fact "process valid transaction operation"
             (logic/process-user-transaction user-mock debit-transaction-1) => (just (-> (assoc user :transactions [credit-transaction-1
                                                                                                                    debit-transaction-1])
                                                                                         (assoc :balance 50M))))

       (fact "validates user balance on credit transaction"
             (-> (logic/calculate-user-balance credit-transaction-2 user-mock)
                 (:balance)) => 300M)

       (fact "validates user balance on debit transaction"
             (-> (logic/calculate-user-balance debit-transaction-1 user-mock)
                 (:balance)) => 50M)

       (fact "validates user balance after few transactions processes"
             (-> (process-mock-transactions user-mock) (:balance)) => (-> (process-mock-transactions user-mock)
                                                                          (logic/consolidate-user-balance)
                                                                          (:balance))))