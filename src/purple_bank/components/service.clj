(ns purple-bank.components.service
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.interceptor.helpers :refer [before]]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]))

(defn- add-system [service]
  (before (fn [context] (assoc-in context [:request :components] service))))

(defn system-interceptors
  [service-map service]
  (update-in service-map
             [::http/interceptors]
             #(vec (->> % (cons (add-system service))))))

(defn base-service [routes port]
  {:env                  :prod
   ::http/router         :prefix-tree
   ::http/routes         #(route/expand-routes (deref routes))
   ::http/resource-path  "/public"
   ::http/type           :jetty
   ::http/port           port})

(defn prod-init [service-map]
  (http/default-interceptors service-map))

(defn dev-init [service-map]
  (-> service-map
      (merge {:env            :dev
              ::http/join?    false
              ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}})
  http/default-interceptors
  http/dev-interceptors))

(defn runnable-service [config routes service]
  (let [env           (:environment config)
        port          (:dev-port  config)
        service-conf  (base-service routes port)]
    (-> (if(= :prod env)
          (prod-init service-conf)
          (dev-init service-conf))
        (system-interceptors service))))

(defrecord Service [config routes]
  component/Lifecycle
  (start [this]
    (assoc this
           :runnable-service
           (runnable-service (:config config) (:routes routes) this)))

  (stop [this]
    (dissoc this :runnable-service)))

(defn new-service [] (map->Service {}))
