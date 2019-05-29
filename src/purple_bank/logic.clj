(ns purple-bank.logic
  (:import [java.util UUID]))

(defn new-visitor [name]
  {:id (UUID/randomUUID)
   :name name})