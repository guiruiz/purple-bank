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




;(def validate-name
;  {:name :validate-name
;   :enter (fn [context]
;            (let [name (get-in context [:request :params :name])]
;              (if (clojure.string/blank? name)
;                (chain/terminate (assoc context :response (ring-resp/status {} 400)))
;                (if (= name "3.14")
;                  (assoc-in context [:request :name] (str "Pi"))
;                  (assoc-in context [:request :name] (clojure.string/capitalize name))))))})
;
;
;(def validate-user-id
;  {:name :validate-user-id
;   :enter (fn [context]
;            (let [user-id (get-in context [:request :json-params])]
;              (assoc-in context [:request :name] (:name json))))})