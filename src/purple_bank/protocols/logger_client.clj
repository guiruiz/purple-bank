(ns purple-bank.protocols.logger-client)

(defprotocol LoggerClient
  (log [logger event data]))



