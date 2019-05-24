(ns purple-bank.service
  (:require [io.pedestal.http :as http]
            [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]))

(defn hello-page
  [request]
  (ring-resp/response (str "Hello, " (get-in request [:name]) "!")))

(def routes #{["/hello"
              :get
              (conj interceptor/common-interceptors
                    interceptor/validade-name
                    `hello-page)]})

(def service {:env                  :prod
              ::http/routes         routes
              ::http/resource-path  "/public"
              ::http/type           :jetty
              ::http/port           8080})
