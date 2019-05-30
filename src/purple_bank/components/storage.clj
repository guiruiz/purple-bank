(ns purple-bank.components.storage
  (:require [com.stuartsierra.component :as component]
            [purple-bank.protocols.storage-client :as storage-client]))

(defrecord InMemoryStorage [storage]
  component/Lifecycle
  (start [this] this)
  (stop [this]
    (reset! storage {})
    this)

  storage-client/StorageClient
  (read-all [_this] @storage)
  (read-one [_this domain] (get-in @storage domain))
  (put! [_this domain data] (swap! storage #(assoc-in % domain data)))
  (clear-all! [_this] (reset! storage {})))

(defn new-in-memory []
  (->InMemoryStorage (atom {})))