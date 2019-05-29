(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client]))


(defn get-visitors [name storage]
  (do (let [visitor (logic/new-visitor name)]
        (storage-client/put! storage
                             #(assoc % (:id visitor) visitor)))
      (str "Visitors: " (storage-client/read-all storage) "!")))
