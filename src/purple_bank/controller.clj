(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.db.purple-bank :as db.purple-bank]
            [purple-bank.protocols.logger-client :as logger-client])
  (:import [java.util UUID]))


(defn create-user
  "Builds and validates user. If it's valid, puts the user on storage and returns it."
  [user-name storage logger]
  (logger-client/log logger "creating-user" {:user-name user-name})
  (let [user (logic/new-user user-name)]
    (if (logic/validate-user user)
      (do (db.purple-bank/save-user! user storage)
          user))))

(defn get-user
  "Retrieves user from storage identified by user-id."
  [user-id storage logger]
  (logger-client/log logger "getting-user" user-id)
  (try (-> (UUID/fromString user-id)
           (db.purple-bank/get-user storage))
       (catch Exception _ false)))

(defn build-transaction
  "Builds and validates transaction. If it's valid, returns the transaction."
  [operation amount logger]
  (logger-client/log logger "building-transaction" {:operation operation :amount amount})
  (->> (logic/new-transaction operation amount)
       (logic/validate-transaction)))

(defn process-transaction
  "Validates transaction operation. If it's valid, process transaction, updates the user on storage
  and returns the transaction."
  [transaction user storage logger]
  (logger-client/log logger "procesing-transaction" {:transaction transaction :user user})
  (if (logic/validate-operation user transaction)
    (do
      (-> (logic/process-user-transaction user transaction)
           (db.purple-bank/save-user! storage))
      transaction)))

