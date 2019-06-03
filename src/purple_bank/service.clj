(ns purple-bank.service
  (:require [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]
            [purple-bank.controller :as controller]))

(defn handle-welcome-message [request]
    (ring-resp/response "Welcome to Purple Bank! Check README.md to get started."))

(defn handle-create-user
  [{params :json-params
    components :components}]
  (if-let [user (controller/create-user (:storage components) params)]
    (-> user
        ring-resp/response
        (ring-resp/header "Location" (str "/users/" (:id user)))
        (ring-resp/status 201))
    (ring-resp/status {} 400)))

(defn handle-get-user
  [{user :user}]
    (ring-resp/response user))

(defn handle-create-transaction
  [{params :json-params
    components :components
    user :user}]
  (if-let [transaction (controller/build-transaction params)]
    (if (controller/process-transaction (:storage components) user transaction)
      (-> transaction
          ring-resp/response
          (ring-resp/header "Location" (str "/users" (:id user) "/transactions/" (:id transaction)))
          (ring-resp/status 201))
      (ring-resp/status {} 403))
    (ring-resp/status {} 400)))

(def routes #{["/" :get (conj interceptor/common-interceptors
                              `handle-welcome-message)]
              ["/users" :post (conj interceptor/common-interceptors
                                    `handle-create-user)]
              ["/users/:user-id" :get (conj interceptor/common-interceptors
                                            interceptor/validate-user-id
                                            `handle-get-user)]
              ["/users/:user-id/transactions" :post (conj interceptor/common-interceptors
                                                          interceptor/validate-user-id
                                                          `handle-create-transaction)]})