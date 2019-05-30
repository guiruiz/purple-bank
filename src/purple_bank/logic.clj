(ns purple-bank.logic
  (:import [java.util UUID]))


(defn new-user [params]
  {:id (UUID/randomUUID)
   :name (:name params)
   :balance 0.0})

(defn validate-user [{:keys [name] :as user}]
  (and (string? name) user))