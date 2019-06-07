(ns purple-bank.system-utils
  (:require [com.stuartsierra.component :as component]
            [purple-bank.components :as pb.components]))

(def system (atom nil))


(defn get-component! [component-name]
  "Get component from system identified by name."
  (some-> system deref (get component-name)))

(defn start-system! [system-map]
  "Starts components system and returns it."
    (->> system-map
         component/start
         (reset! system)))

(defn create-and-start-system!
  "Creates and start components system."
  ([] (create-and-start-system! :dev))
  ([env] (start-system! (pb.components/get-system-map env))))

(defn stop-system!
  "Stops components system and returns it."
  []
  (swap! system #(component/stop %)))

(defn get-or-create-system!
  "Gets or creates component system"
  [env]
  (or (deref system)
      (create-and-start-system! env)))


