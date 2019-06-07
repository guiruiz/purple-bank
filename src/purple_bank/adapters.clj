(ns purple-bank.adapters
  (:import [java.util UUID]))


(defn str->uuid [uuid-str]
  (try
    (UUID/fromString uuid-str)
    (catch Exception _ nil)))