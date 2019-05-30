(ns purple-bank.service
  (:require [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]
            [purple-bank.controller :as controller]))

(defn welcome-message [request]
    (ring-resp/response "Welcome to Purple Bank! Check README.md to get started."))

(defn create-user
  [{params :json-params
    components :components}]
  (if-let [user (controller/create-user (:storage components)
                                        params)]
    (-> user
        ring-resp/response
        (ring-resp/header "Location" (str "/users/" (:id user)))
        (ring-resp/status 201))
    (ring-resp/status {} 400)))

(def routes #{["/" :get (conj interceptor/common-interceptors `welcome-message)]
              ["/users" :post (conj interceptor/common-interceptors `create-user)]})

