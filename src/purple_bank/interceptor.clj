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
  {:name :validate-user-id
   :enter (fn [context]
            (let [storage (get-in context [:request :components :storage])
                  user-id (get-in context [:request :path-params :user-id])]
              (if-let [user (controller/get-user storage user-id)]
                (assoc-in context [:request :user] user)
                (chain/terminate (assoc context :response (ring-resp/status {} 404))))))})