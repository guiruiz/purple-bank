(ns purple-bank.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [purple-bank.controller :as controller]))

(def common-interceptors
  [(body-params/body-params)
   http/json-body])

(defn welcome-message-handler
  "Returns response with status code 200 and welcome message on body."
  [_request]
  (ring-resp/response {:message "Welcome to Purple Bank! Check README.md to get started."}))

(defn create-user-handler
  "Tries to create a new user from request.
  If it's successful, returns response with status code 201, created user on body and a Location
  header containing user path.
  If it's unsuccessful, returns response with status code 400."
  [{{:keys [name]}    :json-params
    {:keys [storage logger]} :components}]
  (let [{:keys [data error]} (controller/create-user! name storage logger)
        user-id (:id data)]
    (if data
      (-> data
          ring-resp/response
          (ring-resp/header "Location" (str "/users/" user-id))
          (ring-resp/status 201))
      (case error
        :invalid-user (ring-resp/status {} 400)))))

(defn get-user-handler
  "Tries to get the user identified by :user-id from path-params.
  If it's successful, returns response with status code 200 and the user on body.
  If it's unsuccessful, returns response with status code 404."
  [{{:keys [user-id]} :path-params
    {:keys [storage logger]} :components}]
  (let [{:keys [data error]} (controller/get-user user-id storage logger)]
    (if data
      (ring-resp/response data)
      (case error
        :user-not-found (ring-resp/status {} 404)))))

(defn create-transaction-handler
  "Tries to create a transaction for the user identified by :user-id from path-params.
  If user is not found, returns a response with status code 404.
  If transaction is invalid, returns a response with status code 400.
  If transaction is valid but the user has no balance to realize it, returns a response with status code 403.
  If everything is valid, returns a response with status code 201 and\n  created transaction  on body."
  [{{:keys [user-id]} :path-params
    {:keys [operation amount]} :json-params
    {:keys [storage logger]} :components}]
  (let [{:keys [data error]} (controller/create-transaction! user-id operation amount storage logger)]
    (if data
      (-> data
          ring-resp/response
          (ring-resp/status 201))
      (case error
        :user-not-found (ring-resp/status {} 404)
        :invalid-transaction (ring-resp/status {} 400)
        :non-sufficient-balance (ring-resp/status {} 403)))))

(defn get-transactions-handler
  [{{:keys [user-id]} :path-params
    {:keys [storage logger]} :components}]
  (let [{:keys [data error]} (controller/get-transactions user-id storage logger)]
    (if data
      (-> data
          ring-resp/response
          (ring-resp/status 201))
      (case error
        :user-not-found (ring-resp/status {} 404)))))


(def routes #{["/" :get (conj common-interceptors
                              `welcome-message-handler)]
              ["/users" :post (conj common-interceptors
                                    `create-user-handler)]
              ["/users/:user-id" :get (conj common-interceptors
                                            `get-user-handler)]
              ["/users/:user-id/transactions" :post (conj common-interceptors
                                                          `create-transaction-handler)]
              ["/users/:user-id/transactions" :get (conj common-interceptors
                                                         `get-transactions-handler)]})