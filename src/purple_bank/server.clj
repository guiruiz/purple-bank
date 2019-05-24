(ns purple-bank.server
  (:gen-class) ; indicates -main method to uberjar
  (:require [io.pedestal.http :as http]
            [purple-bank.service :as service]
            [io.pedestal.http.route :as route]))

(defonce runnable-service (http/create-server service/service))

(defn run-dev
  "The entry point for 'lein run-dev'"
  [& args]
  (println "\n Starting DEV server...")
  (-> service/service
      (merge {:env :dev
              ::http/join? false
              ::http/routes #(route/expand-routes (deref #'service/routes))
              ::http/allowed-origins  {:creds true :allowed-origins (constantly true)}
              ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      http/default-interceptors
      http/dev-interceptors
      http/create-server
      http/start))

(defn -main
  "The entry point for 'lein run'"
  [& args]
  (println "Starting server...")
  (http/start runnable-service))
