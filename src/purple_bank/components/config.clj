(ns purple-bank.components.config
  (:require [com.stuartsierra.component :as component]))

(def prod-config-map {:env         :prod
                      :port        8080})

(def dev-config-map {:env         :dev
                     :port        8080})

(def test-config-map {:env         :test
                      :port        8080})

(def config-map
  {:prod prod-config-map
   :dev dev-config-map
   :test test-config-map})

(defrecord Config [env]
  component/Lifecycle
  (start [this] (assoc this :config (get config-map env)))
  (stop [this] (dissoc this :config)))

(defn new-config [env] (map->Config {:env env}))
