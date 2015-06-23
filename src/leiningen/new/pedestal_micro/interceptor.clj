(ns {{namespace}}.interceptor
  "Common interceptors for services:
   - Timestamping
   - Request tokens
   - Datomic DB support"
  (:require [io.pedestal.interceptor :refer [interceptor]]
            [clj-time.core :as clj-time]
            [datomic.api :as d]
            [{{namespace}}.util :as util]))

(def attach-received-time
  "Attaches a timestamp to every request."
  (interceptor
    {:name ::attach-received-time
     :enter (fn [ctx]
              (assoc-in ctx [:request :received-time] (clj-time/now)))}))

(def attach-request-id
  "Attaches a request ID to every request;
  If there's a '{{namespace}}-request-id' header, it will use that value, otherwise it will
  generate a short hash"
  (interceptor
    {:name ::attach-request-id
     :enter (fn [ctx]
              (let [req-id (get-in ctx
                                   [:request :headers "{{namespace}}-request-id"]
                                   (util/short-hash))]
                (-> ctx
                    (assoc-in [:request :request-id] req-id)
                    ;; just in case someone goes looking in the headers...
                    ;; This is also a special case for "forward-headers"
                    (assoc-in [:request :headers "{{namespace}}-request-id"] req-id))))}))

(defn insert-datomic
  "Provide a Datomic conn and db in all incoming requests"
  [uri]
  (interceptor
    {:name ::insert-datomic
     :enter (fn [context]
              (let [conn (d/connect uri)]
                (-> context
                    (assoc-in [:request :conn] conn)
                    (assoc-in [:request :db] (d/db conn)))))}))

