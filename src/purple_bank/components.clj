(ns purple-bank.components
  (:require [com.stuartsierra.component :as component]
            [purple-bank.components.system-utils :as system-utils]
            [purple-bank.components.config :as config]
            [purple-bank.components.routes :as routes]
            [purple-bank.components.service :as service]
            [purple-bank.components.dev-servlet :as dev-servlet]
            [purple-bank.service :as purple-bank.service]))

(def web-app-deps [:config :routes]) ;; components that will be available in the pedestal http request map

(def base-config-map {:environment :prod
                      :dev-port    8080})

(def local-config-map {:environment :dev
                       :dev-port    8080})

(defn base []
  (component/system-map
    :config (config/new-config base-config-map)
    :routes (routes/new-routes #'purple-bank.service/routes)
    :service (component/using (service/new-service) web-app-deps)
    :servlet (component/using (dev-servlet/new-servlet) [:service])))

(defn e2e []
  (merge (base)
         (component/system-map
           :config (config/new-config local-config-map))))

(defn test-system []
  (merge (base)
         (component/system-map
           :config (config/new-config local-config-map))))

(def systems-map
  {:e2e-system   e2e
   :local-system e2e
   :test-system  test-system
   :base-system  base})

(defn create-and-start-system!
  ([] (create-and-start-system! :base-system))
  ([env] (system-utils/bootstrap! systems-map env)))


(defn stop-system! [] (system-utils/stop-components!))
