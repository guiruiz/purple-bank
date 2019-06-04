(ns purple-bank.user-flow
  (:require [midje.sweet :refer :all]
            [purple-bank.components :as components]
            [purple-bank.protocols.storage-client :as storage-client]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.test-helper :as test-helper]
            [selvage.flow :refer [*world* flow]]))

(defn init!
  [world]
  (let [component-system (components/ensure-system-up! :test)]
    (storage-client/clear-all! (:storage component-system))
    (assoc world :system component-system)))

(defn create-user
  [world body]
  (let [user-response (test-helper/response (system-utils/get-component :servlet)
                                   :post "/users"
                                   :headers {"Content-Type" "application/json"}
                                   :body body)]
    (assoc
      (if (= 201 (:status user-response))
        (assoc world :user-id (get-in user-response [:body :id]))
        world)
      :create-user-response
      user-response)))

(defn get-user
  [world user-id]
  (let [user-response (test-helper/response (system-utils/get-component :servlet)
                                            :get (str "/users/" user-id)
                                            :headers {"Accept" "application/json"})]
    (assoc
      world
      :get-user-response
      user-response)))

(defn create-transaction
  [world body user-id]
  (let [transaction-response (test-helper/response (system-utils/get-component :servlet)
                                                   :post (str "/users/" user-id "/transactions")
                                                   :headers {"Content-Type" "application/json"}
                                                   :body body)]
    (assoc
      (if (= 201 (:status transaction-response))
             (assoc world :transaction-id (get-in transaction-response [:body :id]))
             world)
      :create-transaction-response
      transaction-response)))

(flow
  ;; Init world!
  init!

  ;; Tries to create an invalid user
  (fn [world] (create-user world {:name 1234}))
  ;; Expects response code 400
  (fact (get-in *world* [:create-user-response :status]) => 400)

  ;; Tries to create a valid user
  (fn [world] (create-user world {:name "Joao"}))
  ;; Expects response code 201
  (fact (get-in *world* [:create-user-response :status]) => 201)
  ;; Validates response body
  (fact (get-in *world* [:create-user-response :body]) => (contains {:name "Joao"
                                                                     :balance 0.0
                                                                     :transactions []}))

  ;; Tries to get an nonexistent user
  (fn [world] (get-user world "2jzb16k20"))
  ;; Expects response code 404
  (fact (get-in *world* [:get-user-response :status]) => 404)
  ;; Validates response body
  (fact (get-in *world* [:get-user-response :body]) => "")

  ;; Tries to get an existent user
  (fn [world] (get-user world (:user-id world)))
  ;; Expects response code 200
  (fact (get-in *world* [:get-user-response :status]) => 200)
  ;; Validates response body
  (fact (get-in *world* [:get-user-response :body]) => (contains {:name "Joao"
                                                                  :balance 0.0
                                                                  :transactions []}))

  ;; Tries to create a transaction with invalid params
  (fn [world] (create-transaction world {:operation "foo" :amount nil} (:user-id world)))
  ;; Expects response code 400
  (fact (get-in *world* [:create-transaction-response :status]) => 400)
  ;; Validates response body
  (fact (get-in *world* [:create-transaction-response :body]) => "")

  ;; Tries to create a valid transaction to an nonexistent user
  (fn [world] (create-transaction world {:operation "credit" :amount 20} "anyuser"))
  ;; Expects response code 404
  (fact (get-in *world* [:create-transaction-response :status]) => 404)
  ;; Validates response body
  (fact (get-in *world* [:create-transaction-response :body]) => "")

  ;; Tries to create a valid credit transaction to an existent user
  (fn [world] (create-transaction world {:operation "credit" :amount 20.45} (:user-id world)))
  ;; Expects response code 201
  (fact (get-in *world* [:create-transaction-response :status]) => 201)
  ;; Validates response body
  (fact (get-in *world* [:create-transaction-response :body]) => (contains {:operation "credit" :amount 20.45}))

  ;; Tries to create a valid debit transaction to an existent user
  (fn [world] (create-transaction world {:operation "debit" :amount 5.50} (:user-id world)))
  ;; Expects response code 201
  (fact (get-in *world* [:create-transaction-response :status]) => 201)
  ;; Validates response body
  (fact (get-in *world* [:create-transaction-response :body]) => (contains {:operation "debit" :amount 5.50}))

  ;; Tries to get the existent user and validates its balance
  (fn [world] (get-user world (:user-id world)))
  ;; Expects response code 200
  (fact (get-in *world* [:get-user-response :status]) => 200)
  ;; Validates response body
  (fact (get-in *world* [:get-user-response :body]) => (contains {:balance 14.95}))

  ;; Tries to create a valid debit transaction that exceeds user balance
  (fn [world] (create-transaction world {:operation "debit" :amount 200} (:user-id world)))
  ;; Expects response code 403
  (fact (get-in *world* [:create-transaction-response :status]) => 403)
  ;; Validates response body
  (fact (get-in *world* [:create-transaction-response :body]) => ""))
