(ns purple-bank.service
  (:require [ring.util.response :as ring-resp]
            [purple-bank.interceptor :as interceptor]
            [purple-bank.controller :as controller]))

(defn welcome-message-handler [request]
    (ring-resp/response "Welcome to Purple Bank! Check README.md to get started."))

(defn create-user-handler
  "Tries to create a new user from request. If it's successful, returns response with status code 201,
  created user on body and a Location header containing user path. If it's unsuccessful, returns
  response with status code 400."
  [{params :json-params
    components :components}]
  (if-let [user (controller/create-user (:storage components) params)]
    (-> user
        ring-resp/response
        (ring-resp/header "Location" (str "/users/" (:id user)))
        (ring-resp/status 201))
    (ring-resp/status {} 400)))

(defn get-user-handler
  "Returns response with status code 200 and the user on its body."
  [{user :user}]
    (ring-resp/response user))

(defn create-transaction-handler
  "First, tries to build a transaction. If the transaction is valid, then tries to process it.
  If the transaction process is successful, returns a response with status code 201 and
  created transaction on body. If the transaction couldn't be processed due to insufficient
  user balance, returns a response with status code 403.
  If the transaction is invalid, returns a response with status code 400."
  [{params :json-params
    components :components
    user :user}]
  (if-let [transaction (controller/build-transaction params)]
    (if (controller/process-transaction (:storage components) user transaction)
      (-> transaction
          ring-resp/response
          (ring-resp/status 201))
      (ring-resp/status {} 403))
    (ring-resp/status {} 400)))

(def routes #{["/" :get (conj interceptor/common-interceptors
                              `welcome-message-handler)]
              ["/users" :post (conj interceptor/common-interceptors
                                    `create-user-handler)]
              ["/users/:user-id" :get (conj interceptor/common-interceptors
                                            interceptor/validate-user-id
                                            `get-user-handler)]
              ["/users/:user-id/transactions" :post (conj interceptor/common-interceptors
                                                          interceptor/validate-user-id
                                                          `create-transaction-handler)]})