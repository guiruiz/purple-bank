(ns purple-bank.test-helper
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [clojure.data.json :as json]
            [io.pedestal.http :as http]))

(defn service-fn
  [component]
  (::http/service-fn (:instance component)))

(defn prepare-request-body
  [options]
  (let [options (apply sorted-map options)]
    (-> (if (:body options)
          (update options :body #(json/write-str %))
          options)
        seq
        flatten)))

(defn prepare-response
  [response]
  (if-not (clojure.string/blank? (:body response))
    (update response :body #(json/read-str % :key-fn keyword))
    response))

(defn response
  [component verb url & options]
  (prepare-response (apply response-for
                           (service-fn component)
                           verb
                           url
                           (prepare-request-body options))))