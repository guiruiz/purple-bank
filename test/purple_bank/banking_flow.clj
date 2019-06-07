(ns purple-bank.banking-flow
  (:require [midje.sweet :refer :all]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.protocols.storage-client :as storage-client]
            [purple-bank.http-helpers :as http-helpers]
            [selvage.flow :refer [*world* flow]]))

(defn init!
  [world]
  (let [component-system (system-utils/get-or-create-system! :test)]
    (storage-client/clear-all! (:storage component-system))
    (assoc world :system component-system)))

(defn create-user
  [world body]
  (let [user-response (http-helpers/POST "/users" body)]
    (assoc
      (if (= 201 (:status user-response))
        (assoc world :user-id (get-in user-response [:body :id]))
        world)
      :create-user-response
      user-response)))

(defn get-user
  [world user-id]
  (let [user-response (http-helpers/GET (str "/users/" user-id))]
    (assoc
      world
      :get-user-response
      user-response)))

(defn create-transaction
  [world body user-id]
  (let [transaction-response (http-helpers/POST (str "/users/" user-id "/transactions") body)]
    (assoc
      (if (= 201 (:status transaction-response))
             (assoc world :transaction-id (get-in transaction-response [:body :id]))
             world)
      :create-transaction-response
      transaction-response)))



(flow "Creates user and process few transactions"

      init!

      ;; Create a valid user
      (fn [world] (create-user world {:name "Joao"}))
      (fact "Expects response code 201"
        (get-in *world* [:create-user-response :status]) => 201)
      (fact "Expects valid response body"
        (get-in *world* [:create-user-response :body]) => (contains {:name "Joao"
                                                                     :balance 0.0
                                                                     :transactions []}))

      ;; Get created user
      (fn [world] (get-user world (:user-id world)))
      (fact "Expects response code 200"
            (get-in *world* [:get-user-response :status]) => 200)
      (fact "Validates response body"
            (get-in *world* [:get-user-response :body]) => (contains {:name "Joao"
                                                                      :balance 0.0
                                                                      :transactions []}))

      ;; Tries to create a transaction with invalid params
      (fn [world] (create-transaction world {:operation "foo" :amount nil} (:user-id world)))
      (fact "Expects response code 400"
            (get-in *world* [:create-transaction-response :status]) => 400)
      (fact "Validates response body"
            (get-in *world* [:create-transaction-response :body]) => nil)

      ;; Create a valid credit transaction to existent user
      (fn [world] (create-transaction world {:operation "credit" :amount 20.45} (:user-id world)))
      (fact "Expects response code 201"
            (get-in *world* [:create-transaction-response :status]) => 201)
      (fact "Validates response body"
            (get-in *world* [:create-transaction-response :body]) => (contains {:operation "credit" :amount 20.45}))

      ;; Create a valid debit transaction to existent user
      (fn [world] (create-transaction world {:operation "debit" :amount 5.50} (:user-id world)))
      (fact "Expects response code 201"
            (get-in *world* [:create-transaction-response :status]) => 201)
      (fact "Validates response body"
            (get-in *world* [:create-transaction-response :body]) => (contains {:operation "debit" :amount 5.50}))

      ;; Gets existent user and validates its balance
      (fn [world] (get-user world (:user-id world)))
      (fact "Expects response code 200"
            (get-in *world* [:get-user-response :status]) => 200)
      (fact "Validates response body"
            (get-in *world* [:get-user-response :body]) => (contains {:balance 14.95}))

      ;; Tries to create a valid debit transaction that exceeds user balance
      (fn [world] (create-transaction world {:operation "debit" :amount 200} (:user-id world)))
      (fact "Expects response code 403"
            (get-in *world* [:create-transaction-response :status]) => 403)
      (fact "Validates response body"
            (get-in *world* [:create-transaction-response :body]) => nil))

(flow "Creates an invalid user"
  init!

  ;; Tries to create an invalid user
  (fn [world] (create-user world {:name 1234}))
  (fact "Expects response code 400"
        (get-in *world* [:create-user-response :status]) => 400))

(flow "Get nonexistent user"
      init!

      ;; Tries to get an nonexistent user
      (fn [world] (get-user world "2jzb16k20"))
      (fact "Expects response code 404"
            (get-in *world* [:get-user-response :status]) => 404)
      (fact "Validates response body"
            (get-in *world* [:get-user-response :body]) => nil))

(flow "Create transaction to nonexistent user"
      init!

      ;; Tries to create a valid transaction to an nonexistent user
      (fn [world] (create-transaction world {:operation "credit" :amount 20} "anyuser"))
      (fact "Expects response code 404"
            (get-in *world* [:create-transaction-response :status]) => 404)
      (fact "Validates response body"
            (get-in *world* [:create-transaction-response :body]) => nil))