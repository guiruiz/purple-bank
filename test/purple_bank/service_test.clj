(ns purple-bank.service-test
  (:require [midje.sweet :refer :all]
            [purple-bank.components :as components]
            [purple-bank.http-helpers :refer [GET]]))

(components/get-or-create-system! :test)

(fact "Get welcome message"
      (-> (GET "/")
          (get-in [:body :message])) => (contains "Welcome to Purple Bank!"))