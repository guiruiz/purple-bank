(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client])
  (:import [java.util UUID]))


(defn create-user
  "Builds and validates user. If it's valid, puts the user on storage and returns it."
  [storage params]
  (let [user (logic/new-user params)]
    (if (logic/validate-user user)
      (do (storage-client/put! storage [:users (:id user)] user) user))))

(defn get-user
  "Retrieves user from storage identified by user-id."
  [storage user-id]
  (try (->> (UUID/fromString user-id)
            (vector :users)
            (storage-client/read-one storage))
       (catch Exception e false)))

(defn build-transaction
  "Builds and validates transaction. If it's valid, returns the transaction."
  [params]
  (->> (logic/new-transaction params)
       (logic/validate-transaction)))

(defn process-transaction
  "Validates transaction operation. If it's valid, process transaction, updates the user on storage
  and returns the transaction."
  [storage user transaction]
  (if (logic/validate-operation user transaction)
    (do
      (->> (logic/process-user-transaction user transaction)
           (storage-client/put! storage [:users (:id user)]))
      transaction)))

