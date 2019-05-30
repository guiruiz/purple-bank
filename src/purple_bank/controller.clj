(ns purple-bank.controller
  (:require [purple-bank.logic :as logic]
            [purple-bank.protocols.storage-client :as storage-client]))


(defn get-visitors [name storage]
  (do (let [visitor (logic/new-visitor name)]
        (storage-client/put! storage [:visitors (:id visitor)] visitor))
      (str "Hello, " (->> (storage-client/read-all storage)
                             (:visitors)
                             vals
                             last
                             (:name)) "!")))



