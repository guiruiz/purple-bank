(ns purple-bank.interceptor
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.chain :as chain]
            [ring.util.response :as ring-resp]
            [purple-bank.controller :as controller]))

(def common-interceptors
  [(body-params/body-params)
   http/json-body])

(def validate-user-id
  ;; Get user-id from path-params and tries to retrieve the user from storage.
  ;; If the user is found, associates it to request on context.
  ;; If the user is not found, associates status code 404 to response on context
  ;; and terminates interceptors chain.
  {:name :validate-user-id
   :enter (fn [context]
            (let [storage (get-in context [:request :components :storage])
                  user-id (get-in context [:request :path-params :user-id])]
              (if-let [user (controller/get-user storage user-id)]
                (assoc-in context [:request :user] user)
                (chain/terminate (assoc context :response (ring-resp/status {} 404))))))})