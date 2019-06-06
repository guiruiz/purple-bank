(ns purple-bank.http-helpers
  (:require [io.pedestal.http :as http]
            [clojure.data.json :as json]
            [io.pedestal.test :refer [response-for]]
            [purple-bank.system-utils :as system-utils]))


(defn service-fn []
  (::http/service-fn (:instance (system-utils/get-component :servlet))))

(defn parse-response [{:keys [headers status body]}]
  {:body (when (not (clojure.string/blank? body)) (json/read-str body :key-fn keyword))
   :status status
   :headers headers})

(defn execute-request [method url body]
  (response-for (service-fn) method url
                :body (when body (json/write-str body))
                :headers {"Content-Type" "application/json"}))

(defn request!
  ([method url] (request! method url nil))
  ([method url body]
   (-> (execute-request method url body)
       (parse-response))))

(def GET  (partial request! :get))
(def POST (partial request! :post))