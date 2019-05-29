(ns purple-bank.components.service
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.interceptor.helpers :refer [before]]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]))

(defn- add-system [service-component]
  (before (fn [context] (assoc-in context [:request :components] service-component))))

(defn system-interceptors
  "Extend to service's interceptors to include one to inject the components
   into the request object"
  [service-map service-component]
  (update-in service-map
             [::http/interceptors]
             #(vec (->> % (cons (add-system service-component))))))

(defn base-service-map [routes port]
  {:env                  :prod
   ::http/router         :prefix-tree
   ::http/routes         #(route/expand-routes (deref routes))
   ::http/resource-path  "/public"
   ::http/type           :jetty
   ::http/port           port})

(defn prod-service-init [service-map]
  (http/default-interceptors service-map))

(defn dev-service-init [service-map]
  (-> service-map
      (merge {:env            :dev
              ::http/join?    false
              ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}})
  http/default-interceptors
  http/dev-interceptors))

(defn runnable-service [config routes service-component]
  (let [env           (:env config)
        port          (:port  config)
        service-map  (base-service-map routes port)]
    (-> (if(= env :prod)
          (prod-service-init service-map)
          (dev-service-init service-map))
        (system-interceptors service-component))))

(defrecord Service [config routes storage]
  component/Lifecycle
  (start [this]
    (assoc this
           :runnable-service
           (runnable-service (:config config) (:routes routes) this)))

  (stop [this]
    (dissoc this :runnable-service)))

(defn new-service [] (map->Service {}))
