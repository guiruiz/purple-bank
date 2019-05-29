(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.components.config :as config]
            [purple-bank.components.routes :as routes]
            [purple-bank.components.service :as service]
            [purple-bank.components.dev-servlet :as dev-servlet]
            [purple-bank.service :as purple-bank.service]))


(def base-config-map {:environment :prod
                      :port    8080})

(def dev-config-map {:environment :dev
                       :port    8080})

(defn base-system []
  (component/system-map
    :config (config/new-config base-config-map)
    :routes (routes/new-routes #'purple-bank.service/routes)
    :service (component/using (service/new-service) [:config :routes])
    :servlet (component/using (dev-servlet/new-servlet) [:service])))

(defn dev-system []
  (merge (base-system)
         (component/system-map
           :config (config/new-config dev-config-map))))

(defn test-system []
  (merge (base-system)
         (component/system-map
           :config (config/new-config dev-config-map))))

(def systems-map
  {:prod  base-system
   :dev dev-system
   :test test-system})

(defn create-and-start-system!
  ([] (create-and-start-system! :dev))
  ([env] (system-utils/start-system! (get systems-map env (:dev systems-map)))))


(defn stop-system! [] (system-utils/stop-components!))
