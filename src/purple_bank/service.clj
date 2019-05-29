(ns purple-bank.service
  (:require [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]
            [purple-bank.controller :as controller]))

(defn say-hello
  [{name :name}]
  (let [message (controller/get-hello-message name)]
    (ring-resp/response message)))

(def routes #{["/" :get (conj interceptor/common-interceptors interceptor/validate-name `say-hello)]})

