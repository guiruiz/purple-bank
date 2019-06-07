(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.components.config :as config-component]
            [purple-bank.components.routes :as routes-component]
            [purple-bank.components.storage :as storage-component]
            [purple-bank.components.logger :as logger-component]
            [purple-bank.components.debug-logger :as debug-logger-component]
            [purple-bank.components.service :as service-component]
            [purple-bank.components.servlet :as servlet-component]
            [purple-bank.service :as service]))

(defn base-system-map
  "Builds base components system map and returns it."
  [env]
  (component/system-map
    :config (config-component/new-config env)
    :storage (storage-component/new-in-memory)
    :routes (routes-component/new-routes #'service/routes)
    :logger (component/using (logger-component/new-logger) [:config])
    :service (component/using (service-component/new-service) [:config :routes :storage :logger])
    :servlet (component/using (servlet-component/new-servlet) [:service :config])))

(defn local-system-map
  "Builds base components system map overriding with local components."
  [env]
  (merge (base-system-map env)
         (component/system-map
           :logger (component/using (debug-logger-component/new-debug-logger) [:config]))))

(def system-map
  {:prod  (base-system-map :prod)
   :dev   (local-system-map :dev)
   :test  (local-system-map :test)})

(defn create-and-start-system!
  "Creates and start components system."
  ([] (create-and-start-system! :dev))
  ([env] (system-utils/start-system! (env system-map))))

(defn stop-system!
  "Stops components system and returns it."
  []
  (system-utils/stop-components!))

(defn get-or-create-system!
  "Gets or creates component system"
  [env]
  (or (deref system-utils/system)
      (create-and-start-system! env)))