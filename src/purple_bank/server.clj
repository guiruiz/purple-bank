(ns purple-bank.server
  (:gen-class) ; indicates -main method to uberjar
  (:require [purple-bank.components :as components]))

(defn run-dev
  "The entry point for 'lein run-dev'"
  [& args]
  (println "\n Starting DEV server...")
  (components/create-and-start-system! :local-system))

(defn -main
  "The entry point for 'lein run'"
  [& args]
  (println "Starting server...")
  (components/create-and-start-system! :base-system))
