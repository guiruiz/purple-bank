(ns purple-bank.interceptor
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.chain :as chain]
            [ring.util.response :as ring-resp]))

(def common-interceptors [(body-params/body-params) http/html-body])

(def validade-name
  {:name :validate-name
   :enter (fn [context]
            (let [name (get-in context [:request :params :name])]
              (if (clojure.string/blank? name)
                (chain/terminate (assoc context :response (ring-resp/status {} 400)))
                (if (= name 3.14)
                  (assoc-in context [:request :name] (str "Pi"))
                  (assoc-in context [:request :name] (clojure.string/capitalize name))))))})