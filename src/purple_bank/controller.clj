(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client]))


(defn create-user [storage params]
  (let [user (logic/new-user params)]
    (if (logic/validate-user user)
      (do (storage-client/put! storage [:user (:id user)] user)
          user))))



