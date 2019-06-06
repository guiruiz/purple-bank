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
  (read-one [_this key] (get @storage key))
  (put! [_this key data] (swap! storage #(assoc % key data)))
  (clear-all! [_this] (reset! storage {})))

(defn new-in-memory []
  (->InMemoryStorage (atom {})))