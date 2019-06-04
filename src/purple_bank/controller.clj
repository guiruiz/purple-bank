(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client])
  (:import [java.util UUID]))


(defn create-user [storage params]
  "Builds and validates user. If it's valid, puts the user on storage and returns it."
  (let [user (logic/new-user params)]
    (if (logic/validate-user user)
      (do (storage-client/put! storage [:users (:id user)] user) user))))

(defn get-user [storage user-id]
  "Retrieves user from storage identified by user-id."
  (try (->> (UUID/fromString user-id)
            (vector :users)
            (storage-client/read-one storage))
       (catch Exception e false)))

(defn build-transaction [params]
  "Builds and validates transaction. If it's valid, returns the transaction."
  (->> (logic/new-transaction params)
       (logic/validate-transaction)))

(defn process-transaction [storage user transaction]
  "Validates transaction operation. If it's valid, process transaction, updates the user on storage
  and returns the transaction."
  (if (logic/validate-operation user transaction)
    (do
      (->> (logic/process-user-transaction user transaction)
           (storage-client/put! storage [:users (:id user)]))
      transaction)))

