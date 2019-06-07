(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.db.purple-bank :as db.purple-bank]
            [purple-bank.protocols.logger-client :as logger-client]
            [purple-bank.adapters :refer :all]))

(defn create-user
  "Builds and validates user. If it's valid, puts the user on storage and returns it."
  [user-name storage logger]
  (logger-client/log logger "creating-user" {:user-name user-name})
  (let [user (logic/new-user user-name)]
    (if (logic/validate-user user)
      (do
        (db.purple-bank/save-user! user storage)
        {:data user :error nil})
      {:data nil :error :invalid-user})))

(defn get-user
  "Retrieves user from storage identified by user-id."
  [user-id storage logger]
  (logger-client/log logger "getting-user" {:user-id user-id})
  (let [user-uuid (str->uuid user-id)
        user (db.purple-bank/get-user user-uuid storage)]
    (if user
      {:data user :error nil}
      {:data nil :error :user-not-found})))

(defn create-transaction! [user-id operation amount storage logger]
  (logger-client/log logger "creating-transaction" {:user-id user-id :operation operation :amount amount})
  (let [{user :data :as user-result} (get-user user-id storage logger)
        transaction (logic/new-transaction operation amount)]
    (if user
      (if (logic/validate-transaction transaction)
        (if (logic/validate-user-balance user transaction)
          (do (-> (logic/process-user-transaction user transaction)
                  (db.purple-bank/save-user! storage))
              {:data transaction :error nil})
          {:data nil :error :non-sufficient-funds})
        {:data nil :error :invalid-transaction})
      user-result)))