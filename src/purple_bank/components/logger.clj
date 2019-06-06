(ns purple-bank.components.logger
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [purple-bank.protocols.logger-client :as logger-client]
            [clj-time.core :as t]))

(defrecord Logger [config]
  logger-client/LoggerClient
  (log [_ event _data] (log/info (str (t/now)) (:env config) event))

  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn new-logger [] (map->Logger {}))
