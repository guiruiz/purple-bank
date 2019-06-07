(ns purple-bank.system-utils
  (:require [com.stuartsierra.component :as component]))

(def system (atom nil))

(defn stop-components! []
  "Stops components system and returns it."
  (swap! system #(component/stop %)))

(defn get-component! [component-name]
  "Get component from system identified by name."
  (some-> system deref (get component-name)))

(defn start-system! [system-map]
  "Starts components system and returns it."
    (->> system-map
         component/start
         (reset! system)))


