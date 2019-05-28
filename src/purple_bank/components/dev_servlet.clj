(ns purple-bank.components.dev-servlet
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.service-tools.dev :as dev]))

(defrecord DevServlet [service]
  component/Lifecycle
  (start [this]
    (assoc this :instance (-> service
                              :runnable-service
                              (assoc ::http/join? false)
                              http/create-server
                              http/start)))
  (stop [this]
    (http/stop (:instance this))
    (dissoc this :instance)))

(defn new-servlet [] (map->DevServlet {}))

(defn main [start-fn & _args]
  (start-fn {:mode :embedded}))

(defn run-dev [start-fn & _args]
  (dev/watch)
  (start-fn {:mode :embedded}))
