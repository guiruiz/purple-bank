(ns purple-bank.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [purple-bank.controller :as controller]))

(def common-interceptors
  [(body-params/body-params)
   http/json-body])

(defn welcome-message-handler
  [_request]
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
  [{{:keys [user-id]} :path-params
    {:keys [storage]} :components}]
    (if-let [user (controller/get-user storage user-id)]
      (ring-resp/response user)
      (ring-resp/status {} 404)))

(defn create-transaction-handler
  "First, tries to build a transaction. If the transaction is valid, then tries to process it.
  If the transaction process is successful, returns a response with status code 201 and
  created transaction on body. If the transaction couldn't be processed due to insufficient
  user balance, returns a response with status code 403.
  If the transaction is invalid, returns a response with status code 400."
  [{params            :json-params
    {:keys [user-id]} :path-params
    {:keys [storage]} :components}]
  (if-let [user (controller/get-user storage user-id)]
    (if-let [transaction (controller/build-transaction params)]
      (if (controller/process-transaction storage user transaction)
        (-> transaction
            ring-resp/response
            (ring-resp/status 201))
        (ring-resp/status {} 403))
      (ring-resp/status {} 400))
    (ring-resp/status {} 404)))


(def routes #{["/" :get (conj common-interceptors
                              `welcome-message-handler)]
              ["/users" :post (conj common-interceptors
                                    `create-user-handler)]
              ["/users/:user-id" :get (conj common-interceptors
                                            `get-user-handler)]
              ["/users/:user-id/transactions" :post (conj common-interceptors
                                                          `create-transaction-handler)]})