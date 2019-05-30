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