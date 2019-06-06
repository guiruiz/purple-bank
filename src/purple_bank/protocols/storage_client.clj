(ns purple-bank.protocols.storage-client)

(defprotocol StorageClient
  (read-one   [storage key])
  (put!       [storage key data])
  (clear-all! [storage]))