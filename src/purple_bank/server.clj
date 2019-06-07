(ns purple-bank.server
  (:gen-class) ; indicates -main method to uberjar
  (:require [purple-bank.system-utils :as system-utils]))

(defn run-dev
  "The entry point for 'lein run-dev'"
  [& args]
  (println "\n Starting DEV server...")
  (system-utils/create-and-start-system! :dev))

(defn -main
  "The entry point for 'lein run'"
  [& args]
  (println "Starting server...")
  (system-utils/create-and-start-system! :prod))
