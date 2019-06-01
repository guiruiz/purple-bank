(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client])
  (:import [java.util UUID]))


(defn create-user [storage params]
  (let [user (logic/new-user params)]
    (if (logic/validate-user user)
      (do (storage-client/put! storage [:users (:id user)] user)
          user))))

(defn get-user [storage user-id]
  (try (->> (UUID/fromString user-id)
            (vector :users)
            (storage-client/read-one storage))
       (catch Exception e false)))

(defn create-transaction [storage params user]
  (if-let [transaction (logic/new-transaction params)]
    (if (logic/validate-transaction transaction)
      (if (logic/validate-operation user transaction)
        (do
          (->>
            (conj (:transactions user) transaction)
            (assoc-in user [:transactions])
            (logic/consolidate-user-balance)
            (storage-client/put! storage [:users (:id user)]))
          transaction)
        "403 invalid operation")
      "400 invalid transaction")))
; REFACTOR ASAP
; get-user -> new-transaction -> validate transaction -> validate operation -> persist transaction