(ns purple-bank.components.system-utils
  (:require [com.stuartsierra.component :as component]))

(def system (atom nil))

(defn stop-components! []
  (swap! system #(component/stop %)))

(defn stop-system! []
  (stop-components!)
  (shutdown-agents))

(defn ^:private system-for-env [environment systems]
  (get systems environment (:base-system systems)))

(defn bootstrap! [systems-map environment]
  (let [system-map ((system-for-env environment systems-map))] ;CHECK THIS DOUBLE PARENTHESIS
    (->> system-map
         component/start
         (reset! system))))


