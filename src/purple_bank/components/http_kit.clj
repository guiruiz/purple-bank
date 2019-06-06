(ns purple-bank.components.http-kit
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.client :as http-kit]
            [purple-bank.protocols.http-client :as http-client]))

(defrecord HttpKit []
  http-client/HttpClient
  (request! [_ {:keys [url method] :as options}]
    (let [response @(http-kit/request options)]
      (when (:error response)
        (throw (ex-info "Http error" {}))
        response)))

  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn new-http-client []
  (map->HttpKit {}))
