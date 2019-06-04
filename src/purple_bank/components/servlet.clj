(ns purple-bank.components.servlet
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]))

(defn start-server? [{env :env}]
  (or (= :prod env) (= :dev env)))

(defrecord Servlet [service config]
  component/Lifecycle
  (start [this]
    (assoc this :instance (cond-> service
                                  true :runnable-service
                                  true http/create-server
                                  (start-server? config) http/start)))
  (stop [this]
    (http/stop (:instance this))
    (dissoc this :instance)))

(defn new-servlet [] (map->Servlet {}))
