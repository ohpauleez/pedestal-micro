(ns {{namespace}}.config
  (:require [environ.core :refer [env]]))

(defn config
  ([conf-key]
   (env conf-key))
  ([conf-map conf-key]
   (conf-map conf-key (env conf-key)))
  ([conf-map conf-key not-found]
   (conf-map conf-key (get env conf-key not-found))))

