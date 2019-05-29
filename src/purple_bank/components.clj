(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.components.config :as config-component]
            [purple-bank.components.routes :as routes-component]
            [purple-bank.components.service :as service-component]
            [purple-bank.components.dev-servlet :as dev-servlet-component]
            [purple-bank.service :as service]))

(defn system-map [env]
  (component/system-map
    :config (config-component/new-config env)
    :routes (routes-component/new-routes #'service/routes)
    :service (component/using (service-component/new-service) [:config :routes])
    :servlet (component/using (dev-servlet-component/new-servlet) [:service])))

(defn create-and-start-system!
  ([] (create-and-start-system! :dev))
  ([env] (system-utils/start-system! (system-map env))))


(defn stop-system! [] (system-utils/stop-components!))
