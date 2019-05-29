(ns purple-bank.service
  (:require [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]))

(defn hello-page
  [request]
  (ring-resp/response (str "Hello, " (get-in request [:name]) "!")))

(def routes #{["/" :get (conj interceptor/common-interceptors interceptor/validate-name `hello-page)]})

