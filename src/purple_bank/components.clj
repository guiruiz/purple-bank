(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.components.config :as config-component]
            [purple-bank.components.routes :as routes-component]
            [purple-bank.components.storage :as storage-component]
            [purple-bank.components.service :as service-component]
            [purple-bank.components.dev-servlet :as dev-servlet-component]
            [purple-bank.service :as service]))

(defn system-map
  "Builds components system map and returns it."
  [env]
  (component/system-map
    :config (config-component/new-config env)
    :storage (storage-component/new-in-memory)
    :routes (routes-component/new-routes #'service/routes)
    :service (component/using (service-component/new-service) [:config :routes :storage])
    :servlet (component/using (dev-servlet-component/new-servlet) [:service])))

(defn create-and-start-system!
  "Creates and start components system."
  ([] (create-and-start-system! :dev))
  ([env] (system-utils/start-system! (system-map env))))


(defn stop-system!
  "Stops components system and returns it."
  []
  (system-utils/stop-components!))
