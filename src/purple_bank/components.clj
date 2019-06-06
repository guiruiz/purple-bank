(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.components.config :as config-component]
            [purple-bank.components.routes :as routes-component]
            [purple-bank.components.storage :as storage-component]
            [purple-bank.components.http-kit :as http-kit-component]
            [purple-bank.components.service :as service-component]
            [purple-bank.components.servlet :as servlet-component]
            [purple-bank.service :as service]))

(defn system-map
  "Builds components system map and returns it."
  [env]
  (component/system-map
    :config (config-component/new-config env)
    :storage (storage-component/new-in-memory)
    :routes (routes-component/new-routes #'service/routes)
    :http (http-kit-component/new-http-client)
    :service (component/using (service-component/new-service) [:config :routes :storage])
    :servlet (component/using (servlet-component/new-servlet) [:service :config])))

(defn create-and-start-system!
  "Creates and start components system."
  ([] (create-and-start-system! :dev))
  ([env] (system-utils/start-system! (system-map env))))


(defn stop-system!
  "Stops components system and returns it."
  []
  (system-utils/stop-components!))

(defn get-or-create-system!
  "Gets or creates component system"
  [env]
  (or (deref system-utils/system)
      (create-and-start-system! env)))