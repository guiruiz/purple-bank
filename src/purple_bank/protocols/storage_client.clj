(ns purple-bank.protocols.storage-client)

(defprotocol StorageClient
  (read-all   [storage])
  (read-one   [storage domain])
  (put!       [storage domain data])
  (clear-all! [storage]))