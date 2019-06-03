(ns purple-bank.logic-test
  (:require [midje.sweet :refer :all]
            [purple-bank.logic :as logic]))


(fact "Create a new user"
      (logic/new-user {:name "Joao"}) => (just {:id uuid?
                                                :name "Joao"
                                                :balance 0.00M
                                                :transactions []}))