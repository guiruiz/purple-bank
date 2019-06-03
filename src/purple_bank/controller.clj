(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client])
  (:import [java.util UUID]))


(defn create-user [storage params]
  (let [user (logic/new-user params)]
    (if (logic/validate-user user)
      (do (storage-client/put! storage [:users (:id user)] user) user))))

(defn get-user [storage user-id]
  (try (->> (UUID/fromString user-id)
            (vector :users)
            (storage-client/read-one storage))
       (catch Exception e false)))

(defn build-transaction [params]
  (->> (logic/new-transaction params)
       (logic/validate-transaction)))

(defn process-transaction [storage user transaction]
  (if (logic/validate-operation user transaction)
    (do
      (->> (conj (:transactions user) transaction)
           (assoc-in user [:transactions])
           (logic/consolidate-user-balance transaction)
           (storage-client/put! storage [:users (:id user)]))
      transaction)))
