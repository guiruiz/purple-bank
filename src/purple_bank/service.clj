(ns purple-bank.service
  (:require [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]
            [purple-bank.controller :as controller]))

(defn get-visitors
  [{name :name
    components :components}]
  (let [message (controller/get-visitors name (:storage components))]
    (ring-resp/response message)))

(def routes #{["/" :get (conj interceptor/common-interceptors interceptor/validate-name `get-visitors)]})

