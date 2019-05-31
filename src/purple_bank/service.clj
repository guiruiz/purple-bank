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

(defn get-user
  [{path-params :path-params
    components :components}]
  (if-let [user (controller/get-user (:storage components) (:user-id path-params))]
    (ring-resp/response user)
    (ring-resp/status {} 404)))

(defn create-transaction
  [{params :json-params
    path-params :path-params
    components :components}]
   (let [user-id (:user-id path-params)
         transaction (controller/create-transaction (:storage components)
                                                       params
                                                       user-id)]
     (-> transaction
         ring-resp/response
         (ring-resp/header "Location" (str "/users/" user-id "/transactions/" (:id transaction)))
         (ring-resp/status 200))))

(def routes #{["/" :get (conj interceptor/common-interceptors `welcome-message)]
              ["/users" :post (conj interceptor/common-interceptors `create-user)]
              ["/users/:user-id" :get (conj interceptor/common-interceptors `get-user)]
              ["/users/:user-id/transactions" :post (conj interceptor/common-interceptors `create-transaction)]})