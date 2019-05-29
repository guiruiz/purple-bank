(ns purple-bank.protocols.storage-client)

(defprotocol StorageClient
  (read-all   [storage])
  (put!       [storage update-fn])
  (clear-all! [storage]))