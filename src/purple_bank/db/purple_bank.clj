(ns purple-bank.db.purple-bank
  (:require [purple-bank.protocols.storage-client :as storage-client]))


(defn save-user! [user storage]
  (storage-client/put! storage (:id user) user))

(defn get-user [user-uuid storage]
  (storage-client/read-one storage user-uuid))