(ns purple-bank.service-test
  (:require [midje.sweet :refer :all]
            [purple-bank.system-utils :as system-utils]
            [purple-bank.http-helpers :refer [GET]]))

(system-utils/get-or-create-system! :test)

(fact "Get welcome message"
      (-> (GET "/")
          (get-in [:body :message])) => (contains "Welcome to Purple Bank!"))