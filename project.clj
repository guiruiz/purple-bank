(defproject purple-bank "0.1.0-SNAPSHOT"
  :description "Purple Bank"
  :url "http://nubank.com.br"
  :license {:name "Free"}
  :plugins [[lein-midje "3.2.1"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [com.stuartsierra/component "0.4.0"]
                 [org.clojure/tools.logging "0.4.1"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "purple-bank.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.5"]
                                  [midje "1.9.1"]
                                  [org.clojure/data.json "0.2.6"]
                                  [nubank/selvage "0.0.1"]]}
             :uberjar {:aot [purple-bank.server]}}
  :main ^{:skip-aot true} purple-bank.server)
