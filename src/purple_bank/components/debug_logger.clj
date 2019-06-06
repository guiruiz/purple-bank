(ns purple-bank.components.debug-logger
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [purple-bank.protocols.logger-client :as logger-client]
            [clj-time.core :as t]))

(defrecord DebugLogger [config]
  logger-client/LoggerClient
  (log [_ event data]
    (log/debug
      (str "TIMESTAMP: " (t/now) " - ENV: " (:env config) " - EVENT: " event " - DATA: " data)))

  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn new-debug-logger [] (map->DebugLogger {}))
