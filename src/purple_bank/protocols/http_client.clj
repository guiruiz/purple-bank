(ns purple-bank.protocols.http-client)

(defprotocol HttpClient
  (request! [component options]))



