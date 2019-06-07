(ns purple-bank.controller-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [purple-bank.db.purple-bank :as db.purple-bank]
            [purple-bank.protocols.logger-client :as logger-client]
            [purple-bank.controller :as controller])
  (:import [java.util UUID]))


(def user-uuid (UUID/randomUUID))

(def user-name "Joao")

(def user {:id user-uuid
           :name user-name
           :balance 0.00M
           :transactions []})

(fact "Tries to create valid user"
      (controller/create-user! user-name ..storage.. ..logger..) => (contains {:data not-empty :error nil?})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (db.purple-bank/save-user! (contains {:name user-name}) ..storage..) => irrelevant))

(fact "Tries to create invalid user"
      (controller/create-user! nil ..storage.. ..logger..) => (just {:data nil? :error :invalid-user})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant))

(fact "Tries to get existent user"
      (controller/get-user user-uuid ..storage.. ..logger..) => (just {:data not-empty :error nil?})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (db.purple-bank/get-user irrelevant ..storage..) => user))

(fact "Tries to get nonexistent user"
      (controller/get-user user-uuid ..storage.. ..logger..) => (just {:data nil? :error :user-not-found})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (db.purple-bank/get-user irrelevant ..storage..) => nil))

(fact "Tries to create invalid transaction"
      (controller/create-transaction! user-uuid
                                      :foo
                                      20
                                      ..storage..
                                      ..logger..) => (just {:data nil? :error :invalid-transaction})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (controller/get-user user-uuid irrelevant irrelevant) => {:data user :error nil}))

(fact "Tries to create valid transaction"
      (controller/create-transaction! user-uuid
                                      :credit
                                      300
                                      ..storage..
                                      ..logger..) => (just {:data not-empty :error nil?})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (controller/get-user user-uuid irrelevant irrelevant) => {:data user :error nil}
        (db.purple-bank/save-user! irrelevant ..storage..) => irrelevant))


(fact "Tries to create valid transaction to a user with non-sufficient funds"
      (controller/create-transaction! user-uuid
                                      :debit
                                      300
                                      ..storage..
                                      ..logger..) => (just {:data nil? :error :non-sufficient-balance})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (controller/get-user user-uuid irrelevant irrelevant) => {:data user :error nil}))

(fact "Tries to create valid transaction to a user"
      (controller/create-transaction! user-uuid
                                      :debit
                                      300
                                      ..storage..
                                      ..logger..) => (just {:data nil? :error :non-sufficient-balance})
      (provided
        (logger-client/log ..logger.. irrelevant irrelevant) => irrelevant
        (controller/get-user user-uuid irrelevant irrelevant) => {:data user :error nil}))
