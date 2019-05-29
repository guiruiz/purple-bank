(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.components.config :as config]
            [purple-bank.components.routes :as routes]
            [purple-bank.components.service :as service]
            [purple-bank.components.dev-servlet :as dev-servlet]
            [purple-bank.service :as purple-bank.service]))

(defn system-map [env]
  (component/system-map
    :config (config/new-config env)
    :routes (routes/new-routes #'purple-bank.service/routes)
    :service (component/using (service/new-service) [:config :routes])
    :servlet (component/using (dev-servlet/new-servlet) [:service])))

(defn create-and-start-system!
  ([] (create-and-start-system! :dev))
  ([env] (system-utils/start-system! (system-map env))))


(defn stop-system! [] (system-utils/stop-components!))
